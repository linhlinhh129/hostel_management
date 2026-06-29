# CONTEXT.md Feature Đổi mật khẩu & Quên mật khẩu (Khi đã đăng nhập)
# Người viết: @Phạm Anh Tú | Ngày: 2026-06-24

## 1. PROBLEM STATEMENT
* **Nhu cầu bảo mật định kỳ:** Người dùng đang có phiên làm việc trong hệ thống cần một cơ chế an toàn, chủ động để thay đổi mật khẩu định kỳ, giúp bảo vệ tài khoản khỏi các rủi ro lộ lọt thông tin.
* **Quên mật khẩu khi phiên làm việc còn hạn:** Khá nhiều trường hợp người dùng đang duy trì đăng nhập (nhờ tính năng "Remember Me" hoặc phiên chưa hết hạn) nhưng lại không nhớ mật khẩu cũ để thực hiện thao tác đổi. Việc bắt họ phải đăng xuất ra ngoài màn hình Login để lấy lại mật khẩu gây ra trải nghiệm gián đoạn. Cần một luồng khôi phục nhanh ngay bên trong hệ thống.

## 2. DOMAIN KNOWLEDGE
* **Phiên đăng nhập (Session):** Trạng thái làm việc hiện tại của người dùng được duy trì trên Server thông qua `HttpSession`. Mọi thao tác liên quan đến bảo mật ở luồng này đều phải lấy thông tin định danh từ Session làm gốc.
* **Luồng phụ (Sub-flow) Khôi phục:** Khi người dùng chọn "Quên mật khẩu hiện tại", hệ thống sẽ không yêu cầu họ nhập lại email. Thay vào đó, hệ thống trích xuất trực tiếp email từ `HttpSession` để sinh Recovery Token và gửi link khôi phục.
* **Basic SQL Statement & Nguy cơ bảo mật (SQL Injection):** Do yêu cầu hệ thống bắt buộc sử dụng các câu lệnh SQL cơ bản (Basic SQL Statements) thay vì PreparedStatements, nguy cơ tấn công SQL Injection khi nối chuỗi mật khẩu mới vào câu lệnh `UPDATE` là cực kỳ cao. Đội ngũ phát triển cần có cơ chế làm sạch (sanitize) và thoát chuỗi (escape) dữ liệu nghiêm ngặt trước khi thực thi.

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Cần một giao diện dễ thao tác để cập nhật mật khẩu và có sẵn tùy chọn "phao cứu sinh" (quên mật khẩu cũ) ngay tại trang cá nhân mà không cần thoát khỏi ứng dụng.
* **Đội ngũ Phát triển (Dev/Sec):** Chịu trách nhiệm bảo vệ luồng dữ liệu: kiểm tra Session hợp lệ, so khớp mật khẩu cũ, băm mật khẩu mới và bảo vệ các câu truy vấn Basic SQL khỏi mã độc.

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * **Xác thực phiên:** Controller/Servlet bắt buộc phải kiểm tra `HttpSession`. Nếu Session null hoặc vô hiệu, phải điều hướng người dùng ra trang Đăng nhập.
  * **Bảo mật Cơ sở dữ liệu:** Thao tác cập nhật (`UPDATE`) và kiểm tra mật khẩu (`SELECT`) phải dùng Basic SQL Statements kết hợp hàm sanitize dữ liệu tự xây dựng.
  * **Mã hóa (Hashing):** Mật khẩu mới phải được băm bằng thuật toán tiêu chuẩn (BCrypt hoặc Argon2). Việc so sánh mật khẩu cũ cũng phải dùng hàm `checkpw` (hoặc tương đương) để đối chiếu với chuỗi hash trong Database.
* **Ràng buộc Nghiệp vụ - Chính sách Mật khẩu (Business Constraints - Password Policy):**
  * **Độ dài:** Tối thiểu 8 ký tự.
  * **Chữ hoa:** Có ít nhất 1 chữ cái viết hoa (A-Z).
  * **Chữ thường:** Có ít nhất 1 chữ cái viết thường (a-z).
  * **Chữ số:** Có ít nhất 1 chữ số (0-9).
  * **Ký tự đặc biệt:** Có ít nhất 1 ký tự đặc biệt (!, @, #, $, %, ^, &, *, v.v.).
  * **Khoảng trắng:** Tuyệt đối không được chứa khoảng trắng.
  * **Bảo mật chéo:** Mật khẩu mới không được trùng với mật khẩu cũ, tên đăng nhập (username), hoặc email.

## 5. ASSUMPTIONS
* Giả định người dùng thao tác chức năng này đang sở hữu một `HttpSession` hợp lệ, trong đó có lưu trữ sẵn các thông tin cơ bản như `userId`, `username`, và `email`.
* Giả định luồng gửi email hệ thống (Mail Server/SMTP) đang hoạt động ổn định để phục vụ ngay lập tức cho tác vụ gửi Recovery Token khi người dùng bấm "Quên mật khẩu hiện tại".

## 6. OPEN QUESTIONS
* **Quản lý đa phiên (Multi-session):** Khi người dùng đổi mật khẩu thành công trên thiết bị hiện tại, hệ thống có cần tự động hủy (invalidate) tất cả các `HttpSession` của tài khoản đó đang tồn tại trên các thiết bị/trình duyệt khác để đảm bảo an toàn tuyệt đối không?
* **Thời gian sống của Token (TTL):** Mã Recovery Token sinh ra từ luồng "Quên mật khẩu hiện tại" bên trong hệ thống có nên chia sẻ chung cấu hình hết hạn (ví dụ: 15 phút) như luồng quên mật khẩu từ màn hình ngoài hay không?