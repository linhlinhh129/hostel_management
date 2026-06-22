# Danh sách công việc (Task) - Tính năng Profile & Đổi mật khẩu

- `[x]` **1. Backend - Data Access (DAO)**
  - `[x]` Thêm phương thức `updateProfile(User user)` vào `UserDAO.java` sử dụng `PreparedStatement`.

- `[x]` **2. Backend - Controller (Servlet)**
  - `[x]` Tạo `ProfileServlet.java` kế thừa `BaseServlet`.
  - `[x]` Xử lý hàm `doGet`: Load dữ liệu user bằng `UserDAO.findById(currentUser.getId())` và map ra JSP.
  - `[x]` Xử lý hàm `doPost` nhánh `action=update_profile`: Upload ảnh (Multipart), validate và update DB, sau đó update lại `UserSessionDTO`.
  - `[x]` Xử lý hàm `doPost` nhánh `action=change_password`: Validate old password, hash new password bằng BCrypt, update DB và clear flag `force_change_pass`.

- `[x]` **3. Frontend - Giao diện (JSP)**
  - `[x]` Tạo file `views/common/profile.jsp` sử dụng cấu trúc `app-shell` và chuẩn giao diện Mintlify.
  - `[x]` Tạo Form 1: Upload Avatar và chỉnh sửa thông tin (Tên, SDT, CCCD, Ngày sinh, Giới tính, Địa chỉ).
  - `[x]` Tạo Form 2: Nhập mật khẩu cũ, mật khẩu mới, xác nhận. Có validate client-side cơ bản.
  - `[x]` Cập nhật `topbar.jsp` hoặc `sidebar.jsp` để thêm link dẫn tới `/profile`.

- `[x]` **4. Tích hợp & Kiểm thử**
  - `[x]` Test luồng cập nhật thông tin thành công và báo lỗi nếu có.
  - `[x]` Test luồng đổi mật khẩu (Mật khẩu cũ sai, Confirm password không khớp).
  - `[x]` Đảm bảo Avatar được lưu trữ và hiển thị đúng chuẩn.
