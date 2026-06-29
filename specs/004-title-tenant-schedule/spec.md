# Đặc tả chức năng: Hiển thị Lịch hẹn và Kết quả cho Tenant

**Feature Branch**: `[004-title-tenant-schedule]`
**Created**: 2026-06-29
**Status**: Draft

**Input**: User description: "khi mình tiếp nhân yêu cầu của tenant thì tiếp theo là xác định lịch hẹn thì ở bên operator thì luồng đúng rồi, thì ở bên tenant nó cũng phải xuất hiện lịch hẹn ở trong chi tiết yêu cầu chứ"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Tenant xem được lịch hẹn khi yêu cầu đang xử lý (Priority: P1)

Là một Tenant (Người thuê phòng), tôi muốn xem được thời gian (lịch hẹn) mà Ban quản lý (Operator) đã lên lịch để xử lý sự cố của tôi, nhằm chuẩn bị sắp xếp thời gian có mặt tại phòng.

**Why this priority**: Minh bạch thông tin giữa Operator và Tenant. Khi Operator xác nhận lịch (Đang xử lý), Tenant phải thấy được lịch đó ngay lập tức.

**Independent Test**: Đăng nhập với tư cách Tenant, mở một yêu cầu đang ở trạng thái `IN_PROGRESS`. Giao diện phải hiển thị một khối "Lịch hẹn xử lý" chứa thông tin ngày giờ hẹn.

**Acceptance Scenarios**:

1. **Given** một yêu cầu đang ở trạng thái "Đang xử lý" (IN_PROGRESS) và đã có lịch hẹn được lưu, **When** Tenant xem chi tiết yêu cầu, **Then** hệ thống hiển thị lịch hẹn một cách nổi bật.
2. **Given** một yêu cầu chưa được xác nhận lịch (ví dụ mới `PENDING` hoặc `ASSIGNED`), **When** Tenant xem chi tiết yêu cầu, **Then** hệ thống KHÔNG hiển thị phần lịch hẹn này.

---

### User Story 2 - Tenant xem được hình ảnh và ghi chú hoàn thành (Priority: P2)

Là một Tenant, tôi muốn xem được ghi chú và hình ảnh minh chứng mà Operator đã đính kèm sau khi hoàn thành yêu cầu (DONE).

**Why this priority**: Đảm bảo Tenant nghiệm thu được công việc xử lý sự cố.

**Independent Test**: Đăng nhập với tư cách Tenant, mở một yêu cầu đang ở trạng thái `DONE`. Giao diện phải hiển thị khối "Kết quả xử lý" bao gồm ghi chú và hình ảnh.

**Acceptance Scenarios**:

1. **Given** một yêu cầu đã "Hoàn thành" (DONE) có ghi chú và ảnh đính kèm, **When** Tenant xem chi tiết yêu cầu, **Then** hệ thống hiển thị khối Kết quả xử lý với ghi chú và danh sách ảnh.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST hiển thị thông tin Lịch hẹn (được lưu trữ tạm thời trong trường `rejectionReason` đối với các yêu cầu không bị từ chối) khi trạng thái là `IN_PROGRESS`.
- **FR-002**: Hệ thống MUST hiển thị thông tin Ghi chú hoàn thành (cũng nằm trong `rejectionReason`) và Hình ảnh minh chứng (`attachmentUrls2`) khi trạng thái là `DONE` (Hoàn thành) hoặc `COMPLETED`.
- **FR-003**: Hệ thống MUST vẫn hiển thị Lý do từ chối (nếu có) khi trạng thái là `REJECTED`.

### Key Entities

- **Request**: Chứa trường `rejectionReason` đóng đa vai trò (Lý do từ chối, Lịch hẹn, Ghi chú hoàn thành) phụ thuộc vào trường `status`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Tenant xem được lịch hẹn ngay khi Operator lên lịch mà không cần chờ đến khi hoàn thành.
- **SC-002**: Tránh nhầm lẫn dữ liệu giữa Lý do từ chối, Lịch hẹn, và Ghi chú hoàn thành dựa trên trạng thái của Yêu cầu.

## Assumptions

- Việc sử dụng trường `rejectionReason` cho nhiều mục đích (Lịch hẹn, Ghi chú hoàn thành) đã được thực hiện ở Backend của tính năng trước. Feature này chỉ áp dụng các logic rẽ nhánh tương tự lên UI của Tenant.
