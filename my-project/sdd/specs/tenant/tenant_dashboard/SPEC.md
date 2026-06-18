# Feature: Tenant Dashboard

Status: Draft

Author: Business Analyst

Date: 2026-06-10

# User Story

As a Tenant (Người thuê),

I want to xem trang tổng quan sau khi đăng nhập và truy cập nhanh đến các chức năng chính,

so that tôi có thể theo dõi thông tin cá nhân, hóa đơn, yêu cầu, thông báo và người phụ thuộc một cách thuận tiện.

# Acceptance Criteria (EARS notation)

### AC01 - Hiển thị Dashboard

WHEN người thuê đăng nhập thành công

THE SYSTEM SHALL chuyển hướng người dùng đến màn hình Tenant Dashboard.

### AC01.1 - Yêu cầu đổi mật khẩu lần đầu

WHEN người thuê đăng nhập bằng tài khoản được hệ thống cấp mới và trạng thái tài khoản là "First Login"

THE SYSTEM SHALL chuyển hướng người dùng đến màn hình Đổi mật khẩu.

AND THE SYSTEM SHALL không cho phép truy cập Dashboard cho đến khi đổi mật khẩu thành công.

### AC01.2 - Hiển thị số lượng hóa đơn chưa thanh toán

WHEN Dashboard được tải thành công

THE SYSTEM SHALL hiển thị tổng số lượng hóa đơn có trạng thái:

- Chưa thanh toán

- Quá hạn

### AC01.3 - Hiển thị số lượng yêu cầu đang xử lý

WHEN Dashboard được tải thành công

THE SYSTEM SHALL hiển thị tổng số lượng yêu cầu có trạng thái:

- Chờ xử lý

- Đang xử lý

### AC01.4 - Hiển thị số lượng người phụ thuộc

WHEN Dashboard được tải thành công

THE SYSTEM SHALL hiển thị tổng số lượng người phụ thuộc thuộc người thuê hiện tại.

### AC01.5 - Hiển thị số lượng thông báo gần đây

WHEN Dashboard được tải thành công

THE SYSTEM SHALL hiển thị tổng số lượng thông báo được tạo trong vòng 30 ngày gần nhất.

### AC02 - Hiển thị các menu chức năng

WHEN Tenant Dashboard được tải thành công

THE SYSTEM SHALL hiển thị các chức năng:

- Quản lý thông báo

- Quản lý yêu cầu

- Quản lý thông tin cá nhân

- Quản lý hóa đơn và thanh toán

- Quản lý người phụ thuộc

### AC03 - Điều hướng đến Quản lý thông báo

WHEN người thuê chọn chức năng "Quản lý thông báo"

THE SYSTEM SHALL chuyển đến màn hình Danh sách thông báo.

### AC04 - Điều hướng đến Quản lý yêu cầu

WHEN người thuê chọn chức năng "Quản lý yêu cầu"

THE SYSTEM SHALL chuyển đến màn hình Danh sách yêu cầu.

### AC05 - Điều hướng đến Quản lý thông tin cá nhân

WHEN người thuê chọn chức năng "Quản lý thông tin cá nhân"

THE SYSTEM SHALL chuyển đến màn hình Thông tin cá nhân.

### AC06 - Điều hướng đến Quản lý hóa đơn

WHEN người thuê chọn chức năng "Quản lý hóa đơn và thanh toán"

THE SYSTEM SHALL chuyển đến màn hình Danh sách hóa đơn.

### AC07 - Điều hướng đến Quản lý người phụ thuộc

WHEN người thuê chọn chức năng "Quản lý người phụ thuộc"

THE SYSTEM SHALL chuyển đến màn hình Danh sách người phụ thuộc.

# Technical Notes

## API Endpoint

GET /api/v1/tenant/dashboard

## DB Changes

None

## Validation

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Chỉ được truy cập dữ liệu thuộc tài khoản hiện tại.

- Hệ thống phải kiểm tra Access Token trước khi trả dữ liệu Dashboard.

# Response Data (Sample)

```json
{
  "tenantId": "TN001",
  "fullName": "Nguyễn Văn A",
  "unpaidInvoiceCount": 1,
  "pendingRequestCount": 2,
  "dependentCount": 2,
  "recentNotificationCount": 5
}
```

# UI Components

- Welcome Card

- Notification Shortcut Card

- Request Shortcut Card

- Profile Shortcut Card

- Invoice Shortcut Card

- Dependent Shortcut Card