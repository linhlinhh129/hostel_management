# Feature: Xem Báo Cáo Doanh Thu

**Status:** Draft
**Author:** [Tên] | **Reviewer:** [Tên] | **Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Xem Báo Cáo Doanh Thu cho phép Admin theo dõi tình hình doanh thu của tất cả cơ sở trong hệ thống quản lý nhà trọ.

Thông tin doanh thu giúp ban quản lý đánh giá hiệu quả hoạt động của từng cơ sở, theo dõi khả năng thu hồi công nợ và hỗ trợ việc ra quyết định kinh doanh.

## 2. User Stories

### Story 1 (Happy Path)

Là Admin, tôi muốn xem báo cáo doanh thu để theo dõi doanh thu của tất cả cơ sở trong hệ thống.

### Story 2 (Happy Path)

Là Admin, tôi muốn lọc báo cáo theo khoảng thời gian để phân tích doanh thu trong một giai đoạn cụ thể.

### Story 3 (Edge Case)

Là Admin, khi không có dữ liệu doanh thu trong khoảng thời gian được chọn, tôi muốn hệ thống hiển thị kết quả rỗng.

### Story 4 (Edge Case)

Là Admin, khi nhập khoảng thời gian không hợp lệ, tôi muốn hệ thống thông báo lỗi.

### Story 5 (Happy Path)

Là Admin, tôi muốn xem doanh thu theo từng cơ sở để đánh giá hiệu quả hoạt động của từng cơ sở.

### Story 6 (Happy Path)

Là Admin, tôi muốn xem doanh thu theo từng kỳ để phân tích xu hướng doanh thu theo thời gian.
## 3. Acceptance Criteria (EARS)

### 3.1 Xem báo cáo doanh thu

WHEN Admin truy cập màn hình báo cáo doanh thu

THE SYSTEM SHALL hiển thị báo cáo doanh thu của tất cả cơ sở.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL hiển thị thông tin tổng hợp bao gồm:

- Total Revenue
- Total Outstanding
- Total Billed Amount

WHEN Admin chọn khoảng thời gian hợp lệ

THE SYSTEM SHALL hiển thị dữ liệu doanh thu trong khoảng thời gian được chọn.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL hiển thị tối thiểu các thông tin sau:

* Mã cơ sở
* Tên cơ sở
* Tổng số hóa đơn
* Revenue (Tổng tiền đã thu từ các hóa đơn PAID)
* Outstanding (Tổng tiền chưa thu từ các hóa đơn UNPAID và OVERDUE)
* Total Billed Amount (Revenue + Outstanding)

WHEN không có dữ liệu doanh thu trong khoảng thời gian được chọn

THE SYSTEM SHALL hiển thị trạng thái "Không có dữ liệu".

WHEN ngày bắt đầu lớn hơn ngày kết thúc

THE SYSTEM SHALL trả về lỗi INVALID_DATE_RANGE.

WHEN số lượng cơ sở trong kết quả vượt quá giới hạn hiển thị trên một trang

THE SYSTEM SHALL hỗ trợ phân trang.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL tính Revenue bằng tổng giá trị các hóa đơn có trạng thái PAID.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL tính Outstanding bằng tổng giá trị các hóa đơn có trạng thái UNPAID và OVERDUE.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL tính Total Billed Amount bằng Revenue + Outstanding.

WHEN Admin xem báo cáo doanh thu theo kỳ

THE SYSTEM SHALL nhóm dữ liệu doanh thu theo tháng.

WHEN Admin xem báo cáo doanh thu theo kỳ

THE SYSTEM SHALL hiển thị các thông tin sau cho từng kỳ:

- Period
- Revenue
- Outstanding
- Total Billed Amount

WHEN Admin xem báo cáo doanh thu theo kỳ

THE SYSTEM SHALL sắp xếp dữ liệu theo thời gian tăng dần.


### 3.2 Phân quyền

WHILE người dùng không có quyền xem báo cáo doanh thu

THE SYSTEM SHALL từ chối truy cập chức năng báo cáo doanh thu.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL ghi nhận Audit Log bao gồm:
- User ID
- Thời gian truy cập
- Khoảng thời gian được lọc

### 3.3 Lọc thời gian
WHEN Admin cung cấp fromDate và toDate

THE SYSTEM SHALL lọc dữ liệu theo Invoice Created Date (created_at) nằm trong khoảng thời gian được chọn.
## 4. API Contract

### Lấy báo cáo doanh thu

Endpoint

GET /api/v1/reports/revenue

Query Parameters

| Tham số | Kiểu | Bắt buộc | Mô tả |
|---------|------|----------|-------|
| fromDate | string (YYYY-MM-DD) | Không | Ngày bắt đầu |
| toDate | string (YYYY-MM-DD) | Không | Ngày kết thúc |
| page | number | Không | Số trang (0-based, mặc định 0) |
| size | number | Không | Số bản ghi/trang (mặc định 10) |
| groupBy | string | Không    | MONTH (mặc định MONTH) |


Response 200

```json
{
  "success": true,
  "data": {
    "summary": {
      "totalRevenue": 50000000,
      "totalOutstanding": 12000000,
      "totalBilledAmount": 62000000
    },
    "periods": [
      {
        "period": "2026-01",
        "revenue": 10000000,
        "outstanding": 2000000,
        "totalBilledAmount": 12000000
      }
    ],
    "items": [
      {
        "facilityId": 1,
        "facilityName": "Cơ sở A",
        "totalInvoices": 20,
        "revenue": 20000000,
        "outstanding": 5000000,
        "totalBilledAmount": 25000000
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

Response 200 (không có dữ liệu)

```json
{
  "success": true,
  "data": {
    "summary": {
      "totalRevenue": 0,
      "totalOutstanding": 0,
      "totalBilledAmount": 0
    },
    "periods": [],
    "items": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_DATE_RANGE",
    "message": "Khoảng thời gian không hợp lệ"
  }
}
```

Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Vui lòng đăng nhập để tiếp tục"
  }
}
```

Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Không có quyền xem báo cáo doanh thu"
  }
}
```
GET /api/v1/reports/revenue?fromDate=2026-01-01&toDate=2026-06-30&groupBy=MONTH
{
  "success": true,
  "data": {
    "summary": {
      "totalRevenue": 50000000,
      "totalOutstanding": 12000000,
      "totalBilledAmount": 62000000
    },

    "periods": [
      {
        "period": "2026-01",
        "revenue": 10000000,
        "outstanding": 2000000,
        "totalBilledAmount": 12000000
      },
      {
        "period": "2026-02",
        "revenue": 15000000,
        "outstanding": 3000000,
        "totalBilledAmount": 18000000
      }
    ],

    "items": [
      {
        "facilityId": 1,
        "facilityName": "Cơ sở A",
        "totalInvoices": 20,
        "revenue": 20000000,
        "outstanding": 5000000,
        "totalBilledAmount": 25000000
      }
    ]
  }
}
## 5. Technical Constraints

* Thời gian phản hồi tối đa: 500ms (P95)
* Giới hạn 100 yêu cầu/phút/người dùng
* Hỗ trợ phân trang cho danh sách cơ sở (`page` 0-based, mặc định `size=10`)
* Các giá trị trong summary luôn được tính trên toàn bộ dữ liệu thỏa điều kiện lọc và không bị ảnh hưởng bởi phân trang.
* Chỉ tính các hóa đơn có trạng thái:
  - PAID
  - UNPAID
  - OVERDUE
* Các hóa đơn có trạng thái CANCELLED hoặc DELETED không được tính vào báo cáo.
* Ghi nhận Audit Log cho thao tác xem báo cáo
* Giá trị hợp lệ của groupBy hiện tại là MONTH.
## 6. Out of Scope

* Xuất báo cáo Excel
* Xuất báo cáo PDF
* Biểu đồ thống kê nâng cao
* Dashboard thời gian thực
* Dự báo doanh thu
* Báo cáo lợi nhuận
