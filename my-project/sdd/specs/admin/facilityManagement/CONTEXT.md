# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần quản lý nhiều cơ sở khác nhau. Mỗi cơ sở có thể là một nhà trọ, một khu nhà trọ hoặc một địa điểm cho thuê riêng.

Admin cần khai báo thông tin cơ sở trước khi hệ thống có thể quản lý phòng, người thuê và hợp đồng liên quan đến cơ sở đó.

## 2. Nỗi đau của User

Hiện tại, nếu việc tạo phòng được làm thủ công, Admin có thể gặp các vấn đề sau:

* Nhập sai mã phòng.
* Tạo trùng mã phòng.
* Tạo phòng vượt quá số tầng cho phép.
* Tạo phòng vượt quá số phòng tối đa mỗi tầng.
* Dữ liệu phòng không đồng bộ giữa các cơ sở.
* Khó kiểm soát cơ sở nào đang được sử dụng, cơ sở nào đã ngừng hoạt động.

Vì vậy, hệ thống cần cho phép Admin tạo cơ sở trước, sau đó tự động sinh danh sách phòng khi cơ sở được kích hoạt.

## 3. Mục tiêu

Feature Quản lý Cơ sở giúp Admin:

* Tạo mới cơ sở.
* Lưu thông tin mã cơ sở, tên cơ sở, địa chỉ, số tầng tối đa và số phòng tối đa mỗi tầng.
* Kích hoạt cơ sở để hệ thống tự động sinh danh sách phòng.
* Khóa cấu hình sau khi cơ sở đã ACTIVE.
* Vô hiệu hóa cơ sở không còn sử dụng.
* Cung cấp dữ liệu nền cho Quản lý Phòng, Quản lý Người thuê và Quản lý Hợp đồng.

## 4. Ràng buộc

* Chỉ Admin được quản lý cơ sở.
* Mã cơ sở là duy nhất trong toàn hệ thống.
* Mã cơ sở chỉ gồm chữ cái từ A-Z.
* Khi Admin nhập mã cơ sở bằng chữ thường, hệ thống tự động chuyển sang chữ in hoa.
* Số tầng tối đa từ 1 đến 99.
* Số phòng tối đa mỗi tầng từ 1 đến 99.
* Cơ sở mới tạo có trạng thái mặc định là DRAFT.
* Chỉ cơ sở DRAFT mới được kích hoạt.
* Khi cơ sở chuyển sang ACTIVE, hệ thống tự động sinh danh sách phòng.
* Sau khi cơ sở ACTIVE, không được chỉnh sửa mã cơ sở, địa chỉ, số tầng tối đa và số phòng tối đa mỗi tầng.
* Cơ sở INACTIVE không được tạo dữ liệu mới nhưng vẫn được xem lịch sử.

## 5. Quy tắc sinh mã phòng

Mã phòng được sinh theo cấu trúc:

```text
[Mã cơ sở][Tầng 2 chữ số][Số phòng 2 chữ số]
```

Ví dụ:

```text
HL0101
HL0504
MD1215
```

Công thức:

```text
roomCode = facilityCode + format(floor, "00") + format(roomNumber, "00")
```

Tổng số phòng:

```text
totalRooms = maxFloors * maxRoomsPerFloor
```

## 6. Câu hỏi mở

* Có cho phép Admin chỉnh sửa tên cơ sở sau khi ACTIVE không?
* Có cần lưu lịch sử thay đổi trạng thái cơ sở không?
* Khi sinh phòng thất bại, hệ thống rollback toàn bộ hay sinh lại một phần?
* Cơ sở INACTIVE có được kích hoạt lại trong tương lai không?
* Có cần phân quyền chi tiết giữa Admin và Manager không?
* Có cần Audit Log cho thao tác tạo, cập nhật, kích hoạt và vô hiệu hóa cơ sở không?
* Có cần giới hạn tổng số phòng tối đa của một cơ sở không?
