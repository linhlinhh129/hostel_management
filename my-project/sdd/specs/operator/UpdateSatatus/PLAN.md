# PLAN: Kế hoạch Thực thi Cập nhật trạng thái yêu cầu sửa chữa

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 5-6 weeks

---

## 1. Tổng quan Giải pháp

Feature Cập nhật trạng thái yêu cầu sửa chữa cho phép nhân viên vận hành cập nhật tiến trình ticket từ `pending` sang `accepted`, `in_progress`, `resolved`, hoặc `rejected`, đồng thời ghi nhận lý do và thời gian.

**Kiến trúc:**
- Backend API: `PUT /api/v1/requests/{id}/status`
- Status transitions validated bằng workflow rule
- Audit: `updated_by`, `updated_at`, `reject_reason`
- UI: status changers and progress display

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định trạng thái hợp lệ và transition paths.

**Công việc:**
- Define valid transitions
- Design payload contract
- Identify required audit fields

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** triển khai cập nhật status service với validation.

**Công việc:**
- Implement status update service
- Validate current status and desired next state
- Enforce reject_reason when status = rejected

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** cung cấp điều khiển trạng thái và hiển thị luồng ticket.

**Công việc:**
- Render current status and allowed actions
- Provide confirm dialogue for terminal states
- Update UI after change

---

### Giai đoạn 4: Testing & Deployment (Tuần 5-6)

**Mục tiêu:** đảm bảo transition logic chính xác và không cho trạng thái sai.

**Công việc:**
- Unit tests status transition rules
- Integration tests status update API
- UX validation for status actions

---

## 3. Key Technical Challenges

### Transition Rules
- Prevent invalid transitions (e.g. resolved → pending)
- Enforce reject reason for reject state

### Audit and Traceability
- Persist who updated, when, and why
- Preserve history of status changes

### Concurrency
- Handle simultaneous status changes gracefully
- Use conditional update based on current status

---

## 4. Success Criteria

- ✓ Status update API supports valid transitions
- ✓ Reject requires a reason
- ✓ Invalid transitions rejected consistently
- ✓ UI reflects current workflow and disables invalid actions
- ✓ Response < 500ms p95
- ✓ Test coverage >= 80%

---

## 5. Timeline

- **Week 1:** Design
- **Week 2:** Backend implementation
- **Week 3-4:** Frontend implementation
- **Week 5-6:** Testing & deployment

**Total:** 6 weeks
