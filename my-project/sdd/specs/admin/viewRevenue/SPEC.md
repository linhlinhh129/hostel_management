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

## 3. Acceptance Criteria (EARS)

### 3.1 Xem báo cáo doanh thu

WHEN Admin truy cập màn hình báo cáo doanh thu

THE SYSTEM SHALL hiển thị báo cáo doanh thu của tất cả cơ sở.

WHEN Admin chọn khoảng thời gian hợp lệ

THE SYSTEM SHALL hiển thị dữ liệu doanh thu trong khoảng thời gian được chọn.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL hiển thị tối thiểu các thông tin sau:

* Mã cơ sở
* Tên cơ sở
* Tổng số hóa đơn
* Tổng doanh thu
* Tổng số tiền đã thanh toán
* Tổng số tiền chưa thanh toán

WHEN không có dữ liệu doanh thu trong khoảng thời gian được chọn

THE SYSTEM SHALL hiển thị trạng thái "Không có dữ liệu".

WHEN ngày bắt đầu lớn hơn ngày kết thúc

THE SYSTEM SHALL trả về lỗi INVALID_DATE_RANGE.

### 3.2 Phân quyền

WHILE người dùng không có quyền xem báo cáo doanh thu

THE SYSTEM SHALL từ chối truy cập chức năng báo cáo doanh thu.

WHEN Admin xem báo cáo doanh thu

THE SYSTEM SHALL ghi nhận Audit Log bao gồm:
- User ID
- Thời gian truy cập
- Khoảng thời gian được lọc
## 4. API Contract

### Lấy báo cáo doanh thu

Endpoint

GET /api/v1/reports/revenue

Query Parameters

```json
{
  "fromDate": "2026-01-01",
  "toDate": "2026-01-31"
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "totalRevenue": 50000000,
    "facilities": [
      {
        "facilityId": 1,
        "facilityName": "Cơ sở A",
        "revenue": 25000000
      }
    ]
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
    "message": "Không có quyền truy cập"
  }
}
```

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 500ms (P95)
* Giới hạn 100 yêu cầu/phút/người dùng
* Hỗ trợ phân trang khi số lượng cơ sở lớn
* Chỉ tính các hóa đơn hợp lệ trong hệ thống
* Ghi nhận Audit Log cho thao tác xem báo cáo

## 6. Out of Scope

* Xuất báo cáo Excel
* Xuất báo cáo PDF
* Biểu đồ thống kê nâng cao
* Dashboard thời gian thực
* Dự báo doanh thu
* Báo cáo lợi nhuận
