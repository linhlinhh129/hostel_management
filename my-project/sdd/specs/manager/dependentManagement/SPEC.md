# Feature: Quản lý Người phụ thuộc

**Trạng thái:** Draft
**Người viết:** [Tên]
**Ngày:** 2026-07-13

## User Story

Là Manager, tôi muốn quản lý thông tin người phụ thuộc của người thuê để hệ thống có thể lưu trữ thông tin những người đang ở cùng người thuê chính.

## Acceptance Criteria (EARS notation)

### AC-01 Thêm người phụ thuộc

KHI Manager gửi thông tin người phụ thuộc hợp lệ qua form
HỆ THỐNG PHẢI tạo bản ghi người phụ thuộc mới trong bảng `dbo.dependents`, liên kết với người thuê chính và chuyển hướng về trang chi tiết người thuê.

### AC-02 Kiểm tra người thuê tồn tại

KHI Manager thêm người phụ thuộc cho một người thuê
HỆ THỐNG PHẢI kiểm tra người thuê được chọn có tồn tại và đang ở trạng thái ACTIVE.

### AC-03 Kiểm tra dữ liệu bắt buộc

KHI Manager gửi thông tin người phụ thuộc thiếu trường bắt buộc
HỆ THỐNG PHẢI từ chối yêu cầu, hiển thị thông báo lỗi và chuyển hướng về trang trước.

Các trường bắt buộc bao gồm:

* Họ và tên (`fullName`)
* Quan hệ với người thuê chính (`relationship`)

### AC-04 Xem danh sách người phụ thuộc

KHI Manager mở trang chi tiết người thuê
HỆ THỐNG PHẢI hiển thị danh sách tất cả các người phụ thuộc đang hoạt động (chưa bị xóa mềm) được liên kết với người thuê đó.

### AC-05 Xem chi tiết người phụ thuộc

KHI Manager mở trang chi tiết người phụ thuộc
HỆ THỐNG PHẢI hiển thị đầy đủ thông tin cá nhân của người phụ thuộc, thông tin người thuê chính, form cập nhật thông tin và nút xóa.

### AC-06 Cập nhật người phụ thuộc

KHI Manager cập nhật thông tin người phụ thuộc với dữ liệu hợp lệ qua form
HỆ THỐNG PHẢI cập nhật bản ghi người phụ thuộc trong cơ sở dữ liệu và hiển thị thông báo thành công.

### AC-07 Xóa mềm người phụ thuộc

KHI Manager thực hiện hành động xóa người phụ thuộc
HỆ THỐNG PHẢI cập nhật trường `deleted_at = GETDATE()` cho bản ghi đó trong cơ sở dữ liệu để thực hiện xóa mềm và giữ lại dữ liệu lịch sử.

## Technical Notes

Các URL xử lý trong servlet (được xử lý bởi `ManagerTenantsServlet.java`):

* `POST /manager/tenants/{tenantId}/dependents/add`: Tiếp nhận dữ liệu thêm mới người phụ thuộc.
* `GET /manager/dependents/{dependentId}`: Hiển thị trang chi tiết người phụ thuộc.
* `POST /manager/dependents/{dependentId}/edit`: Lưu thông tin chỉnh sửa người phụ thuộc.
* `POST /manager/dependents/{dependentId}/remove`: Xóa mềm người phụ thuộc.

Cấu trúc bảng `dbo.dependents` thực tế trong cơ sở dữ liệu:

| Tên cột | Kiểu dữ liệu | Mô tả |
| --- | --- | --- |
| `dependent_id` | INT (PK, Identity) | ID của người phụ thuộc |
| `tenant_id` | INT (FK) | ID của người thuê chính |
| `full_name` | NVARCHAR(200) | Họ và tên người phụ thuộc |
| `dob` | DATE | Ngày sinh |
| `gender` | VARCHAR(20) | Giới tính (MALE, FEMALE, OTHER) |
| `relationship` | NVARCHAR(100) | Quan hệ với người thuê chính |
| `phone` | VARCHAR(20) | Số điện thoại |
| `identity_number` | VARCHAR(50) | Số CMND/CCCD |
| `permanent_address`| NVARCHAR(500) | Địa chỉ thường trú |
| `created_at` | DATETIME | Thời gian tạo bản ghi |
| `updated_at` | DATETIME | Thời gian cập nhật gần nhất |
| `deleted_at` | DATETIME | Thời gian xóa mềm (nếu null là ACTIVE, ngược lại là INACTIVE) |

## Validation

* Họ và tên (`fullName`) là bắt buộc, độ dài tối đa 200 ký tự.
* Quan hệ với người thuê chính (`relationship`) là bắt buộc, độ dài tối đa 100 ký tự.
* Giới tính (`gender`) chỉ chấp nhận các giá trị: MALE, FEMALE, OTHER.
* Số điện thoại (`phone`) nếu nhập phải tuân thủ định dạng số di động Việt Nam (gồm 10 số).
* Số CMND/CCCD (`identityNumber`) nếu nhập phải tuân thủ định dạng (gồm 9 hoặc 12 số).
* Người thuê chính phải tồn tại và có trạng thái ACTIVE.
* Việc xóa người phụ thuộc chỉ là xóa mềm (cập nhật `deleted_at`), dữ liệu không bị xóa vật lý.

## Dependency

Feature này phụ thuộc vào Feature: Quản lý Người thuê.

Lý do:

* Người phụ thuộc bắt buộc phải được liên kết với một tài khoản người thuê chính (`tenant_id`).
* Người thuê chính phải ở trạng thái ACTIVE mới được phép thêm người phụ thuộc.
* Khi người thuê chính kết thúc thuê, danh sách người phụ thuộc vẫn được lưu trữ lịch sử để tra cứu.
