# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng tiếp nhận và xử lý yêu cầu từ người thuê (Cư dân) và nhân sự vận hành (Operator).

Người thuê có thể gửi yêu cầu hỗ trợ liên quan đến bảo trì, sự cố, vận hành hoặc các vấn đề phát sinh trong quá trình sinh sống tại nhà trọ. Operator có thể nhận yêu cầu sửa chỉ số điện nước gửi từ Manager.

Sau khi yêu cầu được gửi, Ban quản lý cần tiếp nhận, theo dõi, lên lịch hẹn và xác nhận hoàn thành hoặc từ chối yêu cầu.

## 2. Nỗi đau của User

Nếu không có chức năng tiếp nhận và xử lý yêu cầu tập trung, hệ thống có thể gặp các vấn đề sau:

* Người thuê không có kênh chính thức để gửi yêu cầu hỗ trợ.
* Ban quản lý dễ bỏ sót hoặc xử lý chậm yêu cầu của người thuê.
* Khó biết yêu cầu nào mới tạo, yêu cầu nào đã tiếp nhận, yêu cầu nào đang xử lý.
* Không rõ lịch hẹn xử lý sự cố cụ thể để thông báo cho cư dân.
* Không có lịch sử rõ ràng về quá trình xử lý yêu cầu.
* Khó kiểm tra lý do từ chối nếu yêu cầu không hợp lệ hoặc ngoài phạm vi hỗ trợ.
* Dữ liệu yêu cầu dễ bị mất nếu không lưu trữ tập trung.

Vì vậy, hệ thống cần chuẩn hóa quy trình tiếp nhận, lên lịch và xử lý yêu cầu của cư dân và Operator.

## 3. Mục tiêu

Feature Tiếp nhận và xử lý yêu cầu người thuê giúp hệ thống:

* Cho phép người thuê gửi yêu cầu hỗ trợ (sự cố/khiếu nại).
* Tự động liên kết yêu cầu với người thuê, phòng và cơ sở hiện tại.
* Cho phép Ban quản lý xem danh sách yêu cầu thuộc phạm vi quản lý.
* Cho phép Ban quản lý tiếp nhận yêu cầu mới.
* Cho phép Ban quản lý cập nhật lịch hẹn xử lý sự cố (schedule appointment).
* Cho phép Ban quản lý xác nhận hoàn thành yêu cầu, nhập ghi chú giải quyết và tải lên hình ảnh nghiệm thu thực tế.
* Cho phép Ban quản lý từ chối yêu cầu và ghi rõ lý do.
* Lưu lịch sử xử lý yêu cầu theo từng mốc thời gian và hiển thị thành timeline.
* Đảm bảo mọi thay đổi trạng thái đều được ghi nhận minh bạch và ghi log.

## 4. Ràng buộc

* Chỉ người thuê có tài khoản ACTIVE mới được gửi yêu cầu.
* Khi người thuê gửi yêu cầu, hệ thống tự động liên kết yêu cầu với phòng và cơ sở hiện tại.
* Tiêu đề và nội dung mô tả yêu cầu là bắt buộc.
* Yêu cầu mới được tạo có trạng thái mặc định là `PENDING` hoặc `NEW`.
* Chỉ yêu cầu có trạng thái `PENDING` hoặc `NEW` mới được tiếp nhận.
* Chỉ các yêu cầu chưa hoàn thành hoặc chưa bị từ chối mới được cập nhật lịch hẹn.
* Khi đặt lịch hẹn xử lý sự cố, hệ thống chuyển trạng thái yêu cầu sang `IN_PROGRESS`.
* Khi từ chối yêu cầu, bắt buộc phải nhập lý do từ chối (`reason`).
* Khi xác nhận hoàn thành yêu cầu, bắt buộc phải nhập ghi chú kết quả sửa chữa (`notes`).
* Yêu cầu đã hoàn thành sẽ chuyển sang trạng thái `DONE`.
* Không cho phép xóa cứng dữ liệu yêu cầu.
* File đính kèm hình ảnh sau khi sửa chữa được kiểm tra định dạng và kích thước tối đa 10MB trước khi lưu trữ vào `/uploads/requests/`.

## 5. Luồng trạng thái yêu cầu

Luồng xử lý chính:

```text
NEW / PENDING → RECEIVED → IN_PROGRESS → DONE
```

Luồng từ chối hoặc hủy:

```text
NEW / PENDING / RECEIVED → REJECTED
NEW / PENDING / RECEIVED → CANCELLED (Cư dân tự hủy)
```

Ý nghĩa trạng thái:

```text
NEW / PENDING - Yêu cầu mới khởi tạo, chờ xử lý
RECEIVED      - Ban quản lý đã tiếp nhận yêu cầu
ASSIGNED      - Yêu cầu đã phân công cho nhân sự (cho Operator)
IN_PROGRESS   - Đang tiến hành xử lý yêu cầu (đã hẹn lịch)
DONE          - Yêu cầu đã được xử lý xong
REJECTED      - Yêu cầu bị Ban quản lý từ chối
CANCELLED     - Yêu cầu do Cư dân tự hủy bỏ
```

## 6. Câu hỏi mở

* **Có cần cho phép người thuê hủy yêu cầu khi yêu cầu còn ở trạng thái NEW/PENDING không?**
  * *Trả lời:* Có, hệ thống hỗ trợ trạng thái `CANCELLED` khi cư dân chủ động hủy yêu cầu trên giao diện của họ.
* **Có cần cho phép Ban quản lý đổi nhân sự được phân công không?**
  * *Trả lời:* Không, hệ thống phân công trực tiếp Operator xử lý sự cố chỉ số hoặc Manager tự xử lý qua lịch hẹn.
* **Có cần cho phép nhân sự thêm ghi chú xử lý nhiều lần không?**
  * *Trả lời:* Không, chỉ ghi nhận ghi chú giải quyết duy nhất 1 lần khi Manager hoặc nhân sự xác nhận hoàn thành yêu cầu.
* **Có cần người thuê xác nhận yêu cầu đã được xử lý xong không?**
  * *Trả lời:* Không, trạng thái hoàn tất `DONE` được cập nhật chủ động bởi Manager hoặc nhân sự kỹ thuật.
* **Có cần theo dõi SLA hoặc thời hạn xử lý yêu cầu không?**
  * *Trả lời:* Không được triển khai.
* **Có cần phân loại mức độ ưu tiên của yêu cầu không?**
  * *Trả lời:* Không được triển khai.
* **Có cần gửi thông báo cho người thuê khi trạng thái yêu cầu thay đổi không?**
  * *Trả lời:* Có, dòng thời gian lịch sử trạng thái cập nhật động trên giao diện của cả cư dân và Manager.
* **Có cần giới hạn số lượng file đính kèm hoặc dung lượng file không?**
  * *Trả lời:* Có, giới hạn kích thước tệp tải lên tối đa 10MB cho mỗi file hình ảnh, chỉ hỗ trợ JPG, PNG, PDF.
* **Có cần cho phép từ chối yêu cầu sau khi đã gán lịch hẹn xử lý (IN_PROGRESS) không?**
  * *Trả lời:* Không, sau khi đã thiết lập lịch hẹn xử lý sự cố, hệ thống chuyển sang trạng thái đang tiến hành sửa chữa, chỉ hỗ trợ đóng yêu cầu với trạng thái `DONE`.
