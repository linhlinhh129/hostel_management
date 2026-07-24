# Test Specification: Đăng nhập (Login - Unit Test Only)

**Status:** Draft
**Target Feature:** Đăng nhập (`my-project/sdd/specs/auth/Login`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic xử lý tại lớp `LoginServlet`. Tập trung vào các kịch bản bắt buộc đổi mật khẩu lần đầu (`force_change_pass`), bảo mật chống vét cạn (Brute-force) qua cơ chế khóa tài khoản, và đảm bảo an toàn trạng thái Session.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Đăng nhập bình thường**: KHI người dùng Submit POST `username` và `password` đúng, tài khoản KHÔNG bị khóa (`status = ACTIVE`), VÀ `force_change_pass = 0`, HỆ THỐNG PHẢI khởi tạo Session `UserSessionDTO` (có `firstLogin = false`) VÀ redirect về trang Dashboard.
- **Đăng nhập lần đầu (Cấp sẵn)**: KHI người dùng Submit POST thông tin đúng, NHƯNG cờ `force_change_pass = 1` trong DB, HỆ THỐNG PHẢI tạo Session (có `firstLogin = true`) VÀ redirect ngay lập tức về trang `/auth/force-change-password`.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Sai thông tin (Dưới 5 lần)**: KHI người dùng nhập sai tên hoặc mật khẩu, HỆ THỐNG PHẢI trả về trang Đăng nhập kèm thông báo "Sai tên đăng nhập hoặc mật khẩu" và KHÔNG khóa tài khoản.
- **Khóa tài khoản (Brute-force)**: KHI người dùng nhập sai mật khẩu vượt quá số lần cho phép (>= 5 lần), HỆ THỐNG PHẢI gọi lệnh xuống Mock DB cập nhật trạng thái thành `LOCKED` VÀ hiển thị lỗi yêu cầu liên hệ Admin.
- **Đăng nhập vào tài khoản đã khóa**: KHI người dùng nhập ĐÚNG thông tin nhưng tài khoản đang ở trạng thái `LOCKED`, HỆ THỐNG PHẢI từ chối cấp Session và báo lỗi tài khoản bị khóa.

### 2.3 Boundary Values (Các giá trị biên)
- **Biên giới hạn 5 lần sai**:
  - Sai lần thứ 4: Chỉ hiện thông báo sai, trạng thái vẫn `ACTIVE`.
  - Sai đúng lần thứ 5: Khóa tài khoản NGAY LẬP TỨC.
- **Input rỗng**: KHI Submit `username` hoặc `password` rỗng/khoảng trắng, HỆ THỐNG PHẢI từ chối bằng Validation sớm mà không cần chọc xuống DB.
- **Input quá dài**: KHI Submit `username` hoặc `password` dài 1000 ký tự, HỆ THỐNG PHẢI từ chối để chống DoS qua Bcrypt hoặc ngập lụt Database.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Race Condition khi đếm số lần sai**: KHI Hacker bắn 50 requests sai mật khẩu cùng một lúc bằng Postman, bộ đếm lỗi trên RAM (hoặc DB) PHẢI được thread-safe để khóa tài khoản khi số đếm chạm mốc 5, không bị thất thoát biến đếm dẫn đến việc bị vượt giới hạn sai 50 lần mà không khóa.
