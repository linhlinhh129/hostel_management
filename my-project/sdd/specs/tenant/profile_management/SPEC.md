# Feature: Profile Management

**Status:** Draft\
**Author:** Business Analyst\
**Date:** 2026-06-10

## User Story

As a Tenant (Người thuê),

I want to xem và cập nhật thông tin cá nhân của mình,

so that thông tin liên hệ luôn chính xác và Ban quản lý có thể liên hệ khi cần thiết.

## Acceptance Criteria (EARS notation)

### AC01 - Xem thông tin cá nhân

WHEN người thuê truy cập chức năng Thông tin cá nhân

THE SYSTEM SHALL hiển thị các thông tin sau:

- Mã người thuê

- Họ và tên

- Ngày sinh

- Số điện thoại

- CCCD

- Email

- Phòng đang thuê

- Danh sách người phụ thuộc

### AC02 - Hiển thị thông tin phòng thuê

WHEN màn hình Thông tin cá nhân được tải thành công

THE SYSTEM SHALL hiển thị thông tin phòng mà người thuê đang sử dụng.

### AC03 - Hiển thị danh sách người phụ thuộc

WHEN màn hình Thông tin cá nhân được tải thành công

THE SYSTEM SHALL hiển thị danh sách người phụ thuộc liên kết với người thuê hiện tại.

### AC04 - Cập nhật email thành công

WHEN người thuê cập nhật Email hợp lệ

THE SYSTEM SHALL lưu thông tin mới

AND hiển thị thông báo cập nhật thành công.

### AC05 - Cập nhật số điện thoại thành công

WHEN người thuê cập nhật Số điện thoại hợp lệ

THE SYSTEM SHALL lưu thông tin mới

AND hiển thị thông báo cập nhật thành công.

### AC06 - Email không hợp lệ

WHEN người thuê nhập Email không đúng định dạng

THE SYSTEM SHALL hiển thị thông báo lỗi

AND không cho phép lưu dữ liệu.

### AC07 - Số điện thoại không hợp lệ

WHEN người thuê nhập Số điện thoại không đúng định dạng

THE SYSTEM SHALL hiển thị thông báo lỗi

AND không cho phép lưu dữ liệu.

### AC08 - Email đã tồn tại

WHEN người thuê cập nhật Email đã được sử dụng bởi tài khoản khác

THE SYSTEM SHALL từ chối cập nhật

AND hiển thị thông báo Email đã tồn tại.

### AC09 - Người dùng chưa xác thực

WHEN người dùng truy cập chức năng Thông tin cá nhân mà chưa đăng nhập

THE SYSTEM SHALL chuyển hướng người dùng đến màn hình Đăng nhập.

### AC10 - Truy cập trái phép dữ liệu cá nhân

WHEN người thuê cố gắng truy cập thông tin của người thuê khác

THE SYSTEM SHALL từ chối truy cập

AND return HTTP 403 Forbidden.

## Technical Notes

### API Endpoint

#### Lấy thông tin cá nhân

`GET /api/v1/tenant/profile`

#### Cập nhật thông tin cá nhân

`PUT /api/v1/tenant/profile`

### DB Changes

None

### Validation

#### Email

- Không được để trống.

- Đúng định dạng email.

- Tối đa 100 ký tự.

- Phải là duy nhất trong hệ thống.

#### Số điện thoại

- Không được để trống.

- Chỉ chứa chữ số.

- Độ dài từ 10 đến 11 ký tự.

#### Quyền truy cập

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Chỉ được xem và cập nhật hồ sơ của chính mình.

## Response Data (Sample)

```json
{
  "tenantId": "TEN001",
  "fullName": "Nguyễn Văn A",
  "dateOfBirth": "2000-05-10",
  "phoneNumber": "0901234567",
  "citizenId": "001234567890",
  "email": "nguyenvana@gmail.com",
  "roomCode": "A101",
  "dependents": [
    {
      "dependentId": "DEP001",
      "fullName": "Nguyễn Văn B",
      "relationship": "Em trai"
    }
  ]
}
```

## Update Request (Sample)

```json
{
  "phoneNumber": "0912345678",
  "email": "newemail@gmail.com"
}
```

## UI Components

- Profile Information Form

- Read-only Information Section

- Update Profile Form

- Save Button

- Validation Message

- Success Message

- Error Message