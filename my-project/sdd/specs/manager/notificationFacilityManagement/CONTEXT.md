# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần cho phép Ban quản lý gửi thông báo đến người thuê trong phạm vi cơ sở mà họ được Admin phân công.

Thông báo có thể liên quan đến bảo trì, vận hành, quy định, sự cố hoặc các hoạt động tại từng cơ sở nhà trọ. Ngoài ra, Manager cũng cần đôn đốc thu hồi nợ bằng cách gửi thông báo nhắc nợ quá hạn tiền phòng trực tiếp tới các phòng chưa thanh toán hóa đơn, cũng như báo cáo các sự cố sai lệch chỉ số điện nước gửi Operator kiểm tra.

Khác với Admin, Manager không được gửi thông báo đến toàn bộ người thuê trong hệ thống. Manager chỉ được gửi thông báo theo cơ sở hoặc phòng thuộc phạm vi quản lý của mình.

## 2. Nỗi đau của User

Nếu không có chức năng thông báo riêng cho Ban quản lý, hệ thống có thể gặp các vấn đề sau:

* Manager khó truyền tải thông tin nhanh đến người thuê trong cơ sở mình phụ trách.
* Người thuê có thể không nhận được thông báo kịp thời về bảo trì, sự cố hoặc quy định.
* Manager khó đôn đốc cư dân thanh toán các khoản tiền phòng bị quá hạn định kỳ.
* Khó khăn trong việc báo cáo và phân công sửa chữa khi phát hiện chỉ số điện nước của hóa đơn bị nhập sai.
* Manager có thể gửi nhầm thông báo đến cơ sở không thuộc phạm vi quản lý.
* Dễ xảy ra việc gửi thông báo vượt quyền đến toàn bộ hệ thống.
* Khó kiểm soát lịch sử các thông báo đã được Manager tạo.
* Thiếu cơ chế kiểm tra quyền theo phạm vi cơ sở được phân công.

Vì vậy, hệ thống cần cho phép Manager tạo và quản lý thông báo, nhắc nợ, và báo cáo sai số điện nước trong phạm vi cơ sở được phân quyền.

## 3. Mục tiêu

Feature Quản lý Thông báo cho Ban quản lý giúp Manager:

* Tạo thông báo gửi đến người thuê trong cơ sở được phân công.
* Tạo thông báo gửi đến người thuê trong một phòng cụ thể thuộc cơ sở được phân công.
* Gửi nhắc nợ tiền phòng dựa trên hóa đơn chưa thanh toán tới phòng tương ứng.
* Báo cáo sai số điện nước của hóa đơn và gửi yêu cầu sửa đổi cho Operator phụ trách.
* Xem danh sách thông báo và lọc theo các tab phân hệ (thông báo chung, nhắc nợ, hóa đơn bị báo sai chỉ số).
* Đảm bảo thông báo không được gửi vượt quá phạm vi quản lý.
* Ghi nhận lịch sử thao tác tạo và xem thông báo.

## 4. Ràng buộc

* Manager chỉ được tạo thông báo cho cơ sở được Admin phân công.
* Manager chỉ được gửi thông báo đến phòng thuộc cơ sở được phân công.
* Manager không được gửi thông báo đến toàn bộ người thuê trong hệ thống.
* Manager không được gửi thông báo đến cơ sở/phòng ngoài phạm vi được phân công.
* Chỉ người thuê đang ACTIVE trong phòng mới là đối tượng nhận thông báo.
* Tiêu đề và nội dung thông báo là bắt buộc.
* Đối tượng nhận thông báo là bắt buộc.
* Danh sách thông báo phải hỗ trợ phân trang và tìm kiếm theo tiêu đề hoặc nội dung.
* Thông báo sau khi gửi thành công có trạng thái `SENT`.
* Khi báo cáo sai chỉ số điện nước, trạng thái chỉ số điện nước phải chuyển sang `REPORTED`.
* Yêu cầu gửi cho Operator được lưu trong bảng `requests` với danh mục `UTILITY` và trạng thái `PENDING`.
* Hệ thống phải kiểm tra quyền truy cập cơ sở và ghi Audit Log cho mọi hành động.

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

* **Có cần cho phép Manager lưu nháp thông báo trước khi gửi không?**
  * *Trả lời:* Không, hệ thống gửi trực tiếp sang trạng thái `SENT` mà không hỗ trợ lưu nháp.
* **Có cần Admin duyệt thông báo do Manager tạo trước khi gửi không?**
  * *Trả lời:* Không, Manager được toàn quyền gửi thông báo trong phạm vi cơ sở phụ trách.
* **Có cần giới hạn số thông báo Manager được gửi mỗi ngày không?**
  * *Trả lời:* Không giới hạn số lượng thông báo gửi mỗi ngày.
* **Có cần gửi thông báo đến nhiều cơ sở cùng lúc nếu Manager quản lý nhiều cơ sở không?**
  * *Trả lời:* Không, form gửi thông báo chung chỉ cho phép chọn 1 cơ sở mỗi lần tạo.
* **Có cần gửi thông báo đến nhiều phòng cùng lúc không?**
  * *Trả lời:* Không, chỉ hỗ trợ gửi tới 1 phòng cụ thể hoặc toàn bộ phòng thuộc 1 cơ sở.
* **Có cần lưu danh sách người thuê thực tế nhận thông báo tại thời điểm gửi không?**
  * *Trả lời:* Không, hệ thống liên kết theo `facility_id` hoặc `room_id`, dữ liệu người nhận được xác định động tại thời điểm cư dân truy cập.
* **Nếu người thuê chuyển phòng sau khi thông báo được gửi, lịch sử người nhận có thay đổi không?**
  * *Trả lời:* Có, người thuê sẽ nhìn thấy thông báo được gửi cho phòng mới của họ thay vì phòng cũ.
* **Có cần chức năng xác nhận người thuê đã đọc thông báo hoặc thống kê lượt xem không?**
  * *Trả lời:* Không, hệ thống hiện tại chưa hỗ trợ ghi nhận trạng thái đã đọc hay thống kê lượt xem.
