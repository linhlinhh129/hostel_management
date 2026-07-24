# Implementation Plan: Login Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `LoginServlet.java`, `UserDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Cô lập DB, sử dụng Mock để giả lập các cờ trạng thái như `force_change_pass` và bộ đếm lỗi `status = LOCKED`.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/auth/LoginServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_LoginNormal_Success`: Đăng nhập bằng tài khoản `ACTIVE`, `force_change_pass = 0` -> Chuyển hướng Dashboard.
- `testDoPost_LoginFirstTime_ForceChangePassword`: Đăng nhập bằng tài khoản `ACTIVE`, `force_change_pass = 1` -> Chuyển hướng trang `/auth/force-change-password`.

### 3.2 Error Cases
- `testDoPost_InvalidCredentials`: Sai username hoặc password -> Báo lỗi, cộng dồn đếm số lần sai.
- `testDoPost_AccountLocked_Rejected`: Tài khoản `status = LOCKED` -> Chặn đăng nhập ngay từ đầu.
- `testDoPost_BruteForce_LockAccount`: Cố tình nhập sai mật khẩu vượt quá 5 lần -> Kích hoạt lệnh `UPDATE` lock DB.

### 3.3 Boundary Values
- `testBruteForce_Exactly5thAttempt`: Nhập sai lần 4 (chưa khóa), nhập sai lần 5 (chính thức khóa).
- `testEmptyInputs`: Input rỗng -> Chặn Validation.

### 3.4 Concurrent Scenarios
- `testConcurrency_BruteForceLocking`: Bắn 50 request nhập sai mật khẩu đồng thời. Xác minh rằng hệ thống bắt được lệnh khóa (Thread-safe) mà không bị ghi đè biến đếm.

## 4. Các bước thực hiện
1. Thiết lập `LoginServletTest` với Mockito (`@Mock UserDAO`, `HttpSession`).
2. Viết các test method.
