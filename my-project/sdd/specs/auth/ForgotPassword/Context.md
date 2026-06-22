---
# CONTEXT.md - Feature Quên mật khẩu (Khôi phục qua Email)

**Người viết:** Phạm Anh Tú

**Ngày:** 2026-06-13

**Dự án:** Quản lý Nhà trọ

---

## 1. PROBLEM STATEMENT

### Nguy cơ gián đoạn nghiệp vụ
Trong hệ thống Quản lý Nhà trọ đa vai trò (**Người thuê, Người vận hành, Ban quản lý, Người quản lý chi phí**), việc người dùng quên mật khẩu sau một thời gian dài duy trì phiên đăng nhập là không thể tránh khỏi.
Cần có một quy trình khôi phục tài khoản tự động và bảo mật qua Email đã đăng ký để người dùng có thể nhanh chóng lấy lại quyền truy cập mà không cần sự can thiệp thủ công từ Admin.

### Nguy cơ Spam Email & Blacklist
Nếu không có cơ chế giới hạn tần suất yêu cầu (**Rate Limiting**), kẻ tấn công có thể sử dụng bot để gọi tính năng Quên mật khẩu liên tục. Điều này khiến domain gửi email bị các nhà cung cấp (Gmail, Outlook,...) đánh dấu là Spam hoặc đưa vào Blacklist do gửi hàng loạt thư rác.

---

## 2. DOMAIN KNOWLEDGE

### Reset Password Link & Token
Một URL đặc biệt chứa chuỗi mã hóa an toàn (UUID/JWT) được gửi đến địa chỉ email của người dùng sau khi họ yêu cầu khôi phục mật khẩu.
* Có thời hạn ngắn (ví dụ: 15 phút).
* Chỉ được sử dụng một lần để thiết lập mật khẩu mới.

### Rate Limiting & Cooldown
* **Rate Limiting:** Giới hạn số lần một email được phép yêu cầu khôi phục trong 1 khoảng thời gian.
* **Cooldown:** Khoảng thời gian bắt buộc chờ giữa 2 lần nhấn nút "Gửi lại Link" (VD: 60 giây).

---

## 3. STAKEHOLDERS

### Người dùng (Đa vai trò)
* Cần quy trình rõ ràng, nhận Email khôi phục nhanh chóng.
* Hệ thống cần phản hồi ẩn danh (không tiết lộ Email có tồn tại hay không trên màn hình) để bảo vệ thông tin cá nhân.

### Đội ngũ Phát triển
* Xây dựng luồng JSP/Servlet Form Submit.
* Xây dựng cơ chế cache (RAM) để lưu trữ và xác thực Token.
* Tích hợp gửi Email (Mock in ra Console trong giai đoạn phát triển).

---

## 4. CONSTRAINTS

### Ràng buộc Kỹ thuật
* **Kiến trúc:** Sử dụng Servlet/JSP Form-based thay vì REST API.
* **Thời hạn hiệu lực:** Reset Password Link có hiệu lực tối đa **15 phút**.
* **Thu hồi phiên đăng nhập (Bắt buộc):** Ngay sau khi người dùng đặt lại mật khẩu thành công, hệ thống phải vô hiệu hóa toàn bộ các phiên đăng nhập (sessions) đang tồn tại trên tất cả các thiết bị khác bằng `SessionRegistry`.

### Ràng buộc Phạm vi (Out of Scope)
* Không hỗ trợ khôi phục bằng số điện thoại (SMS).
* Không hỗ trợ khôi phục qua MXH (Zalo/Viber).

---

## 5. ASSUMPTIONS
* Mỗi tài khoản bắt buộc phải có 1 email duy nhất đã được xác thực trước đó.
* Dịch vụ Email: Tạm thời sử dụng cơ chế Mock (Giả lập) in ra cửa sổ Console để kiểm thử.

---

## 6. OPEN QUESTIONS
* **Email Template:** Nội dung email khôi phục cần những thông tin cơ bản nào (Tên user, Nút nhấn hay Text Link trần)?