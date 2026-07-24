# Test Specification: Thay đổi mật khẩu (Change Password - Unit Test Only)

**Status:** Draft
**Target Feature:** Đổi mật khẩu (`my-project/sdd/specs/auth/ChangePassword`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic xử lý tại lớp `ChangePasswordServlet`. Cô lập hoàn toàn cơ sở dữ liệu và thư viện mã hóa Hash. Đảm bảo quy tắc bảo mật chặt chẽ (Validation chính sách mật khẩu) và phân quyền Session được thực thi chính xác.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Đổi mật khẩu thành công**: KHI người dùng Submit POST `oldPassword` đúng và `newPassword` hợp lệ, HỆ THỐNG PHẢI hash mật khẩu mới, gọi Mock Service lưu xuống DB, VÀ trả về trang Profile với thông báo "Đổi mật khẩu thành công".

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Sai mật khẩu cũ**: KHI người dùng nhập `oldPassword` không khớp với Hash trong DB, Mock Service văng lỗi, HỆ THỐNG PHẢI chặn thao tác lưu, hiển thị lỗi "Mật khẩu hiện tại không chính xác".
- **Vi phạm chính sách mật khẩu**: KHI `newPassword` vi phạm 1 trong 7 luật (vd: Không có chữ hoa, ngắn hơn 8 ký tự, trùng mật khẩu cũ), HỆ THỐNG PHẢI Validation lỗi ngay lập tức mà chưa cần gọi DAO.
- **Không có Session**: KHI người dùng chưa đăng nhập gọi tới API, HỆ THỐNG PHẢI Redirect về trang Login (hoặc ném 401).

### 2.3 Boundary Values (Các giá trị biên)
- **Mật khẩu rỗng hoặc toàn khoảng trắng**: KHI nhập chuỗi rỗng `""` hoặc `"   "`, HỆ THỐNG PHẢI trim và bắt lỗi Validation (từ chối).
- **Mật khẩu cực dài**: KHI nhập mật khẩu dài 1000 ký tự (có thể làm chậm thuật toán BCrypt gây DoS), HỆ THỐNG PHẢI bắt lỗi chiều dài tối đa (nếu có định nghĩa, hoặc test khả năng chịu đựng của Validation).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Tranh chấp đồng thời đổi mật khẩu**: KHI 1 tài khoản bắn 10 request Đổi mật khẩu đồng thời (với 10 mật khẩu mới khác nhau), HỆ THỐNG PHẢI xử lý an toàn (Thread-safe) trong nội bộ Servlet. Không để xảy ra tình trạng request này ghi đè biến request kia do lỗi khai báo biến toàn cục (Instance variables leak).
