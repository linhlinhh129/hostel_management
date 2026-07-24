# Implementation Plan: Tính Năng Đăng Nhập (Login)

## 1. Mục tiêu và Phạm vi
Hiện thực hóa chức năng Đăng nhập cho hệ thống quản lý nhà trọ, làm chốt chặn bảo mật đầu tiên, tuân thủ nghiêm ngặt các quy định về an toàn thông tin theo chuẩn thiết kế dự án (Argon2id, Session-based authentication, cấm dùng JWT, cấm thay đổi cấu trúc DB).

## 2. Kiến trúc và Ràng buộc Nghiệp vụ
* **Thuật toán băm mật khẩu:** Sử dụng `BCrypt` để băm mật khẩu.
* **Quản lý phiên (Session):** Sử dụng `HttpSession` với `JSESSIONID`, cấu hình `HttpOnly` và `Secure`.
* **Phân quyền (AuthFilter):** Cần Filter để chặn các request trái phép và ép buộc điều hướng nếu chưa đăng nhập hoặc đang ở trạng thái cần đổi mật khẩu.
* **Cơ chế Khóa (Lockout):** Quản lý đếm số lần sai trên RAM (bằng `LoginAttemptTracker`). Nếu nhập sai 5 lần liên tiếp, cập nhật trạng thái (`status = 'LOCKED'`) trực tiếp xuống CSDL và báo lỗi yêu cầu liên hệ Admin.
* **Cơ sở dữ liệu:** Chỉ sử dụng câu lệnh `SELECT` (tìm kiếm tài khoản) và `UPDATE` (đổi trạng thái). **TUYỆT ĐỐI KHÔNG** sửa đổi cấu trúc bảng, xóa dòng.

## 3. Các thành phần cần phát triển

### 3.1 Backend (Tầng Servlet, Service, DAO)
1. **LoginServlet** (`controller.auth.LoginServlet`):
   - `GET`: Forward đến giao diện `login.jsp`.
   - `POST`: Tiếp nhận `username`, `password`, gọi tầng Service xác thực. 
   - Quản lý điều hướng:
     - Đăng nhập lần đầu (cờ `force_change_pass = true`): Lưu vào session kèm cờ `firstLogin = true` -> Redirect HTTP 302 về `/first-login`.
     - Đăng nhập bình thường: Lưu session -> Redirect về trang tổng quan tương ứng với Role.
     - Lỗi: Forward lại về `login.jsp` kèm biến `errorMessage`.
2. **UserService** / **UserServiceImpl**:
   - Gọi `UserDAO.findByUsername` để lấy thực thể người dùng.
   - Nếu user ở trạng thái `LOCKED`, trả về lỗi bị khóa.
   - Sử dụng `PasswordUtil` để kiểm tra độ khớp mật khẩu (BCrypt).
   - Tích hợp `LoginAttemptTracker` để tăng/xóa bộ đếm số lần sai mật khẩu theo username. Nếu biến đếm chạm mốc 5, gọi `UserDAO.updateStatus(userId, "LOCKED")`.
3. **UserDAO**:
   - `findByUsername(String username)`: Truy vấn tài khoản thông qua PreparedStatement.
   - `updateStatus(int userId, String status)`: Cập nhật trường trạng thái tài khoản thông qua PreparedStatement.
4. **LoginAttemptTracker** (Tiện ích trên RAM):
   - Lớp Singleton có chứa `ConcurrentHashMap<String, Integer>` để đếm số lần đăng nhập sai theo username.
5. **PasswordUtil**:
   - Sử dụng thư viện `jbcrypt` cung cấp hàm `verify` mã băm BCrypt.
6. **AuthFilter**:
   - Kiểm tra request gửi tới có được xác thực trong `HttpSession` chưa.
   - Kiểm tra nếu `currentUser` có cờ `firstLogin == true` thì điều hướng tất cả truy cập (ngoại trừ các endpoint cho việc đổi mật khẩu, đăng xuất) về trang bắt buộc đổi mật khẩu.

### 3.2 Frontend (JSP)
* **/WEB-INF/views/auth/login.jsp**:
  - Giao diện form đăng nhập tuân thủ Bootstrap 5.
  - Bắt lỗi và hiển thị `errorMessage` thông qua JSTL `<c:if test="${not empty errorMessage}">`.

## 4. Giao tiếp Hệ thống
* **Endpoint:** `POST /login`
* **Request:** `application/x-www-form-urlencoded` (`username`, `password`).
* **Response Thành công:** Redirect 302 tới Dashboard hoặc trang `/first-login`.
* **Response Thất bại:** Forward về `login.jsp` với message lỗi "Sai tên đăng nhập hoặc mật khẩu" hoặc "Tài khoản của bạn đã bị khóa do nhập sai mật khẩu quá 5 lần. Vui lòng liên hệ Admin."
