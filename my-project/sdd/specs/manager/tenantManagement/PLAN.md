# PLAN: Kế hoạch Thực thi Quản lý Người thuê (Manager)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 8-10 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Manager tạo tài khoản người thuê, gán vào phòng, quản lý vòng đời, và kết thúc hợp đồng.

**Kiến trúc:**
- Backend API: Create, list, detail, assign room, end tenancy
- Tenant code generation: Auto-generate TEN00001+
- Account service integration: Create account, set temp password
- Room management: Assign one tenant per room, validation
- Lifecycle: ACTIVE → INACTIVE on tenancy end
- Audit Log: Log all operations

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** Design database, account integration, code generation

**Công việc:**
- Design Tenant entity & table
- Design Account integration
- Design tenant code generation
- Design room assignment validation
- Define API contract
- Plan account lifecycle

---

### Giai đoạn 2: Backend Development - Core (Tuần 3-4)

**Mục tiêu:** Implement core tenant management

**Công việc:**
- Implement Tenant entity & repository
- Implement tenant code generation service
- Implement create tenant service with account creation
- Implement tenant validation rules
- Implement room assignment validation

---

### Giai đoạn 3: Backend Development - Lifecycle (Tuần 5-6)

**Mục tiêu:** Implement lifecycle management

**Công việc:**
- Implement list/search tenants service
- Implement get tenant detail service
- Implement assign room service
- Implement end tenancy service
- Implement soft delete logic

---

### Giai đoạn 4: Frontend Development (Tuần 7-8)

**Mục tiêu:** Implement UI

**Công việc:**
- Tenant creation form
- Tenant list & search
- Tenant detail view
- Assign room dialog
- End tenancy confirmation

---

### Giai đoạn 5: Testing & Deployment (Tuần 9-10)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Tenant Code Generation
- Format: TEN00001, TEN00002, ...
- Unique per tenant
- Generated on creation
- Sequence management

### Account Creation
- Auto-create account on tenant creation
- Temp password generation
- Email account credentials
- Force password change on first login
- Disable account on tenancy end

### Room Assignment
- One active tenant per room
- Validate room is AVAILABLE
- Link tenant to room
- Update room status to OCCUPIED
- Prevent double assignment

### Unique Fields
- Email: System-wide unique (unless soft deleted)
- CCCD: System-wide unique (unless soft deleted)
- Phone: Unique for ACTIVE tenants only

### Lifecycle
- Create in ACTIVE status
- ACTIVE → INACTIVE on tenancy end
- Disable account when INACTIVE
- Keep historical data

---

## 4. Dependencies

### External Dependencies
- Account service (create account, disable account)
- Room service (room validation, room status update)
- Email service (send temp password)

### Blocking
- Account service must be ready

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Account creation failure | High | Implement rollback on account creation error |
| Duplicate email/CCCD | High | Implement unique constraints + validation |
| Room assignment failure | High | Transaction rollback |
| Code generation collision | Medium | Sequence-based generation |

---

## 6. Success Criteria

- ✓ Tenant code generation working
- ✓ Account creation/integration working
- ✓ Tenant creation with validation
- ✓ Room assignment working
- ✓ Unique constraints enforced
- ✓ End tenancy working
- ✓ List/search working
- ✓ Response time < 500ms
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 7. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-4:** Backend core development
- **Week 5-6:** Backend lifecycle development
- **Week 7-8:** Frontend development
- **Week 9-10:** Testing & deployment

**Total:** 10 weeks
