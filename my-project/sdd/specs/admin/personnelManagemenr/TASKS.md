# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Nhân sự

**Total Story Points:** ~110 points  
**Sprint Duration:** 2 weeks × 6 sprints = 12 weeks  
**Velocity:** ~18 points/sprint

---

## Epic 1: Backend Infrastructure & Database (22 points)

### Task 1.1: Database Schema Design (4 points)
**Assignee:** Database Admin  
**Duration:** 2 days  
**Description:**
- Design Employee table (id, code, fullName, email, phone, role, status, createdAt, updatedAt, createdBy, lastLoginAt, passwordResetRequired)
- Design EmployeeRole table (id, employeeId, role, assignedAt)
- Design EmployeeFacility table (id, employeeId, facilityId, assignedAt)
- Design AuditLog extension for employee operations
- Create ER diagram with relationships

**Acceptance Criteria:**
- ✓ Tables designed with proper constraints
- ✓ Unique constraints on email, phone
- ✓ Foreign keys defined
- ✓ Indexes on frequently queried columns

---

### Task 1.2: Database Migration Scripts (2 points)
**Assignee:** Database Admin  
**Duration:** 1 day  
**Description:**
- Write migration scripts
- Test rollback scripts
- Create seed data (if needed)

**Acceptance Criteria:**
- ✓ Migrations execute without error
- ✓ Rollback working

---

### Task 1.3: Entity Implementation - Employee (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement Employee JPA entity
- Implement field validations (email format, phone length, etc.)
- Implement role enum (MANAGER, TECHNICIAN, COST_MANAGER)
- Implement status enum (ACTIVE, INACTIVE)
- Setup relationships (facilities, audit logs)
- Create repository with custom queries

**Acceptance Criteria:**
- ✓ Entity mapped correctly
- ✓ Validations working
- ✓ Relationships configured
- ✓ Repository queries support pagination

---

### Task 1.4: Entity Implementation - Relationships (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement EmployeeFacility junction entity
- Implement role assignment relationships
- Setup cascading behaviors
- Create repository for facility assignments

**Acceptance Criteria:**
- ✓ Relationships correct
- ✓ Cascade delete/update working
- ✓ Queries optimized with joins

---

### Task 1.5: Audit Log Extension (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Extend AuditLog for employee operations
- Implement logging aspect for CRUD
- Implement logging for lock/unlock
- Implement logging for facility assignments
- Test logging accuracy

**Acceptance Criteria:**
- ✓ All operations logged
- ✓ Audit trail complete
- ✓ User information captured

---

### Task 1.6: Validation & Error Handling (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement validation annotations
- Create custom validators (email, phone)
- Map all error codes
- Create global exception handler

**Acceptance Criteria:**
- ✓ All validations working
- ✓ Error codes mapped
- ✓ Proper HTTP status codes

---

## Epic 2: Employee Management Service (32 points)

### Task 2.1: Employee Code Generation (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 day  
**Description:**
- Implement auto-generated employee code
- Format: EMP + sequential number (EMP001, EMP002, etc.)
- Ensure uniqueness
- Non-editable after creation

**Acceptance Criteria:**
- ✓ Code auto-generated
- ✓ Format correct & unique
- ✓ Cannot be edited

---

### Task 2.2: Create Employee Service (6 points)
**Assignee:** Backend Developer  
**Duration:** 3 days  
**Description:**
- Implement createEmployee() service
- Validate required fields (name, email, phone, role)
- Validate email format & uniqueness
- Validate phone format & uniqueness
- Prevent Admin role creation
- Check Cost Manager constraint if role = COST_MANAGER
- Generate temporary password
- Set status = ACTIVE by default
- Trigger email sending
- Create audit log

**Acceptance Criteria:**
- ✓ Employee created
- ✓ All validations working
- ✓ Error codes correct (INVALID_EMAIL_FORMAT, EMAIL_ALREADY_EXISTS, etc.)
- ✓ Temp password generated
- ✓ Email triggered
- ✓ Cost Manager constraint checked
- ✓ Audit logged

**Unit Tests:**
- Test valid creation
- Test validation errors
- Test Cost Manager constraint
- Test email trigger

---

### Task 2.3: Get Employee List Service (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Implement getEmployeeList() with pagination
- Support search by name, email, phone
- Support filter by role (MANAGER, TECHNICIAN, COST_MANAGER)
- Support filter by status (ACTIVE, INACTIVE)
- Return employee + facility list

**Acceptance Criteria:**
- ✓ Pagination working
- ✓ Search working
- ✓ Filter working
- ✓ Facilities populated
- ✓ Empty result handled

---

### Task 2.4: Get Employee Detail Service (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 day  
**Description:**
- Implement getEmployeeDetail(id) service
- Return employee info with facilities list
- Handle EMPLOYEE_NOT_FOUND error

**Acceptance Criteria:**
- ✓ Detail retrieved
- ✓ Facilities populated
- ✓ 404 handled

---

### Task 2.5: Update Employee Info Service (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement updateEmployee() service
- Allow update: fullName, email, phone, role, facilities
- Prevent employee from updating own role
- Validate email uniqueness (excluding self)
- Validate phone uniqueness (excluding self)
- Prevent changing to ADMIN role
- Update facility assignments
- Create audit log

**Acceptance Criteria:**
- ✓ Updates applied
- ✓ Validations working
- ✓ Self-role change prevented
- ✓ Facilities updated
- ✓ Audit logged

**Unit Tests:**
- Test valid updates
- Test validation errors
- Test role prevention
- Test facility updates

---

### Task 2.6: Cost Manager Constraint Service (6 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement checkCostManagerUniqueness() service
- On create: Check if active Cost Manager exists
- On update role to COST_MANAGER: Check constraint
- On lock COST_MANAGER: Check if only one, allow if deactivating
- On unlock COST_MANAGER: Check if another active exists, prevent if yes
- Error: COST_MANAGER_ALREADY_ACTIVE

**Acceptance Criteria:**
- ✓ Constraint enforced on create
- ✓ Constraint enforced on role change
- ✓ Constraint enforced on lock/unlock
- ✓ Error messages clear
- ✓ Edge cases tested (concurrent requests, multiple state changes)

**Unit Tests:**
- Test create with existing active Cost Manager
- Test update to Cost Manager role
- Test lock/unlock scenarios
- Test concurrent operations

---

### Task 2.7: Facility Assignment Service (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement assignFacilities() service
- Manager/Technician must have >= 1 facility
- Only assign active facilities
- Validate facility exists & is active
- Clear old assignments & set new
- Create audit log for changes

**Acceptance Criteria:**
- ✓ Facilities assigned correctly
- ✓ Validation working (active only)
- ✓ Manager/Technician constraint enforced
- ✓ Old assignments cleared
- ✓ Audit logged

---

### Task 2.8: Lock/Unlock Account Service (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement lockAccount(id) service
- Prevent locking own account (error: CANNOT_DEACTIVATE_SELF)
- Change status to INACTIVE
- Create audit log
- Implement unlockAccount(id) service
- Change status to ACTIVE
- Check Cost Manager constraint if applicable
- Create audit log

**Acceptance Criteria:**
- ✓ Lock/unlock working
- ✓ Self-lock prevented
- ✓ Cost Manager constraint enforced on unlock
- ✓ Audit logged

---

## Epic 3: Authentication & Password Management (18 points)

### Task 3.1: Temporary Password Generation (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Implement generateTemporaryPassword() utility
- Generate secure random password (12 chars, mix of upper/lower/numbers/special)
- Create hash for storage
- Return plain password for email sending

**Acceptance Criteria:**
- ✓ Password strong & random
- ✓ Hash stored correctly
- ✓ Plain password returned once

---

### Task 3.2: Email Service Integration (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement EmailService integration
- Create employee welcome email template
- Send async (don't block API)
- Implement retry logic (max 3 retries)
- Log send success/failure
- Handle send failure gracefully

**Acceptance Criteria:**
- ✓ Email service integrated
- ✓ Async sending working
- ✓ Retry logic implemented
- ✓ Logging complete
- ✓ Failure handling graceful

---

### Task 3.3: First Login Password Reset (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement passwordResetRequired flag in Employee
- On login with temp password: Set flag = true
- Require password change on first login
- After password change: Set flag = false
- Validate new password strength
- Update password hash

**Acceptance Criteria:**
- ✓ Flag set on first login
- ✓ Password change enforced
- ✓ Password validation working
- ✓ Flag cleared after change
- ✓ Old password invalidated

---

### Task 3.4: Password Reset Endpoint (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement self-service password reset
- Employee can reset own password
- Send confirmation email
- Generate reset token (expire in 24 hours)
- Validate token on password reset
- Update password hash

**Acceptance Criteria:**
- ✓ Reset request creates token
- ✓ Email sent with token
- ✓ Token validation working
- ✓ Password updated
- ✓ Token expires correctly

---

## Epic 4: Employee API & Controller (16 points)

### Task 4.1: Employee API Controller (5 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Create REST controller with endpoints:
  - POST /api/v1/employees (create)
  - GET /api/v1/employees (list)
  - GET /api/v1/employees/{id} (detail)
  - PUT /api/v1/employees/{id} (update)
  - POST /api/v1/employees/{id}/lock (lock)
  - POST /api/v1/employees/{id}/unlock (unlock)
- Implement DTOs for request/response
- Add authorization (Admin only)
- Handle exceptions

**Acceptance Criteria:**
- ✓ All endpoints working
- ✓ DTOs correct
- ✓ Authorization enforced
- ✓ Error handling complete

---

### Task 4.2: Facility Assignment API (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Add endpoint to facility assignment APIs
- PUT /api/v1/employees/{id}/facilities (assign)
- GET /api/v1/employees/{id}/facilities (list assigned)
- Implement DTOs

**Acceptance Criteria:**
- ✓ Endpoints working
- ✓ DTOs correct
- ✓ Validation working

---

### Task 4.3: Search & Filter API (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Implement query parameters for search
- Implement filter by role & status
- Implement sorting options
- Optimize database queries

**Acceptance Criteria:**
- ✓ Search working
- ✓ Filters working
- ✓ Sorting working
- ✓ Performance acceptable

---

### Task 4.4: Response Formatting (2 points)
**Assignee:** Backend Developer  
**Duration:** 1 day  
**Description:**
- Standardize all API responses
- Implement pagination wrapper
- Implement error response format
- Add metadata (timestamps, etc.)

**Acceptance Criteria:**
- ✓ Response format consistent
- ✓ Pagination metadata included
- ✓ Error format standard

---

### Task 4.5: API Documentation (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Generate Swagger/OpenAPI docs
- Document all endpoints
- Document error codes
- Add examples

**Acceptance Criteria:**
- ✓ Documentation complete
- ✓ All endpoints documented
- ✓ Examples clear

---

## Epic 5: Frontend Development (24 points)

### Task 5.1: Employee List Page (4 points)
**Assignee:** Frontend Developer  
**Duration:** 2-3 days  
**Description:**
- Create table with columns: Code, Name, Email, Role, Status, Facilities
- Implement pagination
- Implement search by name/email/phone
- Implement filter by role & status
- Implement action buttons (view, edit, lock/unlock)

**Acceptance Criteria:**
- ✓ Table displays correctly
- ✓ Pagination working
- ✓ Search/filter working
- ✓ Action buttons working
- ✓ Responsive design

---

### Task 5.2: Create Employee Form (5 points)
**Assignee:** Frontend Developer  
**Duration:** 2-3 days  
**Description:**
- Create form with fields: fullName, email, phone, role, facilities
- Implement role selector (MANAGER, TECHNICIAN, COST_MANAGER)
- Implement multi-select for facilities
- Show validation errors
- Implement client-side validation
- Call create API

**Acceptance Criteria:**
- ✓ Form displays correctly
- ✓ Validation working
- ✓ Role selection working
- ✓ Facility multi-select working
- ✓ API call correct
- ✓ Success/error handling

---

### Task 5.3: Employee Edit Form (5 points)
**Assignee:** Frontend Developer  
**Duration:** 2-3 days  
**Description:**
- Create form similar to create
- Show employee code as read-only
- Pre-populate form with current data
- Implement facility assignment UI
- Call update API

**Acceptance Criteria:**
- ✓ Form pre-populated correctly
- ✓ Read-only fields locked
- ✓ Update API call correct
- ✓ Facility changes reflected

---

### Task 5.4: Employee Detail Page (4 points)
**Assignee:** Frontend Developer  
**Duration:** 2 days  
**Description:**
- Display employee info in read-only view
- Show code, name, email, phone, role, status
- Show assigned facilities
- Show timestamps
- Implement action buttons (edit, lock/unlock)

**Acceptance Criteria:**
- ✓ Detail displays correctly
- ✓ All info shown
- ✓ Action buttons present
- ✓ Responsive design

---

### Task 5.5: Lock/Unlock Dialogs (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1-2 days  
**Description:**
- Create confirmation dialogs for lock/unlock
- Show warning messages
- Call API on confirmation
- Handle success/error responses

**Acceptance Criteria:**
- ✓ Dialogs appear correctly
- ✓ API calls correct
- ✓ Results handled

---

### Task 5.6: Facility Assignment Selector (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 day  
**Description:**
- Create multi-select component for facilities
- Load active facilities on form open
- Show already selected facilities
- Validate selection (Manager/Technician need >= 1)

**Acceptance Criteria:**
- ✓ Multi-select working
- ✓ Facilities loaded correctly
- ✓ Validation working
- ✓ Selection persisted

---

### Task 5.7: UI/UX Polish (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 day  
**Description:**
- Review UI consistency
- Test responsive design
- Optimize performance
- Add accessibility features

**Acceptance Criteria:**
- ✓ Consistent styling
- ✓ Responsive
- ✓ Accessible
- ✓ Fast

---

## Epic 6: Testing & Quality (15 points)

### Task 6.1: Unit Tests - Backend (4 points)
**Assignee:** Backend Developer / QA  
**Duration:** 2 days  
**Description:**
- Test service methods
- Test validation logic
- Test Cost Manager constraint
- Aim for >= 80% coverage

**Acceptance Criteria:**
- ✓ Coverage >= 80%
- ✓ All tests passing
- ✓ Edge cases covered

---

### Task 6.2: Unit Tests - Authentication (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Test password generation
- Test temporary password logic
- Test first login reset
- Test password reset token

**Acceptance Criteria:**
- ✓ All password tests passing
- ✓ Security tests covered

---

### Task 6.3: Integration Tests (4 points)
**Assignee:** QA Engineer  
**Duration:** 2 days  
**Description:**
- Test API endpoints
- Test complete workflows
- Test error scenarios
- Test Cost Manager constraint across endpoints

**Acceptance Criteria:**
- ✓ All APIs tested
- ✓ Workflows working
- ✓ Error handling verified

---

### Task 6.4: E2E & UAT (2 points)
**Assignee:** QA Engineer  
**Duration:** 1-2 days  
**Description:**
- Write E2E tests
- Manual UAT
- Performance testing

**Acceptance Criteria:**
- ✓ E2E tests passing
- ✓ UAT completed
- ✓ Performance acceptable

---

### Task 6.5: Security Testing (2 points)
**Assignee:** QA Engineer  
**Duration:** 1 day  
**Description:**
- Test authorization
- Test input validation
- Test password security
- Test email verification

**Acceptance Criteria:**
- ✓ Security tests passing
- ✓ No vulnerabilities

---

## Epic 7: Documentation & Deployment (3 points)

### Task 7.1: User Documentation (1 point)
**Assignee:** Tech Lead  
**Duration:** 1 day  
**Description:**
- Write admin guide for employee management
- Add screenshots
- Document workflows

**Acceptance Criteria:**
- ✓ Guide complete
- ✓ Clear & helpful

---

### Task 7.2: Deployment (2 points)
**Assignee:** DevOps / Tech Lead  
**Duration:** 2-3 days  
**Description:**
- Prepare environment
- Run migrations
- Deploy code
- Setup monitoring
- Go-live verification

**Acceptance Criteria:**
- ✓ Deployment successful
- ✓ No issues
- ✓ Monitoring active

---

## Summary by Sprint

| Sprint | Duration | Points | Focus |
|--------|----------|--------|-------|
| Sprint 1 | Wk 1-2 | 22 | DB design, entities, migrations |
| Sprint 2 | Wk 3-4 | 20 | Create, list, detail services |
| Sprint 3 | Wk 5-6 | 20 | Update, lock/unlock, constraints |
| Sprint 4 | Wk 7 | 18 | Password, authentication, email |
| Sprint 5 | Wk 8 | 16 | Frontend list, create, edit forms |
| Sprint 6 | Wk 9-12 | 14 | Testing, UAT, deployment |

---

## Critical Dependencies

- Task 1.1 → 1.2 → 1.3, 1.4, 1.5, 1.6
- Task 1.3 → 2.1, 2.2, 2.3, 2.4
- Task 2.2 → 3.1, 3.2 (temp password)
- Task 2.6 → 2.2, 2.5 (Cost Manager checks)
- Task 2.7 → 2.2, 2.5 (Facility assignment)
- Task 4.1 → Task 5.1, 5.2, 5.3 (Frontend)

---

## Resource Allocation

| Role | Hours | Duration |
|------|-------|----------|
| Backend Developer | 200 | 5-6 weeks |
| Frontend Developer | 120 | 3-4 weeks |
| QA Engineer | 80 | 2 weeks |
| Database Admin | 30 | 1 week |
| Tech Lead | 60 | 1.5 weeks |
