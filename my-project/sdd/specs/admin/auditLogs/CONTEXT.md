# CONTEXT.md

## 1. Bối cảnh

Hệ thống quản lý nhà trọ cần có chức năng theo dõi nhật ký hoạt động để Admin có thể truy vết toàn bộ các hành động quan trọng được thực hiện trong hệ thống.

Audit Log ghi nhận mọi thao tác thay đổi dữ liệu (tạo, cập nhật, xóa) từ tất cả các module nghiệp vụ như Quản lý Nhân sự, Quản lý Cơ sở, Quản lý Hợp đồng, Quản lý Thông báo, v.v.

Mỗi bản ghi nhật ký bao gồm thông tin về đối tượng bị tác động, loại hành động, giá trị trước và sau thay đổi, địa chỉ IP và người thực hiện.

## 2. Nỗi đau của User

Nếu không có chức năng xem nhật ký hệ thống tập trung, Admin có thể gặp các vấn đề sau:

* Không thể xác định ai đã thực hiện thay đổi dữ liệu quan trọng trong hệ thống.
* Khó điều tra nguyên nhân khi dữ liệu bị sai lệch hoặc xảy ra sự cố.
* Không có cơ sở để kiểm tra khi có tranh chấp về thao tác của nhân sự.
* Không đáp ứng được yêu cầu kiểm toán nội bộ hoặc kiểm tra bảo mật.
* Mất nhiều thời gian khi phải truy vết thủ công qua nhiều màn hình khác nhau.
* Khó phát hiện hành vi bất thường hoặc truy cập trái phép.

Vì vậy, hệ thống cần cung cấp cho Admin một giao diện tập trung để xem, lọc và tra cứu lịch sử thao tác một cách nhanh chóng và chính xác.

## 3. Mục tiêu

Feature Xem Nhật ký Hệ thống giúp Admin:

* Xem danh sách toàn bộ nhật ký hoạt động trong hệ thống.
* Lọc nhật ký theo loại đối tượng (entity type).
* Lọc nhật ký theo loại hành động (CREATE, UPDATE, DELETE).
* Lọc nhật ký theo khoảng thời gian.
* Lọc nhật ký theo người thực hiện.
* Xem chi tiết giá trị trước và sau khi thay đổi.
* Đảm bảo nhật ký không thể bị sửa đổi hoặc xóa qua giao diện.

## 4. Ràng buộc

* Chỉ Admin được truy cập chức năng Xem Nhật ký Hệ thống.
* Audit Log chỉ hỗ trợ đọc, không cho phép tạo, sửa hoặc xóa thủ công.
* Danh sách nhật ký phải được sắp xếp theo thời gian tạo giảm dần (createdAt DESC).
* Danh sách phải hỗ trợ phân trang.
* Tham số lọc không hợp lệ phải trả về lỗi rõ ràng.
* fromDate không được lớn hơn toDate.
* Thời gian phản hồi tối đa 500ms (P95).

## 5. Câu hỏi mở

* Có cần lưu trữ audit log trong bao lâu trước khi archive hoặc xóa không?
* Có cần hỗ trợ xuất nhật ký ra file Excel/PDF trong tương lai không?
* Có cần thêm tính năng xem chi tiết từng bản ghi nhật ký qua màn hình riêng không?
* Có cần ghi nhận audit log cho cả thao tác đọc (read) không, hay chỉ ghi cho thao tác ghi (write)?
* Có cần cơ chế cảnh báo (alert) khi phát hiện hành vi bất thường không?
* Có cần phân quyền chi tiết hơn để MANAGER cũng được xem một phần nhật ký không?
