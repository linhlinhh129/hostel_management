# Kế hoạch phát triển tính năng: User Profile & Change Password

Tính năng này dành cho người dùng đã đăng nhập thành công vào hệ thống. Kế hoạch tuân thủ nghiêm ngặt yêu cầu: **Tuyệt đối không sửa đổi CSDL, chỉ truy vấn và update các trường (column) có sẵn trong bảng `users`.**

## 1. Phân tích Dữ liệu hiện có (Database)
Dựa vào class `User.java` và `UserDAO.java`, bảng `users` đã có sẵn các trường:
* **Thông tin cá nhân:** `full_name`, `email` (readonly), `phone`, `avatar_url`, `identity_number`, `dob`, `gender`, `permanent_address`.
* **Thông tin tài khoản:** `username` (readonly), `role`, `status`.
* **Đổi mật khẩu:** `password_hash`, `force_change_pass`.

## 2. Luồng xử lý (Backend)
- **Cập nhật `UserDAO.java`:** Thêm phương thức `updateProfile(User user)` sử dụng `PreparedStatement` để cập nhật các trường: `full_name`, `phone`, `avatar_url`, `identity_number`, `dob`, `gender`, `permanent_address`.
- **Tạo `ProfileServlet.java` (`@WebServlet("/profile")`):**
  - `GET`: Lấy thông tin chi tiết từ `UserDAO.findById(session.userId)` và trả về giao diện.
  - `POST`: Nhận tham số `action` để chia làm 2 nhánh xử lý:
    1. `action=update_profile`: Xử lý upload ảnh đại diện (nếu có), validate thông tin, lưu xuống database qua `UserDAO.updateProfile`. Đồng thời cập nhật lại thông tin hiển thị (Avatar, Fullname) trong `UserSessionDTO` của Session.
    2. `action=change_password`: Xác thực mật khẩu cũ (BCrypt), mã hóa mật khẩu mới (BCrypt) và lưu xuống qua `UserDAO.updatePassword`.

## 3. Thiết kế Giao diện (Frontend)
- **Tạo `views/common/profile.jsp`:**
  - Kế thừa layout chuẩn (Mintlify) với `app-shell`.
  - Chia giao diện thành 2 phần (hoặc 2 Tabs / 2 Cards độc lập):
    - **Card 1: Chỉnh sửa hồ sơ:** Hiển thị và cho phép upload Avatar, nhập các trường thông tin cá nhân.
    - **Card 2: Đổi mật khẩu:** Yêu cầu nhập "Mật khẩu hiện tại", "Mật khẩu mới" và "Xác nhận mật khẩu".
- **Gắn Link Điều hướng:**
  - Sửa link avatar / Tên người dùng ở `topbar.jsp` hoặc link "Hồ sơ" ở `sidebar.jsp` để trỏ về `/profile`.

Bạn hãy review kỹ Plan này, nếu đồng ý mình sẽ bắt đầu viết code!
