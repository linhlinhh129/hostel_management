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

Là Admin, khi nhập `period` không hợp lệ hoặc để trống, tôi muốn hệ thống tự động hiển thị dữ liệu của tháng hiện tại thay vì báo lỗi.

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

WHEN `period` để trống hoặc không đúng format (`YYYY-MM` hoặc `MM/yyyy`)

THE SYSTEM SHALL tự động fallback về tháng hiện tại và tiếp tục hiển thị báo cáo bình thường.

> **Ghi chú thiết kế:** Feature dùng `period` (1 tháng duy nhất) thay vì `fromDate/toDate` range,
> nên lỗi `INVALID_DATE_RANGE` không còn áp dụng. Client-side validation được xử lý tự động
> bởi `<input type="month">` của browser.

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

THE SYSTEM SHALL sắp xếp dữ liệu theo thời gian giảm dần.


### 3.2 Phân quyền

WHILE người dùng không có quyền xem báo cáo doanh thu

THE SYSTEM SHALL từ chối truy cập chức năng báo cáo doanh thu.

WHEN Admin xem báo cáo doanh thu

## 4. Servlet Contract

### 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `AdminRevenueServlet` |
| **URL Pattern** | `GET /admin/revenue` — tổng quan (index) |
| **URL Pattern** | `GET /admin/revenue/by-facility` — doanh thu theo cơ sở |
| **URL Pattern** | `GET /admin/revenue/by-period` — doanh thu theo kỳ |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `BaseServlet`) |

---

### 4.2 Query Parameters

**GET /admin/revenue và /admin/revenue/by-facility**

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `period` | `String` | Kỳ cần xem. Chấp nhận `"YYYY-MM"` (input[type=month]) hoặc `"MM/yyyy"`. Mặc định tháng hiện tại |
| `page` | `int` | Chỉ dùng ở `by-facility`, mặc định `1` |

**GET /admin/revenue/by-period**

| Tham số | Kiểu | Mô tả |
|---|---|---|
| `months` | `int` | Số tháng gần nhất cần hiển thị, mặc định `12` |

---

### 4.3 Request Attributes — Tổng quan (index.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `systemRevenue` | `SystemRevenueDTO` | `RevenueService.getSystemRevenue(period)` | KPI tổng hợp toàn hệ thống |
| `facilityRevenues` | `List<FacilityRevenueStatDTO>` | `RevenueService.getFacilityRevenues(period)` | Doanh thu từng cơ sở (không phân trang) |
| `periodRevenues` | `List<FacilityRevenueStatDTO>` | `RevenueService.getRevenueTrend(6)` | Xu hướng 6 tháng gần nhất |
| `selectedPeriod` | `String` | Resolve từ param `period` | Kỳ đang chọn, format `"MM/yyyy"` |

---

### 4.4 Request Attributes — Theo cơ sở (by-facility.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `page` | `PageDTO<FacilityRevenueStatDTO>` | `RevenueService.getFacilityRevenuesPaged(period, page, 10)` | Dữ liệu phân trang, `PAGE_SIZE = 10` |
| `facilityRevenues` | `List<FacilityRevenueStatDTO>` | Từ `page.items` | Danh sách cơ sở trang hiện tại |
| `selectedPeriod` | `String` | Resolve từ param `period` | Kỳ đang chọn |

---

### 4.5 Request Attributes — Theo kỳ (by-period.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `periodRevenues` | `List<FacilityRevenueStatDTO>` | `RevenueService.getRevenueTrend(months)` | Danh sách kỳ, mỗi item là 1 tháng |
| `selectedMonths` | `int` | Parse từ param `months` | Số tháng đang chọn |

---

### 4.6 SystemRevenueDTO

| Field | Type | Mô tả |
|---|---|---|
| `totalRevenue` | `BigDecimal` | Tổng đã thu (hóa đơn PAID) |
| `totalOutstanding` | `BigDecimal` | Tổng dư nợ (UNPAID + OVERDUE) |
| `totalBilledAmount` | `BigDecimal` | Tổng phát sinh = `totalRevenue + totalOutstanding` |
| `paidCount` | `int` | Số hóa đơn PAID |
| `unpaidCount` | `int` | Số hóa đơn UNPAID |
| `overdueCount` | `int` | Số hóa đơn OVERDUE |
| `collectionRate` | `int` | Tỷ lệ thu (%) |

---

### 4.7 FacilityRevenueStatDTO

| Field | Type | Mô tả |
|---|---|---|
| `facilityId` | `int` | ID cơ sở |
| `facilityCode` | `String` | Mã cơ sở hoặc kỳ tháng (dùng cho by-period) |
| `facilityName` | `String` | Tên cơ sở |
| `totalRevenue` | `BigDecimal` | Doanh thu đã thu (PAID) |
| `totalOutstanding` | `BigDecimal` | Dư nợ (UNPAID + OVERDUE) |
| `totalBilledAmount` | `BigDecimal` | Tổng phát sinh |
| `paidCount` | `int` | Số hóa đơn PAID |
| `unpaidCount` | `int` | Số hóa đơn UNPAID |
| `overdueCount` | `int` | Số hóa đơn OVERDUE |
| `collectionRate` | `int` | Tỷ lệ thu (%) |

---

### 4.8 Xử lý period

`resolvePeriod()` chấp nhận 2 format:
- `"YYYY-MM"` từ `<input type="month">` → convert sang `"MM/yyyy"`
- `"MM/yyyy"` → dùng trực tiếp
- Bỏ trống hoặc không hợp lệ → fallback tháng hiện tại

---

### 4.9 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| Path không hợp lệ | HTTP 404 |

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 500ms (P95)
* Giới hạn 100 yêu cầu/phút/người dùng
* Hỗ trợ phân trang cho danh sách cơ sở (`page` 1-based, mặc định `PAGE_SIZE=10`)
* Các giá trị trong summary luôn được tính trên toàn bộ dữ liệu thỏa điều kiện lọc và không bị ảnh hưởng bởi phân trang.
* Chỉ tính các hóa đơn có trạng thái:
  - PAID
  - UNPAID
  - OVERDUE
* Các hóa đơn có trạng thái CANCELLED hoặc DELETED không được tính vào báo cáo.
* Giá trị hợp lệ của groupBy hiện tại là MONTH.
## 6. Out of Scope

* Xuất báo cáo Excel
* Xuất báo cáo PDF
* Biểu đồ thống kê nâng cao
* Dashboard thời gian thực
* Dự báo doanh thu
* Báo cáo lợi nhuận
