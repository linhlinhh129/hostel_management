# PLAN: Kế hoạch Thực thi Quản lý Người phụ thuộc (Manager)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 4-6 weeks

---

## 1. Tổng quan Giải pháp

Feature Quản lý Người phụ thuộc cho phép Manager thêm, xem, cập nhật, và xóa mềm những người phụ thuộc của người thuê (vợ/chồng, con, bố mẹ, người thân khác).

**Kiến trúc:**
- Backend API: CRUD người phụ thuộc, link với tenant
- Frontend UI: List, create, edit, delete mềm người phụ thuộc
- Database: Dependent table với relationship đến Tenant
- Audit Log: Ghi nhận tất cả thao tác

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Design database, API contract

**Công việc:**
- Design Dependent entity & table
- Define API endpoints
- Design validation rules
- Identify soft delete strategy

---

### Giai đoạn 2: Backend Development (Tuần 2-3)

**Mục tiêu:** Implement backend API

**Công việc:**
- Create Dependent entity & repository
- Implement CRUD services
- Implement validation (tenant active, CCCD unique)
- Implement soft delete logic
- Implement audit logging

---

### Giai đoạn 3: Frontend Development (Tuần 4)

**Mục tiêu:** Implement UI

**Công việc:**
- Dependent list component
- Create/edit form
- Delete confirmation dialog

---

### Giai đoạn 4: Testing & Deployment (Tuần 5-6)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Soft Delete Strategy
- Use status field (ACTIVE/INACTIVE)
- Keep historical data
- Cannot physically delete

### Validation
- Tenant must exist & be ACTIVE
- CCCD must be unique for ACTIVE dependents
- Required fields: name, relationship, DOB, CCCD

### Relationships
- Many dependents per tenant
- One tenant per dependent
- Link via tenantId

---

## 4. Success Criteria

- ✓ CRUD operations working
- ✓ Validation enforced
- ✓ Soft delete working
- ✓ Audit log complete
- ✓ Response time < 500ms
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2-3:** Backend development  
- **Week 4:** Frontend development
- **Week 5-6:** Testing & deployment

**Total:** 6 weeks
