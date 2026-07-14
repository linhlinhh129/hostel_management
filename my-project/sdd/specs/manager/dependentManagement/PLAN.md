# PLAN: Kế hoạch Thực thi Quản lý Người phụ thuộc (Manager)

**Status:** Completed  
**Date:** 2026-07-13  
**Priority:** High  
**Estimated Duration:** Completed

---

## 1. Tổng quan Giải pháp

Feature Quản lý Người phụ thuộc cho phép Manager thêm, xem, cập nhật, và xóa mềm những người phụ thuộc của người thuê (vợ/chồng, con, bố mẹ, người thân khác).

**Kiến trúc:**
- Backend API: Servlet Controller điều hướng form POST/GET kết nối với database thông qua DAO.
- Frontend UI: Tích hợp bảng danh sách và modal thêm tại trang chi tiết người thuê, trang chi tiết người phụ thuộc hỗ trợ xem thông tin, chỉnh sửa và xóa.
- Database: Bảng `dbo.dependents` có quan hệ khóa ngoại `tenant_id` liên kết đến người thuê.
- Audit Log: Ghi nhận tất cả các thao tác thay đổi dữ liệu vào bảng `dbo.audit_logs`.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Hoàn thành)

**Mục tiêu:** Design database, API contract

**Công việc:**
- Design `dbo.dependents` table schema.
- Define servlet URLs for Manager.
- Design validation rules (phone, CCCD formats).
- Identify soft delete strategy (`deleted_at` field).

---

### Giai đoạn 2: Backend Development (Hoàn thành)

**Mục tiêu:** Implement backend services & controller

**Công việc:**
- Create Dependent model & DAO class.
- Implement business logic in `TenantServiceImpl.java` (`addDependent`, `editDependent`, `removeDependent`, `getTenantDependents`).
- Implement validations (valid VN phone, valid VN identity).
- Implement soft delete (`deleted_at` timestamp update).
- Implement audit logging for all changes.

---

### Giai đoạn 3: Frontend Development (Hoàn thành)

**Mục tiêu:** Implement UI

**Công việc:**
- Show dependents list on tenant profile page.
- Implement create modal on tenant profile.
- Implement dependent detail page with edit form and delete confirmation.

---

### Giai đoạn 4: Testing & Deployment (Hoàn thành)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Soft Delete Strategy
- Use `deleted_at` timestamp field (`deleted_at IS NULL` represents active, otherwise deleted/inactive).
- Keep historical data and relations.
- Prevent physical record deletion.

### Validation
- Tenant must exist and be ACTIVE.
- Required fields on form: name, relationship.
- Validate Vietnamese mobile phone number formatting (10 digits).
- Validate identity number formatting (9 or 12 digits).

### Relationships
- One-to-many relationship: One tenant can have multiple active dependents.
- Linked via foreign key `tenant_id` pointing to `dbo.users(user_id)`.

---

## 4. Success Criteria

- ✓ CRUD operations working via form actions
- ✓ Validation of phone and CCCD formats enforced
- ✓ Soft delete using `deleted_at` working
- ✓ Audit logging complete
- ✓ Response time < 500ms
- ✓ UAT passed

---

## 5. Timeline

- **Week 1:** Design & preparation (Completed)
- **Week 2-3:** Backend development (Completed)
- **Week 4:** Frontend development (Completed)
- **Week 5-6:** Testing & deployment (Completed)

**Total:** Completed
