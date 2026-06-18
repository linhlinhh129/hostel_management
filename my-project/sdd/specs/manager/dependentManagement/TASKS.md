# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Người phụ thuộc (Manager)

**Total Story Points:** ~42 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~14 points/sprint

---

## Epic 1: Backend Infrastructure (10 points)

### Task 1.1: Database Schema Design (2 points)
**Duration:** 1 day  
**Description:**
- Design Dependent table
- Define relationship to Tenant
- Create indexes

---

### Task 1.2: Entity Implementation (3 points)
**Duration:** 1-2 days  
**Description:**
- Create Dependent JPA entity
- Setup Tenant relationship
- Create repository

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- Write migration scripts
- Test rollback

---

### Task 1.4: Validation & Error Handling (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement validation rules
- Map error codes
- Implement exception handling

---

## Epic 2: Dependent Management Service (16 points)

### Task 2.1: Create Dependent Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement createDependent() service
- Validate tenant exists & ACTIVE
- Validate CCCD unique for ACTIVE dependents
- Validate required fields
- Create audit log

**Error Codes:** TENANT_NOT_FOUND, TENANT_NOT_ACTIVE, VALIDATION_ERROR, CCCD_ALREADY_EXISTS

---

### Task 2.2: Get Dependent List Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getDepependents(tenantId) with pagination
- Support search/filter
- Load only ACTIVE dependents by default

---

### Task 2.3: Get Dependent Detail Service (2 points)
**Duration:** 1 day  
**Description:**
- Implement getDependent(dependentId) service
- Return full information
- Handle DEPENDENT_NOT_FOUND error

---

### Task 2.4: Update Dependent Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement updateDependent() service
- Validate all fields same as create
- Check CCCD uniqueness (excluding self)
- Create audit log

---

### Task 2.5: Soft Delete Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement softDeleteDependent() service
- Change status to INACTIVE
- Keep historical data
- Create audit log

---

## Epic 3: Dependent API Controller (8 points)

### Task 3.1: Dependent API Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- POST /api/v1/tenants/{tenantId}/dependents (create)
- GET /api/v1/tenants/{tenantId}/dependents (list)
- GET /api/v1/dependents/{dependentId} (detail)
- PUT /api/v1/dependents/{dependentId} (update)
- DELETE /api/v1/dependents/{dependentId} (soft delete)
- Implement DTOs
- Add authorization

---

### Task 3.2: API Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- Standard response format
- Pagination metadata
- Error response format

---

### Task 3.3: API Documentation (2 points)
**Duration:** 1 day  
**Description:**
- Generate Swagger docs
- Document all endpoints
- Add examples

---

## Epic 4: Frontend Development (12 points)

### Task 4.1: Dependent List Component (3 points)
**Duration:** 1-2 days  
**Description:**
- Create table within tenant detail page
- Show: name, DOB, relationship, phone, status
- Implement action buttons (edit, delete)
- Handle empty state

---

### Task 4.2: Create/Edit Dependent Form (4 points)
**Duration:** 2 days  
**Description:**
- Create form with fields: name, DOB, relationship, CCCD, phone
- Implement validation
- Call create or update API
- Show success/error

---

### Task 4.3: Delete Confirmation Dialog (2 points)
**Duration:** 1 day  
**Description:**
- Create confirmation dialog
- Show warning message
- Call delete API on confirm

---

### Task 4.4: UI Polish (3 points)
**Duration:** 1 day  
**Description:**
- Review UI consistency
- Test responsive design
- Optimize performance

---

## Epic 5: Testing (6 points)

### Task 5.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test service methods
- Test validation
- Aim for >= 80% coverage

---

### Task 5.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test API endpoints
- Test complete workflows
- Test error handling

---

### Task 5.3: E2E & UAT (2 points)
**Duration:** 1 day  
**Description:**
- E2E tests
- Manual UAT
- Performance testing

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | DB schema, entities, migrations |
| Sprint 2 | 14 | Create, list, detail, update services |
| Sprint 3 | 14 | Frontend, testing, deployment |

---

## Critical Dependencies

- Task 1.1 → 1.2 → 2.1 (DB then entities then services)
- Task 2.1-2.5 → 3.1 (services needed for API)
- Task 3.1 → 4.1-4.3 (API needed for frontend)
- Task 4.1-4.3 → 5.3 (frontend for E2E testing)
