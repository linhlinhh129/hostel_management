# Feature: Chi tiết yêu cầu sửa chữa & xử lý sai số điện nước (Operator)

**Status:** Approved (Updated)  
**Author:** Phạm Anh Tú / Antigravity  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-07-25  
**Priority:** High

---

## 1. Business Context

Tính năng này cung cấp toàn bộ thông tin chi tiết về một sự cố hoặc yêu cầu sửa chữa / báo cáo sai số điện nước (`UTILITY`) để nhân viên vận hành (Operator) đánh giá, tiếp nhận/đặt lịch xử lý và báo cáo hoàn thành sau khi kiểm tra thực tế.

---

## 2. User Stories

### Story 1 (Xem chi tiết yêu cầu)
**As an** Operator, **I want to** xem toàn bộ tiêu đề, nội dung mô tả sai số điện nước và thông tin phòng/cơ sở **so that** tôi nắm rõ tình hình cần kiểm tra thực tế.

### Story 2 (Xác nhận & Đặt lịch xử lý)
**As an** Operator, **when** nhận được yêu cầu ở trạng thái `PENDING`, **I want to** nhấn "Xác nhận & Đặt lịch xử lý" **so that** trạng thái chuyển sang `IN_PROGRESS` và hệ thống ghi nhận tôi đang phụ trách.

### Story 3 (Xác nhận hoàn thành)
**As an** Operator, **when** đã kiểm tra/điều chỉnh chỉ số điện nước xong, **I want to** nhấn "Xác nhận hoàn thành" và nhập ghi chú (không bắt buộc) **so that** công việc chuyển sang trạng thái `COMPLETED`.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị chi tiết yêu cầu UTILITY
**WHEN** Operator truy cập `GET /operator/requests/detail?id={id}`
**THE SYSTEM SHALL** hiển thị Tiêu đề, Nội dung chi tiết (Mã HĐ, chỉ số điện/nước nghi ngờ sai), Phòng, Cơ sở, Ngày tạo và Trạng thái hiện tại.

### AC02 – Xác nhận & Đặt lịch xử lý
**WHEN** yêu cầu ở trạng thái `PENDING` và Operator nhấn "Xác nhận & Đặt lịch xử lý"
**THE SYSTEM SHALL** cập nhật trạng thái yêu cầu sang `IN_PROGRESS` và gửi thông báo thành công.

### AC03 – Xác nhận hoàn thành công việc
**WHEN** yêu cầu ở trạng thái `IN_PROGRESS` và Operator nhấn "Xác nhận hoàn thành"
**THE SYSTEM SHALL**
- Chấp nhận submit form kể cả khi trường Ghi chú hoàn thành bị bỏ trống (Optional), nhưng nếu có nhập thì tối đa 1000 ký tự (có validation trên giao diện và backend).
- Cập nhật trạng thái yêu cầu sang `COMPLETED`.
- Cập nhật chỉ số điện nước liên quan sang `CORRECTED` (hoặc `NORMAL`).
- Redirect về lại trang danh sách hoặc chi tiết yêu cầu kèm thông báo thành công.

---

## 4. Technical Integration

- **Endpoint**: `GET /operator/requests/detail?id={id}`
- **Form Submit Action**: `POST /operator/requests/detail`
- **Parameters**: `id`, `action` ("accept" / "schedule" / "complete"), `notes` (optional).
