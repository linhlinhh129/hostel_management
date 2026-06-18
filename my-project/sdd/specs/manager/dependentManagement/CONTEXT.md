# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần lưu trữ thông tin những người đang ở cùng người thuê chính, ví dụ như vợ/chồng, con, bố mẹ hoặc người thân.

Những người này được gọi là người phụ thuộc. Người phụ thuộc không có tài khoản đăng nhập riêng trong hệ thống và bắt buộc phải được liên kết với một người thuê chính.

Feature Quản lý Người phụ thuộc giúp Manager quản lý thông tin người phụ thuộc để ban quản lý nắm được tình trạng cư trú thực tế trong từng phòng.

## 2. Nỗi đau của User

Nếu không quản lý người phụ thuộc, ban quản lý có thể gặp các vấn đề sau:

* Không biết chính xác trong phòng có những ai đang cư trú.
* Khó kiểm soát số lượng người ở cùng người thuê chính.
* Khó tra cứu thông tin người thân hoặc người đi kèm của người thuê.
* Dữ liệu cư trú không đầy đủ khi cần kiểm tra hoặc đối chiếu.
* Khó lưu lại lịch sử cư trú khi người thuê chính kết thúc thuê.
* Dễ nhầm lẫn giữa người thuê chính và người phụ thuộc.
* Không kiểm soát được CCCD/CMND của người phụ thuộc.

Vì vậy, hệ thống cần cho phép Manager thêm, xem, cập nhật và xóa mềm người phụ thuộc của từng người thuê.

## 3. Mục tiêu

Feature Quản lý Người phụ thuộc giúp Manager:

* Thêm người phụ thuộc cho một người thuê đang ACTIVE.
* Xem danh sách người phụ thuộc của từng người thuê.
* Cập nhật thông tin người phụ thuộc.
* Xóa mềm người phụ thuộc khi không còn cư trú cùng người thuê.
* Lưu trữ thông tin người phụ thuộc để phục vụ tra cứu lịch sử.
* Đảm bảo mỗi người phụ thuộc được liên kết với đúng một người thuê chính.

## 4. Ràng buộc

* Người phụ thuộc bắt buộc phải được liên kết với một người thuê chính.
* Người thuê chính phải tồn tại trong hệ thống.
* Người thuê chính phải có trạng thái ACTIVE khi thêm người phụ thuộc.
* Người phụ thuộc không có tài khoản đăng nhập riêng.
* Không cho phép tạo người phụ thuộc độc lập nếu không có người thuê chính.
* Họ và tên là bắt buộc.
* Quan hệ với người thuê chính là bắt buộc.
* Ngày sinh là bắt buộc.
* Số CCCD/CMND là bắt buộc.
* Số CCCD/CMND phải là duy nhất đối với người phụ thuộc đang ACTIVE.
* Chỉ cho phép xóa mềm người phụ thuộc, không xóa vật lý dữ liệu.
* Khi xóa người phụ thuộc, hệ thống chuyển trạng thái người phụ thuộc sang INACTIVE.
* Khi người thuê chính kết thúc thuê, danh sách người phụ thuộc liên quan vẫn được giữ lại để tra cứu lịch sử.

## 5. Câu hỏi mở

* Có cần giới hạn số lượng người phụ thuộc tối đa cho một người thuê không?
* Có cần kiểm tra số người trong phòng có vượt quá sức chứa phòng không?
* Có cần lưu ảnh CCCD/CMND của người phụ thuộc không?
* Có cần che một phần số CCCD/CMND khi hiển thị không?
* Có cần kiểm tra trùng CCCD/CMND với người thuê chính không?
* Có cần ghi Audit Log khi thêm, cập nhật hoặc xóa mềm người phụ thuộc không?
* Khi người thuê chính chuyển phòng, người phụ thuộc có tự động đi theo người thuê chính không?
* Khi người thuê chính kết thúc thuê, người phụ thuộc có tự động chuyển sang INACTIVE không?
