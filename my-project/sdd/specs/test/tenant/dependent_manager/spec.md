# Test Strategy & Acceptance Criteria: Dependent Management (Tenant)

**Nguyên tắc tối thượng:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm).

Tài liệu này định nghĩa chiến lược kiểm thử và các Tiêu chí chấp nhận (Acceptance Criteria) đóng vai trò là các test case ẩn trước khi tiến hành viết code/test cho tính năng Quản lý người phụ thuộc của Tenant.

---

## 1. Happy Path (Các kịch bản thành công chính)

Mục tiêu: Đảm bảo các luồng nghiệp vụ cốt lõi mà người dùng mong đợi hoạt động trơn tru.

- **TC-HP-01 (Danh sách)**: Khi Tenant đã đăng nhập thành công và có người phụ thuộc, hệ thống trả về danh sách các người phụ thuộc của chính Tenant đó.
  - *Behavior*: API trả về mảng dữ liệu. Dữ liệu chỉ chứa những người phụ thuộc có `tenant_id` trùng khớp và `deleted_at IS NULL`.
- **TC-HP-02 (Chi tiết & Masking)**: Khi Tenant yêu cầu xem chi tiết một `dependentId` hợp lệ thuộc về mình, hệ thống trả về toàn bộ thông tin chi tiết.
  - *Behavior*: Phải áp dụng quy tắc che khuất (masking) PII cho trường CCCD/CMND (Ví dụ: `0790******123`).
- **TC-HP-03 (Sắp xếp)**: Khi Tenant xem danh sách, kết quả mặc định phải được sắp xếp theo họ tên (Alphabetical Order).
  - *Behavior*: Thứ tự mảng JSON trả về tuân thủ thứ tự từ điển tăng dần của trường `fullName`.

---

## 2. Error Cases (Các kịch bản lỗi & Unwanted states)

Mục tiêu: Xử lý đúng đắn các ngoại lệ theo định nghĩa bằng các mẫu câu `WHERE` / `WHEN` trong Spec.

- **TC-ERR-01 (FR08 - IDOR / Unauthorized Access)**: 
  - *Spec (WHERE)*: Tenant truy cập người phụ thuộc không thuộc quyền quản lý.
  - *Behavior*: Hệ thống từ chối truy cập, tuyệt đối không trả về dữ liệu, mã lỗi HTTP 403 Forbidden.
- **TC-ERR-02 (FR09 - Unauthenticated)**:
  - *Spec (WHEN)*: Người dùng chưa đăng nhập.
  - *Behavior*: Hệ thống không cho phép thực hiện API, trả về HTTP 401 Unauthorized (hoặc điều hướng sang Login ở UI).
- **TC-ERR-03 (FR10 - Not Found & Soft Delete)**:
  - *Spec (WHEN)*: `dependentId` không tồn tại hoặc đã bị Soft Delete (`deleted_at IS NOT NULL`).
  - *Behavior*: Hệ thống trả về HTTP 404 Not Found. Không phân biệt giữa "không tồn tại từ đầu" và "đã bị xóa" để tránh lộ lọt thông tin.
- **TC-ERR-04 (FR11 - System Failure)**:
  - *Spec (WHERE)*: Hệ thống không thể tải danh sách (lỗi DB, timeout).
  - *Behavior*: Bắt Exception an toàn, không rò rỉ stack trace ra ngoài, trả về HTTP 500 Internal Server Error kèm message chung chung để UI hiển thị trạng thái Error và nút Retry.

---

## 3. Boundary Values (Các giá trị biên)

Mục tiêu: Đảm bảo hệ thống ổn định ở các vùng dữ liệu đặc biệt (tối thiểu, tối đa, rỗng, null).

- **TC-BV-01 (Empty State - FR07)**:
  - *Spec (WHERE)*: Tenant chưa có người phụ thuộc nào.
  - *Behavior*: API trả về mảng rỗng `[]` (HTTP 200 OK), không bị lỗi NullPointerException hay 404. Hệ thống phải hỗ trợ UI hiển thị màn hình Empty State: *"Hiện chưa có người phụ thuộc nào được đăng ký."*
- **TC-BV-02 (Missing Optional Fields)**:
  - *Behavior*: Khi bản ghi trong database thiếu các thông tin không bắt buộc (ví dụ: `email` là null), API vẫn parse và trả về thành công mà không gây lỗi format.
- **TC-BV-03 (Data Lengths)**:
  - *Behavior*: Người phụ thuộc có tên rất dài (hoặc có chứa ký tự đặc biệt hợp lệ). API vẫn xử lý và trả về đúng UTF-8 payload.

---

## 4. Concurrent Scenarios (Các kịch bản truy cập đồng thời)

Mục tiêu: Đảm bảo tính nhất quán dữ liệu (Data Consistency) và hiệu suất trong các điều kiện đa luồng.

- **TC-CS-01 (Read while Delete)**:
  - *Kịch bản*: Tenant (hoặc UI) đang gửi request lấy danh sách/chi tiết, cùng lúc đó một tiến trình khác (hoặc Manager) đang thực hiện Soft Delete chính người phụ thuộc đó.
  - *Behavior (Cách ly giao dịch)*: Nếu Soft Delete được commit trước, Tenant sẽ nhận 404. Nếu request đọc thực thi trước, trả về chi tiết. Hệ thống không rơi vào trạng thái lock kéo dài hoặc dirty read.
- **TC-CS-02 (High Load / Connection Pooling)**:
  - *Kịch bản*: Hàng trăm tenants đồng loạt làm mới (refresh) màn hình Quản lý người phụ thuộc.
  - *Behavior*: Hệ thống vẫn đảm bảo thời gian phản hồi `< 200ms (P95)`. Không gây cạn kiệt Connection Pool của Database. Mọi request đọc độc lập với nhau.

---
**Kết luận**: Toàn bộ các test cases (Unit Test / API Test) tiếp theo khi implement phải được ánh xạ trực tiếp từ các Acceptance Criteria trên để đảm bảo đúng nguyên tắc kiểm thử hành vi (BDD - Behavior Driven Development).
