# PLAN: Kế hoạch Thực thi Quản lý Nhân sự

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 10-12 weeks

---

## 1. Tổng quan Giải pháp

Feature Quản lý Nhân sự cho phép Admin quản lý toàn bộ tài khoản nhân sự với các vai trò: Manager, Technician, Cost Manager. Đặc biệt quản lý gán cơ sở và kiểm soát số lượng tài khoản Cost Manager (tối đa 1 tài khoản hoạt động).

**Kiến trúc:**
- Backend API: CRUD nhân sự, gán vai trò, gán cơ sở, khóa/mở khóa tài khoản
- Authentication: Gén mật khẩu tạm thời, gửi email
- Frontend UI: Danh sách, create, edit, detail, khóa/mở khóa
- Database: Lưu nhân sự, vai trò, cơ sở được gán, trạng thái

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** Thiết kế API, database, authentication flow

**Công việc:**
- Design database schema (Employee, EmployeeRole, EmployeeFacility)
- Define API contract
- Design temporary password generation & email
- Design role assignment logic
- Identify Cost Manager constraint enforcement

**Deliverables:**
- ERD
- API specification
- Authentication flow diagram
- Error code mapping

---

### Giai đoạn 2: Backend Development (Tuần 3-6)

**Mục tiêu:** Implement backend API & business logic

**Công việc:**
- Implement Employee entity & repository
- Implement role assignment logic
- Implement facility assignment logic
- Implement Cost Manager constraint
- Implement CRUD APIs
- Implement authentication integration
- Implement audit logging
- Email integration for password

**Key Features:**
- Create employee with temp password
- List employees with pagination
- Get employee detail
- Update employee (info, role, facilities)
- Lock/unlock account
- Search & filter
- Cost Manager uniqueness enforcement

---

### Giai đoạn 3: Frontend Development (Tuần 7-8)

**Mục tiêu:** Implement UI

**Công việc:**
- Employee list page
- Create employee form
- Employee detail page
- Edit employee form
- Lock/unlock dialogs
- Facility assignment selector
- Role assignment selector

---

### Giai đoạn 4: Authentication & Email (Tuần 8-9)

**Mục tiêu:** Implement password generation & email sending

**Công việc:**
- Implement temporary password generation
- Implement email sending service
- Implement password change on first login
- Implement password reset flow

---

### Giai đoạn 5: Testing & Deployment (Tuần 10-12)

**Mục tiêu:** Testing, UAT, deployment

**Công việc:**
- Unit tests
- Integration tests
- E2E tests
- Security testing
- UAT
- Documentation
- Deployment

---

## 3. Key Technical Challenges

### Cost Manager Uniqueness
- Enforce at most 1 active Cost Manager account
- Check on create & update
- Allow multiple inactive accounts
- Handle lock/unlock carefully

### Facility Assignment
- Manager/Technician must have >= 1 facility
- Cost Manager doesn't require facility
- Only assign active facilities
- Validate facility exists & is active

### Temporary Password
- Generate secure random password
- Send via email
- Force change on first login
- Track if password reset required

### Email Integration
- Async email sending (don't block API)
- Email retry on failure
- Template for welcome email

---

## 4. Dependencies

### External Dependencies
- Facility service (list active facilities)
- Email service (send temporary password)
- Authentication service (password reset)

### Internal Dependencies
- Used by: Department assignments, facility access control
- Needs: Facility must be created first

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Cost Manager constraint violated | High | Implement at DB & service level, test thoroughly |
| Email delivery failure | Medium | Implement retry logic, log failures |
| Concurrent Cost Manager activation | High | Use DB transaction & lock |
| Temporary password security | High | Generate strong password, use secure transmission |
| Facility assignment complexity | Medium | Comprehensive validation & testing |

---

## 6. Success Criteria

- ✓ All CRUD operations working
- ✓ Role assignment correct
- ✓ Facility assignment working
- ✓ Cost Manager constraint enforced
- ✓ Password generation & email working
- ✓ Lock/unlock functionality correct
- ✓ Search & pagination working
- ✓ Response time < 500ms
- ✓ >= 80% code coverage
- ✓ UAT passed
- ✓ Email delivery reliable

---

## 7. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-6:** Backend development
- **Week 7-8:** Frontend development
- **Week 8-9:** Authentication & email
- **Week 10-12:** Testing & deployment

**Total:** 12 weeks

---

## 8. Constraints

- Only Admin can manage personnel
- Cannot create/manage Admin accounts
- Cannot change role to Admin
- Must ensure data consistency with permissions
- Email delivery must be reliable
- Audit log mandatory for all operations
