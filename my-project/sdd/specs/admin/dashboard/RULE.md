# RULE.md — Admin Dashboard

## 1. dashboard — Admin Dashboard

### 1.1 Phân quyền

* `BR-DB-01` Chỉ người dùng có vai trò ADMIN được truy cập Admin Dashboard.
* `BR-DB-02` Người dùng chưa đăng nhập phải được chuyển hướng về trang đăng nhập.
* `BR-DB-03` Người dùng đã đăng nhập nhưng không phải ADMIN phải nhận lỗi `FORBIDDEN`.

### 1.2 KPI Cards

* `BR-DB-04` Dashboard phải hiển thị đủ 5 KPI Cards: tổng doanh thu tháng, tổng số cơ sở, tổng nhân sự ACTIVE, tổng thông báo, tổng audit log hôm nay.
* `BR-DB-05` Doanh thu tháng hiện tại chỉ tính hóa đơn có trạng thái `PAID` trong tháng đang xét.
* `BR-DB-06` Nếu không có dữ liệu, KPI Card hiển thị giá trị `0` — không được hiển thị `null` hay để trống.

### 1.3 Widget Thống kê Cơ sở

* `BR-DB-07` Widget thống kê cơ sở phải hiển thị: tổng số cơ sở, số ACTIVE, số DRAFT, số INACTIVE.
* `BR-DB-08` Số liệu phản ánh trạng thái thực tế tại thời điểm request.

### 1.4 Widget Thống kê Nhân sự

* `BR-DB-09` Widget thống kê nhân sự phải hiển thị: tổng nhân sự, số MANAGER, số OPERATOR.
* `BR-DB-10` Chỉ đếm nhân sự có vai trò MANAGER và OPERATOR — không đếm tài khoản ADMIN.

### 1.5 Widget Hoạt động Gần đây

* `BR-DB-11` Dashboard chỉ hiển thị tối đa 10 bản ghi Audit Log mới nhất, sắp xếp theo `createdAt` giảm dần.
* `BR-DB-12` Mỗi bản ghi hiển thị: thời gian, người thực hiện, hành động.
* `BR-DB-13` Nếu không có bản ghi nào, hiển thị trạng thái "Chưa có hoạt động nào".

### 1.6 Widget Doanh thu theo Cơ sở

* `BR-DB-14` Doanh thu theo cơ sở chỉ tính trong tháng hiện tại (từ ngày 1 đến ngày hiện tại).
* `BR-DB-15` Chỉ tính hóa đơn có trạng thái `PAID` — bỏ qua UNPAID, OVERDUE, CANCELLED.
* `BR-DB-16` Hiển thị tất cả cơ sở ACTIVE, kể cả cơ sở có doanh thu = 0.

### 1.7 Hiệu năng và Độ tin cậy

* `BR-DB-17` Dữ liệu Dashboard được cache tối đa 60 giây để tránh query nặng khi nhiều Admin truy cập đồng thời.
* `BR-DB-18` Nếu một nguồn dữ liệu bị lỗi (vd: service Audit Log timeout), Dashboard vẫn trả về phần còn lại với giá trị `null` cho phần lỗi — không được trả về HTTP 500 toàn bộ.
* `BR-DB-19` Thời gian phản hồi tối đa: 1 giây (P95).

### 1.8 Điều hướng

* `BR-DB-20` Mỗi KPI Card và widget phải có link điều hướng tới module tương ứng.
* `BR-DB-21` Dashboard không cung cấp chức năng tạo, sửa, xóa dữ liệu trực tiếp.
