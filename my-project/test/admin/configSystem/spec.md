# Test Specification: System Config (Unit Test Only)

## 1. Test Strategy
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito, AssertJ). Đối tượng kiểm thử chính là `AdminSystemConfigServlet`, các object `EmailConfigDTO`, `VNPayConfigDTO` và các logic xử lý phía Service. Giao tiếp CSDL (DAO) và tự động hóa trình duyệt (Selenium) không nằm trong spec này.

Các khía cạnh kiểm thử bao gồm: Happy path, Error cases, Boundary values và Concurrent scenarios.

---

## 2. Test Cases

### 2.1. Happy Path (Các kịch bản thành công chính)
- **Hiển thị form Config (GET)**: Test hàm `doGet`. Mock Service để trả về cấu hình Email và VNPay giả lập. Verify `password` và `secretKey` không có mặt trong DTO và UI sẽ hiển thị placeholder.
- **Update Email thành công (POST)**: Gửi request đầy đủ host, port, username, password, from. Mock Service báo lưu thành công. Verify hệ thống gọi lệnh redirect `?success=email_updated`.
- **Update VNPay thành công (POST)**: Gửi request đầy đủ payUrl, returnUrl, tmnCode, secretKey, apiUrl. Verify gọi lệnh redirect `?success=vnpay_updated`.

### 2.2. Error Cases (Các kịch bản lỗi từ "WHEN/WHILE... THE SYSTEM SHALL...")
- **Bỏ trống trường bắt buộc (Email)**: Gửi form cấu hình Email nhưng thiếu `host` hoặc `password`. Verify hệ thống forward lại `system-config.jsp` kèm `errorMessage` và KHÔNG gọi hàm lưu ở Service.
- **Bỏ trống trường bắt buộc (VNPay)**: Gửi form cấu hình VNPay nhưng thiếu `tmnCode` hoặc `secretKey`. Verify hệ thống báo lỗi tương tự và chặn việc lưu.
- **Port không phải số nguyên**: Truyền chuỗi chữ cái `"abc"` vào trường `port` của Email. Verify hệ thống văng lỗi parse, bắt lại và trả về `errorMessage`.
- **Port ngoài khoảng cho phép**: Truyền `port = -10` hoặc `port = 99999`. Verify logic validation chặn cập nhật và trả về `errorMessage`.
- **Lỗi ghi DB từ Service**: Cố tình mock Service ném Exception (mô phỏng mất kết nối DB) khi lưu. Verify Controller bắt được lỗi, log ERROR, và forward về lại màn hình kèm `errorMessage` chung.
- **Chưa đăng nhập / Không phải Admin**: Mock Session thiếu `ADMIN` role. Verify hệ thống chặn POST request (ném lỗi `FORBIDDEN`).

### 2.3. Boundary Values (Giá trị biên)
- **Biên hợp lệ của Port**: 
  - Truyền `port = 1` (Min). Verify lưu thành công.
  - Truyền `port = 65535` (Max). Verify lưu thành công.
- **Biên không hợp lệ của Port**:
  - Truyền `port = 0`. Verify văng lỗi validation.
  - Truyền `port = 65536`. Verify văng lỗi validation.
- **Dữ liệu khoảng trắng (Whitespace)**: Truyền các trường bắt buộc chỉ toàn khoảng trắng (`"   "`). Verify hệ thống trim() chuỗi, phát hiện chuỗi rỗng và báo lỗi validation.
- **Độ dài chuỗi tối đa**: Truyền chuỗi rất dài (e.g. 1000 ký tự) cho `host` hoặc `username`. Kiểm thử hành vi validation của Service đối với kích thước chuỗi vượt giới hạn DB (nếu có logic check).

### 2.4. Concurrent Scenarios (Tranh chấp đồng thời)
*(Vì là Unit Test, sử dụng CountDownLatch và ExecutorService để kiểm tra tính luồng an toàn - Thread Safety)*
- **Concurrent Config Updates (Race Condition)**: Mock `SystemConfigService`. Tạo 10 thread đồng thời nạp dữ liệu cập nhật cấu hình Email và 10 thread nạp VNPay. Test instance của `AdminSystemConfigServlet` hoặc `Service` có chia sẻ chung biến instance không (nếu thiết kế Singleton mà dùng biến instance cho state thì sẽ lỗi). Verify rằng tất cả 20 thread đều truyền đúng thông số xuống DAO Mock mà không bị dính chéo tham số giữa các request.
- **Read-Write Conflict (Đọc ghi đồng thời)**: Giả lập Thread A gọi GET để đọc cấu hình, Thread B gọi POST để update cấu hình cùng lúc. Verify ở mức Service logic xử lý không văng ConcurrentModificationException hay lỗi state.
