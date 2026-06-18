# PLAN: Kế hoạch Thực thi Quản lý Hồ sơ Cá nhân (Tenant)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 4-5 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Tenant xem và cập nhật thông tin cá nhân (email, phone), xem thông tin phòng và danh sách người phụ thuộc.

**Kiến trúc:**
- Backend API: Get profile, update profile (email/phone only)
- Update fields: email (unique), phone
- Read-only fields: name, DOB, CCCD, room, dependents
- Frontend UI: Profile view, edit form

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Design API contract, validation rules

**Công việc:**
- Define API endpoints (GET, PUT)
- Define updatable fields (email, phone only)
- Plan validation rules
- Plan response format

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** Implement backend APIs

**Công việc:**
- Implement get profile service
- Implement update profile service
- Implement validation (email unique, phone format)
- Implement access control

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** Implement UI

**Công việc:**
- Profile view with read-only sections
- Editable form (email, phone)
- Validation & error handling

---

### Giai đoạn 4: Testing & Deployment (Tuần 5)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Field-Level Control
- View-only: tenantId, name, DOB, CCCD, room
- Editable: email, phone

### Validation
- Email: format, unique, max 100 chars
- Phone: numbers only, 10-11 chars

### Related Data
- Show current room info
- Show linked dependents

---

## 4. Success Criteria

- ✓ Get profile working
- ✓ Email/phone update working
- ✓ Email uniqueness enforced
- ✓ Phone format validated
- ✓ Read-only fields protected
- ✓ Response time < 300ms (P95)
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2:** Backend development
- **Week 3-4:** Frontend development
- **Week 5:** Testing & deployment

**Total:** 5 weeks
