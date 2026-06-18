# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Người thuê (Manager)

**Total Story Points:** ~78 points  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~15.6 points/sprint

---

## Epic 1: Database & Entities (10 points)

### Task 1.1: Database Schema Design (3 points)
**Duration:** 1-2 days  
**Description:**
- Design Tenant table (id, code, firstName, lastName, email, phone, cccd, cmnd, dob, gender, nationality, address, status, roomId, accountId, createdAt, updatedAt)
- Design unique constraints (email, cccd, cmnd)
- Create indexes on code, email, phone
- Relationship to Room & Account

---

### Task 1.2: Entity Implementation (3 points)
**Duration:** 1-2 days  
**Description:**
- Create Tenant JPA entity
- Setup Room relationship (many-to-one)
- Setup Account relationship (one-to-one)
- Create repository

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- Write migration script
- Create unique constraints
- Test migration & rollback

---

### Task 1.4: Audit Logging (2 points)
**Duration:** 1 day  
**Description:**
- Setup audit log
- Log creation, update, end tenancy

---

## Epic 2: Tenant Code Generation (6 points)

### Task 2.1: Tenant Code Generation Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement generateTenantCode() service
- Sequence-based generation (TEN00001, TEN00002, ...)
- Thread-safe (database sequence or atomic increment)
- Support code formatting
- Handle edge cases (rollback)

---

### Task 2.2: Code Validation & Uniqueness (2 points)
**Duration:** 1 day  
**Description:**
- Validate code format
- Check uniqueness
- Handle collision (rare but possible)

---

## Epic 3: Account Integration (16 points)

### Task 3.1: Account Creation Service (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement createAccount() service (calls external account service)
- Generate temp password
- Set username = email
- Set initial role = TENANT
- Store account ID in tenant record
- Handle account service errors

---

### Task 3.2: Temp Password Generation & Email (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement temp password generation (random, secure)
- Send email with credentials
- Include password change link
- Async email sending with retry

---

### Task 3.3: Account Disable Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement disableAccount(accountId) service (calls external account service)
- Called when tenancy ends
- Prevent login
- Keep historical data

---

### Task 3.4: Password Change on First Login (3 points)
**Duration:** 1-2 days  
**Description:**
- Account service requirement: Force password change
- Check if account needs password change
- Redirect to change password form
- Validate new password strength

---

### Task 3.5: Account Service Integration (2 points)
**Duration:** 1 day  
**Description:**
- Define account service interface
- Implement mock account service for testing
- Handle account service errors (retry, rollback)

---

## Epic 4: Tenant Creation & Validation (18 points)

### Task 4.1: Create Tenant Service (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement createTenant() service
- Validate all required fields: firstName, lastName, email, phone, cccd/cmnd, dob
- Check email unique
- Check cccd/cmnd unique
- Generate tenant code
- Create account via account service
- Create audit log

**Error Codes:** VALIDATION_ERROR, EMAIL_ALREADY_EXISTS, CCCD_ALREADY_EXISTS, ACCOUNT_CREATION_FAILED

---

### Task 4.2: Email & CCCD Validation (4 points)
**Duration:** 2 days  
**Description:**
- Implement email format validation (RFC 5322)
- Implement email uniqueness check
- Implement CCCD format validation (Vietnam format)
- Implement CMND format validation
- Check uniqueness for active tenants only (soft deleted excluded)

---

### Task 4.3: Room Assignment Validation (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement validateRoomAssignment(roomId) service
- Check room exists
- Check room is AVAILABLE
- Check no other ACTIVE tenant in room
- Check room belongs to facility
- Return validation result

---

### Task 4.4: Tenant Data Validation (4 points)
**Duration:** 2 days  
**Description:**
- Validate firstName/lastName not empty
- Validate phone format
- Validate dob is valid date
- Validate gender enum
- Validate nationality if provided
- Return detailed error messages

---

## Epic 5: Tenant Lifecycle Management (18 points)

### Task 5.1: List Tenants Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getTenants(filters) service
- Support pagination
- Support search by name, email, phone, code
- Support filter by status, facility, room
- Return summary info

---

### Task 5.2: Get Tenant Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getTenantDetail(tenantId) service
- Return full tenant info
- Include room details
- Include account status
- Handle TENANT_NOT_FOUND

---

### Task 5.3: Assign Room Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement assignRoom(tenantId, roomId) service
- Validate room assignment
- Update tenant's roomId
- Update room status to OCCUPIED
- Create audit log

**Error Codes:** TENANT_NOT_FOUND, ROOM_NOT_AVAILABLE, INVALID_ROOM, ASSIGNMENT_FAILED

---

### Task 5.4: End Tenancy Service (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement endTenancy(tenantId) service
- Validate tenant is ACTIVE
- Change status to INACTIVE
- Disable account via account service
- Update room status to AVAILABLE
- Create audit log
- Handle cascade: disable dependents

---

### Task 5.5: Soft Delete & Historical Data (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement soft delete on tenant end
- Keep historical data (created, updated, endedAt)
- Exclude soft-deleted from active queries
- Show deleted indicator in admin views

---

## Epic 6: Tenant API Controller (12 points)

### Task 6.1: Tenant Create API (3 points)
**Duration:** 1-2 days  
**Description:**
- POST /api/v1/tenants
- Implement request DTO
- Call create service
- Return created tenant with code & account

---

### Task 6.2: Tenant List & Search API (3 points)
**Duration:** 1-2 days  
**Description:**
- GET /api/v1/tenants (list)
- Support pagination, search, filter
- Return summary info
- Support sorting (by name, date, etc)

---

### Task 6.3: Tenant Detail API (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenants/{id} (detail)
- Return full info
- Handle TENANT_NOT_FOUND

---

### Task 6.4: Room Assignment API (2 points)
**Duration:** 1 day  
**Description:**
- PUT /api/v1/tenants/{id}/assign-room
- Implement DTO
- Call assign service

---

### Task 6.5: End Tenancy API (2 points)
**Duration:** 1 day  
**Description:**
- PUT /api/v1/tenants/{id}/end-tenancy
- Call end service
- Confirm dialog with warning

---

## Epic 7: Frontend - Tenant Management (14 points)

### Task 7.1: Tenant Creation Form (5 points)
**Duration:** 2-3 days  
**Description:**
- Create form with fields: firstName, lastName, email, phone, cccd, cmnd, dob, gender, nationality, address
- Implement validation
- Email unique check (async)
- CCCD format validation
- Call create API
- Show success with tenant code & temp password

---

### Task 7.2: Tenant List & Search (4 points)
**Duration:** 2 days  
**Description:**
- Create table: code, name, email, phone, status, room, createdDate
- Implement pagination
- Implement search by name/email/phone/code
- Implement filter by status
- Action buttons: view, assign room, end tenancy

---

### Task 7.3: Tenant Detail View (3 points)
**Duration:** 1-2 days  
**Description:**
- Display tenant info (editable if ACTIVE)
- Show room assignment
- Show account status
- Show action buttons (assign room, end tenancy)

---

### Task 7.4: Assign Room Dialog (2 points)
**Duration:** 1 day  
**Description:**
- Create modal with room selector
- Load available rooms
- Confirm & assign
- Show success message

---

## Epic 8: Testing (6 points)

### Task 8.1: Unit Tests - Services (2 points)
**Duration:** 1 day  
**Description:**
- Test code generation
- Test validation
- Test lifecycle operations
- >= 80% coverage

---

### Task 8.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test create tenant (with account creation)
- Test assign room
- Test end tenancy
- Test error scenarios

---

### Task 8.3: E2E Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test complete workflow
- Test frontend validation
- Test email sending
- Performance testing (< 500ms)

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 16 | DB schema, code generation, account integration |
| Sprint 2 | 16 | Tenant creation, validation |
| Sprint 3 | 16 | Lifecycle management, APIs |
| Sprint 4 | 16 | Frontend forms, list, detail |
| Sprint 5 | 14 | Testing, E2E, deployment |

---

## Critical Dependencies

- Task 1.1 → 1.2 → 2.1 → 4.1 (DB then entities then generation then create)
- Task 3.1 → 4.1 (account integration needed for create)
- Task 4.1-4.4 → Task 5.1-5.5 (validation needed for lifecycle)
- Task 5.1-5.5 → Task 6.1-6.5 (services needed for API)
- Task 6.1-6.5 → Task 7.1-7.4 (API needed for frontend)
- Task 7.1-7.4 → Task 8.3 (frontend needed for E2E)

---

## Test Scenarios

1. ✓ Create tenant with valid data (generates code, creates account)
2. ✗ Create tenant with duplicate email (validation error)
3. ✗ Create tenant with invalid email format (validation error)
4. ✗ Create tenant with duplicate CCCD (validation error)
5. ✓ Assign tenant to available room
6. ✗ Cannot assign tenant to occupied room
7. ✓ List tenants with pagination
8. ✓ Search tenants by name/email/code
9. ✓ End tenancy (status ACTIVE → INACTIVE, account disabled)
10. ✓ Cannot assign room to inactive tenant
11. ✓ Temp password email sent on creation
12. ✓ Account creation required before tenant creation
