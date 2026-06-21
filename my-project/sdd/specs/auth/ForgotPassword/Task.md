# Danh sách công việc (Tasks) - Chức năng Quên Mật Khẩu

## 1. Tầng Tiện ích & Core
- [x] Bổ sung `Optional<User> findByEmail(String email)` vào `UserDAO.java`.
- [x] Xây dựng `ResetTokenManager.java` (Quản lý Token đổi mật khẩu, TTL 15 phút).
- [x] Tạo `EmailService.java` (Sử dụng Mock log ra console để test trước).
- [x] Thiết lập `UserSessionListener` và `SessionRegistry` để phục vụ việc thu hồi phiên đăng nhập khi đổi mật khẩu xong.

## 2. Tầng Controller (Các Servlet)
- [x] Tạo `ForgotPasswordServlet` (`/forgot-password`): Nhận email, sinh Token, tra cứu user và gửi link chứa token qua `EmailService`.
- [x] Tạo `ResetPasswordServlet` (`/reset-password`): Xác minh token từ URL, cập nhật mật khẩu mới, hủy token và thu hồi các phiên đăng nhập khác.

## 3. Tầng View (Giao diện JSP)
- [x] Cập nhật file `/WEB-INF/views/auth/forgot-password.jsp` (Giao diện nhập Email).
- [x] Cập nhật file `/WEB-INF/views/auth/reset-password.jsp` (Giao diện nhập mật khẩu mới).
