# RULE.md — Admin Dashboard

## 1. dashboard — Admin Dashboard

### 1.1 Phân quyền

* `BR-DB-01` Chỉ người dùng có vai trò ADMIN được truy cập Admin Dashboard.
* `BR-DB-02` Người dùng chưa đăng nhập phải được chuyển hướng về trang đăng nhập.
* `BR-DB-03` Người dùng đã đăng nhập nhưng không phải ADMIN phải nhận lỗi FORBIDDEN.

### 1.2 KPI Cards

* `BR-DB-04` Dashboard phải hiển thị đủ 4 KPI Cards: tổng doanh thu tháng, tổng số cơ sở, tổng thông báo, tổng audit log hôm nay.
* `BR-DB-05` Doanh thu tháng hiện tại chỉ tính hóa đơn có trạng thái `PAID` trong tháng đang xét.
* `BR-DB-06` Nếu không có dữ liệu, KPI Card hiển thị giá trị `0` — không được hiển thị `null` hay để trống.

### 1.3 Widget Thống kê Nhân sự

* `BR-DB-07` Widget thống kê nhân sự phải hiển thị: tổng nhân sự, số MANAGER, số OPERATOR.
* `BR-DB-08` Chỉ đếm nhân sự có vai trò MANAGER và OPERATOR — không đếm tài khoản ADMIN.

### 1.4 Widget Hoạt động Gần đây

* `BR-DB-09` Dashboard chỉ hiển thị tối đa 5 bản ghi Audit Log mới nhất, sắp xếp theo `createdAt` giảm dần.
* `BR-DB-10` Mỗi bản ghi hiển thị: thời gian (định dạng `dd/MM/yyyy HH:mm:ss`), người thực hiện, hành động.
* `BR-DB-11` Nếu không có bản ghi nào, hiển thị trạng thái "Chưa có hoạt động nào".

### 1.5 Widget Doanh thu theo Cơ sở

* `BR-DB-12` Doanh thu theo cơ sở chỉ tính trong tháng hiện tại (từ ngày 1 đến ngày hiện tại).
* `BR-DB-13` Chỉ tính hóa đơn có trạng thái `PAID` — bỏ qua UNPAID, OVERDUE, CANCELLED.
* `BR-DB-14` Hiển thị tất cả cơ sở ACTIVE, kể cả cơ sở có doanh thu = 0.
* `BR-DB-15` Mỗi hàng cơ sở phải hiển thị số hóa đơn chưa thanh toán (UNPAID) của tháng hiện tại.
* `BR-DB-16` Mỗi hàng cơ sở phải hiển thị số hóa đơn quá hạn (OVERDUE) của tháng hiện tại; nếu > 0 thì hiển thị badge cảnh báo đỏ.
* `BR-DB-17` Nếu không có dữ liệu, hiển thị thông báo "Chưa có dữ liệu doanh thu kỳ này".

### 1.6 Hiệu năng và Độ tin cậy

* `BR-DB-18` Nếu một nguồn dữ liệu bị lỗi, Dashboard vẫn hiển thị các phần còn lại với giá trị `0` cho phần lỗi — không được trả về HTTP 500 toàn bộ.
* `BR-DB-19` Thời gian phản hồi tối đa: 1 giây (P95).

### 1.7 Điều hướng và Tác vụ nhanh

* `BR-DB-20` Dashboard không cung cấp chức năng tạo, sửa, xóa dữ liệu trực tiếp.
* `BR-DB-21` Dashboard cung cấp Quick Actions để điều hướng nhanh tới: Thêm cơ sở, Thêm nhân sự, Tạo thông báo.
