# Test Specification: Quên mật khẩu (Forgot Password - Unit Test Only)

**Status:** Draft
**Target Feature:** Quên mật khẩu (`my-project/sdd/specs/auth/ForgotPassword`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic xử lý tại lớp `ForgotPasswordServlet` và `ResetPasswordServlet`. Các yếu tố cốt lõi bao gồm: Tránh User Enumeration, Validate Token, và logic Thu hồi phiên (Session Revocation).

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Gửi Link khôi phục**: KHI POST email hợp lệ, HỆ THỐNG PHẢI gọi Mock tạo Token, mô phỏng gửi email, VÀ hiển thị thông báo "Link khôi phục đã được gửi".
- **Hiển thị Form Reset**: KHI GET link khôi phục với Token hợp lệ, HỆ THỐNG PHẢI hiển thị form đổi mật khẩu.
- **Reset mật khẩu thành công**: KHI POST Token đúng + Mật khẩu mới hợp lệ, HỆ THỐNG PHẢI lưu mật khẩu mới, VÀ gọi mock tới `SessionRegistry` để invalidate toàn bộ Session cũ của người dùng.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Chống Dò quét (Anti-User Enumeration)**: KHI POST một email không tồn tại trong DB, HỆ THỐNG PHẢI âm thầm bỏ qua việc tạo Token, NHƯNG VẪN hiển thị thông báo "Link khôi phục đã được gửi" giống y hệt Happy Path.
- **Token sai hoặc hết hạn**: KHI GET hoặc POST với Token quá 15 phút, HỆ THỐNG PHẢI ném lỗi "Token không hợp lệ hoặc đã hết hạn".
- **Mật khẩu không khớp**: KHI POST reset với `newPassword` và `confirmPassword` lệch nhau, HỆ THỐNG PHẢI Validation lỗi.

### 2.3 Boundary Values (Các giá trị biên)
- **Biên thời gian Token**: KHI Token vừa tròn 15 phút (cận biên).
- **Email rỗng / Ký tự đặc biệt**: KHI nhập email rỗng hoặc sai định dạng regex, HỆ THỐNG PHẢI chặn ở vòng Validation.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Xóa Session đồng thời**: Đảm bảo an toàn luồng (Thread-Safety) khi `SessionRegistry` thực thi hành động Invalidate 100 sessions cùng lúc của cùng một User sau khi đổi mật khẩu thành công (Test cơ chế khóa Map của SessionRegistry).
