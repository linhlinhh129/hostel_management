# Đặc tả: Chống trùng lặp Mật khẩu

**Feature Branch**: `[006-title-auth-password-duplicate]`
**Created**: 2026-06-30
**Status**: Draft

**Input**: Chống trùng lặp (Cơ bản): Mật khẩu mới không được giống Mật khẩu hiện tại. Nếu giống, báo lỗi: "Mật khẩu mới không được trùng với mật khẩu cũ". (Lưu ý: Không áp dụng thời gian chờ, đổi xong có thể thao tác đổi ngược lại ngay lập tức).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ngăn chặn đổi mật khẩu trùng lặp (Priority: P1)

Là một Người dùng, khi tôi đổi mật khẩu (hoặc đổi mật khẩu trong lần đăng nhập đầu tiên), tôi không thể đặt mật khẩu mới giống hệt mật khẩu hiện tại. Hệ thống phải cảnh báo tôi.

**Why this priority**: Bảo mật cơ bản, giúp người dùng không lãng phí thao tác khi cố đặt lại mật khẩu như cũ.

**Independent Test**: Đăng nhập, vào Profile -> Đổi mật khẩu. Nhập mật khẩu hiện tại vào ô "Mật khẩu hiện tại", và nhập lại đúng mật khẩu đó vào ô "Mật khẩu mới". Submit form. Phải thấy thông báo lỗi "Mật khẩu mới không được trùng với mật khẩu cũ" và mật khẩu không bị thay đổi.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST so sánh mật khẩu mới với mật khẩu hiện tại tại các chức năng: Profile (Đổi mật khẩu) và FirstLogin (Đổi mật khẩu lần đầu).
- **FR-002**: Nếu `newPassword.equals(currentPassword)` trả về true, hệ thống MUST ngăn chặn hành động và redirect kèm tham số lỗi (VD: `error=password_duplicate`).
- **FR-003**: Giao diện (JSP) MUST bắt tham số lỗi này và hiển thị thông báo "Mật khẩu mới không được trùng với mật khẩu cũ" rõ ràng cho người dùng.
- **FR-004**: Không có yêu cầu cấm sử dụng lại mật khẩu cũ trong quá khứ (không cần lưu lịch sử password), chỉ cần so sánh trực tiếp ở bước thay đổi hiện tại.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Các trường hợp submit đổi mật khẩu giống hệt mật khẩu cũ đều bị chặn. Mật khẩu mới hợp lệ được đổi thành công mà không gặp trở ngại.
