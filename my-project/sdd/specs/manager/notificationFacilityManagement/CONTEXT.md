# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần cho phép Ban quản lý gửi thông báo đến người thuê trong phạm vi cơ sở mà họ được Admin phân công.

Thông báo có thể liên quan đến bảo trì, vận hành, quy định, sự cố hoặc các hoạt động tại từng cơ sở nhà trọ.

Khác với Admin, Manager không được gửi thông báo đến toàn bộ người thuê trong hệ thống. Manager chỉ được gửi thông báo theo cơ sở hoặc phòng thuộc phạm vi quản lý của mình.

## 2. Nỗi đau của User

Nếu không có chức năng thông báo riêng cho Ban quản lý, hệ thống có thể gặp các vấn đề sau:

* Manager khó truyền tải thông tin nhanh đến người thuê trong cơ sở mình phụ trách.
* Người thuê có thể không nhận được thông báo kịp thời về bảo trì, sự cố hoặc quy định.
* Manager có thể gửi nhầm thông báo đến cơ sở không thuộc phạm vi quản lý.
* Dễ xảy ra việc gửi thông báo vượt quyền đến toàn bộ hệ thống.
* Khó kiểm soát lịch sử các thông báo đã được Manager tạo.
* Khó xác định thông báo nào được gửi cho cơ sở nào hoặc phòng nào.
* Thiếu cơ chế kiểm tra quyền theo phạm vi cơ sở được phân công.

Vì vậy, hệ thống cần cho phép Manager tạo và quản lý thông báo trong phạm vi cơ sở được phân quyền.

## 3. Mục tiêu

Feature Quản lý Thông báo cho Ban quản lý giúp Manager:

* Tạo thông báo gửi đến người thuê trong cơ sở được phân công.
* Tạo thông báo gửi đến người thuê trong một phòng cụ thể thuộc cơ sở được phân công.
* Xem danh sách thông báo trong phạm vi cơ sở được phân công.
* Xem chi tiết nội dung, người tạo và phạm vi nhận thông báo.
* Đảm bảo thông báo không được gửi vượt quá phạm vi quản lý.
* Ghi nhận lịch sử thao tác tạo và xem thông báo.

## 4. Ràng buộc

* Manager chỉ được tạo thông báo cho cơ sở được Admin phân công.
* Manager chỉ được gửi thông báo đến phòng thuộc cơ sở được phân công.
* Manager không được gửi thông báo đến toàn bộ người thuê trong hệ thống.
* Manager không được gửi thông báo đến cơ sở ngoài phạm vi được phân công.
* Manager không được gửi thông báo đến phòng ngoài phạm vi được phân công.
* Chỉ người thuê đang ACTIVE mới là đối tượng nhận thông báo.
* Tiêu đề thông báo là bắt buộc.
* Nội dung thông báo là bắt buộc.
* Đối tượng nhận thông báo là bắt buộc.
* Nội dung thông báo tối đa 5000 ký tự.
* Danh sách thông báo phải hỗ trợ phân trang.
* Danh sách thông báo phải hỗ trợ tìm kiếm theo tiêu đề hoặc nội dung.
* Thông báo sau khi gửi thành công có trạng thái SENT.
* Hệ thống phải kiểm tra quyền truy cập cơ sở trước khi tạo hoặc xem thông báo.
* Hệ thống phải ghi nhận Audit Log cho thao tác tạo và xem thông báo.

## 5. Loại đối tượng nhận thông báo

Hệ thống hỗ trợ các loại đối tượng nhận sau:

```text
ALL      - Gửi toàn bộ người thuê trong hệ thống
FACILITY - Gửi theo cơ sở
ROOM     - Gửi theo phòng
```

Trong phạm vi của Manager:

```text
ALL      - Không được phép sử dụng
FACILITY - Được phép, nếu cơ sở thuộc phạm vi được phân công
ROOM     - Được phép, nếu phòng thuộc cơ sở được phân công
```

## 6. Câu hỏi mở

* Có cần cho phép Manager lưu nháp thông báo trước khi gửi không?
* Có cần Admin duyệt thông báo do Manager tạo trước khi gửi không?
* Có cần giới hạn số thông báo Manager được gửi mỗi ngày không?
* Có cần gửi thông báo đến nhiều cơ sở cùng lúc nếu Manager quản lý nhiều cơ sở không?
* Có cần gửi thông báo đến nhiều phòng cùng lúc không?
* Có cần lưu danh sách người thuê thực tế nhận thông báo tại thời điểm gửi không?
* Nếu người thuê chuyển phòng sau khi thông báo được gửi, lịch sử người nhận có thay đổi không?
* Có cần chức năng xác nhận người thuê đã đọc thông báo không?
* Có cần thống kê số người đã xem thông báo không?
