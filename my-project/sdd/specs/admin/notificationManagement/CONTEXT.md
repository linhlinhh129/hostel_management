# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng thông báo để admin truyền tải thông tin đến user một cách nhanh chóng.

Thông báo có thể liên quan đến bảo trì, vận hành, quy định, sự cố hoặc các hoạt động chung của cơ sở.

## 2. Nỗi đau của User

Hiện tại, nếu không có chức năng thông báo tập trung, admin có thể gặp các vấn đề sau:

* Khó gửi thông tin đến toàn bộ người dùng cùng lúc.
* Dễ bỏ sót người nhận khi thông báo thủ công.
* Khó gửi thông báo theo từng cơ sở hoặc từng phòng.
* Không có nơi lưu lại lịch sử các thông báo đã tạo.
* Người quản lý khó kiểm tra lại nội dung và đối tượng nhận của thông báo cũ.

Vì vậy, hệ thống cần cho phép Admin tạo và quản lý thông báo trong hệ thống.

## 3. Mục tiêu

Feature Quản lý Thông báo giúp Admin:

* Tạo thông báo mới.
* Gửi thông báo đến toàn bộ người dùng.
* Xem danh sách thông báo đã tạo.
* Xem chi tiết nội dung và đối tượng nhận của từng thông báo.

## 4. Ràng buộc

* Chỉ người dùng có quyền mới được truy cập chức năng quản lý thông báo.
* Chỉ người dùng có quyền tạo thông báo mới được tạo thông báo.
* Tiêu đề thông báo là bắt buộc.
* Nội dung thông báo là bắt buộc.
* Đối tượng nhận thông báo là bắt buộc.
* Nội dung thông báo tối đa 1000 ký tự.
* Danh sách thông báo phải hỗ trợ phân trang.
* Hệ thống cần ghi nhận Audit Log cho thao tác tạo thông báo.
* Trong phạm vi hiện tại, hệ thống chưa hỗ trợ gửi Email, SMS hoặc Push Notification.

## 5. Câu hỏi mở

* Có cần phân biệt thông báo gửi cho toàn bộ người thuê, theo cơ sở và theo phòng bằng các loại recipientType riêng không?
* Có cần lưu danh sách người nhận thực tế tại thời điểm tạo thông báo không?
* Nếu người thuê chuyển phòng sau khi thông báo được tạo, lịch sử người nhận có thay đổi không?
* Có cần trạng thái thông báo như DRAFT, SENT không?
* Có cho phép Admin chỉnh sửa thông báo sau khi đã tạo trong tương lai không?
* Có cần chức năng xác nhận người thuê đã đọc thông báo không?
* Có cần thống kê số lượng người đã xem thông báo không?
