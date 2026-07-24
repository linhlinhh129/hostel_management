## 1. facilityManagement — Quản lý Cơ sở

### 1.1 Phân quyền

* `BR-FM-01` Chỉ ADMIN được tạo, cập nhật, kích hoạt và vô hiệu hóa cơ sở.



### 1.2 Mã cơ sở (Facility Code)

* `BR-FM-02` Mã cơ sở là bắt buộc, không được để trống.
* `BR-FM-03` Mã cơ sở chỉ được chứa chữ cái A–Z (không chứa số, khoảng trắng hoặc ký tự đặc biệt).
* `BR-FM-04` Mã cơ sở phải là duy nhất trong toàn hệ thống.
* `BR-FM-05` Khi Admin nhập mã cơ sở bằng chữ thường, hệ thống tự động chuyển sang chữ in hoa trước khi lưu.

### 1.3 Cấu hình cơ sở

* `BR-FM-06` Số tầng tối đa phải nằm trong khoảng từ 1 đến 10.
* `BR-FM-07` Số phòng tối đa mỗi tầng phải nằm trong khoảng từ 1 đến 30.
* `BR-FM-08` Địa chỉ là bắt buộc, không được để trống hoặc chỉ gồm khoảng trắng.
* `BR-FM-09` Tên cơ sở là bắt buộc, không được chỉ gồm khoảng trắng.

### 1.4 Vòng đời trạng thái

* `BR-FM-10` Cơ sở mới tạo luôn có trạng thái mặc định là `DRAFT`.
* `BR-FM-11` Luồng trạng thái một chiều: `DRAFT → ACTIVE → INACTIVE`.
* `BR-FM-12` Chỉ cơ sở ở trạng thái `DRAFT` mới được kích hoạt sang `ACTIVE`.
* `BR-FM-13` Chỉ cơ sở ở trạng thái `ACTIVE` mới được vô hiệu hóa sang `INACTIVE`.
* `BR-FM-14` Cơ sở đã chuyển sang `INACTIVE` không được kích hoạt lại trong phiên bản hiện tại.

### 1.5 Chỉnh sửa theo trạng thái

* `BR-FM-15` Khi cơ sở ở trạng thái `DRAFT`, Admin được phép chỉnh sửa toàn bộ thông tin.
* `BR-FM-16` Khi cơ sở đã `ACTIVE`, chỉ được phép chỉnh sửa tên cơ sở.
* `BR-FM-17` Khi cơ sở đã `ACTIVE`, nếu cần thay đổi cấu hình bị khóa thì phải tạo cơ sở mới.

### 1.6 Kích hoạt và sinh phòng tự động

* `BR-FM-18` Khi kích hoạt thành công, hệ thống tự động sinh danh sách phòng theo công thức:

```text
roomCode = facilityCode + floor(2 digits) + room(2 digits)
```

* `BR-FM-19` Tổng số phòng được sinh = `maxFloors × maxRoomsPerFloor`.
* `BR-FM-20` Mỗi phòng được sinh có trạng thái mặc định là `AVAILABLE`.
* `BR-FM-21` Mỗi mã phòng phải là duy nhất. Nếu trùng → lỗi `ROOM_001`.
* `BR-FM-22` Kích hoạt cơ sở và sinh phòng phải nằm trong cùng một transaction.

### 1.7 Vô hiệu hóa cơ sở

* `BR-FM-23` Không được xóa dữ liệu lịch sử khi vô hiệu hóa cơ sở.
* `BR-FM-24` Cơ sở `INACTIVE` không được tạo phòng mới hoặc hợp đồng mới.

### 1.8 Liên kết với Quản lý Phòng

* `BR-FM-25` Chỉ trả về cơ sở có trạng thái `ACTIVE` khi các module khác yêu cầu danh sách cơ sở.