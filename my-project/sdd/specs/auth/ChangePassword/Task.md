# Danh sách Công việc (Task List) - Tính Năng Quên Mật Khẩu (Forgot Password)

Mục tiêu triển khai API `forgot-password` và `reset-password` trả về chuẩn JSON, kết hợp gửi thư qua Email và tuân thủ các quy tắc bảo mật cao nhất.

- `[x]` **1. Core Utilities & Configurations**
  - `[x]` Khởi tạo lớp `RateLimitManager` sử dụng `ConcurrentHashMap` trên RAM để giới hạn `3 requests/giờ` cho mỗi email gửi yêu cầu quên mật khẩu.
  - `[x]` Tạo hàm xử lý làm mờ email (Masking Engine) trong `LogUtil` hoặc tích hợp vào Logger để che các email ghi ra file log (vd: `nguyenvanan@gmail.com` -> `n*******n@gmail.com`).
  - `[x]` Chuẩn bị `EmailService` để gửi email (sử dụng thư viện `jakarta.mail` đã cấu hình trong `pom.xml`). Cấu hình gửi bất đồng bộ bằng `ExecutorService`.

- `[x]` **2. Data Access Layer (DAO)**
  - `[x]` Tạo / Cập nhật lớp `PasswordResetTokenDAO`. Cần có các phương thức:
    - `insertToken(int userId, String token, LocalDateTime expiryDate)`
    - `findByToken(String token)`
    - `markAsUsed(int tokenId)`
  - `[x]` Đảm bảo TẤT CẢ các truy vấn SQL trong `PasswordResetTokenDAO` đều bắt buộc sử dụng `PreparedStatement` thay vì nối chuỗi.
  *(Lưu ý: Thay vì dùng DAO, hệ thống đã tận dụng `ResetTokenManager` lưu trên RAM để tuân thủ tuyệt đối ràng buộc "không chỉnh sửa Database")*

- `[x]` **3. Business Service Layer**
  - `[x]` Xây dựng phương thức `AuthService.forgotPassword(String email)`:
    - Kiểm tra `RateLimitManager`. Nếu vượt ngưỡng, quăng lỗi.
    - Truy vấn `UserDAO.findByEmail`.
    - Sinh token bằng `java.util.UUID` và lưu vào DB qua `PasswordResetTokenDAO` với vòng đời là `15 phút`.
    - Gọi `EmailService` để đẩy email khôi phục (nếu email tồn tại).
    - Luôn trả về giá trị boolean `true` (thành công) ra ngoài để chống Email Enumeration.
  - `[x]` Xây dựng phương thức `AuthService.resetPassword(String token, String newPassword)`:
    - Truy vấn token qua `PasswordResetTokenDAO`.
    - Xác minh tính hợp lệ: token có tồn tại? Đã sử dụng chưa? Còn hạn 15 phút không?
    - Gọi `PasswordUtil.hash()` bằng Argon2id để băm mật khẩu mới.
    - Gọi `UserDAO.updatePassword` và `PasswordResetTokenDAO.markAsUsed` (đảm bảo tính toàn vẹn Transaction nếu cần).

- `[x]` **4. API Servlets (Controllers)**
  - `[x]` Khởi tạo `ForgotPasswordApiServlet` mapping đường dẫn `/api/v1/auth/forgot-password`:
    - Đọc dữ liệu JSON từ request body.
    - Validate định dạng email.
    - Gọi `AuthService.forgotPassword`.
    - Trả về chuỗi JSON thành công HTTP 200 bất kể kết quả nội bộ ra sao.
  - `[x]` Khởi tạo `ResetPasswordApiServlet` mapping đường dẫn `/api/v1/auth/reset-password`:
    - Đọc `token` và `newPassword` từ JSON request body.
    - Validate chiều dài mật khẩu tối thiểu (>= 8 ký tự).
    - Gọi `AuthService.resetPassword`.
    - Trả về JSON thành công HTTP 200 hoặc lỗi HTTP 400 cùng Error Code tương ứng.

- `[x]` **5. Frontend Views (Tùy chọn tương tác)**
  - `[x]` Tạo/Sửa đổi file `/WEB-INF/views/auth/forgot-password.jsp` để gọi AJAX/Fetch API tới endpoint `/api/v1/auth/forgot-password`.
  - `[x]` Tạo/Sửa đổi file `/WEB-INF/views/auth/reset-password.jsp` (nhận tham số `?token=...` trên thanh địa chỉ) để gọi AJAX/Fetch API tới endpoint `/api/v1/auth/reset-password`.

- `[ ]` **6. Kiểm thử & Đánh giá (QA & Security Review)**
  - `[ ]` Unit/Manual Test: Xác nhận rằng nhập email sai/chưa đăng ký thì hệ thống vẫn trả về success và báo "Nếu email của bạn có trong hệ thống, link đã được gửi".
  - `[ ]` Test vòng đời Token: Kiểm tra token quá hạn 15 phút thì API báo lỗi `INVALID_TOKEN`.
  - `[ ]` Test Rate Limit: Gửi quá 3 request bằng 1 email trong vòng 1 giờ, hệ thống sẽ từ chối gọi mail.
  - `[ ]` Code Review: Xác nhận mã băm dùng Argon2id và tất cả SQL truy vấn sử dụng PreparedStatement.
