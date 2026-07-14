# Feature: Dashboard cho Ban quản lý

**Trạng thái:** Completed
**Người viết:** [Tên]
**Ngày:** 2026-07-13

## User Story

Là Manager, tôi muốn xem một trang Dashboard tổng hợp tất cả các số liệu vận hành và tài chính của cơ sở để có cái nhìn tổng quát, giúp việc kiểm soát phòng trọ, công nợ và tiếp nhận sự cố được xử lý nhanh chóng.

## Acceptance Criteria (EARS notation)

### AC-01 Xác thực người dùng

KHI người dùng chưa đăng nhập truy cập trang Dashboard
HỆ THỐNG PHẢI chuyển hướng người dùng về trang đăng nhập `/login`.

### AC-02 Phân quyền quản lý cơ sở

KHI Manager đã đăng nhập truy cập trang Dashboard
HỆ THỐNG PHẢI chỉ hiển thị thông tin và số liệu thống kê thuộc cơ sở mà Manager đó được giao phụ trách (trùng `manager_id` trong bảng `dbo.facilities`).

### AC-03 Hiển thị thông tin cơ sở

KHI tải trang Dashboard thành công
HỆ THỐNG PHẢI hiển thị tên cơ sở, mã cơ sở và trạng thái hoạt động của cơ sở tương ứng.

### AC-04 Thống kê vận hành phòng và cư dân

KHI tải trang Dashboard thành công
HỆ THỐNG PHẢI tính toán và hiển thị chính xác các chỉ số vận hành sau:
* Tổng số phòng (`totalRooms`)
* Số phòng đã thuê (`occupiedRooms`)
* Số phòng trống (`vacantRooms`)
* Tỷ lệ lấp đầy phòng (`occupancyRate` = occupiedRooms / totalRooms * 100)
* Tổng số người thuê chính đang hoạt động (`totalTenants`)
* Tổng số người phụ thuộc đang hoạt động (`totalDependents`)

### AC-05 Thống kê tài chính và công nợ

KHI tải trang Dashboard thành công
HỆ THỐNG PHẢI tính toán và hiển thị chính xác các chỉ số tài chính sau:
* Số hợp đồng đang hiệu lực (`activeContracts`)
* Số hóa đơn chưa thanh toán (`unpaidInvoices`)
* Số hóa đơn nợ quá hạn (`overdueInvoices`)
* Số giao dịch thanh toán đang chờ duyệt (`pendingPayments`)
* Doanh thu đã thu trong tháng hiện tại (`monthlyRevenue`)
* Tổng số tiền nợ chưa thu (`totalOutstanding`)

### AC-06 Thống kê trạng thái sự cố

KHI tải trang Dashboard thành công
HỆ THỐNG PHẢI hiển thị số lượng yêu cầu sự cố phân chia theo nhóm:
* Sự cố Mới: tổng số yêu cầu có trạng thái `NEW` hoặc `PENDING`.
* Sự cố Đang xử lý: tổng số yêu cầu ở trạng thái `RECEIVED`, `ASSIGNED`, hoặc `IN_PROGRESS`.
* Sự cố Hoàn thành: tổng số yêu cầu ở trạng thái `RESOLVED` hoặc `DONE`.
* Sự cố Từ chối: tổng số yêu cầu ở trạng thái `REJECTED`.

### AC-07 Danh sách sự cố gần nhất

KHI tải trang Dashboard thành công
HỆ THỐNG PHẢI hiển thị danh sách 5 sự cố mới gửi gần nhất gồm: mã yêu cầu, tiêu đề, phòng gửi, thời gian gửi, vai trò người gửi (Cư dân/Operator) và nhãn trạng thái kèm màu sắc tương ứng.

## Technical Notes

Mọi yêu cầu được xử lý thông qua Servlet Controller [ManagerDashboardServlet.java](file:///d:/Ki_5/hostel_management/src/main/java/com/quanlyphongtro/controller/manager/ManagerDashboardServlet.java):
* **URL:** `GET /manager/dashboard`
* **JSP View:** `/WEB-INF/views/manager/dashboard.jsp`

Các thuộc tính request được truyền sang JSP bao gồm:

| Tên thuộc tính | Kiểu dữ liệu | Ý nghĩa |
| --- | --- | --- |
| `facilityName` | String | Tên cơ sở trọ đang hiển thị |
| `facilityCode` | String | Mã cơ sở trọ |
| `facilityStatus` | String | Trạng thái hoạt động của cơ sở |
| `totalRooms` | Integer | Tổng số phòng |
| `occupiedRooms` | Integer | Số phòng đang có người thuê |
| `vacantRooms` | Integer | Số phòng đang trống |
| `totalTenants` | Integer | Số người thuê chính |
| `totalDependents` | Integer | Số người phụ thuộc |
| `pendingTickets` | Integer | Số sự cố đang chờ xử lý |
| `sentNotifications` | Integer | Số lượng thông báo Manager đã gửi |
| `occupancyRate` | Integer | Tỷ lệ lấp đầy (%) |
| `activeContracts` | Integer | Số hợp đồng hiệu lực |
| `unpaidInvoices` | Integer | Số hóa đơn chưa thanh toán |
| `overdueInvoices` | Integer | Số hóa đơn trễ hạn thanh toán |
| `pendingPayments` | Integer | Số thanh toán chờ duyệt |
| `monthlyRevenue` | BigDecimal | Doanh thu thực tế đã thu trong tháng hiện tại |
| `totalOutstanding` | BigDecimal | Tổng tiền nợ chưa thu |
| `ticketCountNew` | Integer | Số sự cố mới |
| `ticketCountInProgress`| Integer | Số sự cố đang tiến hành sửa chữa |
| `ticketCountDone` | Integer | Số sự cố đã hoàn tất |
| `ticketCountRejected` | Integer | Số sự cố bị từ chối |
| `recentTickets` | List\<Map\> | Danh sách 5 sự cố mới nhất |

Bảng cơ sở dữ liệu truy vấn chính:
* `dbo.facilities`: Lấy thông tin cơ sở.
* `dbo.rooms`: Đếm số phòng và trạng thái lấp đầy.
* `dbo.users`: Lọc đếm người thuê hoạt động (`role = 'TENANT'`).
* `dbo.dependents`: Đếm người phụ thuộc liên quan.
* `dbo.requests`: Đếm số lượng sự cố theo trạng thái và lấy danh sách mới nhất.
* `dbo.notifications`: Đếm số lượng thông báo đã phát đi từ Manager.
* `dbo.contracts`: Đếm số hợp đồng hoạt động.
* `dbo.invoices`: Đếm hóa đơn chưa thanh toán, quá hạn, tính doanh thu tháng và tổng tiền nợ tồn đọng.
* `dbo.payments`: Đếm số lượng giao dịch thanh toán đang chờ duyệt.

## Validation

* Manager bắt buộc phải đăng nhập mới truy cập được trang Dashboard.
* Nếu Manager chưa được phân công quản lý bất kỳ cơ sở nào, các thẻ thông tin hiển thị giá trị mặc định là `—` hoặc `0` để tránh phát sinh lỗi hệ thống.
* Lọc tính doanh thu tháng hiện tại chính xác bằng hàm SQL `MONTH(i.created_at) = MONTH(GETDATE()) AND YEAR(i.created_at) = YEAR(GETDATE())`.

## Dependency

Màn hình Dashboard phụ thuộc vào tất cả các phân hệ quản lý dữ liệu khác trong hệ thống để tổng hợp thông tin:
* **Quản lý Cơ sở & Phòng:** Cung cấp thông tin cơ sở và thống kê phòng trống/đang ở.
* **Quản lý Người thuê & Người phụ thuộc:** Cung cấp số lượng cư dân đang lưu trú thực tế.
* **Quản lý Hợp đồng:** Cung cấp thông tin hợp đồng đang kích hoạt.
* **Quản lý Hóa đơn & Thanh toán:** Cung cấp các số liệu công nợ, giao dịch thu tiền phòng và doanh thu tháng.
* **Quản lý Yêu cầu sự cố:** Cung cấp các thông tin sự cố hư hỏng vật chất cần sửa chữa.
