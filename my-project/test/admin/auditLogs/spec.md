# Test Specification: Audit Logs (Unit Test Only)

## 1. Test Strategy
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito, AssertJ). Các thành phần được Unit test bao gồm `AdminAuditLogServlet`, `AuditLogService` (nếu có) và DTOs. Tầng DAO và E2E web automation nằm ngoài phạm vi spec này.

Các khía cạnh kiểm thử bao gồm: Happy path, Error cases, Boundary values và Concurrent scenarios.

---

## 2. Test Cases

### 2.1. Happy Path (Các kịch bản thành công chính)
- **Truy vấn danh sách không filter**: Test hàm `doGet` khi request không chứa param filter. Mock service trả về danh sách đầy đủ.
- **Lọc theo action/entityType**: Test hàm `doGet` với param `action=CREATE` hoặc `entityType=users`. Assert service được gọi với đúng tham số lọc.
- **Lọc theo khoảng thời gian**: Gửi `dateFrom` và `dateTo` hợp lệ.
- **Phân trang**: Truyền param `page=2`. Verify service được gọi với `page=2` và tính toán `hasNextPage` trả về view đúng.
- **Xem chi tiết log**: Test truy cập route `/admin/audit-logs/1`. Mock service trả về `AuditLog` ID 1. Verify `auditLog` attribute được set.

### 2.2. Error Cases (Các kịch bản lỗi từ "WHEN/WHILE... THE SYSTEM SHALL...")
- **INVALID_FILTER (Lỗi tham số)**: Gửi tham số lọc không hợp lệ. Verify hệ thống ném `IllegalArgumentException` và trả về HTTP 400.
- **fromDate lớn hơn toDate**: Gửi `dateFrom` là ngày mai, `dateTo` là hôm nay. Verify hệ thống trả về danh sách rỗng, không báo lỗi HTTP 400 (xử lý an toàn).
- **Chưa đăng nhập (UNAUTHORIZED)**: Mock session chưa có user. Verify request bị redirect về `/login`.
- **Không phải Admin (FORBIDDEN)**: Mock session user có role là `USER`. Verify hệ thống chặn truy cập và ném lỗi `FORBIDDEN` (HTTP 403).
- **Log ID không tồn tại**: Try cập chi tiết với ID không có thực. Mock service ném `NotFoundException`. Verify hệ thống forward về giao diện báo lỗi HTTP 404.
- **DAO ném Exception**: Mock DAO/Service ném `SQLException`. Verify hệ thống log ERROR và forward về `list.jsp` hiển thị thông báo lỗi.

### 2.3. Boundary Values (Giá trị biên)
- **Page nhỏ hơn 1**: Gửi `page=0` hoặc `page=-1`. Verify hệ thống tự động fallback về `page=1`.
- **Page quá lớn**: Gửi `page=999999` (quá tổng số trang). Verify hệ thống trả về mảng rỗng và `hasNextPage=false`.
- **Chuỗi filter rỗng**: Gửi `action=""` hoặc `actor="   "`. Verify hệ thống tự động trim và loại bỏ bộ lọc này (coi như null).
- **ID biên**: Truy cập chi tiết `/admin/audit-logs/0` hoặc ID âm. Verify ném 404.

### 2.4. Concurrent Scenarios (Tranh chấp đồng thời)
*(Vì là Unit Test, sử dụng Mockito và Thread / ExecutorService để kiểm tra thread-safety của Controller/Service)*
- **Concurrent Read**: Giả lập 50 thread đồng thời gọi `doGet` trên cùng một instance `AdminAuditLogServlet`. Verify servlet không bị dính trạng thái (state leak) giữa các thread (không dùng biến toàn cục cho request/response).
- **Service Singleton Thread-safety**: Test instance của `AuditLogService` có xử lý an toàn khi nhiều thread gọi `findAll` với các tham số phân trang khác nhau cùng một lúc.
