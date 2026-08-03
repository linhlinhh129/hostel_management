# Feature Specification: limit-meter-reading-digits

**Feature Branch**: `[013-limit-meter-reading-digits]`

**Created**: 2026-08-04

**Status**: Draft

**Input**: User description: "Cập nhập lại cho mình trọ mình dùng công tơ 100000 số max giới hạn, và nhân viên vận hành chỉ có nhập đc từ 0 đến 99999 (5 số thôi) @[f:\SU26\New folder\hostel_management\my-project\sdd\specs\operator]"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Giới hạn số trên giao diện nhập liệu (Priority: P1)

Là một nhân viên vận hành (Operator), khi tôi nhập chỉ số điện nước mới, tôi chỉ có thể nhập một số từ 0 đến 99999 (tối đa 5 chữ số), để đảm bảo dữ liệu khớp với thiết kế vật lý của công tơ tại khu trọ (tối đa 100000 số).

**Why this priority**: Việc giới hạn đúng 5 chữ số từ UI (HTML/JS) giúp Operator tránh nhập sai sót (ví dụ dư 1 số 0 khiến hóa đơn sai lệch lớn), tiết kiệm thời gian vận hành. 

**Independent Test**: Có thể kiểm thử trực tiếp trên giao diện cập nhật chỉ số điện/nước. Nhập một số > 99999 hoặc có 6 chữ số sẽ bị chặn, không submit được form.

**Acceptance Scenarios**:

1. **Given** một Operator đang ở trang cập nhật chỉ số, **When** họ nhập số `100000` (hoặc dài hơn 5 chữ số), **Then** form sẽ báo lỗi ngay lập tức hoặc ngăn chặn việc nhập ký tự thứ 6.
2. **Given** một Operator đang ở trang cập nhật chỉ số, **When** họ nhập số `99999` hoặc nhỏ hơn (không âm), **Then** họ có thể submit form bình thường.
3. **Given** số cũ là `99998` và số mới là `2`, **When** Operator nhập số `2`, **Then** hệ thống hiểu là công tơ quay vòng (nếu chức năng quay vòng được hỗ trợ) hoặc xử lý bình thường trong giới hạn. (Tuy nhiên, theo user requirement chỉ đơn giản là chặn max 99999 trên input).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST giới hạn các trường nhập liệu chỉ số điện (electricity) và nước (water) ở giá trị tối đa là `99999`.
- **FR-002**: Hệ thống MUST từ chối mọi submit form từ Operator nếu chỉ số mới nhỏ hơn 0 hoặc lớn hơn `99999`.
- **FR-003**: Hệ thống MUST trả về thông báo lỗi rõ ràng nếu có dữ liệu không hợp lệ vượt quá 5 chữ số.
- **FR-004**: Đặc tả thiết kế (SDD) tại `specs/operator` liên quan đến việc nhập chỉ số MUST được cập nhật lại theo quy tắc giới hạn mới này.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% các thao tác nhập chỉ số mới của Operator qua form UI không thể vượt quá giá trị 99999.
- **SC-002**: Backend API (Servlet) bắt và xử lý 100% các request có chứa tham số chỉ số vượt quá 99999, tránh lỗi tràn số liệu vào Database.
- **SC-003**: Spec hiện tại trong thư mục `my-project/sdd/specs/operator` được cập nhật đồng bộ với yêu cầu thực tế.

## Assumptions

- Việc công tơ đạt mốc 100000 và quay về số 0 có thể đã được tính đến trong logic hoặc sẽ được xử lý riêng. Requirement này tập trung chủ yếu vào việc giới hạn "nhập liệu" cho 5 chữ số (0 - 99999) do đồng hồ chỉ hiển thị 5 số cơ học.
- Yêu cầu này áp dụng cho cả chỉ số điện và chỉ số nước.
