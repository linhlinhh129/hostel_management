# AI Self-Review — N+1 & Query Analysis

Version: 2.0
Status: ACTIVE

---

# Purpose

This document defines mandatory query analysis requirements for all AI-generated DAO, Repository, Service, and SQL code.

The AI MUST assume that unoptimized queries will cause performance degradation in production and MUST proactively identify and eliminate N+1 patterns, unbounded queries, and missing indexes before generating code.

---

# Definition: Query Budget Per Page

Every list screen is allowed:

* 1 main data query (with JOIN)
* 1 COUNT query for pagination (if applicable)
* Maximum 1 additional lookup query (e.g., dropdown filter options)

**Total budget: ≤ 3 queries per page load.**

Any screen exceeding this budget MUST be explicitly justified in the Query Analysis output.

---

# RULE-01: Pre-Generation Self-Review

Before generating any DAO, Repository, Service, or SQL code, the AI MUST answer the following questions.

## N+1 Query Review

Verify:

* Is any DAO / repository / database call executed inside a loop?
* Is any SQL statement executed once per record?
* Is any nested loop triggering database access?
* Can JOIN eliminate additional queries?
* Can `IN (...)` be used for batch loading?
* Can aggregation be pushed into SQL?

If any answer is YES → redesign the implementation before generating code.

---

## Query Count Review

For every list screen, estimate:

* Number of SQL queries executed
* Expected rows returned per query
* Whether query count grows with N

The implementation MUST maintain:

* **O(1) query count per page**

The implementation MUST NOT produce:

* O(N) query growth
* O(N²) query growth

---

## DTO Review

Verify:

* Is a DTO projection more appropriate than loading a full entity?
* Is the code loading unnecessary columns?
* Is related data fetched efficiently via JOIN rather than separate calls?

---

# RULE-02: Pagination Is Mandatory

Every query that returns a list MUST include pagination.

**Required:**

```sql
SELECT *
FROM invoices
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;
```

**Forbidden:**

```sql
SELECT * FROM invoices;
-- No LIMIT — loads entire table
```

**Default page size:** 20 rows
**Maximum page size:** 100 rows

AI MUST NOT generate unbounded list queries under any circumstance.

---

# RULE-03: N+1 Pattern Reference

The following patterns are forbidden. The AI MUST recognize and eliminate them.

---

## Pattern A — Loop + DAO Call (Forbidden)

```java
// FORBIDDEN: executes N queries
for (Apartment apt : apartments) {
    Resident r = residentDAO.findByApartmentId(apt.getId());
}
```

**Fix:** JOIN apartments with residents in a single query.

```sql
SELECT a.id, a.unit_number, r.full_name
FROM apartments a
LEFT JOIN residents r ON r.apartment_id = a.id
WHERE a.building_id = ?;
```

---

## Pattern B — Lazy Child Collection (Forbidden)

```java
// FORBIDDEN: triggers N queries via implicit loading
List<Building> buildings = buildingDAO.findAll();
for (Building b : buildings) {
    int count = b.getApartments().size();
}
```

**Fix:** Push aggregation into SQL.

```sql
SELECT b.id, b.name, COUNT(a.id) AS apartment_count
FROM buildings b
LEFT JOIN apartments a ON a.building_id = b.id
GROUP BY b.id, b.name;
```

---

## Pattern C — Repeated Single Lookup (Forbidden)

```java
// FORBIDDEN: executes N queries for N invoices
for (Invoice inv : invoices) {
    String name = residentDAO.getNameById(inv.getResidentId());
}
```

**Fix:** JOIN at the query level.

```sql
SELECT i.id, i.amount, r.full_name
FROM invoices i
JOIN residents r ON r.id = i.resident_id
WHERE i.building_id = ?
LIMIT 20 OFFSET 0;
```

---

## Pattern D — IN (...) Batch Load (Allowed)

When a two-step load is unavoidable, use batch loading instead of per-item queries.

```java
// ALLOWED: 1 query regardless of N
List<Integer> apartmentIds = getApartmentIds();
List<Resident> residents = residentDAO.findByApartmentIds(apartmentIds);
```

```sql
SELECT *
FROM residents
WHERE apartment_id IN (?, ?, ?, ...);
```

---

# RULE-04: Aggregation Push-Down

Never compute aggregations in Java. Always push them into SQL.

**Forbidden:**

```java
List<Invoice> all = invoiceDAO.findAll();

long total = all.stream()
    .mapToLong(Invoice::getAmount)
    .sum();
// Loads all rows into memory before computing
```

**Required:**

```sql
SELECT SUM(amount) AS total
FROM invoices
WHERE resident_id = ?;
```

**Rule:** If the result can be computed in SQL, it MUST be computed in SQL.

---

# RULE-05: Slow Query Awareness

A query is considered **SLOW** if its estimated execution time exceeds:

| Screen Type          | Threshold |
| -------------------- | --------- |
| List Screen          | 200ms     |
| Single Record Lookup | 100ms     |
| Report / Aggregation | 500ms     |

AI MUST flag any query that:

* Lacks an index on WHERE or JOIN columns
* Uses `LIKE '%value%'` (causes full table scans)
* Performs aggregation over unbounded rows
* Contains subqueries that could be rewritten as JOINs

When flagging a query, the AI MUST suggest a resolution.

---

# RULE-06: Index Awareness

After generating any SQL query, the AI MUST identify index requirements.

**Review all columns appearing in:**

* WHERE clauses
* JOIN ON clauses
* ORDER BY clauses
* GROUP BY clauses

**Required output format:**

```text
### Index Review

| Column               | Used In      | Recommendation              |
|----------------------|--------------|-----------------------------|
| invoices.resident_id | JOIN, WHERE  | Verify index exists         |
| invoices.status      | WHERE        | Add index if filtered often |
| invoices.due_date    | ORDER BY     | Add index for sorting       |
```

If an index is missing, the AI MUST include the CREATE INDEX statement.

```sql
-- Required indexes
CREATE INDEX idx_invoices_resident_id
ON invoices(resident_id);

CREATE INDEX idx_invoices_due_date
ON invoices(due_date);
```

---

# RULE-07: Connection Pool Is Mandatory

AI MUST ensure all database access uses a connection pool.

**Required:**

```java
Connection conn = DataSourceProvider.getDataSource().getConnection();
```

**Forbidden:**

```java
// Creates a new physical connection per request
Connection conn = DriverManager.getConnection(url, user, pass);
```

Raw `DriverManager` connections MUST NOT be generated under any circumstance.

---

# RULE-08: Mandatory Query Analysis Output

After generating any DAO, Service, or SQL code, the AI MUST produce the following output.

---

## Query Analysis

```text
### Query Analysis

| # | Description                    | Tables Involved           | Count |
|---|--------------------------------|---------------------------|-------|
| 1 | Load apartments with residents | apartments JOIN residents | 1     |
| 2 | Total count for pagination     | apartments                | 1     |

Total queries per page load: 2

---

N+1 Risk: None
Reason: Resident data loaded via JOIN, not per-apartment loop.

---

Pagination: Yes
LIMIT 20 OFFSET {offset}

---

Index Requirements:
- apartments.building_id — must be indexed
- residents.apartment_id — must be indexed

Suggested:
CREATE INDEX idx_apartments_building_id
ON apartments(building_id);

CREATE INDEX idx_residents_apartment_id
ON residents(apartment_id);

---

Aggregation: Computed in SQL (COUNT, SUM)
Java-side aggregation: None

---

Connection Pool: Used via DataSourceProvider
Raw DriverManager: Not used

---

Slow Query Risk: Low
Estimated rows scanned per page: < 500
```

---

# Pull Request Review Checklist

Every DAO / Repository / SQL-related pull request MUST verify:

* [ ] No DAO or database call is executed inside a loop
* [ ] All list queries include LIMIT and OFFSET
* [ ] JOIN is used instead of per-record queries
* [ ] Aggregations are computed in SQL, not in Java
* [ ] All WHERE and JOIN columns have indexes
* [ ] No `LIKE '%value%'` on large tables without justification
* [ ] Connection pool is used — no raw `DriverManager`
* [ ] Query Analysis output is included in the PR description
* [ ] Query count is O(1) per page — not O(N) or O(N²)

Any violation MUST be fixed before merge.

---

# References

* ADR-000: Technology Stack Selection
* ADR-001: Session-Based Authentication
* Security Standards v1.0
* CONSTITUTION.md
