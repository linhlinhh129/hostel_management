# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Người thuê (Manager)

**Total Story Points:** ~78 points (Completed)  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~15.6 points/sprint

---

## Epic 1: Database & Entities (10 points) - Completed

### Task 1.1: Database Schema Design (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Design user structure for Tenant role (`user_id`, `username`, `password`, `email`, `role`, `status`, `full_name`, `phone`, `identity_number`, `dob`, `gender`, `permanent_address`, `created_at`, `updated_at`, `deleted_at`)
- [x] Connect `dbo.rooms` table to `dbo.users` via `tenant_id`
- [x] Create unique index constraints on `identity_number` and `email`

---

### Task 1.2: Entity Implementation (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Create Tenant/User entity mapping classes
- [x] Establish one-to-one Room relationship
- [x] Implement DAO for database operations

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- [x] Write SQL scripts for schema alterations
- [x] Apply migrations and test rollback

---

### Task 1.4: Audit Logging (2 points)
**Duration:** 1 day  
**Description:**
- [x] Implement Audit Logging for tenant changes (Update info, Lock, Unlock, End Rental, Delete)

---

## Epic 2: Tenant Code Generation (6 points) - Completed

### Task 2.1: Tenant Code Generation Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement auto-code generation sequence (e.g. `TENxxxx`)
- [x] Handle concurrency during registration

---

### Task 2.2: Code Validation & Uniqueness (2 points)
**Duration:** 1 day  
**Description:**
- [x] Verify code format uniqueness during database insert

---

## Epic 3: Account Integration & Security (16 points) - Completed

### Task 3.1: Account Creation Redirect (5 points)
**Duration:** 2-3 days  
**Description:**
- [x] Block direct creation in `ManagerTenantsServlet` and redirect to contracts page
- [x] Auto-register user account when signature is created in contracts page

---

### Task 3.2: Password Change on First Login (4 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `FirstLoginServlet` filters
- [x] Force tenant to change password upon first system entry

---

### Task 3.3: Account Lock/Unlock Mechanics (4 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement lock servlet action (`/manager/tenants/{id}/lock`)
- [x] Implement unlock servlet action (`/manager/tenants/{id}/unlock`)
- [x] Clear fail counters in `LoginAttemptTracker` on unlock

---

### Task 3.4: Account Disable on End Tenancy (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Disable login for user when tenancy ends (sets status to `INACTIVE`)

---

## Epic 4: Tenant Data Validation (18 points) - Completed

### Task 4.1: Edit Tenant Form Validation (6 points)
**Duration:** 2-3 days  
**Description:**
- [x] Require name, phone, email, and identityNumber on profile updates
- [x] Validate Vietnamese mobile phone number format
- [x] Validate identity number format (CCCD/CMND)

---

### Task 4.2: Email & Identity Uniqueness (6 points)
**Duration:** 2 days  
**Description:**
- [x] Enforce email unique checks in database
- [x] Enforce identity number unique checks in database

---

### Task 4.3: Room Assignment Rules (6 points)
**Duration:** 2-3 days  
**Description:**
- [x] Ensure only one ACTIVE tenant is assigned per room
- [x] Ensure room is active before registration

---

## Epic 5: Tenant Lifecycle Management (18 points) - Completed

### Task 5.1: List Tenants Service (4 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement paginated `getTenants` query
- [x] Filter by keyword and tenant status

---

### Task 5.2: Get Tenant Detail Service (4 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `getTenantDetail`
- [x] Retrieve tenant info, room info, active contract, and dependents

---

### Task 5.3: End Tenancy Service (5 points)
**Duration:** 2-3 days  
**Description:**
- [x] Implement `endRental()` service
- [x] Update tenant status to `INACTIVE`
- [x] Free room by setting `tenant_id = NULL`
- [x] Prevent updates to inactive/readonly dependents

---

### Task 5.4: Soft Delete (5 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `softDeleteTenant` service
- [x] Set `deleted_at = GETDATE()` for deleted records

---

## Epic 6: Frontend - Tenant Management JSP (14 points) - Completed

### Task 6.1: Tenant List page (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create list JSP view `list.jsp` with keyword search and status filters

---

### Task 6.2: Tenant Detail & Forms (6 points)
**Duration:** 2-3 days  
**Description:**
- [x] Create detail JSP view `detail.jsp`
- [x] Implement edit profile modal
- [x] Implement end rental form (date and reason inputs)
- [x] Implement account action buttons (Lock, Unlock, Delete)

---

### Task 6.3: Alerts & Redirect UI (4 points)
**Duration:** 2 days  
**Description:**
- [x] Show error and success banners via flash messages

---

## Epic 7: Testing (6 points) - Completed

- [x] Unit test tenant lifecycle operations
- [x] Integration test database transaction safety
- [x] UAT complete test cases (Redirect -> List -> Detail -> Edit -> Lock -> Unlock -> End Rental -> Delete)

---

## Summary by Sprint

| Sprint | Focus | Status |
|--------|-------|--------|
| Sprint 1 | Database schema and account security | Completed |
| Sprint 2 | Edit, lock, and unlock services | Completed |
| Sprint 3 | End rental and soft delete lifecycles | Completed |
| Sprint 4 | Frontend list and detail JSP pages | Completed |
| Sprint 5 | Quality assurance and verification | Completed |
