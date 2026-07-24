# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Hợp đồng (Manager)

**Total Story Points:** ~65 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~21.6 points/sprint

---

## Epic 1: Database & Data Access Layer (12 points)

### Task 1.1: Database Schema & Entity Mapping (4 points)
**Duration:** 2 days  
**Description:**
- [ ] Verify `dbo.contracts` table schema (`contract_id`, `code`, `room_id`, `tenant_id`, `tenant_full_name`, `tenant_dob`, `tenant_address`, `tenant_identity`, `tenant_identity_date`, `tenant_identity_place`, `tenant_phone`, `rent_price`, `deposit_amount`, `amount_in_words`, `signed_date`, `start_date`, `end_date`, `status`, `created_by`, `created_at`, `updated_at`, `deleted_at`)
- [ ] Create `Contract.java` model class with all attributes and encapsulation
- [ ] Establish relations between `Contract`, `Room`, `Facility`, and `User`

---

### Task 1.2: ContractDAO Implementation (5 points)
**Duration:** 2-3 days  
**Description:**
- [ ] Implement `getContractsByManager(long managerId, String searchKeyword)` in `ContractDAOImpl.java`
- [ ] Implement `getContractById(long contractId, long managerId)` with facility scope protection
- [ ] Implement `insertContract(Contract contract)` returning generated `contract_id`
- [ ] Implement `updateContractStatus(long contractId, String status)`
- [ ] Implement `softDeleteContract(long contractId)` setting `deleted_at = GETDATE()`
- [ ] Implement `getAvailableRoomsByManager(long managerId)`

---

### Task 1.3: Audit Logging Integration (3 points)
**Duration:** 1 day  
**Description:**
- [ ] Integrate `AuditLogDAO` for tracking contract creation (`CREATE`), tenant linking (`ADD_TENANT`), and soft deletion (`DELETE`)

---

## Epic 2: Service Layer & Business Validation (16 points)

### Task 2.1: Contract Code Generation (4 points)
**Duration:** 1-2 days  
**Description:**
- [ ] Implement auto-generation format: `HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`
- [ ] Handle sequence incrementing logic within the same day to guarantee code uniqueness

---

### Task 2.2: Contract Creation Validation Service (6 points)
**Duration:** 2-3 days  
**Description:**
- [ ] Validate mandatory inputs: `tenantFullName`, `tenantIdentity` (9 or 12 digits), `signedDate`, `endDate`
- [ ] Validate date logic: `endDate >= signedDate` (throw `INVALID_CONTRACT_DATE` error code on failure)
- [ ] Check room existence and manager facility access rights
- [ ] Check active contract conflicts: Prevent creation if target room already has an `ACTIVE` contract (`ROOM_ALREADY_HAS_ACTIVE_CONTRACT`)

---

### Task 2.3: Tenant Account Generation & Link Service (6 points)
**Duration:** 2-3 days  
**Description:**
- [ ] Implement `addTenantFromContract()` in `ContractServiceImpl.java`
- [ ] Check if user exists by Email or Identity Number
- [ ] If new user: Create `User` with role `TENANT`, generate temporary password, send email notification
- [ ] If existing inactive user: Support reactivation confirm flow
- [ ] Update `tenant_id` in `contracts` table and update `rooms.status` to `OCCUPIED` inside a single DB transaction

---

## Epic 3: Controller Routing & Servlets (14 points)

### Task 3.1: Servlet Setup & Authorization Filter (4 points)
**Duration:** 1-2 days  
**Description:**
- [ ] Create `ContractServlet.java` mapping `/manager/contracts/*`
- [ ] Restrict access exclusively to users with `MANAGER` or `ADMIN` roles
- [ ] Extract session manager context and check facility permissions

---

### Task 3.2: List & Detail Servlet Handlers (4 points)
**Duration:** 1-2 days  
**Description:**
- [ ] Handle `GET /manager/contracts`: Fetch contracts list with search filter and forward to `list.jsp`
- [ ] Handle `GET /manager/contracts/detail`: Validate contract ownership and forward to `detail.jsp`

---

### Task 3.3: Create & Add-Tenant Servlet Handlers (4 points)
**Duration:** 2 days  
**Description:**
- [ ] Handle `GET /manager/contracts/create`: Load available rooms list and forward to `create.jsp`
- [ ] Handle `POST /manager/contracts/create`: Process form submission, catch validation errors, and redirect/forward
- [ ] Handle `GET/POST /manager/contracts/add-tenant`: Process tenant account registration from contract

---

### Task 3.4: Delete Servlet Handler (2 points)
**Duration:** 1 day  
**Description:**
- [ ] Handle `POST /manager/contracts/delete`: Reject delete if status is `ACTIVE`; execute soft delete if `INACTIVE`

---

## Epic 4: Frontend JSP Views (17 points)

### Task 4.1: Contract List View (list.jsp) (4 points)
**Duration:** 2 days  
**Description:**
- [ ] Build `list.jsp` using standard design system (`hostel-design.css`)
- [ ] Display contract code, room code, tenant name, CCCD, signed/start/end dates, status badge (`ACTIVE`/`INACTIVE`)
- [ ] Add search form and action buttons (Detail, Print, Delete)

---

### Task 4.2: Contract Detail View (detail.jsp) (5 points)
**Duration:** 2 days  
**Description:**
- [ ] Build `detail.jsp` displaying Party A (Manager/Facility info) and Party B (Tenant info)
- [ ] Display financial summary (Rent price, deposit, amount in words)
- [ ] Add action buttons: "Print Contract", "Create Tenant Account", "Delete Contract"

---

### Task 4.3: Create Contract Form (create.jsp) (4 points)
**Duration:** 2 days  
**Description:**
- [ ] Build `create.jsp` form with client-side validations
- [ ] Add JS event listener on room select dropdown to automatically auto-fill room code, floor, rent price, and deposit amount

---

### Task 4.4: Legal Print Layout (print.jsp) (4 points)
**Duration:** 2 days  
**Description:**
- [ ] Build `print.jsp` with `@media print` CSS stylesheet optimized for A4 paper
- [ ] Render national emblem header, contract title, Party A info, Party B info
- [ ] Render Articles 1 to 4 (Room info, Fees & Deposit, Rights & Obligations, General Provisions)
- [ ] Render signature blocks for Party A and Party B

---

## Epic 5: Verification & Quality Assurance (6 points)

### Task 5.1: Unit & Integration Testing (3 points)
**Duration:** 1-2 days  
**Description:**
- [ ] Test contract code generator format and uniqueness
- [ ] Test date validation logic (`endDate < signedDate`)
- [ ] Test transactional integrity of tenant linking and room status update

---

### Task 5.2: End-to-End UAT & Security Testing (3 points)
**Duration:** 1-2 days  
**Description:**
- [ ] Verify RBAC cross-facility isolation (Manager A cannot view Manager B's contracts)
- [ ] Verify print template layout in Chrome/Edge print preview
- [ ] Verify AuditLog entries generated for contract actions

---

## Summary by Sprint

| Sprint | Focus | Status |
|--------|-------|--------|
| Sprint 1 | Database schema, DAO queries, and Service validations | Planned |
| Sprint 2 | Servlet routing, List/Detail/Create JSP views | Planned |
| Sprint 3 | Add-Tenant integration, Print layout, Soft Delete & Testing | Planned |

---

## Phase 6: Convergence

- [x] Task 6.1: Implement legal print template JSP view `print.jsp` in `/WEB-INF/views/manager/contracts/` per SPEC Section 3.6 & 3.7 (missing) (Completed)


