# CONTEXT.md — Admin Dashboard

## 1. Bối cảnh

Admin Dashboard là màn hình trung tâm sau khi Admin đăng nhập vào hệ thống quản lý nhà trọ. Đây là điểm xuất phát để Admin nắm bắt nhanh tình trạng tổng thể của toàn hệ thống và điều hướng tới các module nghiệp vụ.

Dashboard tổng hợp dữ liệu từ các module: Quản lý Cơ sở, Quản lý Nhân sự, Báo cáo Doanh thu, Quản lý Thông báo và Audit Log.

## 2. Nỗi đau của User

Hiện tại, Admin phải vào từng module riêng lẻ để kiểm tra tình trạng hệ thống:

* Phải vào module Doanh thu mới biết doanh thu tháng này
* Phải vào module Cơ sở mới biết có bao nhiêu cơ sở đang hoạt động
* Phải vào module Hóa đơn mới biết cơ sở nào có nhiều hóa đơn quá hạn
* Phải vào Audit Log mới biết nhân sự vừa thực hiện thao tác gì
* Mất nhiều thời gian để nắm bắt tình trạng tổng thể
* Không phát hiện sớm được vấn đề tiềm ẩn (ví dụ: cơ sở có doanh thu thấp bất thường)

Vì vậy, hệ thống cần một Dashboard tổng hợp giúp Admin xem overview toàn hệ thống trong một màn hình duy nhất.

## 3. Mục tiêu

Feature Admin Dashboard giúp Admin:

* Xem KPI quan trọng nhất (doanh thu tháng, tổng cơ sở, thông báo, audit log) ngay khi đăng nhập
* Theo dõi doanh thu từng cơ sở trong tháng hiện tại
* Phát hiện cơ sở có nhiều hóa đơn chưa thanh toán hoặc quá hạn
* Xem 5 hoạt động gần đây của hệ thống (ai làm gì, khi nào)
* Theo dõi thống kê nhân sự (tổng số, Ban Quản lý, Vận hành)
* Điều hướng nhanh tới các module chi tiết hoặc tác vụ thường dùng (Quick Actions)
* Đánh giá hiệu quả vận hành của từng cơ sở

## 4. Ràng buộc

### Phân quyền
* Chỉ Admin được truy cập Admin Dashboard
* Người dùng chưa đăng nhập bị chuyển về trang login
* Người dùng không phải Admin nhận lỗi FORBIDDEN

### KPI Cards
* Dashboard hiển thị đủ 4 KPI Cards: doanh thu tháng, tổng cơ sở, thông báo, audit log hôm nay
* Doanh thu tháng chỉ tính hóa đơn PAID trong tháng hiện tại
* KPI Card không có dữ liệu hiển thị `0` thay vì lỗi hoặc trống

### Widget Thống kê Nhân sự
* Hiển thị: tổng nhân sự, số MANAGER, số OPERATOR
* Chỉ đếm MANAGER và OPERATOR, không đếm tài khoản ADMIN

### Widget Hoạt động Gần đây
* Hiển thị tối đa 5 bản ghi Audit Log mới nhất
* Sắp xếp theo thời gian giảm dần
* Hiển thị: thời gian, người thực hiện, hành động
* Empty state: "Chưa có hoạt động nào"

### Widget Doanh thu theo Cơ sở
* Chỉ tính trong tháng hiện tại (từ ngày 1 đến ngày hiện tại)
* Chỉ tính hóa đơn PAID — bỏ qua UNPAID, OVERDUE, CANCELLED
* Hiển thị tất cả cơ sở ACTIVE, kể cả cơ sở có doanh thu = 0
* Mỗi hàng hiển thị: mã cơ sở, tên cơ sở, doanh thu đã thu, số hóa đơn chưa thanh toán, số hóa đơn quá hạn
* Hóa đơn quá hạn > 0 hiển thị badge đỏ

### Hiệu năng và Độ tin cậy
* Dữ liệu Dashboard được cache tối đa 60 giây
* Nếu một nguồn dữ liệu bị lỗi, Dashboard vẫn trả về phần còn lại với giá trị `0` cho phần lỗi
* Thời gian phản hồi tối đa: 1 giây (P95)
* Dashboard không cung cấp chức năng tạo, sửa, xóa dữ liệu trực tiếp

## 5. Nguồn dữ liệu

Dashboard tổng hợp dữ liệu từ 5 DAO:

| DAO | Dữ liệu lấy | Phạm vi |
|---|---|---|
| `RevenueDAO` | Doanh thu, hóa đơn theo cơ sở | Tháng hiện tại, PAID |
| `FacilityDAO` | Tổng cơ sở, cơ sở ACTIVE | Tất cả |
| `PersonnelDAO` | Tổng nhân sự, số MANAGER, số OPERATOR | MANAGER + OPERATOR |
| `NotificationDAO` | Tổng số thông báo | Tất cả |
| `AuditLogDAO` | Hoạt động gần đây | 5 bản ghi mới nhất |

## 6. Câu hỏi mở

* Có cần widget "Thống kê cơ sở" hiển thị breakdown theo trạng thái (DRAFT, INACTIVE) không?
* Có cần tăng số lượng hoạt động gần đây lên 10 bản ghi thay vì 5 không?
* Có cần bỏ giây (`:ss`) trong format thời gian hiển thị không? (hiện `dd/MM/yyyy HH:mm:ss`)
* Có cần thêm KPI Card "Tổng nhân sự ACTIVE" không?
* Có cần thêm link điều hướng cho KPI Cards không? (hiện chỉ có link ở widgets)
* Có cần widget "Tỷ lệ phòng trống / đang thuê" toàn hệ thống không?
* Có cần so sánh doanh thu tháng này với tháng trước không?
* Có cần biểu đồ (chart) thay vì chỉ hiển thị số liệu không?
* Có cần caching riêng cho từng widget thay vì cache toàn bộ Dashboard không?
* Có cần export Dashboard ra PDF/Excel không?

