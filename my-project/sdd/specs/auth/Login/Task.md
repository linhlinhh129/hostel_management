# Task List: Tính Năng Đăng Nhập (Login)

Danh sách công việc chi tiết để triển khai tính năng Đăng nhập. Tuân thủ nghiêm ngặt nguyên tắc chỉ truy vấn và cập nhật trên các bảng/trường đã có sẵn.

- `[x]` **1. Backend: Cấu hình và Utilities**
  - `[x]` Tạo/Cập nhật `PasswordUtil` sử dụng thư viện `jbcrypt` để kiểm tra hàm băm mật khẩu.
  - `[x]` Tạo lớp `LoginAttemptTracker` sử dụng `ConcurrentHashMap` để quản lý số lần đăng nhập sai của người dùng trực tiếp trên RAM.
  - `[x]` Khởi tạo `UserSessionDTO` lưu thông tin người dùng cho Session (cần có các trường cơ bản như `userId`, `role` và cờ `firstLogin`).

- `[x]` **2. Backend: Tầng DAO (Data Access Object)**
  - `[x]` Viết phương thức `UserDAO.findByUsername(String username)`: Truy vấn dữ liệu tài khoản bằng PreparedStatement.
  - `[x]` Viết phương thức `UserDAO.updateStatus(int userId, String status)`: Thực thi lệnh `UPDATE` trạng thái tài khoản (ví dụ chuyển thành `LOCKED`).

- `[x]` **3. Backend: Tầng Service**
  - `[x]` Xây dựng `UserService.authenticate(String username, String password)` xử lý logic xác thực chính.
  - `[x]` Thêm logic tăng biến đếm trong `LoginAttemptTracker` nếu nhập sai mật khẩu.
  - `[x]` Gọi `UserDAO.updateStatus` khóa tài khoản nếu số lần nhập sai >= 5. Reset biến đếm nếu đăng nhập thành công.

- `[x]` **4. Backend: Tầng Servlet (Controller)**
  - `[x]` Khởi tạo `LoginServlet` cho đường dẫn `/login`.
  - `[x]` Cài đặt `doGet`: Forward request về giao diện `/WEB-INF/views/auth/login.jsp`.
  - `[x]` Cài đặt `doPost`: Gọi `UserService.authenticate()`.
  - `[x]` Logic điều hướng: Nếu thành công, thiết lập session `currentUser` và Redirect (302) về `/first-login` nếu người dùng phải đổi mật khẩu lần đầu, hoặc về Dashboard theo Role. 
  - `[x]` Xử lý ngoại lệ: Trả về attribute `errorMessage` cho trang JSP tương ứng với các tình huống (sai mật khẩu, tài khoản bị khóa).

- `[x]` **5. Backend: Tầng Filter**
  - `[x]` Tạo/Bổ sung `AuthFilter` để đảm bảo an ninh: Nếu tài khoản có cờ `firstLogin = true` nằm trong session, ép buộc Redirect mọi request về màn hình `/first-login`.

- `[x]` **6. Frontend: Tầng Giao diện JSP**
  - `[x]` Phát triển giao diện `login.jsp` theo UI/UX guideline (Bootstrap 5).
  - `[x]` Tích hợp JSTL `c:if` để kiểm tra và hiển thị các biến lỗi `errorMessage` trả về từ Servlet.

- `[x]` **7. Kiểm thử (Testing & QA)**
  - `[ ]` (Happy Path) Kiểm tra đăng nhập với tài khoản hợp lệ, xác minh tính năng khởi tạo session thành công.
  - `[ ]` (Edge Case) Kiểm tra đăng nhập lần đầu với mật khẩu tạm thời -> Bị ép chuyển hướng bắt buộc đổi mật khẩu.
  - `[ ]` (Edge Case) Kiểm tra nhập sai mật khẩu >= 5 lần -> Hiển thị cảnh báo khóa tài khoản, trạng thái DB chuyển thành `LOCKED`.
  - `[ ]` Kiểm tra truy cập các trang nội bộ khi bị dính cờ `firstLogin = true` -> Bị văng ngược lại trang đổi mật khẩu.
