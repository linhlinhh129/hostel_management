# TASKS: Phân chia Chi tiết Đầu Việc - Danh sách yêu cầu sửa chữa

**Total Story Points:** ~34 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Backend List Service (12 points)

### Task 1.1: List Request API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define GET /api/v1/requests contract
- Specify query params: page, size, status, category_id, facility_id, room_id

---

### Task 1.2: List Service Implementation (4 points)
**Duration:** 2 days  
**Description:**
- Implement filtering and pagination
- Include assigned requests only
- Support stable ordering

---

### Task 1.3: Database Query Optimization (3 points)
**Duration:** 2 days  
**Description:**
- Add necessary indexes
- Avoid N+1 fetches
- Ensure query uses filters and paginates efficiently

---

### Task 1.4: Response DTO and Metadata (3 points)
**Duration:** 1 day  
**Description:**
- Build list response with total count
- Include page info and applied filters

---

## Epic 2: API & Security (10 points)

### Task 2.1: GET Request List Endpoint (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint
- Authorize based on current operator
- Validate query params

---

### Task 2.2: Filter Validation (3 points)
**Duration:** 1 day  
**Description:**
- Validate status/category/facility IDs
- Guard against invalid page size or offset

---

### Task 2.3: Error Handling & Standardization (2 points)
**Duration:** 1 day  
**Description:**
- Standardize API responses
- Handle no-results and bad-request states

---

### Task 2.4: Performance Check (2 points)
**Duration:** 1 day  
**Description:**
- Measure API latency
- Adjust query / indexing as needed

---

## Epic 3: Frontend List Page (8 points)

### Task 3.1: List Layout (3 points)
**Duration:** 2 days  
**Description:**
- Create request table with key columns
- Show status, room, facility, category, date

---

### Task 3.2: Filter & Paging Controls (3 points)
**Duration:** 2 days  
**Description:**
- Build filter inputs and buttons
- Implement paging controls
- Keep state when navigating to detail

---

### Task 3.3: Empty & Error States (2 points)
**Duration:** 1 day  
**Description:**
- Show no-data state when list is empty
- Show messages for invalid filter values

---

## Epic 4: Testing (4 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test filter combinations and pagination
- Test auth scoping to current operator

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Verify list API and filters
- Verify empty list behavior

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | List service design & implementation |
| Sprint 2 | 10 | API, validation, optimization |
| Sprint 3 | 12 | Frontend and testing |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (list service required by endpoint)
- Task 2.1 → Task 3.1 (frontend depends on API contract)
- Task 3.2 → Task 2.2 (UI uses validated filter parameters)
