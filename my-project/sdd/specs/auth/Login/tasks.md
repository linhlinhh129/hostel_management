# Danh sách công việc (Tasks) - Đăng Nhập (Login)

## Phase 1: Kiến trúc cốt lõi & Tiện ích (Hoàn thành)
- [x] T001 [US1] Tạo/Cập nhật `PasswordUtil` sử dụng thư viện `jbcrypt` để kiểm tra hàm băm mật khẩu.
- [x] T002 [US1] Tạo lớp `LoginAttemptTracker` sử dụng `ConcurrentHashMap` để quản lý số lần đăng nhập sai của người dùng trực tiếp trên RAM.
- [x] T003 [US1] Khởi tạo `UserSessionDTO` lưu thông tin người dùng cho Session (cần có các trường cơ bản như `userId`, `role` và cờ `firstLogin`).

## Phase 2: Giao tiếp Cơ sở dữ liệu (Hoàn thành)
- [x] T004 [US1] Viết phương thức `UserDAO.findByUsername(String username)`: Truy vấn dữ liệu tài khoản bằng PreparedStatement.
- [x] T005 [US1] Viết phương thức `UserDAO.updateStatus(int userId, String status)`: Thực thi lệnh `UPDATE` trạng thái tài khoản.

## Phase 3: Xử lý Nghiệp vụ & Controllers (Hoàn thành)
- [x] T006 [US1] Xây dựng `UserService.authenticate(String username, String password)` xử lý logic xác thực chính.
- [x] T007 [US1] Thêm logic tăng biến đếm trong `LoginAttemptTracker` nếu nhập sai mật khẩu. Gọi `UserDAO.updateStatus` khóa tài khoản nếu số lần nhập sai >= 5. Reset biến đếm nếu đăng nhập thành công.
- [x] T008 [US1] Khởi tạo `LoginServlet` cho đường dẫn `/login` (Cài đặt `doGet` và `doPost`).
- [x] T009 [US1] Logic điều hướng: Nếu thành công, thiết lập session `currentUser` và Redirect về `/first-login` hoặc Dashboard. 
- [x] T010 [US1] Xử lý ngoại lệ: Trả về attribute `errorMessage` cho trang JSP tương ứng.

## Phase 4: Giao diện & Bảo mật (Hoàn thành)
- [x] T011 [US1] Frontend: Phát triển giao diện `login.jsp` theo UI/UX guideline, tích hợp JSTL `c:if` hiển thị lỗi.
- [x] T012 [US1] Backend: Tạo/Bổ sung `AuthFilter` để ép buộc Redirect về `/first-login` đối với người dùng đăng nhập lần đầu.

## Phase 5: Nâng cấp Bảo mật Cache (Mới cập nhật)
- [x] T013 [US1] Backend: Bổ sung các cấu hình `Cache-Control`, `Pragma`, `Expires` vào `AuthFilter.java` để ngăn trình duyệt lưu cache đối với các trang đã đăng nhập, xử lý lỗi Back button sau khi đăng xuất.
