# Feature: Dependent Management

**Status:** Draft\
**Author:** Business Analyst\
**Date:** 2026-06-10

## User Story

As a Tenant (Người thuê),

I want to xem danh sách và thông tin chi tiết của những người phụ thuộc đã được đăng ký,

so that tôi có thể quản lý và kiểm tra thông tin cư trú của các thành viên liên quan đến hợp đồng thuê của mình.

## Acceptance Criteria (EARS notation)

### AC01 - Hiển thị danh sách người phụ thuộc

WHEN người thuê truy cập chức năng Quản lý người phụ thuộc

THE SYSTEM SHALL hiển thị danh sách người phụ thuộc thuộc tài khoản người thuê hiện tại.

### AC02 - Hiển thị thông tin tóm tắt người phụ thuộc

WHEN danh sách người phụ thuộc được tải thành công

THE SYSTEM SHALL hiển thị các thông tin sau:

- Mã người phụ thuộc

- Họ và tên

- Mối quan hệ

- Số điện thoại

### AC03 - Sắp xếp danh sách người phụ thuộc

WHEN danh sách người phụ thuộc được hiển thị

THE SYSTEM SHALL sắp xếp danh sách theo họ tên tăng dần.

### AC04 - Xem chi tiết người phụ thuộc

WHEN người thuê chọn một người phụ thuộc trong danh sách

THE SYSTEM SHALL hiển thị màn hình Chi tiết người phụ thuộc.

### AC05 - Hiển thị đầy đủ thông tin người phụ thuộc

WHEN màn hình Chi tiết người phụ thuộc được tải thành công

THE SYSTEM SHALL hiển thị các thông tin sau:

- Mã người phụ thuộc

- Họ và tên

- Ngày sinh

- Số điện thoại

- CCCD

- Email

- Mối quan hệ

- Người thuê bảo trợ (Phụ thuộc bởi ai)

### AC06 - Không có người phụ thuộc

WHEN người thuê chưa đăng ký người phụ thuộc nào

THE SYSTEM SHALL hiển thị thông báo:

"Hiện chưa có người phụ thuộc nào được đăng ký."

### AC07 - Người phụ thuộc không tồn tại

WHEN người thuê truy cập một người phụ thuộc không tồn tại

THE SYSTEM SHALL return HTTP 404 Not Found.

### AC08 - Truy cập trái phép

WHEN người thuê cố gắng truy cập thông tin người phụ thuộc không thuộc quyền quản lý của mình

THE SYSTEM SHALL từ chối truy cập

AND return HTTP 403 Forbidden.

### AC09 - Người dùng chưa xác thực

WHEN người dùng truy cập chức năng Quản lý người phụ thuộc mà chưa đăng nhập

THE SYSTEM SHALL chuyển hướng người dùng đến màn hình Đăng nhập.

### AC10 - Lỗi tải dữ liệu

WHEN hệ thống không thể tải danh sách hoặc chi tiết người phụ thuộc

THE SYSTEM SHALL hiển thị thông báo lỗi phù hợp

AND cho phép người dùng tải lại dữ liệu.

## Technical Notes

### API Endpoint

#### Danh sách người phụ thuộc

`GET /api/v1/tenant/dependents`

#### Chi tiết người phụ thuộc

`GET /api/v1/tenant/dependents/{dependentId}`

### DB Changes

None

### Validation

#### Quyền truy cập

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Chỉ được xem người phụ thuộc thuộc tài khoản của chính mình.

#### dependentId

- Phải tồn tại trong hệ thống.

- Phải thuộc Tenant hiện tại.

- Là mã hợp lệ theo định dạng hệ thống.

## Response Data - Dependent List

```json
[
  {
    "dependentId": "DEP001",
    "fullName": "Nguyễn Văn B",
    "relationship": "Em trai",
    "phoneNumber": "0912345678"
  },
  {
    "dependentId": "DEP002",
    "fullName": "Nguyễn Thị C",
    "relationship": "Mẹ",
    "phoneNumber": "0987654321"
  }
]
```

## Response Data - Dependent Detail

```json
{
  "dependentId": "DEP001",
  "fullName": "Nguyễn Văn B",
  "dateOfBirth": "2005-10-12",
  "phoneNumber": "0912345678",
  "citizenId": "001234567890",
  "email": "nguyenvanb@gmail.com",
  "relationship": "Em trai",
  "sponsoredBy": {
    "tenantId": "TEN001",
    "fullName": "Nguyễn Văn A"
  }
}
```

## UI Components

- Dependent List

- Dependent Card

- Dependent Detail View

- Empty State Message

- Error Message

- Back Button