## 4. auditLogs — Xem Nhật ký Hệ thống

### 4.1 Phân quyền

* `BR-AL-01` Chỉ ADMIN được truy cập chức năng Xem Nhật ký Hệ thống.
* `BR-AL-02` Người dùng chưa xác thực → HTTP 401 `UNAUTHORIZED`.
* `BR-AL-03` Người dùng đã xác thực nhưng không phải ADMIN → HTTP 403 `FORBIDDEN`.

### 4.2 Tính bất biến của Audit Log

* `BR-AL-04` Audit Log chỉ được phép đọc qua API này.
* `BR-AL-05` Không được tạo, sửa hoặc xóa bản ghi Audit Log thủ công qua API.
* `BR-AL-06` Mọi bản ghi Audit Log phải được hệ thống tự động sinh khi có thao tác nghiệp vụ.

### 4.3 Danh sách và phân trang

* `BR-AL-07` Danh sách nhật ký phải hỗ trợ phân trang bắt buộc (page 1-based, size mặc định 10).
* `BR-AL-08` Danh sách phải được sắp xếp theo `createdAt` giảm dần theo mặc định.
* `BR-AL-09` Khi không có bản ghi phù hợp với điều kiện lọc, hệ thống trả về danh sách rỗng và thông báo không có dữ liệu.

### 4.4 Lọc và tìm kiếm

* `BR-AL-10` Hỗ trợ lọc theo `entityType` (ví dụ: Tenant, Employee, Facility, Notification).
* `BR-AL-11` Hỗ trợ lọc theo `action` (CREATE, UPDATE, DELETE).
* `BR-AL-12` Hỗ trợ lọc theo `actor` (Tên người thực hiện, khớp một phần).
* `BR-AL-13` Hỗ trợ lọc theo khoảng thời gian `fromDate` và `toDate` (định dạng YYYY-MM-DD).
* `BR-AL-14` `fromDate` lớn hơn `toDate` hoặc sai định dạng ngày → Hệ thống xử lý an toàn và trả về danh sách rỗng (không ném lỗi).
* `BR-AL-15` Tham số lọc không hợp lệ → Hệ thống xử lý an toàn và trả về danh sách rỗng (không ném lỗi).

### 4.5 Nội dung bản ghi

* `BR-AL-16` Mỗi bản ghi Audit Log phải chứa tối thiểu: `auditLogId`, `entityType`, `entityId`, `action`, `oldValue`, `newValue`, `ipAddress`, `comment`, `createdBy`, `createdAt`.
* `BR-AL-17` Với hành động CREATE: `oldValue = null`, `newValue` = dữ liệu được tạo.
* `BR-AL-18` Với hành động UPDATE: `oldValue` = dữ liệu trước khi sửa, `newValue` = dữ liệu sau khi sửa.
* `BR-AL-19` Với hành động DELETE: `oldValue` = dữ liệu bị xóa, `newValue = null`.

### 4.6 Hiệu năng

* `BR-AL-20` Thời gian phản hồi tối đa 500ms (P95).
* `BR-AL-21` Rate limit: 100 requests/phút/người dùng.

---
