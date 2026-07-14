# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Người phụ thuộc (Manager)

**Total Story Points:** ~42 points (Completed)  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~14 points/sprint

---

## Epic 1: Backend Infrastructure (10 points) - Completed

### Task 1.1: Database Schema Design (2 points)
**Duration:** 1 day  
**Description:**
- [x] Design `dbo.dependents` table structure
- [x] Define foreign key relationship to `dbo.users` (Tenant)
- [x] Create indexes on `tenant_id`

---

### Task 1.2: Entity Implementation (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Create Dependent model class
- [x] Map fields to database columns
- [x] Implement DAO for CRUD operations

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- [x] Write SQL migration script for table creation
- [x] Execute and verify table structure in DB

---

### Task 1.4: Validation & Error Handling (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement validation rules for Vietnamese phone numbers
- [x] Implement validation rules for identity numbers (CCCD)
- [x] Implement error handling and flash message alerts in Servlet

---

## Epic 2: Dependent Management Service (16 points) - Completed

### Task 2.1: Create Dependent Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `addDependent()` in service layer
- [x] Verify tenant exists and is ACTIVE
- [x] Verify form field validation for name and relationship
- [x] Create audit log upon successful addition

---

### Task 2.2: Get Dependent List Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `getTenantDependents()` service
- [x] Ensure only non-deleted dependents are loaded

---

### Task 2.3: Get Dependent Detail Service (2 points)
**Duration:** 1 day  
**Description:**
- [x] Implement `getDependentDetail()` service
- [x] Return full information including main tenant's details
- [x] Verify manager owns the facility of the tenant

---

### Task 2.4: Update Dependent Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `editDependent()` service
- [x] Validate modified fields (name, relationship, phone, CCCD formats)
- [x] Create audit log upon successful update

---

### Task 2.5: Soft Delete Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `removeDependent()` service
- [x] Update `deleted_at` field to current timestamp
- [x] Create audit log upon deletion

---

## Epic 3: Dependent API Controller (8 points) - Completed

### Task 3.1: Dependent API Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement GET `/manager/dependents/{id}` (Detail view)
- [x] Implement POST `/manager/tenants/{tenantId}/dependents/add` (Create submit)
- [x] Implement POST `/manager/dependents/{id}/edit` (Update submit)
- [x] Implement POST `/manager/dependents/{id}/remove` (Delete submit)
- [x] Enforce authorization check in Controller

---

### Task 3.2: API Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- [x] Standardize Servlet forward dispatching to JSP
- [x] Implement error and success alerts redirection

---

### Task 3.3: API Documentation (2 points)
**Duration:** 1 day  
**Description:**
- [x] Document routes and parameters in SPEC.md

---

## Epic 4: Frontend Development (12 points) - Completed

### Task 4.1: Dependent List Component (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Create dependents table inside tenant detail JSP
- [x] Show fields: Name, relationship, DOB, gender, phone
- [x] Implement detail links

---

### Task 4.2: Create/Edit Dependent Form (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create dependent creation modal on tenant detail page
- [x] Create edit form on dependent detail page
- [x] Call corresponding POST Servlet actions on form submit

---

### Task 4.3: Delete Confirmation Dialog (2 points)
**Duration:** 1 day  
**Description:**
- [x] Implement `onsubmit="return confirm(...)"` on deletion form
- [x] Trigger soft delete POST action

---

### Task 4.4: UI Polish (3 points)
**Duration:** 1 day  
**Description:**
- [x] Review UI consistency with HMS styles
- [x] Test responsive layouts
- [x] Add status alert messages

---

## Epic 5: Testing (6 points) - Completed

### Task 5.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- [x] Verify service and DAO methods behavior
- [x] Verify phone and identity validation helper methods

---

### Task 5.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- [x] Verify servlet routing and security filters
- [x] Verify database transactions commit/rollback

---

### Task 5.3: E2E & UAT (2 points)
**Duration:** 1 day  
**Description:**
- [x] Test complete flows in the browser (add -> list -> detail -> edit -> delete)

---

## Summary by Sprint

| Sprint | Points | Focus | Status |
|--------|--------|-------|--------|
| Sprint 1 | 14 | DB schema, entities, migrations | Completed |
| Sprint 2 | 14 | Create, list, detail, update services | Completed |
| Sprint 3 | 14 | Frontend, testing, deployment | Completed |

---

## Critical Dependencies

- Task 1.1 → 1.2 → 2.1 (DB then entities then services)
- Task 2.1-2.5 → 3.1 (services needed for API)
- Task 3.1 → 4.1-4.3 (API needed for frontend)
- Task 4.1-4.3 → 5.3 (frontend for E2E testing)
