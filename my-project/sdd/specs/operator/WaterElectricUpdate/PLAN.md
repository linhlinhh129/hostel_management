# PLAN: Kế hoạch Thực thi Cập nhật đồng hồ điện nước

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 5-6 weeks

---

## 1. Tổng quan Giải pháp

Feature Cập nhật đồng hồ điện nước cho phép nhân viên vận hành ghi nhận số liệu tiêu thụ mới vào hệ thống, với kiểm tra dữ liệu hợp lệ và đảm bảo dữ liệu không bị ghi đè sai.

**Kiến trúc:**
- Backend API: `POST /api/v1/meter-readings` hoặc `PUT /api/v1/meter-readings/{id}`
- Input validation: positive readings, valid reading date, room/facility scope
- Audit trail: log who cập nhật và thời điểm

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định schema cập nhật và business rules.

**Công việc:**
- Thiết kế payload input
- Xác định validation rules
- Xác định audit columns và constraints

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** triển khai API tạo/cập nhật reading.

**Công việc:**
- Implement create/update service
- Enforce positive/monotonic readings
- Ensure current operator scope

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** xây dựng form nhập liệu meter readings.

**Công việc:**
- Build update form
- Add inline validation
- Provide success/failure feedback

---

### Giai đoạn 4: Testing & Deployment (Tuần 5-6)

**Mục tiêu:** validate input and audit.

**Công việc:**
- Unit tests validation rules
- Integration tests for create/update
- UI tests for form submission

---

## 3. Key Technical Challenges

### Data Validation
- Ensure reading > 0
- Ensure reading date valid and not future
- Prevent regressive or duplicate entries

### Scoped Access
- Only allow updates for assigned rooms/facilities
- Protect against unauthorized meter changes

### Audit and Integrity
- Persist who updated reading and when
- Avoid data loss by checking existing values

---

## 4. Success Criteria

- ✓ Update form loads correctly
- ✓ Validation rejects invalid readings
- ✓ Audit metadata stored
- ✓ Response latency < 500ms
- ✓ Scope restricted to assigned operator
- ✓ Coverage >= 80%

---

## 5. Timeline

- **Week 1:** Design
- **Week 2:** Backend implementation
- **Week 3-4:** Frontend implementation
- **Week 5-6:** Testing & deployment

**Total:** 6 weeks
