# Kế hoạch Triển khai (Implementation Plan) - Tính Năng Quên Mật Khẩu (Forgot Password)

## 1. Mục tiêu và Phạm vi
Triển khai tính năng Quên mật khẩu và Đặt lại mật khẩu dựa trên tài liệu đặc tả (Spec) trong thư mục `ChangePassword`. Tính năng cung cấp luồng self-service cho người dùng lấy lại tài khoản thông qua Email, bảo vệ hệ thống khỏi các rủi ro Enumeration Attack và Spam/DDoS.

## 2. Phân tích Xung đột Đặc tả & Đề xuất Giải quyết
Trong quá trình đối chiếu giữa `Context.md` của tính năng này và các tài liệu kiến trúc lõi (`AGENTS.md`, `CLAUDE.md`, `safety.md`), phát hiện các điểm xung đột nghiêm trọng cần tuân thủ theo nguyên tắc cao nhất:

> [!WARNING]
> **Xung đột 1: Sử dụng PreparedStatement vs Nối chuỗi SQL (SQL Injection)**
> - *Context.md* yêu cầu: "Không dùng cơ chế tham số hóa PreparedStatements, phải tự chế hàm sanitize chuỗi."
> - *AGENTS.md & business.md* (Quy tắc lõi): BẮT BUỘC dùng `PreparedStatement`, TUYỆT ĐỐI KHÔNG nối chuỗi SQL.
> - **Quyết định (Đề xuất):** Phải tuân thủ `AGENTS.md` bằng cách sử dụng `PreparedStatement` ở mọi truy vấn CSDL để đảm bảo an toàn tuyệt đối chống SQL Injection mà không cần tự phát minh hàm sanitize chuỗi kém an toàn.

> [!NOTE]
> **Xung đột 2: Giao tiếp API trả về JSON vs SSR (Server-Side Rendering)**
> - *Spec.md* định nghĩa endpoint `/api/v1/auth/...` trả về dữ liệu chuẩn JSON.
> - *CLAUDE.md* đề cập đến luồng render HTML form cho chức năng này.
> - **Quyết định (Đề xuất):** Sẽ thiết kế đúng theo Spec.md: tạo 2 Servlet đóng vai trò API `/api/v1/auth/forgot-password` và `/api/v1/auth/reset-password`, trả về JSON. Frontend sẽ dùng AJAX/Fetch API từ các trang JSP tĩnh để gọi lên server.

## 3. Thiết kế Các Thành phần (Architecture Components)

### 3.1. API Servlets
- **`ForgotPasswordApiServlet`** (`/api/v1/auth/forgot-password`):
  - Tiếp nhận `email` (JSON body).
  - Trả về thông báo thành công chung chung cho dù email có tồn tại hay không (Ngăn chặn Email Enumeration).
  - Tích hợp cơ chế Rate Limit.
- **`ResetPasswordApiServlet`** (`/api/v1/auth/reset-password`):
  - Tiếp nhận `token` và `newPassword` (JSON body).
  - Validate token (sự tồn tại, thời hạn 15 phút, trạng thái chưa sử dụng).
  - Trả về JSON thông báo lỗi hoặc thành công.

### 3.2. Service & Business Logic
- **`AuthService.forgotPassword(email)`**:
  - Kiểm tra `RateLimitManager` (không quá 3 lần/giờ/email).
  - Kiểm tra `UserDAO` xem email tồn tại không (nếu không, dừng xử lý nội bộ nhưng API vẫn trả success).
  - Sinh chuỗi ngẫu nhiên (UUID hoặc SecureRandom) làm `RecoveryToken`.
  - Lưu token xuống DB thông qua `PasswordResetTokenDAO` với thời hạn là thời gian hiện tại + 15 phút.
  - Giao việc gửi Email cho `EmailService` / `ExecutorService` để không block API chính.
- **`AuthService.resetPassword(token, newPassword)`**:
  - Tra cứu token bằng `PasswordResetTokenDAO`.
  - Nếu token lỗi / hết hạn: Quăng Exception -> Servlet trả HTTP 400.
  - Nếu hợp lệ: Băm `newPassword` bằng `Argon2id` -> Gọi `UserDAO.updatePassword()` -> `PasswordResetTokenDAO.markAsUsed()`.

### 3.3. Tầng Data Access (DAO)
- **`PasswordResetTokenDAO`**:
  - `createToken(userId, token, expiresAt)`
  - `findByToken(token)`
  - `markAsUsed(tokenId)`
- **`UserDAO`**:
  - Sử dụng hàm `findByEmail` và `updatePassword` hiện có. BẮT BUỘC dùng PreparedStatement.

### 3.4. Utilities (Tiện ích)
- **`RateLimitManager`**: Quản lý tần suất request/email bằng `ConcurrentHashMap` trên RAM, mỗi entry có cấu trúc đếm số lần và thời gian reset (1 giờ).
- **`EmailService`**: Chịu trách nhiệm render template gửi email chứa đường link `https://domain/reset-password?token=...`.

## 4. Bảo mật & An toàn Cơ sở dữ liệu
- Tuân thủ `safety.md`, hệ thống không tự sinh lệnh `CREATE TABLE` tự động. Giả định bảng lưu token (ví dụ: `password_reset_tokens`) đã được tạo sẵn trong `schema.sql`. (Nếu chưa có, cần thông báo yêu cầu con người duyệt script DB).
- Băm mật khẩu luôn dùng `Argon2id` (chuyển qua thư viện `argon2-jvm` đã cấu hình ở chức năng Login).
- Tránh lộ thông tin nhạy cảm: Tuyệt đối không in Token hoặc Email tường minh trong log lỗi hệ thống (Cần dùng mask: `n*****n@gmail.com`).
