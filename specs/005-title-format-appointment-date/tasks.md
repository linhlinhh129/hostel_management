# Implementation Tasks: Định dạng hiển thị Lịch hẹn

**Feature Branch**: `[005-title-format-appointment-date]`
**Created**: 2026-06-30

## Phase 1: Logic Back-end (Java)

- [x] **Task 1.1**: Sửa đổi file `Request.java` (`src/main/java/com/quanlyphongtro/model/Request.java`).
  - [x] Thêm hàm `public String getFormattedAppointmentDate()`.
  - [x] Kiểm tra nếu `status` không phải là `IN_PROGRESS` hoặc `rejectionReason` rỗng thì trả về `rejectionReason`.
  - [x] Thêm block `try-catch` dùng `LocalDateTime.parse` để parse chuỗi ISO trong `rejectionReason`.
  - [x] Định dạng lại ngày giờ theo format `HH:mm - dd/MM/yyyy` bằng `DateTimeFormatter`.
  - [x] Bắt `Exception` (hoặc `DateTimeParseException`) và fallback trả về nguyên gốc `rejectionReason` nếu parse lỗi.

## Phase 2: Giao diện Front-end (JSP)

- [x] **Task 2.1**: Sửa đổi file `detail.jsp` của Operator (`operator/requests/detail.jsp`).
  - [x] Thay thế `${reqDetail.rejectionReason}` bằng `${reqDetail.formattedAppointmentDate}` tại dòng hiển thị "Lịch hẹn xử lý".
- [x] **Task 2.2**: Sửa đổi file `detail.jsp` của Tenant (`tenant/tickets/detail.jsp`).
  - [x] Thay thế `${ticket.rejectionReason}` bằng `${ticket.formattedAppointmentDate}` tại dòng hiển thị "Lịch hẹn xử lý".

## Phase 3: Kiểm thử & Xác nhận (Manual Verification)

- [x] **Task 3.1**: Xác nhận phía Operator.
  - [x] Mở một yêu cầu đã được lên lịch, kiểm tra xem lịch hẹn có hiển thị theo format thân thiện `17:58 - 01/07/2026` không.
- [x] **Task 3.2**: Xác nhận phía Tenant.
  - [x] Đăng nhập Tenant, mở cùng yêu cầu đó và kiểm tra hiển thị.
