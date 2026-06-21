# Kế hoạch Hiện thực hóa (Implementation Plan) - Chức năng Đăng nhập

## 1. Phân tích hiện trạng
- Tài liệu `Spec.md` và `Context.md` đã được cập nhật để bám sát thiết kế Database (Khóa cứng tài khoản khi sai 5 lần) và kiến trúc JSP/Servlet Session hiện tại của project.
- **Lỗi UX Khóa tài khoản**: Hiện tại khi tài khoản bị khóa cứng dưới DB (`status = 'LOCKED'`), hệ thống chỉ trả về rỗng và màn hình hiện "Sai tên đăng nhập hoặc mật khẩu", gây khó hiểu cho người dùng.
- **Lỗi Bắt buộc đổi mật khẩu**: `UserServiceImpl` đã map cờ `firstLogin` từ `force_change_pass`, nhưng `LoginServlet` chưa xử lý luồng điều hướng sang trang đổi mật khẩu.

## 2. Các công việc cần thực hiện
1. **Tinh chỉnh Trải nghiệm Khóa tài khoản:** Sửa lại `UserServiceImpl` và `LoginServlet` để nếu tài khoản đã bị khóa trong DB, màn hình phải hiển thị đúng dòng chữ "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin".
2. **Xử lý luồng Bắt buộc đổi mật khẩu (First Login):** 
   - Thêm logic kiểm tra `user.isFirstLogin()` trong `LoginServlet.java`.
   - Cập nhật `AuthFilter` chặn người dùng điều hướng đi nơi khác.
   - Nếu `true`, điều hướng người dùng đến trang `/profile?forceChange=true` thay vì vào thẳng Dashboard.
