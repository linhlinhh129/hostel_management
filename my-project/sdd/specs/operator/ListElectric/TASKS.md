# TASKS: Phân chia Chi tiết Đầu Việc - Danh sách điện nước

**Total Story Points:** ~30 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~10 points/sprint

---

## Epic 1: Backend Meter List Service (12 points)

### Task 1.1: Meter List API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define GET /api/v1/meter-readings contract
- Specify query params: page, size, facility_id, room_id, meter_type, start_date, end_date

---

### Task 1.2: Meter List Service Implementation (4 points)
**Duration:** 2 days  
**Description:**
- Implement filter and pagination logic
- Support meter type and date range filtering
- Scope results to current operator

---

### Task 1.3: Database Optimization (3 points)
**Duration:** 2 days  
**Description:**
- Add indexes on date, facility_id, room_id, meter_type
- Optimize query when date range is provided

---

### Task 1.4: Summary Data Response (3 points)
**Duration:** 1 day  
**Description:**
- Include totals or summary row in response
- Include pagination metadata

---

## Epic 2: API & Security (8 points)

### Task 2.1: GET Meter Readings Endpoint (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint
- Authorize operator access
- Validate query params

---

### Task 2.2: Filter Validation (2 points)
**Duration:** 1 day  
**Description:**
- Validate dates and meter type values
- Guard against invalid page/size

---

### Task 2.3: Error Handling (3 points)
**Duration:** 1 day  
**Description:**
- Standardize response codes
- Handle invalid filter combinations

---

## Epic 3: Frontend Meter List Page (6 points)

### Task 3.1: Meter List Layout (3 points)
**Duration:** 2 days  
**Description:**
- Render row list with date, type, reading, room
- Show summary totals at top or bottom

---

### Task 3.2: Filter Controls (2 points)
**Duration:** 1 day  
**Description:**
- Build date range controls
- Build meter type and location filters

---

### Task 3.3: Empty/Error States (1 point)
**Duration:** 1 day  
**Description:**
- Display no-results UI and errors

---

## Epic 4: Testing (4 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test filter combinations and totals
- Test date range behavior

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Validate API response and pagination
- Validate access scoping

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | Backend meter list implementation |
| Sprint 2 | 8 | API security and validation |
| Sprint 3 | 10 | Frontend and testing |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (service required by endpoint)
- Task 3.2 → Task 2.2 (frontend filters depend on valid query semantics)
