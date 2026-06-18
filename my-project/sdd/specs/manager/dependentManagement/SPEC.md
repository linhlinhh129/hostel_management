# Feature: Quản lý Người phụ thuộc

**Trạng thái:** Draft
**Người viết:** [Tên]
**Ngày:** [YYYY-MM-DD]

## User Story

Là Manager, tôi muốn quản lý thông tin người phụ thuộc của người thuê để hệ thống có thể lưu trữ thông tin những người đang ở cùng người thuê chính.

## Acceptance Criteria (EARS notation)

### AC-01 Thêm người phụ thuộc

KHI Manager gửi thông tin người phụ thuộc hợp lệ
HỆ THỐNG PHẢI tạo bản ghi người phụ thuộc mới và liên kết người phụ thuộc với người thuê đã chọn.

### AC-02 Kiểm tra người thuê tồn tại

KHI Manager thêm người phụ thuộc cho một người thuê
HỆ THỐNG PHẢI kiểm tra người thuê được chọn có tồn tại và đang ở trạng thái ACTIVE.

### AC-03 Kiểm tra dữ liệu bắt buộc

KHI Manager gửi thông tin người phụ thuộc thiếu trường bắt buộc
HỆ THỐNG PHẢI từ chối yêu cầu và hiển thị lỗi kiểm tra dữ liệu.

Các trường bắt buộc bao gồm:

* Họ và tên
* Quan hệ với người thuê chính
* Ngày sinh
* Số CCCD/CMND

### AC-04 Xem danh sách người phụ thuộc

KHI Manager mở trang chi tiết người thuê
HỆ THỐNG PHẢI hiển thị danh sách người phụ thuộc được liên kết với người thuê đó.

### AC-05 Cập nhật người phụ thuộc

KHI Manager cập nhật thông tin người phụ thuộc với dữ liệu hợp lệ
HỆ THỐNG PHẢI cập nhật bản ghi người phụ thuộc và trả về phản hồi thành công.

### AC-06 Xóa mềm người phụ thuộc

KHI Manager xóa người phụ thuộc khỏi người thuê
HỆ THỐNG PHẢI chuyển trạng thái người phụ thuộc thành INACTIVE và giữ lại dữ liệu lịch sử.

### AC-07 Người thuê không tồn tại

KHI Manager thêm người phụ thuộc cho một người thuê không tồn tại
HỆ THỐNG PHẢI từ chối yêu cầu và trả về mã lỗi TENANT_NOT_FOUND.

### AC-08 Người thuê không hoạt động

KHI Manager thêm người phụ thuộc cho một người thuê có trạng thái INACTIVE
HỆ THỐNG PHẢI từ chối yêu cầu và trả về mã lỗi TENANT_NOT_ACTIVE.

## Technical Notes

API endpoint:

```http
POST /api/v1/tenants/{tenantId}/dependents
```

API endpoint:

```http
GET /api/v1/tenants/{tenantId}/dependents
```

API endpoint:

```http
PUT /api/v1/dependents/{dependentId}
```

API endpoint:

```http
DELETE /api/v1/dependents/{dependentId}
```

Thay đổi DB: thêm bảng dependents

Các trường đề xuất cho bảng dependents:

| Trường         | Mô tả                        |
| -------------- | ---------------------------- |
| dependentId    | ID của người phụ thuộc       |
| tenantId       | ID người thuê chính          |
| fullName       | Họ và tên người phụ thuộc    |
| relationship   | Quan hệ với người thuê chính |
| dateOfBirth    | Ngày sinh                    |
| identityNumber | Số CCCD/CMND                 |
| phone          | Số điện thoại, nếu có        |
| status         | ACTIVE hoặc INACTIVE         |
| createdBy      | Người tạo                    |
| createdAt      | Thời gian tạo                |
| updatedBy      | Người cập nhật               |
| updatedAt      | Thời gian cập nhật           |

## Validation

Họ và tên là bắt buộc.

Quan hệ với người thuê chính là bắt buộc.

Ngày sinh là bắt buộc.

Số CCCD/CMND là bắt buộc.

Số CCCD/CMND phải là duy nhất đối với người phụ thuộc đang ACTIVE.

Người thuê chính phải tồn tại.

Người thuê chính phải có trạng thái ACTIVE.

Người phụ thuộc không có tài khoản đăng nhập riêng.

Người phụ thuộc bắt buộc phải được liên kết với đúng một người thuê chính.

Chỉ cho phép xóa mềm, không xóa vật lý dữ liệu.

## Dependency

Feature này phụ thuộc vào Feature: Quản lý Người thuê.

Lý do:

* Người phụ thuộc phải được gán với một người thuê chính.
* Người phụ thuộc không có tài khoản đăng nhập riêng.
* Người phụ thuộc không được tạo độc lập nếu không có người thuê chính.
* Khi người thuê chính kết thúc thuê, danh sách người phụ thuộc liên quan vẫn được giữ lại để tra cứu lịch sử.
