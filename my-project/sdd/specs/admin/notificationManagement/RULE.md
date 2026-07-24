## 3. notificationManagement — Quản lý Thông báo

### 3.2 Tạo thông báo

* `BR-NM-01` Tiêu đề là bắt buộc.
* `BR-NM-02 ` Nội dung là bắt buộc, tối đa 1000 ký tự.
* `BR-NM-03` Đối tượng nhận là bắt buộc → lỗi `RECIPIENT_REQUIRED`.
* `BR-NM-04` Hỗ trợ 1 loại người nhận: `ALL`

### 3.3 Gửi thông báo

* `BR-NM-05` ALL → gửi toàn bộ user trong hệ thống.

### 3.4 Tính bất biến

* `BR-NM-06` Không được chỉnh sửa sau khi tạo.
* `BR-NM-07` Không được xóa hoặc hủy sau khi tạo.
