# CONTEXT.md — Admin Dashboard

## 1. Bối cảnh

Hệ thống quản lý nhà trọ có nhiều module nghiệp vụ riêng lẻ (cơ sở, nhân sự, doanh thu, thông báo, audit log). Sau khi đăng nhập, Admin cần một màn hình trung tâm để nắm bắt tổng thể tình trạng hệ thống mà không phải vào từng module.

Admin Dashboard đóng vai trò là "bảng điều khiển" — tổng hợp các chỉ số quan trọng nhất từ tất cả module vào một màn hình duy nhất, đồng thời cung cấp điểm điều hướng nhanh tới từng chức năng.

## 2. Nỗi đau của User

Nếu không có Dashboard tập trung, Admin gặp các vấn đề sau:

* Phải vào từng module mới biết tình trạng hệ thống — mất thời gian và dễ bỏ sót.
* Không có cái nhìn tổng thể về doanh thu toàn hệ thống trong tháng.
* Không biết có nhân sự nào bị khóa hoặc cơ sở nào mới được thêm hay không.
* Không theo dõi được các thao tác gần đây mà không vào Audit Log.
* Mỗi lần đăng nhập đều phải click nhiều bước để đến đúng module cần làm việc.

## 3. Mục tiêu

Admin Dashboard giúp Admin:

* Xem tổng quan hệ thống ngay khi đăng nhập (KPI Cards).
* Theo dõi doanh thu tháng hiện tại của từng cơ sở.
* Xem thống kê nhanh về cơ sở và nhân sự.
* Xem 10 hoạt động gần nhất từ Audit Log.
* Điều hướng nhanh tới các module nghiệp vụ.

## 4. Ràng buộc

* Chỉ Admin mới được truy cập Dashboard này.
* Dashboard là màn hình read-only — không có thao tác tạo/sửa/xóa trực tiếp.
* Dữ liệu được tổng hợp từ nhiều nguồn — cần đảm bảo hiệu năng bằng caching.
* Nếu một nguồn dữ liệu lỗi, Dashboard vẫn hiển thị được các phần còn lại.
* Doanh thu chỉ tính hóa đơn PAID trong tháng hiện tại.
* Hoạt động gần đây giới hạn tối đa 10 bản ghi.

## 5. Câu hỏi mở

* Có cần Dashboard riêng cho MANAGER không (phạm vi theo cơ sở được phân công)?
* Thời gian cache Dashboard là bao lâu là hợp lý — 30 giây hay 60 giây?
* Có cần thêm widget so sánh doanh thu tháng này vs tháng trước không?
* Khi click vào KPI Card, có cần filter sẵn khi điều hướng sang module tương ứng không?
* Có cần hiển thị cảnh báo nếu có cơ sở không có MANAGER được phân công không?
