# Đặc tả chức năng: Luồng xử lý yêu cầu (Request) của Operator

**Feature Branch**: `[003-title-operator-request-workflow]`
**Created**: 2026-06-29
**Status**: Draft

**Input**: User description: "cái yêu cầu từ tenant cho operator ý, thì operator xác nhận tiếp nhận cái yêu cầu này thì tiếp theo mới xác nhận lịch và cuối cùng khi mà operator làm xong yêu cầu thì ấn hoàn thành."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Operator tiếp nhận yêu cầu từ Tenant (Priority: P1)

Là một Operator (Ban quản lý), tôi muốn xác nhận rằng tôi đã nhận được một yêu cầu (sự cố/báo cáo) từ Tenant, để Tenant biết rằng yêu cầu của họ đang được chú ý và chuẩn bị được xử lý.

**Why this priority**: Bước đầu tiên và quan trọng nhất để thiết lập sự giao tiếp giữa Tenant và Operator, tránh trường hợp yêu cầu bị bỏ sót.

**Independent Test**: Có thể test độc lập bằng cách chọn một yêu cầu đang ở trạng thái `PENDING` và cập nhật nó lên `ASSIGNED`.

**Acceptance Scenarios**:

1. **Given** có một yêu cầu mới được gửi từ Tenant (Trạng thái: Mới tạo - PENDING), **When** Operator xem yêu cầu và chọn "Tiếp nhận", **Then** hệ thống ghi nhận Operator này là người phụ trách và chuyển trạng thái yêu cầu sang "Đã tiếp nhận" (ASSIGNED).

---

### User Story 2 - Operator xác nhận lịch xử lý yêu cầu (Priority: P1)

Là một Operator, sau khi đã tiếp nhận yêu cầu, tôi muốn lên lịch hẹn (chọn ngày/giờ) để Tenant biết chính xác khi nào tôi hoặc thợ sẽ đến xử lý sự cố.

**Why this priority**: Cung cấp thông tin thời gian cụ thể giúp Tenant chủ động sắp xếp thời gian ở nhà.

**Independent Test**: Có thể test độc lập bằng cách lấy một yêu cầu đã được tiếp nhận (`ASSIGNED`), cập nhật ngày hẹn và đổi trạng thái sang `IN_PROGRESS`.

**Acceptance Scenarios**:

1. **Given** một yêu cầu đang ở trạng thái "Đã tiếp nhận" (ASSIGNED), **When** Operator nhập ngày giờ hẹn và xác nhận lịch, **Then** hệ thống lưu lại ngày hẹn này và chuyển trạng thái yêu cầu sang "Đang xử lý" (IN_PROGRESS).

---

### User Story 3 - Operator báo cáo hoàn thành yêu cầu (Priority: P1)

Là một Operator, tôi muốn xác nhận rằng yêu cầu/sự cố đã được giải quyết xong, đồng thời có thể ghi chú lại kết quả hoặc đính kèm hình ảnh minh chứng để kết thúc luồng công việc.

**Why this priority**: Khép lại luồng xử lý, đánh dấu hoàn tất công việc để lưu trữ hoặc tính phí (nếu có).

**Independent Test**: Có thể test độc lập bằng cách lấy một yêu cầu đang xử lý (`IN_PROGRESS`), điền ghi chú hoàn thành và đổi trạng thái sang `DONE`.

**Acceptance Scenarios**:

1. **Given** một yêu cầu đang ở trạng thái "Đang xử lý" (IN_PROGRESS), **When** Operator hoàn tất công việc, nhập ghi chú (và ảnh nếu có) rồi chọn "Hoàn thành", **Then** hệ thống cập nhật trạng thái yêu cầu sang "Hoàn thành" (DONE).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST hỗ trợ 4 trạng thái tuyến tính cho luồng yêu cầu: Mới tạo (`PENDING`) -> Đã tiếp nhận (`ASSIGNED`) -> Đang xử lý (`IN_PROGRESS`) -> Hoàn thành (`DONE`).
- **FR-002**: Ở trạng thái `PENDING`, Operator MUST có thể xác nhận "Tiếp nhận" yêu cầu.
- **FR-003**: Ở trạng thái `ASSIGNED`, Operator MUST có thể nhập nội dung lịch hẹn (Text/Date) để chuyển sang "Xác nhận lịch".
- **FR-004**: Ở trạng thái `IN_PROGRESS`, Operator MUST có thể nhập ghi chú hoàn thành và đính kèm tệp để chuyển trạng thái sang "Hoàn thành".
- **FR-005**: Mọi thay đổi trạng thái MUST được lưu trữ lịch sử rõ ràng (Audit Log) để có thể truy vết thời gian chuyển đổi.

### Key Entities

- **Request**: Lưu trữ thông tin yêu cầu của Tenant (bao gồm mã, nội dung, ID người gửi, ID operator phụ trách, và trạng thái hiện hành).
- **AuditLog**: Ghi lại lịch sử mỗi lần trạng thái Request bị thay đổi (từ PENDING sang ASSIGNED, v.v.).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% các yêu cầu đi qua đúng trình tự 3 bước trạng thái mà không bị kẹt hoặc nhảy cóc (trừ phi bị huỷ/từ chối).
- **SC-002**: Người dùng Tenant thấy được thông tin trạng thái cập nhật theo thời gian thực (hoặc sau khi refresh) mỗi khi Operator thực hiện chuyển bước.

## Assumptions

- Operator có đủ quyền hạn để tự phân công (assign) chính mình hoặc hệ thống tự gán Operator hiện tại khi họ bấm "Tiếp nhận".
- Giao diện Tenant đã có sẵn để hiển thị các trạng thái này (phạm vi của feature này chỉ tập trung vào luồng thao tác của Operator).
