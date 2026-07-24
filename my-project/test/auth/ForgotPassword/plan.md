# Implementation Plan: Forgot Password Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ForgotPasswordServlet.java`, `ResetPasswordServlet.java`, `AuthService`, `SessionRegistry`
- **Constraint**: Đảm bảo 100% Unit Test. Các logic sinh Token, gửi Email, và Hủy phiên (Session) đều phải được Mock toàn diện.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/auth/ForgotPasswordServletTest.java`
- `src/test/java/com/quanlyphongtro/controller/auth/ResetPasswordServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_SendLink_Success`: Gửi email khôi phục thành công.
- `testDoGet_ResetForm_ValidToken`: Token hợp lệ thì mở form Reset.
- `testDoPost_ResetPassword_Success`: Đặt lại mật khẩu thành công, xác minh có gọi `SessionRegistry` để xóa phiên.

### 3.2 Error Cases
- `testAntiEnumeration_InvalidEmail`: Nhập email rác vẫn báo thành công (nhưng Mock logic gửi mail không được chạy).
- `testDoPost_InvalidOrExpiredToken`: Reset với Token hết hạn -> Báo lỗi.
- `testDoPost_PasswordMismatch`: Nhập `newPassword` và `confirmPassword` lệch nhau.

### 3.3 Boundary Values
- `testTokenBoundary_Exactly15Mins`: Test Token ở mốc thời gian vừa tròn 15 phút.
- `testEmptyEmailOrToken`: Xử lý đầu vào rỗng mượt mà.

### 3.4 Concurrent Scenarios
- `testConcurrency_SessionRevocation`: Test độ an toàn Thread-safe khi `SessionRegistry` cố gắng invalidate 100 sessions cùng lúc.

## 4. Các bước thực hiện
1. Setup hai tệp Test cho hai Servlet `Forgot` và `Reset`.
2. Map đầy đủ theo cấu trúc EARS.
