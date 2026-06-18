# CONTEXT.md Feature Quên mật khẩu (Chưa đăng nhập)
# Người viết: @Phạm Anh Tú | Ngày: 2026-06-11

## 1. PROBLEM STATEMENT
* **Sự cố gián đoạn trải nghiệm người dùng:** Khách hàng không thể truy cập vào hệ thống do quên mật khẩu, gây gián đoạn luồng công việc và làm giảm mức độ tương tác (retention rate).
* **Quá tải bộ phận hỗ trợ (Support Overhead):** Nếu không có luồng tự phục vụ (self-service), đội ngũ hỗ trợ kỹ thuật hoặc quản trị viên sẽ phải tốn rất nhiều thời gian và chi phí để cấp lại mật khẩu thủ công cho từng người dùng.
* **Rủi ro rò rỉ dữ liệu người dùng (Email Enumeration):** Nếu hệ thống báo lỗi rõ ràng "Email chưa đăng ký", kẻ gian có thể lợi dụng API này để dò quét (brute-force) và thu thập danh sách email thực tế đang sử dụng hệ thống.

## 2. DOMAIN KNOWLEDGE
* **Recovery Token (Mã khôi phục):** Một chuỗi ký tự ngẫu nhiên, duy nhất, sinh ra tại thời điểm yêu cầu khôi phục mật khẩu. Token này được liên kết trực tiếp với một tài khoản người dùng và chỉ có giá trị sử dụng một lần (One-Time Use).
* **Email Enumeration Prevention:** Nguyên tắc bảo mật bắt buộc API yêu cầu khôi phục mật khẩu phải luôn trả về một phản hồi (response) giống hệt nhau (thành công) bất kể email đó có tồn tại trong hệ thống hay không.
* **Basic SQL Statement & SQL Injection (SQLi):** Do hệ thống sử dụng các câu lệnh SQL cơ bản (không dùng cơ chế tham số hóa PreparedStatements), các dữ liệu đầu vào (như email, mật khẩu mới, token) có nguy cơ cao chứa mã độc. Cần có cơ chế làm sạch (sanitize) và thoát chuỗi (escape) cực kỳ nghiêm ngặt trước khi nối chuỗi vào câu truy vấn.
* **Rate Limiting:** Cơ chế kiểm soát lưu lượng để ngăn chặn việc gọi API liên tục, bảo vệ dịch vụ gửi email khỏi tình trạng bị lạm dụng để spam (Gửi bom thư - Email Bombing).

## 3. STAKEHOLDERS
* **Người dùng cuối (Users):** Cần một quy trình khôi phục nhanh chóng, an toàn và có hướng dẫn rõ ràng qua email.
* **Đội ngũ Hỗ trợ khách hàng (Customer Support / Admin):** Được hưởng lợi nhờ việc giảm thiểu các ticket yêu cầu reset mật khẩu.
* **Đội ngũ Phát triển (Dev/Sec):** Chịu trách nhiệm đảm bảo tính toàn vẹn của logic bảo mật (hash mật khẩu, chống SQL Injection, quản lý vòng đời token).

## 4. CONSTRAINTS
* **Ràng buộc Kỹ thuật (Technical Constraints):**
  * **Vòng đời Token:** Bắt buộc hết hạn sau đúng 15 phút kể từ lúc khởi tạo.
  * **Rate Limit:** Tối đa 3 yêu cầu/giờ cho mỗi địa chỉ email.
  * **Bảo mật Cơ sở dữ liệu:** Các truy vấn bắt buộc sử dụng Basic SQL Statements. Phải xây dựng/sử dụng hàm tự chế để làm sạch (sanitize) chuỗi đầu vào chống SQL Injection trước khi thực thi.
  * **Bảo mật Mật khẩu:** Mật khẩu mới phải được băm bằng thuật toán tiêu chuẩn (BCrypt/Argon2) trước khi lưu trữ.
* **Ràng buộc Phạm vi (Scope Constraints):**
  * Không triển khai xác thực qua SMS OTP.
  * Không tự động khóa tài khoản khi nhập sai token nhiều lần (chống tình trạng kẻ xấu cố tình khóa tài khoản người khác).

## 5. ASSUMPTIONS
* Giả định hệ thống đã được tích hợp sẵn một dịch vụ Email Delivery (như SendGrid, Amazon SES hoặc một SMTP Server) đang hoạt động ổn định để gửi mail ngay lập tức.
* Giả định Frontend có cấu trúc định tuyến (routing) sẵn sàng để xử lý các liên kết khôi phục từ email (ví dụ: `https://domain.com/reset-password?token=...`).

## 6. OPEN QUESTIONS
* **Chiến lược sinh Token:** Khi người dùng gửi yêu cầu 3 lần liên tiếp (trong giới hạn rate limit), hệ thống sẽ tạo ra 3 token khác nhau (token cũ có bị vô hiệu hóa không?), hay luôn gửi lại cùng một token còn hiệu lực?
* **Cơ chế lưu trữ Token:** Token sẽ được lưu trực tiếp vào bảng người dùng trong Database chính, lưu ở một bảng phụ riêng biệt, hay lưu trên hệ thống In-memory Cache (như Redis) để tận dụng cơ chế tự động xóa sau 15 phút (TTL)?
* **Quy chuẩn Sanitize SQL:** Đội dự án đã có sẵn một hàm/thư viện chung để sanitize dữ liệu cho Basic SQL Statement chưa, hay người triển khai tính năng này phải tự viết từ đầu?