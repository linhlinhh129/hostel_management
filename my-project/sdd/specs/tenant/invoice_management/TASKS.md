# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Hóa đơn (Tenant)

**Total Story Points:** ~40 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8 weeks  
**Velocity:** ~10 points/sprint

---

## Epic 1: Backend Services (12 points)

### Task 1.1: List Invoices Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getInvoices(tenantId) service
- Filter by tenant ID
- Support pagination
- Order by period descending

---

### Task 1.2: Get Invoice Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getInvoiceDetail(invoiceId, tenantId) service
- Validate ownership
- Return full invoice with breakdown

---

### Task 1.3: Payment History Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getPaymentHistory(tenantId) service
- Return payments related to invoices
- Order by date descending

---

### Task 1.4: Access Control (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement tenant-scoped access control
- Validate all operations scoped to tenant

---

## Epic 2: APIs (8 points)

### Task 2.1: List Invoices Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/invoices
- Support pagination
- Implement DTO

---

### Task 2.2: Invoice Detail Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/invoices/{id}
- Implement DTO with full breakdown

---

### Task 2.3: Payment History Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/payments/history
- Support pagination
- Implement DTO

---

### Task 2.4: Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- Standard response format
- Error handling

---

## Epic 3: Frontend - Invoice Views (14 points)

### Task 3.1: Invoice List Page (4 points)
**Duration:** 2 days  
**Description:**
- Create table: period, amount, dueDate, status
- Pagination
- Click to view detail
- Empty state

---

### Task 3.2: Invoice Detail Page (5 points)
**Duration:** 2-3 days  
**Description:**
- Display full invoice breakdown
- Show: rent, electric, water, service, tax, total
- Display calculation details
- Show payment status

---

### Task 3.3: Payment History Tab (3 points)
**Duration:** 1-2 days  
**Description:**
- Show payment transactions
- Table: code, amount, date, method, status
- Pagination

---

### Task 3.4: UI Polish (2 points)
**Duration:** 1 day  
**Description:**
- Review consistency
- Test responsive

---

## Epic 4: Testing (6 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test services
- >= 80% coverage

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test APIs
- Test access control

---

### Task 4.3: E2E & UAT (2 points)
**Duration:** 1 day  
**Description:**
- E2E tests
- Manual UAT

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 10 | List, detail services, API |
| Sprint 2 | 10 | Payment history, APIs |
| Sprint 3 | 10 | Frontend list, detail |
| Sprint 4 | 10 | Payment history, testing |

---

## Critical Dependencies

- Task 1.1-1.4 → Task 2.1-2.4 (services needed for API)
- Task 2.1-2.4 → Task 3.1-3.4 (API needed for frontend)
