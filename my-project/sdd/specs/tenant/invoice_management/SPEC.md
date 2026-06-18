# Feature: Invoice Management

**Status:** Draft\
**Author:** Business Analyst\
**Date:** 2026-06-10

## User Story

As a Tenant (Người thuê),

I want to xem danh sách hóa đơn, chi tiết hóa đơn và lịch sử thanh toán,

so that tôi có thể theo dõi các khoản phí phát sinh và tình trạng thanh toán của mình.

## Acceptance Criteria (EARS notation)

### AC01 - Hiển thị danh sách hóa đơn

WHEN người thuê truy cập chức năng Quản lý hóa đơn

THE SYSTEM SHALL hiển thị danh sách các hóa đơn thuộc người thuê hiện tại.

### AC02 - Hiển thị thông tin tóm tắt hóa đơn

WHEN danh sách hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị các thông tin sau:

- Kỳ hóa đơn

- Tổng tiền phải thanh toán

- Hạn thanh toán

- Trạng thái thanh toán

### AC03 - Sắp xếp hóa đơn theo kỳ gần nhất

WHEN danh sách hóa đơn được hiển thị

THE SYSTEM SHALL sắp xếp hóa đơn theo kỳ hóa đơn giảm dần.

### AC04 - Xem chi tiết hóa đơn

WHEN người thuê chọn một hóa đơn trong danh sách

THE SYSTEM SHALL hiển thị đầy đủ thông tin chi tiết của hóa đơn.

### AC05 - Hiển thị chi tiết tiền phòng

WHEN màn hình Chi tiết hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị tiền phòng cố định của kỳ hóa đơn.

### AC06 - Hiển thị chi tiết điện

WHEN màn hình Chi tiết hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị:

- Chỉ số điện cũ

- Chỉ số điện mới

- Thành tiền điện

### AC07 - Hiển thị chi tiết nước

WHEN màn hình Chi tiết hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị:

- Chỉ số nước cũ

- Chỉ số nước mới

- Thành tiền nước

### AC08 - Hiển thị các khoản phí khác

WHEN màn hình Chi tiết hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị phí dịch vụ và các khoản phụ phí (nếu có).

### AC09 - Hiển thị tổng tiền phải thanh toán

WHEN màn hình Chi tiết hóa đơn được tải thành công

THE SYSTEM SHALL hiển thị tổng tiền phải thanh toán của hóa đơn.

### AC10 - Hiển thị trạng thái thanh toán

WHEN hóa đơn được hiển thị

THE SYSTEM SHALL hiển thị một trong các trạng thái sau:

- Chưa thanh toán

- Đã thanh toán

- Quá hạn

### AC11 - Hiển thị lịch sử thanh toán

WHEN người thuê truy cập chức năng Lịch sử thanh toán

THE SYSTEM SHALL hiển thị danh sách các giao dịch thanh toán đã hoàn thành.

### AC12 - Hiển thị chi tiết giao dịch thanh toán

WHEN lịch sử thanh toán được tải thành công

THE SYSTEM SHALL hiển thị các thông tin sau:

- Mã giao dịch

- Kỳ hóa đơn

- Số tiền thanh toán

- Thời gian thanh toán

- Phương thức thanh toán

- Trạng thái giao dịch

### AC13 - Không có dữ liệu hóa đơn

WHEN hệ thống không tìm thấy hóa đơn nào

THE SYSTEM SHALL hiển thị thông báo "Hiện chưa có hóa đơn nào".

### AC14 - Không có lịch sử thanh toán

WHEN hệ thống không tìm thấy giao dịch thanh toán nào

THE SYSTEM SHALL hiển thị thông báo "Chưa có lịch sử thanh toán".

### AC15 - Truy cập trái phép dữ liệu hóa đơn

WHEN người thuê cố gắng truy cập hóa đơn không thuộc quyền sở hữu của mình

THE SYSTEM SHALL từ chối truy cập

AND return HTTP 403 Forbidden.

### AC16 - Người dùng chưa xác thực

WHEN người dùng truy cập chức năng Quản lý hóa đơn mà chưa đăng nhập

THE SYSTEM SHALL chuyển hướng người dùng đến màn hình Đăng nhập.

### AC17 - Hóa đơn không tồn tại

WHEN người thuê truy cập một hóa đơn không tồn tại

THE SYSTEM SHALL return HTTP 404 Not Found.

## Technical Notes

### API Endpoint

#### Danh sách hóa đơn

`GET /api/v1/tenant/invoices`

#### Chi tiết hóa đơn

`GET /api/v1/tenant/invoices/{invoiceId}`

#### Lịch sử thanh toán

`GET /api/v1/tenant/payments/history`

### DB Changes

None

### Validation

#### Quyền truy cập

- Người dùng phải đăng nhập hợp lệ.

- Người dùng phải có vai trò Tenant.

- Chỉ được xem hóa đơn thuộc tài khoản hiện tại.

- Chỉ được xem lịch sử thanh toán của chính mình.

#### invoiceId

- Phải tồn tại trong hệ thống.

- Phải thuộc người thuê hiện tại.

- Là số nguyên dương hợp lệ.

#### paymentId

- Phải tồn tại trong hệ thống.

- Phải thuộc người thuê hiện tại.

## Response Data - Invoice List

```json
[
  {
    "invoiceId": 101,
    "billingPeriod": "06/2026",
    "totalAmount": 4500000,
    "dueDate": "2026-06-15",
    "paymentStatus": "UNPAID"
  }
]
```

## Response Data - Invoice Detail

```json
{
  "invoiceId": 101,
  "roomCode": "A101",
  "billingPeriod": "06/2026",
  "roomFee": 3500000,
  "electricityOld": 120,
  "electricityNew": 180,
  "electricityAmount": 300000,
  "waterOld": 50,
  "waterNew": 65,
  "waterAmount": 150000,
  "serviceFee": 550000,
  "totalAmount": 4500000,
  "dueDate": "2026-06-15",
  "paymentStatus": "UNPAID"
}
```

## Response Data - Payment History

```json
[
  {
    "paymentId": "PAY001",
    "invoiceId": 95,
    "billingPeriod": "05/2026",
    "amount": 4300000,
    "paymentMethod": "BANK_TRANSFER",
    "paymentDate": "2026-05-12T10:30:00",
    "status": "SUCCESS"
  }
]
```

## UI Components

- Invoice List

- Invoice Card

- Invoice Detail View

- Payment History List

- Empty State Message

- Error Message

- Back Button