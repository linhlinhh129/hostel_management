# Đặc tả: Định dạng hiển thị Lịch hẹn

**Feature Branch**: `[005-title-format-appointment-date]`
**Created**: 2026-06-30
**Status**: Draft

**Input**: User description: "cái phần định dạng khi mà xác định lịch hẹn ý, bạn chỉnh lại giao diện cái này, rõ ràng ngày giờ cho mình đi ở bên dashborad của operator và yêu cầu của tenant cho mình nhé"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Định dạng hiển thị ngày giờ thân thiện (Priority: P1)

Là một Operator hoặc Tenant, tôi muốn xem lịch hẹn dưới định dạng thân thiện và dễ đọc (ví dụ: `17:58 - 01/07/2026`) thay vì chuỗi ISO raw thô cứng (`2026-07-01T17:58`).

**Why this priority**: Cải thiện trải nghiệm người dùng, giúp thông tin ngày giờ dễ đọc và chuyên nghiệp hơn.

**Independent Test**: Đăng nhập Operator hoặc Tenant, mở xem chi tiết một yêu cầu ở trạng thái `IN_PROGRESS` (Đang xử lý). Giao diện phần "Lịch hẹn xử lý" phải hiển thị ngày giờ đã được định dạng rõ ràng, không còn chứa chữ "T" hay dấu gạch ngang ngược.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST tự động phân tích (parse) chuỗi ngày giờ từ trường `rejectionReason` nếu trạng thái là `IN_PROGRESS`.
- **FR-002**: Hệ thống MUST định dạng lại ngày giờ theo chuẩn Việt Nam (ví dụ: `HH:mm - dd/MM/yyyy`).
- **FR-003**: Hệ thống MUST áp dụng định dạng này cho cả giao diện Operator (`operator/requests/detail.jsp`) và Tenant (`tenant/tickets/detail.jsp`).
- **FR-004**: Nếu chuỗi không phải là định dạng ngày giờ hợp lệ (ví dụ ghi chú tay), hệ thống MUST fallback trả về nguyên bản chuỗi đó mà không gây lỗi (Exception).

### Key Entities

- **Request**: Thêm hàm tiện ích `getFormattedAppointmentDate()` hoặc tương tự để phân giải và trả về chuỗi đã format.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Giao diện hiển thị ngày giờ lịch hẹn hoàn toàn bằng ngôn ngữ/định dạng chuẩn, không lộ chuỗi ISO thô.
