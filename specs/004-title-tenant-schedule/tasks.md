# Implementation Tasks: Hiển thị Lịch hẹn và Kết quả cho Tenant

**Feature Branch**: `[004-title-tenant-schedule]`
**Created**: 2026-06-29

## Phase 1: Giao diện Front-end (JSP)

- [x] **Task 1.1**: Sửa đổi file `detail.jsp` của Tenant (`tenant/tickets/detail.jsp`) phần "Thông tin xử lý" (Cột phải).
  - [x] Hiển thị "Lý do từ chối" nếu trạng thái là `REJECTED`.
  - [x] Hiển thị "Lịch hẹn xử lý" nếu trạng thái là `IN_PROGRESS`. Đọc dữ liệu từ `ticket.rejectionReason`.
  - [x] Hiển thị "Ghi chú hoàn thành" nếu trạng thái là `DONE`. Đọc dữ liệu từ `ticket.rejectionReason`.
- [x] **Task 1.2**: Sửa đổi file `detail.jsp` phần "Nội dung yêu cầu" (Cột trái).
  - [x] Thêm một khối (block) để hiển thị Hình ảnh kết quả xử lý (`ticket.attachmentUrls2`) nếu trạng thái là `DONE`. Có thể phân tách chuỗi `attachmentUrls2` bằng dấu phẩy và in ra nhiều ảnh nếu có.

## Phase 2: Kiểm thử & Xác nhận (Manual Verification)

- [x] **Task 2.1**: Đăng nhập tài khoản Tenant và mở một yêu cầu đang ở trạng thái `IN_PROGRESS`.
  - [x] Xác nhận giao diện hiển thị lịch hẹn mà Operator đã đặt.
- [x] **Task 2.2**: Mở một yêu cầu đang ở trạng thái `DONE`.
  - [x] Xác nhận giao diện hiển thị Ghi chú hoàn thành.
  - [x] Xác nhận giao diện hiển thị các Hình ảnh kết quả xử lý.
