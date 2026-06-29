# Implementation Tasks: Luồng xử lý yêu cầu của Operator

**Feature Branch**: `[003-title-operator-request-workflow]`
**Created**: 2026-06-29

## Phase 1: Giao diện Front-end (JSP)

- [x] **Task 1.1**: Sửa đổi file `detail.jsp` của Operator (`operator/requests/detail.jsp`) để hỗ trợ rẽ nhánh giao diện dựa trên `reqDetail.status`.
  - [x] Thêm điều kiện hiển thị nút "Tiếp nhận" khi trạng thái là `PENDING`. Form action = `accept`.
  - [x] Thêm điều kiện hiển thị form "Xác nhận lịch" (nhập `appointmentDate`) khi trạng thái là `ASSIGNED`. Form action = `schedule`.
  - [x] Thêm điều kiện hiển thị form "Hoàn thành" (nhập `notes` và đính kèm `after_images`) khi trạng thái là `IN_PROGRESS`. Form action = `complete`.
  - [x] Ẩn các thao tác này đối với các trạng thái đã đóng như `DONE`, `REJECTED`, `CANCELLED`.

## Phase 2: Logic Back-end (Java)

- [x] **Task 2.1**: Kiểm tra lại `RequestServiceImpl.java` để đảm bảo luồng nghiệp vụ không bị nghẽn.
  - [x] Đảm bảo `acceptRequest` chuyển đúng từ PENDING -> ASSIGNED.
  - [x] Đảm bảo `scheduleAppointmentText` chuyển đúng từ ASSIGNED -> IN_PROGRESS.
  - [x] Đảm bảo `completeRequest` chuyển đúng từ IN_PROGRESS -> DONE.

## Phase 3: Kiểm thử & Xác nhận (Manual Verification)

- [x] **Task 3.1**: Kiểm thử luồng Tiếp nhận.
  - Operator mở yêu cầu PENDING, bấm Tiếp nhận -> Chuyển sang ASSIGNED.
- [x] **Task 3.2**: Kiểm thử luồng Xác nhận lịch.
  - Operator mở yêu cầu ASSIGNED, nhập lịch, bấm Xác nhận -> Chuyển sang IN_PROGRESS.
- [x] **Task 3.3**: Kiểm thử luồng Hoàn thành.
  - Operator mở yêu cầu IN_PROGRESS, nhập ghi chú, tải ảnh lên, bấm Hoàn thành -> Chuyển sang DONE.
