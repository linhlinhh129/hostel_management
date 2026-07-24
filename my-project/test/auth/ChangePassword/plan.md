# Implementation Plan: Change Password Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ChangePasswordServlet.java`, `UserService`
- **Constraint**: Đảm bảo 100% Unit Test. Cô lập hệ thống CSDL và các hàm băm BCrypt thông qua Mock Service.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/auth/ChangePasswordServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_ChangePassword_Success`: Mật khẩu cũ đúng, mật khẩu mới đúng 7 luật. Cập nhật thành công.

### 3.2 Error Cases
- `testDoPost_IncorrectOldPassword`: Nhập sai mật khẩu cũ -> Trả về giao diện lỗi.
- `testDoPost_PolicyViolation`: Mật khẩu mới quá ngắn, thiếu ký tự hoa/đặc biệt -> Lỗi Validation.
- `testAuth_NoSession_RedirectsToLogin`: Phiên đăng nhập hết hạn.

### 3.3 Boundary Values
- `testDoPost_EmptyOrWhitespacePasswords`: Các trường bị xóa trắng.
- `testDoPost_ExtremelyLongPassword`: Mật khẩu dài > 1000 ký tự.

### 3.4 Concurrent Scenarios
- `testConcurrency_ChangePassword`: Gửi 50 request cập nhật mật khẩu đồng thời.

## 4. Các bước thực hiện
1. Thiết lập `ChangePasswordServletTest` với `@Mock UserService`.
2. Tạo các test cases theo phân loại.
