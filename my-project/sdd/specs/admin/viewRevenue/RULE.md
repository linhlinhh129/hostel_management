
## 4. viewRevenue — Xem Báo cáo Doanh thu

### 4.1 Phân quyền

* `BR-VR-01` Chỉ ADMIN được truy cập.

### 4.2 Nguồn dữ liệu

* `BR-VR-02` Chỉ tính từ hóa đơn hợp lệ.

### 4.3 Bộ lọc thời gian

* `BR-VR-03` `period` không hợp lệ hoặc để trống → fallback về tháng hiện tại.
* `BR-VR-04` Không có dữ liệu → trả kết quả rỗng.

### 4.4 Nội dung báo cáo

* `BR-VR-05` Hiển thị: mã cơ sở, tên cơ sở, số hóa đơn, doanh thu, đã thanh toán, chưa thanh toán.
* `BR-VR-06` Hỗ trợ phân trang.
