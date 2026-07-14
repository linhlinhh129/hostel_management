# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng quản lý người thuê để lưu trữ thông tin người thuê chính đang đại diện cho một phòng.

Người thuê trong feature này là người có tài khoản đăng nhập riêng và được gán trực tiếp với một phòng cụ thể thuộc một cơ sở nhà trọ.

Tài khoản người thuê chính bắt buộc phải được khởi tạo từ luồng ký kết hợp đồng thuê phòng (ở phân hệ Hợp đồng). Mỗi phòng tại một thời điểm chỉ có tối đa một người thuê chính ở trạng thái `ACTIVE`. Những người ở cùng người thuê chính sẽ được quản lý trong phân hệ Quản lý Người phụ thuộc và không có tài khoản đăng nhập riêng.

## 2. Nỗi đau của User

Nếu không có chức năng quản lý người thuê tập trung, hệ thống có thể gặp các vấn đề sau:

* Ban quản lý khó biết phòng nào đang có người thuê.
* Khó xác định ai là người thuê chính đại diện cho phòng.
* Dễ gán nhiều người thuê ACTIVE vào cùng một phòng.
* Khó tra cứu thông tin người thuê theo mã, số điện thoại, email hoặc CCCD/CMND.
* Khó kiểm soát tài khoản đăng nhập của người thuê (khóa/mở khóa tài khoản khi có vi phạm hoặc reset khi nhập sai nhiều lần).
* Khi người thuê kết thúc thuê, dữ liệu lịch sử có thể bị mất nếu xóa trực tiếp.
* Khó phân biệt người thuê chính với người phụ thuộc ở cùng phòng.

Vì vậy, hệ thống cần quản lý người thuê chính theo phòng, đảm bảo mỗi phòng chỉ có một người thuê ACTIVE tại một thời điểm và hỗ trợ lưu trữ lịch sử lâu dài.

## 3. Mục tiêu

Feature Quản lý Người thuê giúp Manager:

* Quản lý thông tin cư dân được tạo từ hợp đồng thuê phòng.
* Khóa hoặc mở khóa tài khoản đăng nhập của người thuê chính.
* Xem danh sách người thuê thuộc phạm vi quản lý có phân trang.
* Tìm kiếm người thuê theo mã, tên hoặc số điện thoại.
* Xem chi tiết hồ sơ người thuê (thông tin cá nhân, phòng đang ở, hợp đồng hiện tại, danh sách người phụ thuộc).
* Kết thúc thuê (trả phòng) bằng cách chuyển người thuê sang `INACTIVE` và giải phóng phòng.
* Xóa mềm người thuê đã ngừng hoạt động để lưu trữ.

## 4. Ràng buộc

* Tạo mới tài khoản người thuê phải thông qua luồng Hợp đồng (bị chặn tạo trực tiếp tại phân hệ này).
* Mỗi phòng chỉ được có tối đa một người thuê ở trạng thái `ACTIVE` tại một thời điểm.
* Mã người thuê do hệ thống tự động sinh, là duy nhất và không được phép chỉnh sửa thủ công.
* Email đăng nhập và số CCCD/CMND phải là duy nhất trong toàn hệ thống.
* Số điện thoại phải là duy nhất đối với các người thuê đang hoạt động.
* Khi người thuê kết thúc thuê, trạng thái người thuê chuyển thành `INACTIVE` và tài khoản bị vô hiệu hóa đăng nhập.
* Khi kết thúc thuê, phòng sẽ được giải phóng (gỡ liên kết `tenant_id` tại phòng).
* Chỉ cho phép xóa mềm đối với tài khoản `INACTIVE`, không xóa vật lý dữ liệu người thuê.
* Mọi thao tác khóa, mở khóa, cập nhật, kết thúc thuê đều phải ghi nhận log vào hệ thống.

## 5. Định nghĩa trạng thái người thuê

```text
ACTIVE
- Người thuê hiện đang là tài khoản đại diện cho một phòng.
- Tài khoản đăng nhập hoạt động bình thường.

LOCKED
- Tài khoản đăng nhập tạm thời bị khóa do Manager chủ động hoặc nhập sai mật khẩu quá số lần quy định.

INACTIVE
- Người thuê đã kết thúc thời gian thuê (trả phòng).
- Tài khoản đăng nhập bị vô hiệu hóa.
- Dữ liệu lịch sử vẫn được giữ lại để tra cứu.
```

## 6. Câu hỏi mở

* **Khi tạo người thuê, hệ thống có cần tự sinh mật khẩu tạm thời và gửi email không?**
  * *Trả lời:* Mật khẩu được sinh tự động đi kèm luồng đăng ký hợp đồng và gửi email thông tin tài khoản cho người thuê.
* **Có cần yêu cầu người thuê đổi mật khẩu trong lần đăng nhập đầu tiên không?**
  * *Trả lời:* Có, hệ thống sử dụng `FirstLoginServlet` để bắt buộc người thuê phải đổi mật khẩu trong lần đăng nhập đầu tiên.
* **Khi kết thúc thuê, người phụ thuộc có tự động chuyển sang INACTIVE không?**
  * *Trả lời:* Có, hồ sơ người phụ thuộc tự động chuyển sang trạng thái lưu trữ (Chỉ đọc) để bảo toàn lịch sử.
* **Có cần lưu ngày bắt đầu thuê và ngày kết thúc thuê không?**
  * *Trả lời:* Có, ngày bắt đầu được lấy từ hợp đồng và ngày kết thúc được Manager nhập khi xác nhận kết thúc thuê.
* **Có cần kiểm tra phòng đang có hợp đồng hiệu lực trước khi gán người thuê không?**
  * *Trả lời:* Có, vì luồng gán tài khoản bắt buộc đi liền với tạo hợp đồng mới.
* **Có cần cho phép chuyển phòng cho người thuê không?**
  * *Trả lời:* Chuyển phòng được xử lý bằng cách cập nhật/tạo hợp đồng mới tương ứng với phòng mới.
* **Có cần lưu lịch sử các phòng người thuê đã từng ở không?**
  * *Trả lời:* Có, các hợp đồng cũ vẫn được lưu trong cơ sở dữ liệu phục vụ đối soát tài chính lịch sử.
* **Có cần che một phần CCCD/CMND khi hiển thị không?**
  * *Trả lời:* Không che số CCCD đối với tài khoản người thuê chính (Manager cần đối soát trực tiếp), chỉ che CCCD đối với tài khoản người phụ thuộc.
