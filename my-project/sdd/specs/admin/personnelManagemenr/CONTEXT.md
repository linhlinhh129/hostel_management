# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng quản lý nhân sự để Admin kiểm soát các tài khoản nội bộ trong hệ thống.

Nhân sự có thể thuộc các vai trò như Ban Quản Lý, Nhân viên vận hành. Mỗi vai trò sẽ có phạm vi thao tác khác nhau trong hệ thống.

Đối với Ban Quản Lý và Nhân viên vận hành, hệ thống cần xác định rõ nhân sự đó được phân công quản lý cơ sở nào để giới hạn dữ liệu được phép xem và thao tác.

## 2. Nỗi đau của User

Nếu không có chức năng quản lý nhân sự tập trung, Admin có thể gặp các vấn đề sau:

* Khó kiểm soát ai đang có quyền truy cập hệ thống.
* Khó phân biệt vai trò và trách nhiệm của từng nhân sự.
* Nhân sự có thể xem hoặc thao tác dữ liệu ngoài phạm vi được phân công.
* Khó khóa tài khoản khi nhân sự nghỉ việc hoặc tạm ngưng công tác.
* Dễ tạo trùng email hoặc số điện thoại.
* Khó kiểm soát người phụ trách tài chính nếu có nhiều tài khoản cùng hoạt động.
* Không có lịch sử rõ ràng khi tạo, cập nhật, khóa hoặc gán cơ sở cho nhân sự.

Vì vậy, hệ thống cần cho phép Admin quản lý nhân sự, vai trò, trạng thái tài khoản và phạm vi cơ sở được phân công.

## 3. Mục tiêu

Feature Quản lý Nhân sự giúp Admin:

* Xem danh sách nhân sự.
* Xem chi tiết thông tin nhân sự.
* Tạo mới tài khoản nhân sự.
* Cập nhật thông tin nhân sự.
* Gán vai trò cho nhân sự.
* Gán cơ sở quản lý cho nhân sự.
* Khóa hoặc mở khóa tài khoản nhân sự.
* Giới hạn phạm vi dữ liệu theo cơ sở được phân công.
* Đảm bảo chỉ có tối đa một tài khoản Quản lý tài chính đang hoạt động.

## 4. Ràng buộc

* Chỉ Admin được truy cập chức năng Quản lý Nhân sự.
* Không cho phép tạo mới tài khoản Admin từ chức năng này.
* Không cho phép thay đổi vai trò của nhân sự thành Admin.
* Hệ thống chỉ hỗ trợ các vai trò cố định: ADMIN, MANAGER, OPERATOR.
* Chỉ được quản lý các nhân sự thuộc vai trò MANAGER, OPERATOR.
* Email phải đúng định dạng và là duy nhất trong hệ thống.
* Số điện thoại chỉ gồm chữ số, có độ dài 10 ký tự và là duy nhất trong hệ thống.
* Mã nhân sự được hệ thống tự động sinh và không cho phép chỉnh sửa.
* Khi tạo nhân sự mới, hệ thống phải tạo mật khẩu tạm thời và gửi đến email của nhân sự.
* Nhân sự phải đổi mật khẩu khi đăng nhập lần đầu bằng mật khẩu tạm thời.
* Nhân sự có vai trò MANAGER hoặc OPERATOR phải được gán duy nhất một cơ sở quản lý.
* Chỉ được gán nhân sự vào các cơ sở đang ACTIVE.
* Không được xóa cứng nhân sự khỏi cơ sở dữ liệu.
* Mọi thao tác tạo, cập nhật, khóa/mở khóa và gán cơ sở quản lý phải được ghi log.

## 5. Câu hỏi mở

* Có cần cho phép một nhân sự có nhiều vai trò cùng lúc không?
* Khi nhân sự bị khóa tài khoản, có giữ nguyên danh sách cơ sở đã được gán không?
* Khi cơ sở chuyển sang INACTIVE, nhân sự đang được gán cơ sở đó sẽ xử lý như thế nào?
* Có cần chức năng reset mật khẩu cho nhân sự không?
* Có cần gửi lại mật khẩu tạm thời nếu nhân sự chưa đăng nhập lần đầu không?
* Có cần giới hạn số lượng cơ sở tối đa mà một nhân sự có thể quản lý không?
* Có cần tách riêng quyền của MANAGER và TECHNICIAN theo từng loại nghiệp vụ không?
