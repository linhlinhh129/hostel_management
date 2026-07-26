# Feature Specification: Invoice Note Validation

**Feature Branch**: `[008-invoice-note-validation]`

**Created**: 2026-07-27

**Status**: Draft

**Input**: User description: "cái phần ghi chú ở trang hóa đơn (tạo hóa đơn mới) của manager ý, mình cần validate tầm 1000 ký tự và ở phần giao diện phần ghi chú ghi thêm thông báo ở đấy để người dùng biết nhập là 1000 ký tự nhé"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Validate Note Length (Priority: P1)

Là Ban quản lý (Manager), khi tôi tạo hóa đơn mới, tôi muốn được thông báo về giới hạn ký tự cho trường Ghi chú (Note) và không cho phép nhập quá 1000 ký tự, để đảm bảo dữ liệu ghi chú ngắn gọn và không gây lỗi hệ thống.

**Why this priority**: Đảm bảo trải nghiệm người dùng tốt, tránh lỗi hệ thống khi nhập quá nhiều dữ liệu.

**Independent Test**: Can be fully tested by attempting to enter a note longer than 1000 characters and verifying the UI prevents it and displays the correct instruction.

**Acceptance Scenarios**:

1. **Given** người dùng đang ở trang Tạo hóa đơn mới, **When** người dùng nhìn vào trường Ghi chú, **Then** hệ thống hiển thị thông báo hướng dẫn giới hạn 1000 ký tự (ví dụ: "Tối đa 1000 ký tự").
2. **Given** người dùng nhập vào trường Ghi chú, **When** số lượng ký tự đạt đến 1000, **Then** hệ thống chặn không cho nhập thêm ký tự mới.
3. **Given** người dùng cố tình gửi dữ liệu Ghi chú dài hơn 1000 ký tự, **When** thao tác Tạo hóa đơn được thực hiện, **Then** hệ thống từ chối và báo lỗi độ dài vượt mức.

### Edge Cases

- What happens when người dùng copy/paste một đoạn văn bản dài hơn 1000 ký tự vào trường Ghi chú?
- How does system handle các ký tự đặc biệt (emoji, khoảng trắng, xuống dòng) khi đếm ký tự?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST giới hạn độ dài của trường Ghi chú (Note) trong form tạo hóa đơn tối đa là 1000 ký tự.
- **FR-002**: System MUST hiển thị một thông báo hướng dẫn (hint/helper text) ngay dưới hoặc bên trong trường Ghi chú để thông báo cho người dùng về giới hạn 1000 ký tự.
- **FR-003**: System MUST chặn thao tác tạo hóa đơn nếu độ dài Ghi chú vượt quá 1000 ký tự.

### Key Entities

- **Invoice**: Thực thể hóa đơn chứa thuộc tính `note` (ghi chú).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% hóa đơn mới được tạo ra có trường Ghi chú không vượt quá 1000 ký tự.
- **SC-002**: Thông báo giới hạn ký tự được hiển thị rõ ràng trên giao diện form tạo hóa đơn.

## Assumptions

- Độ dài 1000 ký tự được tính bao gồm cả khoảng trắng và các ký tự xuống dòng.
- Cơ sở dữ liệu hiện tại đã hỗ trợ lưu trữ đủ 1000 ký tự cho trường ghi chú.
