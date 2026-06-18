# ADR-001: Chiến lược Xác thực và Bảo mật (Authentication Strategy)
# Status: ACCEPTED
# Date: 2026-06-08
# Owner: @tech-lead

## 1. Bối cảnh (Context)
Hệ thống sử dụng kiến trúc Server-Side Rendering (SSR) với Java Servlet và JSP. Chúng ta cần thiết lập một cơ chế xác thực (Login/Logout) và quản lý phiên (Session) an toàn, tuân thủ nghiêm ngặt các quy tắc bảo mật từ layer 1 và layer 2, đồng thời KHÔNG sử dụng các framework cấp cao như Spring Security.

## 2. Các phương án đã xem xét (Options Considered)
- **Phương án 1:** Băm mật khẩu bằng MD5 hoặc Bcrypt + quản lý bằng `HttpSession` mặc định. (BỊ LOẠI do vi phạm luật bảo mật cấm dùng MD5/Bcrypt).
- **Phương án 2:** Trả về JWT (JSON Web Token) cho Client và lưu ở LocalStorage. (BỊ LOẠI vì mô hình của chúng ta là JSP/Servlet render HTML trực tiếp tại server, dùng JWT sẽ gây phức tạp không cần thiết và dễ dính lỗi XSS).
- **Phương án 3:** Băm mật khẩu bằng **Argon2id** + Quản lý phiên bằng `HttpSession` (JSESSIONID) với cấu hình cookie bảo mật (HttpOnly).

## 3. Quyết định (Decision)
Chúng tôi chọn **Phương án 3**.
AI Agent bắt buộc phải tuân thủ các quy tắc triển khai sau:
- **Thuật toán băm mật khẩu:** BẮT BUỘC dùng thuật toán `argon2id` (memory: 64MB, time: 3, threads: 4). Tuyệt đối cấm lưu plaintext, MD5 hay Bcrypt.
- **Độ dài mật khẩu:** Tối thiểu 7 ký tự (phải validate ngay tại Servlet trước khi chọc xuống DB).
- **Quản lý Session:** Sử dụng `HttpServletRequest.getSession()`. Cookie `JSESSIONID` bắt buộc phải được set cờ `HttpOnly` và `Secure` (nếu chạy HTTPS).
- **Bảo vệ Endpoint (Authorization):** Bắt buộc phải tạo một `AuthFilter.java` (implements `javax.servlet.Filter`) để chặn các request chưa đăng nhập truy cập vào các trang JSP nhạy cảm.

## 4. Lý do (Rationale)
- Việc sử dụng `argon2id` đáp ứng đúng tiêu chuẩn mã hóa bảo mật hiện đại nhất được định nghĩa trong `business.md` của dự án.
- Sử dụng `HttpSession` truyền thống hoàn toàn phù hợp và tự nhiên với View Engine là JSP. Controller (Servlet) có thể dễ dàng đẩy dữ liệu user vào `sessionScope` để JSTL (như `<c:if test="${not empty sessionScope.user}">`) xử lý hiển thị giao diện.
- Tránh được sự cồng kềnh (over-engineering) nếu cố nhồi nhét JWT vào hệ thống JSP thuần.

## 5. Hậu quả & Hành động tiếp theo (Consequences)
- **Thư viện:** Cần tải file `.jar` hỗ trợ Argon2 cho Java (ví dụ: thư viện `argon2-jvm`) vào thư mục `WEB-INF/lib/` và khai báo classpath trong `build.xml` của Apache Ant.
- **AI Rule:** Bất kỳ Servlet nào thực hiện thao tác thay đổi dữ liệu (POST, PUT, DELETE) đều phải đi qua `AuthFilter` để kiểm tra quyền hợp lệ trước khi thực thi logic nghiệp vụ.