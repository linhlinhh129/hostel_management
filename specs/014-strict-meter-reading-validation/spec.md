# Feature Specification: strict-meter-reading-validation

**Feature Branch**: `[014-strict-meter-reading-validation]`

**Created**: 2026-08-04

**Status**: Draft

**Input**: User description: "bắt hết trường hợp lỗi khi nhập số điện nước cho mình đi không nhập đc âm, và không nhập được số thập phân báo lỗi hết ra màn hình cho mình đi @[f:\SU26\New folder\hostel_management\my-project\sdd\specs\operator]"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Chặn nhập số âm và số thập phân (Priority: P1)

Là một nhân viên vận hành (Operator), khi tôi nhập chỉ số điện nước mới, hệ thống phải ngăn chặn việc nhập các giá trị âm hoặc giá trị có chứa số thập phân (dấu chấm, dấu phẩy) và hiển thị cảnh báo ngay trên màn hình để tôi biết và điều chỉnh, tránh làm sai lệch lượng tiêu thụ tính toán.

**Why this priority**: Việc nhập số liệu sai như số âm hoặc số thập phân có thể gây lỗi nghiêm trọng trong việc tính toán tiền điện/nước trong hóa đơn (Invoice). Chặn từ sớm ở UI và Backend đảm bảo tính toàn vẹn dữ liệu.

**Independent Test**: Có thể kiểm thử trực tiếp trên giao diện cập nhật chỉ số điện/nước (hoặc gọi API qua Postman). Cố tình nhập `-1` hoặc `15.5`, hệ thống phải báo lỗi.

**Acceptance Scenarios**:

1. **Given** một Operator đang ở trang cập nhật chỉ số, **When** họ nhập số `-5` vào ô chỉ số điện/nước, **Then** hệ thống báo lỗi không được nhập số âm và ngăn chặn submit.
2. **Given** một Operator đang ở trang cập nhật chỉ số, **When** họ nhập số `12.5` (số thập phân), **Then** hệ thống báo lỗi chỉ được nhập số nguyên (không thập phân) và ngăn chặn submit.
3. **Given** request gửi thẳng lên backend chứa `newElectric=-10`, **When** hệ thống nhận request, **Then** backend từ chối và trả về giao diện kèm thông báo lỗi rõ ràng.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST chặn nhập số âm trên giao diện Frontend (sử dụng thuộc tính `min="0"` hoặc xử lý JS). Nếu cố tình nhập số âm, Frontend MUST hiển thị thông báo lỗi (Alert/Warning) cho người dùng.
- **FR-002**: Hệ thống MUST chặn nhập số thập phân trên giao diện Frontend (sử dụng thuộc tính `step="1"` hoặc JS regex). Nếu người dùng nhập số có dấu chấm `.`, dấu phẩy `,` hoặc kí tự `e`, Frontend MUST hiển thị thông báo lỗi.
- **FR-003**: Backend Servlet MUST validate đầu vào, nếu phát hiện số âm hoặc số thập phân/chuỗi không thể ép kiểu sang số nguyên dương (`Integer`), phải trả về thông báo lỗi "Chỉ số không hợp lệ. Phải là số nguyên không âm."
- **FR-004**: Đặc tả thiết kế (SDD) tại `specs/operator` MUST được cập nhật để phản ánh các ràng buộc mới (không âm, không thập phân).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% các giá trị điện/nước lưu vào database luôn luôn là số nguyên (`Integer`), lớn hơn hoặc bằng 0.
- **SC-002**: 100% các lỗi nhập sai định dạng (số âm, thập phân) đều bị bắt trên giao diện (UI) kèm thông báo rõ ràng cho nhân viên vận hành, không để lọt xuống Backend gây lỗi HTTP 500 hoặc lưu dữ liệu rác.

## Assumptions

- Mọi công tơ cơ học thực tế chỉ tính số nguyên không âm. Do đó việc loại bỏ số thập phân là hợp lý và tuân thủ thực tế đo lường dân dụng hiện hành.
