# Kế hoạch Hiện thực hóa (Implementation Plan) - Chức năng Quên Mật Khẩu

## 1. Phân tích hiện trạng & Kiến trúc
- **Database (`UserDAO`)**: Cần đảm bảo có thể truy vấn `findByEmail(String email)` để lấy tài khoản.
- **Kiến trúc hệ thống**: Xây dựng luồng JSP/Servlet truyền thống.
- **Dịch vụ Email**: Tạm thời tạo `EmailService.java` dùng log ra console để in đường dẫn khôi phục mật khẩu.
- **Token**: Tạo `ResetTokenManager.java` (quản lý token trên bộ nhớ đệm `ConcurrentHashMap` với thời gian sống 15 phút).

## 2. Các công việc cần thực hiện

### Giai đoạn 1: Chuẩn bị Tiện ích & DAO
1. **DAO**: Bổ sung hàm `findByEmail(String email)` vào `UserDAO.java`.
2. **Utils**: Tạo `ResetTokenManager.java` (lưu UUID token theo User ID kèm thời gian sống 15 phút).
3. **Services**: Tạo `EmailService.java` giả lập gửi Email (in console).

### Giai đoạn 2: Xây dựng Controller & JSP
1. `ForgotPasswordServlet` (`/forgot-password`): 
   - View: `forgot-password.jsp`. Form nhận email.
   - Post: Tìm user, sinh token, gửi qua `EmailService`. Luôn báo "Đã gửi".
2. `ResetPasswordServlet` (`/reset-password`): 
   - View: `reset-password.jsp`. Form nhập mật khẩu mới (yêu cầu có `?token=...`).
   - Post: Check token, update password, invalidate token.

### Giai đoạn 3: Thu hồi phiên đăng nhập
- Sử dụng `SessionRegistry` (kết hợp `UserSessionListener`) để hủy toàn bộ các `HttpSession` đang gắn với tài khoản vừa đổi mật khẩu thành công.
