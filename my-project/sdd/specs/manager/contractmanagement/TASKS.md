# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Hợp đồng (Manager)

**Total Story Points:** ~65 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~21.6 points/sprint

---

## Epic 1: Database & Data Access Layer (12 points)

### Task 1.1: Database Schema & Entity Mapping (4 points)
- [x] Verify `dbo.contracts` table schema (`contract_id`, `code`, `room_id`, `tenant_id`, `tenant_full_name`, `tenant_dob`, `tenant_address`, `tenant_identity`, `tenant_identity_date`, `tenant_identity_place`, `tenant_phone`, `rent_price`, `deposit_amount`, `amount_in_words`, `signed_date`, `start_date`, `end_date`, `status`, `created_by`, `created_at`, `updated_at`, `deleted_at`)
- [x] Create `Contract.java` model class with all attributes and encapsulation
- [x] Establish relations between `Contract`, `Room`, `Facility`, and `User`

---

### Task 1.2: ContractDAO Implementation (5 points)
- [x] Implement `getContractsByManager(long managerId, String searchKeyword)` in `ContractDAO.java`
- [x] Implement `getContractById(long contractId, long managerId)` with facility scope protection
- [x] Implement `insertContract(Contract contract)` returning generated `contract_id`
- [x] Implement `updateContractStatus(long contractId, String status)`
- [x] Implement `softDeleteContract(long contractId)` setting `deleted_at = GETDATE()`
- [x] Implement `getAvailableRoomsByManager(long managerId)`
- [x] Implement `getUserIdByIdentityNumber` & `getUserRoleAndIdentityByIdentityNumber` in `ContractDAO.java` for CCCD identification

---

### Task 1.3: Audit Logging Integration (3 points)
- [x] Integrate `AuditLogDAO` for tracking contract creation (`CREATE`), tenant linking (`ADD_TENANT`), and soft deletion (`DELETE`)

---

## Epic 2: Service Layer & Business Validation (16 points)

### Task 2.1: Contract Code Generation (4 points)
- [x] Implement auto-generation format: `HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`
- [x] Handle sequence incrementing logic within the same day to guarantee code uniqueness

---

### Task 2.2: Contract Creation Validation Service (6 points)
- [x] Validate mandatory inputs: `tenantFullName`, `tenantIdentity` (9 or 12 digits), `signedDate`, `endDate`
- [x] Validate date logic: `endDate >= signedDate`
- [x] Check room existence and manager facility access rights
- [x] Check active contract conflicts: Prevent creation if target room already has an `ACTIVE` contract
- [x] Equalize deposit amount with room fee (`depositAmount = roomFee`)

---

### Task 2.3: Tenant Account Generation & Link Service (6 points)
- [x] Implement `addTenantFromContract()` in `ContractServiceImpl.java`
- [x] Check if returning tenant account exists by CCCD (`identityNumber`)
- [x] If new user: Create `User` with role `TENANT`, generate temporary password, send email notification
- [x] If existing inactive user: Support reactivation confirm flow
- [x] Update `tenant_id` in `contracts` table and update `rooms.status` to `OCCUPIED` inside a single DB transaction

---

## Epic 3: Controller Routing & Servlets (14 points)

### Task 3.1: Servlet Setup & Authorization Filter (4 points)
- [x] Create `ContractServlet.java` mapping `/manager/contracts/*`
- [x] Restrict access exclusively to users with `MANAGER` or `ADMIN` roles
- [x] Extract session manager context and check facility permissions

---

### Task 3.2: List & Detail Servlet Handlers (4 points)
- [x] Handle `GET /manager/contracts`: Fetch contracts list with search filter and forward to `list.jsp`
- [x] Handle `GET /manager/contracts/detail`: Validate contract ownership and forward to `detail.jsp`

---

### Task 3.3: Create & Add-Tenant Servlet Handlers (4 points)
- [x] Handle `GET /manager/contracts/create`: Load available rooms list and forward to `create.jsp`
- [x] Handle `POST /manager/contracts/create`: Process form submission, catch validation errors, and redirect/forward
- [x] Handle `GET/POST /manager/contracts/add-tenant`: Process tenant account registration from contract

---

### Task 3.4: Delete Servlet Handler (2 points)
- [x] Handle `POST /manager/contracts/delete`: Reject delete if status is `ACTIVE`; execute soft delete if `INACTIVE`

---

## Epic 4: Frontend JSP Views (17 points)

### Task 4.1: Contract List View (list.jsp) (4 points)
- [x] Build `list.jsp` using standard design system (`hostel-design.css`)
- [x] Display contract code, room code, tenant name, CCCD, signed/start/end dates, status badge (`ACTIVE`/`INACTIVE`)
- [x] Add search form and action buttons (Detail, Print, Delete)

---

### Task 4.2: Contract Detail View (detail.jsp) (5 points)
- [x] Build `detail.jsp` displaying Party A (Manager/Facility info) and Party B (Tenant info)
- [x] Display financial summary (Rent price, deposit, amount in words)
- [x] Add action buttons: "Print Contract", "Create Tenant Account", "Delete Contract"

---

### Task 4.3: Create Contract Form (create.jsp) (4 points)
- [x] Build `create.jsp` form with client-side validations
- [x] Add JS event listener on room select dropdown to automatically auto-fill room code, floor, rent price, and deposit amount

---

### Task 4.4: Legal Print Layout (print.jsp) (4 points)
- [x] Build `print.jsp` with `@media print` CSS stylesheet optimized for A4 paper
- [x] Render national emblem header, contract title, Party A info, Party B info
- [x] Render Articles 1 to 4 (Room info, Fees & Deposit, Rights & Obligations, General Provisions)
- [x] Render signature blocks for Party A and Party B

---

## Epic 5: Verification & Quality Assurance (6 points)

### Task 5.1: Unit & Integration Testing (3 points)
- [x] Test contract code generator format and uniqueness
- [x] Test date validation logic (`endDate < signedDate`)
- [x] Test CCCD tenant reactivation flow
- [x] Test end rental immediate termination as of current date

---

## Phase 6 & 7: Additional Tasks & Cleanup
- [x] T015 [US7] Xây dựng form `add_tenant.jsp` pre-fill dữ liệu lấy từ hợp đồng cũ
- [x] T016 [US7] Xử lý `GET /manager/contracts/add-tenant` để load form
- [x] T017 [US7] Xử lý `POST /manager/contracts/add-tenant`: Validate trùng lặp Email/CCCD, tạo Account Tenant mới, gán `tenant_id` vào hợp đồng, chuyển status phòng thành OCCUPIED
- [x] T018 [US8] Hiển thị nút "Xóa" trên giao diện chi tiết hoặc danh sách, chỉ enable khi `status` = `INACTIVE`
- [x] T019 [US8] Triển khai `POST /manager/contracts/delete`, bắt Exception và reject nếu cố tình xóa hợp đồng đang `ACTIVE`
