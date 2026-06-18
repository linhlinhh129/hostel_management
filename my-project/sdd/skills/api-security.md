# BẢN ĐẶC TẢ BẢO MẬT API & WEB (API Security Policy)
# Áp dụng cho: Toàn bộ hệ thống Java Servlet + JSP + SQL Server 2022
# LƯU Ý CHO AI: Đây là file Constraint Layer 1. Mọi code vi phạm các quy tắc dưới đây đều bị đánh giá là FAILED và không được phép thực thi [3].

## 1. XÁC THỰC VÀ PHÂN QUYỀN (AUTHENTICATION & AUTHORIZATION)
- **SEC-AUTH-01:** Mọi Endpoint/Servlet thay đổi dữ liệu (POST, PUT, DELETE) BẮT BUỘC (SHALL) phải đi qua `AuthFilter` để kiểm tra `HttpSession` hợp lệ [6], [7].
- **SEC-AUTH-02:** Mật khẩu BẮT BUỘC mã hóa bằng thuật toán `argon2id`. TUYỆT ĐỐI CẤM dùng MD5, SHA hay lưu plain-text [3].
- **SEC-AUTH-03:** API/Servlet KHÔNG ĐƯỢC trả về thông báo lỗi khác nhau cho trường hợp "Sai email" và "Sai mật khẩu" (để chống User Enumeration dò quét tài khoản). Phải trả về thông báo chung: "Tài khoản hoặc mật khẩu không chính xác" [8].

## 2. CHỐNG TẤN CÔNG INJECTION (SQLi & XSS)
- **SEC-INJ-01 (SQL Injection):** Mọi truy vấn xuống SQL Server 2022 BẮT BUỘC dùng `PreparedStatement`. TUYỆT ĐỐI CẤM cộng chuỗi (string concatenation) khi viết câu lệnh T-SQL [6], [7].
- **SEC-INJ-02 (XSS):** Khi render dữ liệu từ người dùng (user input) lên file `.jsp`, BẮT BUỘC dùng thẻ `<c:out value="${...}">` của JSTL hoặc hàm `fn:escapeXml()` để tự động escape HTML tags. TUYỆT ĐỐI CẤM in trực tiếp biến chưa được sanitize ra view.

## 3. QUẢN LÝ DỮ LIỆU NHẠY CẢM (SENSITIVE DATA)
- **SEC-DAT-01 (Logging):** KHÔNG ĐƯỢC log các thông tin nhạy cảm (PII) như mật khẩu, JWT token, chuỗi kết nối Database, hay thông tin cá nhân của khách hàng ra console của Tomcat. Cần mask (che) dữ liệu trước khi log (VD: `0912***456`) [9], [7].
- **SEC-DAT-02 (Error Handling):** Thông báo lỗi chi tiết của hệ thống (Exception Stack Trace) CHỈ được ghi vào log server, TUYỆT ĐỐI KHÔNG trả về cho client/trình duyệt [4], [7]. Bắt buộc forward về trang `error.jsp` kèm message lỗi thân thiện với người dùng.
- **SEC-DAT-03 (Hardcode):** TUYỆT ĐỐI CẤM lưu trữ secret key, URL database, username/password SQL Server trực tiếp dưới dạng plain-text trong mã nguồn `.java`. Phải đọc từ file `.properties` hoặc biến môi trường [3].

## 4. BẢO VỆ REQUEST & HEADERS
- **SEC-NET-01:** Cookie `JSESSIONID` BẮT BUỘC phải được set cờ `HttpOnly` (và `Secure` nếu dùng HTTPS) để chống đánh cắp session.
- **SEC-NET-02 (Brute-force):** Các Servlet xử lý đăng nhập hoặc quên mật khẩu phải có cơ chế giới hạn số lần thử (VD: Khóa tài khoản 30 phút nếu đăng nhập sai 5 lần liên tiếp) [10].