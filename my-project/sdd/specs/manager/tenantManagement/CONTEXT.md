# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng quản lý người thuê để lưu trữ thông tin người thuê chính đang đại diện cho một phòng.

Người thuê trong feature này là người có tài khoản đăng nhập riêng và được gán trực tiếp với một phòng cụ thể thuộc một cơ sở nhà trọ.

Những người ở cùng người thuê chính sẽ được quản lý trong feature Quản lý Người phụ thuộc và không có tài khoản đăng nhập riêng.

## 2. Nỗi đau của User

Nếu không có chức năng quản lý người thuê tập trung, hệ thống có thể gặp các vấn đề sau:

* Ban quản lý khó biết phòng nào đang có người thuê.
* Khó xác định ai là người thuê chính đại diện cho phòng.
* Dễ gán nhiều người thuê ACTIVE vào cùng một phòng.
* Khó tra cứu thông tin người thuê theo mã, số điện thoại, email hoặc CCCD/CMND.
* Khó kiểm soát tài khoản đăng nhập của người thuê.
* Khi người thuê kết thúc thuê, dữ liệu lịch sử có thể bị mất nếu xóa trực tiếp.
* Khó phân biệt người thuê chính với người phụ thuộc ở cùng phòng.

Vì vậy, hệ thống cần quản lý người thuê chính theo phòng, đảm bảo mỗi phòng chỉ có một người thuê ACTIVE tại một thời điểm.

## 3. Mục tiêu

Feature Quản lý Người thuê giúp Manager:

* Tạo tài khoản người thuê mới.
* Gán người thuê vào một phòng đang hoạt động.
* Tự động sinh mã người thuê duy nhất.
* Xem danh sách người thuê có phân trang.
* Tìm kiếm người thuê theo mã, tên, email, số điện thoại hoặc CCCD/CMND.
* Xem chi tiết thông tin người thuê.
* Xem thông tin phòng và cơ sở của người thuê.
* Xem danh sách người phụ thuộc liên quan.
* Kết thúc thuê bằng cách chuyển người thuê sang INACTIVE và vô hiệu hóa tài khoản đăng nhập.

## 4. Ràng buộc

* Người thuê phải được gán vào một phòng khi tạo mới.
* Phòng được chọn phải đang hoạt động.
* Mỗi phòng chỉ được có một người thuê ở trạng thái ACTIVE tại một thời điểm.
* Không cho phép tạo người thuê mới cho phòng đã có người thuê ACTIVE.
* Người thuê ACTIVE là tài khoản đại diện cho phòng.
* Người phụ thuộc không có tài khoản đăng nhập riêng.
* Mã người thuê do hệ thống tự động sinh.
* Mã người thuê là duy nhất và không được phép chỉnh sửa thủ công.
* Email đăng nhập phải là duy nhất trong hệ thống.
* Số CCCD/CMND phải là duy nhất trong toàn hệ thống.
* Số điện thoại phải là duy nhất đối với các người thuê đang ACTIVE.
* Khi người thuê kết thúc thuê, trạng thái người thuê chuyển thành INACTIVE.
* Khi người thuê kết thúc thuê, tài khoản đăng nhập của người thuê phải bị vô hiệu hóa.
* Sau khi người thuê chuyển sang INACTIVE, phòng có thể được gán cho người thuê mới.
* Chỉ cho phép xóa mềm, không xóa vật lý dữ liệu người thuê.
* Dữ liệu lịch sử phải được giữ lại sau khi người thuê kết thúc thuê.
* Thông tin audit phải được ghi nhận.

## 5. Định nghĩa trạng thái người thuê

```text
ACTIVE
- Người thuê hiện đang là tài khoản đại diện cho một phòng.
- Người thuê còn đang trong thời gian thuê.
- Tài khoản đăng nhập còn hoạt động.

INACTIVE
- Người thuê đã kết thúc thuê.
- Tài khoản đăng nhập bị vô hiệu hóa.
- Dữ liệu lịch sử vẫn được giữ lại để tra cứu.
```

## 6. Câu hỏi mở

* Khi tạo người thuê, hệ thống có cần tự sinh mật khẩu tạm thời và gửi email không?
* Có cần yêu cầu người thuê đổi mật khẩu trong lần đăng nhập đầu tiên không?
* Khi kết thúc thuê, người phụ thuộc có tự động chuyển sang INACTIVE không?
* Có cần lưu ngày bắt đầu thuê và ngày kết thúc thuê không?
* Có cần kiểm tra phòng đang có hợp đồng hiệu lực trước khi gán người thuê không?
* Có cần cho phép chuyển phòng cho người thuê không?
* Có cần lưu lịch sử các phòng người thuê đã từng ở không?
* Có cần che một phần CCCD/CMND khi hiển thị không?
