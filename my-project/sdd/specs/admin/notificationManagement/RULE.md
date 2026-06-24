## 3. notificationManagement — Quản lý Thông báo

### 3.1 Phân quyền

* `BR-NM-01` Chỉ người dùng được phân quyền mới được tạo và xem thông báo.

### 3.2 Tạo thông báo

* `BR-NM-02` Tiêu đề là bắt buộc.
* `BR-NM-03` Nội dung là bắt buộc, tối đa 1000 ký tự.
* `BR-NM-04` Đối tượng nhận là bắt buộc → lỗi `RECIPIENT_REQUIRED`.
* `BR-NM-05` Hỗ trợ 1 loại người nhận: `ALL`

### 3.3 Gửi thông báo

* `BR-NM-06` ALL → gửi toàn bộ cư dân.
* `BR-NM-07` User nhận thông báo phải có status = ACTIVE.

### 3.4 Tính bất biến

* `BR-NM-09` Không được chỉnh sửa sau khi tạo.
* `BR-NM-10` Không được xóa hoặc hủy sau khi tạo.

### 3.5 Audit Log

* `BR-NM-11` Tạo thông báo phải được ghi Audit Log.
