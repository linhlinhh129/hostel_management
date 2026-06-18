# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Người phụ thuộc (Tenant)

**Total Story Points:** ~24 points  
**Sprint Duration:** 2 weeks × 2.5 sprints = 5 weeks  
**Velocity:** ~9.6 points/sprint

---

## Epic 1: Backend Services (8 points)

### Task 1.1: List Dependents Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getDependents(tenantId) service
- Filter by tenant ID
- Support pagination
- Order by name

---

### Task 1.2: Get Dependent Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getDependent(dependentId, tenantId) service
- Validate ownership (dependent belongs to tenant)
- Return full details

---

### Task 1.3: Access Control (2 points)
**Duration:** 1 day  
**Description:**
- Implement tenant-scoped access control
- Validate tenant ID matches

---

## Epic 2: APIs (6 points)

### Task 2.1: List Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/dependents
- Support pagination
- Implement DTO

---

### Task 2.2: Detail Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/dependents/{id}
- Implement DTO

---

### Task 2.3: Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- Standard response format
- Error handling

---

## Epic 3: Frontend Development (8 points)

### Task 3.1: Dependent List Component (4 points)
**Duration:** 2 days  
**Description:**
- Create table: code, name, relationship, phone
- Pagination
- Click to view detail
- Empty state message

---

### Task 3.2: Dependent Detail View (3 points)
**Duration:** 1-2 days  
**Description:**
- Display all info (read-only)
- Back button to list

---

### Task 3.3: UI Polish (1 point)
**Duration:** 1 day  
**Description:**
- Review consistency
- Test responsive

---

## Epic 4: Testing (2 points)

### Task 4.1: Unit & Integration Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test services & APIs
- >= 80% coverage

---

### Task 4.2: E2E & UAT (1 point)
**Duration:** 1 day  
**Description:**
- E2E tests
- Manual UAT

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 10 | List, detail service, API |
| Sprint 2 | 10 | Frontend list, detail |
| Sprint 3 | 4 | Testing, deployment |

---

## Critical Dependencies

- Task 1.1-1.3 → Task 2.1-2.3 (services needed for API)
- Task 2.1-2.3 → Task 3.1-3.3 (API needed for frontend)
