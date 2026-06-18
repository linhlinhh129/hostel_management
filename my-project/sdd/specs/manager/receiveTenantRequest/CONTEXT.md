# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng tiếp nhận và xử lý yêu cầu từ người thuê.

Người thuê có thể gửi yêu cầu hỗ trợ liên quan đến bảo trì, sự cố, vận hành hoặc các vấn đề phát sinh trong quá trình sinh sống tại nhà trọ.

Sau khi yêu cầu được gửi, Ban quản lý cần tiếp nhận, theo dõi, phân công nhân sự phụ trách và cập nhật tiến độ xử lý.

## 2. Nỗi đau của User

Nếu không có chức năng tiếp nhận và xử lý yêu cầu tập trung, hệ thống có thể gặp các vấn đề sau:

* Người thuê không có kênh chính thức để gửi yêu cầu hỗ trợ.
* Ban quản lý dễ bỏ sót hoặc xử lý chậm yêu cầu của người thuê.
* Khó biết yêu cầu nào mới tạo, yêu cầu nào đã tiếp nhận, yêu cầu nào đang xử lý.
* Không rõ nhân sự nào đang phụ trách yêu cầu.
* Nhân sự có thể xử lý nhầm yêu cầu không được phân công.
* Không có lịch sử rõ ràng về quá trình xử lý yêu cầu.
* Khó kiểm tra lý do từ chối nếu yêu cầu không hợp lệ hoặc ngoài phạm vi hỗ trợ.
* Dữ liệu yêu cầu dễ bị mất nếu không lưu trữ tập trung.

Vì vậy, hệ thống cần chuẩn hóa quy trình tiếp nhận, phân công và xử lý yêu cầu người thuê.

## 3. Mục tiêu

Feature Tiếp nhận và xử lý yêu cầu người thuê giúp hệ thống:

* Cho phép người thuê gửi yêu cầu hỗ trợ.
* Tự động liên kết yêu cầu với người thuê, phòng và cơ sở hiện tại.
* Cho phép Ban quản lý xem danh sách yêu cầu.
* Cho phép Ban quản lý tiếp nhận yêu cầu mới.
* Cho phép Ban quản lý phân công yêu cầu cho nhân sự phụ trách.
* Cho phép nhân sự được phân công cập nhật trạng thái xử lý.
* Cho phép Ban quản lý từ chối yêu cầu và ghi rõ lý do.
* Lưu lịch sử xử lý yêu cầu theo từng mốc thời gian.
* Đảm bảo mọi thay đổi trạng thái đều được ghi nhận minh bạch.

## 4. Ràng buộc

* Chỉ người thuê có tài khoản ACTIVE mới được gửi yêu cầu.
* Khi người thuê gửi yêu cầu, hệ thống phải tự động liên kết yêu cầu với phòng và cơ sở hiện tại.
* Tiêu đề yêu cầu là bắt buộc.
* Nội dung mô tả là bắt buộc.
* Loại yêu cầu là bắt buộc.
* Yêu cầu mới được tạo có trạng thái mặc định là NEW.
* Chỉ yêu cầu có trạng thái NEW mới được tiếp nhận.
* Chỉ yêu cầu có trạng thái RECEIVED mới được phân công xử lý.
* Một yêu cầu chỉ được phân công cho một nhân sự tại một thời điểm.
* Chỉ nhân sự được phân công mới được cập nhật trạng thái xử lý yêu cầu.
* Yêu cầu có trạng thái ASSIGNED mới được chuyển sang IN_PROGRESS.
* Yêu cầu có trạng thái IN_PROGRESS mới được chuyển sang RESOLVED.
* Khi từ chối yêu cầu, bắt buộc phải nhập lý do từ chối.
* Yêu cầu đã ở trạng thái REJECTED hoặc RESOLVED không được tiếp nhận hoặc phân công lại.
* Mọi thay đổi trạng thái phải được lưu vào lịch sử xử lý và Audit Log.
* Không cho phép xóa cứng dữ liệu yêu cầu.
* File đính kèm nếu có chỉ lưu URL hoặc đường dẫn file, không lưu trực tiếp nội dung file trong bảng request.

## 5. Luồng trạng thái yêu cầu

Luồng xử lý chính:

```text
NEW → RECEIVED → ASSIGNED → IN_PROGRESS → RESOLVED
```

Luồng từ chối:

```text
NEW / RECEIVED → REJECTED
```

Ý nghĩa trạng thái:

```text
NEW         - Người thuê vừa tạo yêu cầu
RECEIVED    - Ban quản lý đã tiếp nhận yêu cầu
ASSIGNED    - Yêu cầu đã được phân công cho nhân sự
IN_PROGRESS - Nhân sự đang xử lý yêu cầu
RESOLVED    - Yêu cầu đã được xử lý xong
REJECTED    - Yêu cầu bị từ chối
```

## 6. Câu hỏi mở

* Có cần cho phép người thuê hủy yêu cầu khi yêu cầu còn ở trạng thái NEW không?
* Có cần cho phép Ban quản lý đổi nhân sự được phân công không?
* Có cần cho phép nhân sự thêm ghi chú xử lý nhiều lần không?
* Có cần người thuê xác nhận yêu cầu đã được xử lý xong không?
* Có cần theo dõi SLA hoặc thời hạn xử lý yêu cầu không?
* Có cần phân loại mức độ ưu tiên của yêu cầu như LOW, MEDIUM, HIGH không?
* Có cần gửi thông báo cho người thuê khi trạng thái yêu cầu thay đổi không?
* Có cần giới hạn số lượng file đính kèm hoặc dung lượng file không?
* Có cần cho phép từ chối yêu cầu sau khi đã ASSIGNED hoặc IN_PROGRESS không?
