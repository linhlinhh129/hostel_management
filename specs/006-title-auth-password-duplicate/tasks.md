# Implementation Tasks: Chống trùng lặp Mật khẩu

**Feature Branch**: `[006-title-auth-password-duplicate]`
**Created**: 2026-06-30

## Phase 1: Logic Back-end (Java)

- [x] **Task 1.1**: Cập nhật `ProfileServlet.java` (`src/main/java/com/quanlyphongtro/controller/auth/ProfileServlet.java`).
  - [x] Thêm điều kiện kiểm tra `if (newPassword.equals(currentPassword))` trong logic `"change_password"`.
  - [x] Redirect về `/profile?error=password_duplicate` nếu trùng lặp.
- [x] **Task 1.2**: Cập nhật `FirstLoginServlet.java` (`src/main/java/com/quanlyphongtro/controller/auth/FirstLoginServlet.java`).
  - [x] Thêm điều kiện kiểm tra `PasswordUtil.verify(newPassword, user.getPasswordHash())` trong logic xử lý POST.
  - [x] Redirect về `/first-login?error=password_duplicate` nếu trùng lặp.

## Phase 2: Giao diện Front-end (JSP)

- [x] **Task 2.1**: Cập nhật file `profile.jsp` (`src/main/webapp/WEB-INF/views/common/profile.jsp`).
  - [x] Bổ sung khối `<c:when test="${param.error == 'password_duplicate'}">` hiển thị thông báo lỗi "Mật khẩu mới không được trùng với mật khẩu cũ".
- [x] **Task 2.2**: Cập nhật file `first-login.jsp` (`src/main/webapp/WEB-INF/views/auth/first-login.jsp`).
  - [x] Tương tự, bổ sung khối hiển thị thông báo lỗi `password_duplicate`.

## Phase 3: Kiểm thử & Xác nhận (Manual Verification)

- [x] **Task 3.1**: Kiểm thử tính năng đổi mật khẩu (Profile).
  - [x] Đăng nhập, nhập Mật khẩu mới trùng với Mật khẩu cũ và xác nhận bị chặn.
  - [x] Nhập mật khẩu mới khác mật khẩu cũ và xác nhận đổi thành công.
- [x] **Task 3.2**: Kiểm thử tính năng First Login.
  - [x] Giả lập một tài khoản đăng nhập lần đầu, đổi mật khẩu trùng với mật khẩu hiện tại trong DB. Xác nhận bị chặn.
