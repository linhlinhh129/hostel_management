# Danh sách công việc (Tasks) - Chức năng Đăng nhập

## 1. Tầng Service & Logic
- [x] Chỉnh sửa `UserServiceImpl.java`: Nếu tài khoản có `status = LOCKED`, ném ra exception `ForbiddenException` hoặc trả về mã lỗi cụ thể (thay vì chỉ trả về Optional.empty()) để phân biệt với lỗi sai mật khẩu.

## 2. Tầng Controller
- [x] Cập nhật `LoginServlet.java`: Bắt lỗi `ForbiddenException` khi tài khoản bị khóa vĩnh viễn và hiển thị "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin".
- [x] Xử lý ngoại lệ bắt buộc đổi mật khẩu (`firstLogin = true`). Thay vì chuyển đến Dashboard, chuyển hướng đến trang `/profile?forceChange=true`.
- [x] Cập nhật `AuthFilter.java` để ngăn người dùng dùng URL thủ công lách qua trang thay đổi mật khẩu.
