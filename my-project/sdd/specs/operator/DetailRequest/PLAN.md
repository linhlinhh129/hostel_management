# PLAN: Kế hoạch Thực thi Chi tiết yêu cầu sửa chữa

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 6-7 weeks

---

## 1. Tổng quan Giải pháp

Feature Chi tiết yêu cầu sửa chữa cho phép nhân viên vận hành xem toàn bộ thông tin ticket, đánh giá sự cố, và thực hiện nhận hoặc từ chối yêu cầu một cách an toàn.

**Kiến trúc:**
- Backend API: `GET /api/v1/requests/{id}` và `PUT /api/v1/requests/{id}/assign`
- UI: chi tiết yêu cầu, ảnh thumbnail/lightbox, nút nhận/từ chối, modal nhập lý do từ chối
- Concurrency: bảo vệ race condition khi nhận yêu cầu
- Read-only: không cho phép sửa nội dung yêu cầu gốc

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định dữ liệu chi tiết, luồng nhận/từ chối, concurrency control.

**Công việc:**
- Thiết kế API detail request
- Thiết kế API assign/reject request
- Xác định trạng thái cho `pending`, `accepted`, `rejected`
- Thiết kế UI lightbox và modal từ chối

---

### Giai đoạn 2: Backend Development (Tuần 2-3)

**Mục tiêu:** triển khai detail retrieval và cập nhật trạng thái an toàn.

**Công việc:**
- Implement get request detail service
- Implement assign request service with optimistic concurrency
- Implement reject request service with `reject_reason`
- Ensure `UPDATE` validates current status before committing

---

### Giai đoạn 3: Frontend Development (Tuần 4-5)

**Mục tiêu:** xây dựng trang chi tiết, xem ảnh, nhận/từ chối.

**Công việc:**
- Render detail fields and image thumbnails
- Provide lightbox for image preview
- Show buttons for pending state only
- Implement accept/reject actions and modal

---

### Giai đoạn 4: Testing & Deployment (Tuần 6-7)

**Mục tiêu:** đảm bảo race condition, quyền, và status logic.

**Công việc:**
- Unit tests service logic
- Integration tests for assign/reject
- End-to-end validation of UI flow

---

## 3. Key Technical Challenges

### Concurrency Control
- Prevent two users nhận cùng ticket cùng lúc
- Use conditional update based on current status
- Handle conflict gracefully

### Reject Reason Enforcement
- `reject_reason` required khi từ chối
- Must store reason with request

### Read-only Data Integrity
- No editing of original request content
- Only status transitions allowed

---

## 4. Success Criteria

- ✓ Detail screen renders full request data
- ✓ Pending request shows accept/reject buttons
- ✓ Accept locks request to current user
- ✓ Reject saves reason and redirects to list
- ✓ Race conditions prevented
- ✓ Response time < 500ms (p95)
- ✓ >= 80% coverage

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2-3:** Backend implementation
- **Week 4-5:** Frontend implementation
- **Week 6-7:** Testing & deployment

**Total:** 7 weeks
