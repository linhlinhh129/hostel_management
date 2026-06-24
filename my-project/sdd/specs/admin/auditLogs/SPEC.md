# Feature: Xem Nhật ký Hệ thống (Audit Log)

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** [YYYY-MM-DD]
**Priority:** Medium

## 1. Business Context

Tính năng Xem Nhật ký Hệ thống cho phép Admin theo dõi và truy vết toàn bộ các hành động quan trọng được thực hiện trong hệ thống quản lý nhà trọ. Thông tin nhật ký giúp tăng tính minh bạch, hỗ trợ kiểm tra, giám sát và xử lý các sự cố phát sinh.

Tính năng này hỗ trợ mục tiêu quản lý hệ thống hiệu quả, đảm bảo khả năng kiểm soát các thay đổi dữ liệu và nâng cao tính bảo mật cho toàn bộ hệ thống.

---

## 2. User Stories

### Story 1 (Happy Path)

As an Admin, I want to view system audit logs so that I can monitor user activities and system changes.

### Story 2 (Edge Case)

As an Admin, when there are no audit logs matching the selected filters, I want the system to display an empty result message.

### Story 3 (Edge Case)

As an Admin, when I select a large date range, I want the system to return paginated results to avoid performance issues.

---

## 3. Acceptance Criteria (EARS)

### 3.1 Xem danh sách nhật ký

WHEN Admin yêu cầu danh sách nhật ký hệ thống
THE SYSTEM SHALL trả về danh sách nhật ký có phân trang, sắp xếp theo createdAt giảm dần.

WHEN Admin lọc nhật ký theo entityType
THE SYSTEM SHALL chỉ trả về các bản ghi có loại đối tượng khớp với giá trị được chọn.

WHEN Admin lọc nhật ký theo action
THE SYSTEM SHALL chỉ trả về các bản ghi có loại hành động khớp với giá trị được chọn.

WHEN Admin lọc nhật ký theo khoảng thời gian
THE SYSTEM SHALL chỉ trả về các bản ghi được tạo trong khoảng thời gian được chỉ định.

WHEN Admin lọc nhật ký theo createdBy
THE SYSTEM SHALL chỉ trả về các bản ghi do người dùng được chỉ định thực hiện.

WHEN không có bản ghi nhật ký nào khớp với điều kiện lọc
THE SYSTEM SHALL trả về danh sách rỗng và thông báo "Không có dữ liệu".

### 3.2 Hiển thị nội dung nhật ký

WHILE hiển thị danh sách nhật ký
THE SYSTEM SHALL hiển thị tối thiểu các thông tin sau:

* Mã nhật ký
* Loại đối tượng (Entity Type)
* Mã đối tượng (Entity ID)
* Loại hành động (Action)
* Giá trị trước thay đổi (Old Value)
* Giá trị sau thay đổi (New Value)
* Địa chỉ IP
* Ghi chú (Comment)
* Người thực hiện (Created By)
* Thời điểm thực hiện (Created At)

WHEN hiển thị bản ghi nhật ký
THE SYSTEM SHALL hiển thị oldValue và newValue theo quy tắc sau:

* CREATE: oldValue = null, newValue = dữ liệu được tạo
* UPDATE: oldValue = dữ liệu trước khi sửa, newValue = dữ liệu sau khi sửa
* DELETE: oldValue = dữ liệu bị xóa, newValue = null

### 3.3 Validation tham số lọc

WHEN tham số lọc không hợp lệ được gửi lên
THE SYSTEM SHALL trả về HTTP 400 với lỗi INVALID_FILTER.

WHEN fromDate lớn hơn toDate
THE SYSTEM SHALL trả về HTTP 400 với lỗi INVALID_DATE_RANGE.

### 3.4 Phân quyền

WHILE người dùng không phải Admin
THE SYSTEM SHALL từ chối truy cập và trả về lỗi FORBIDDEN.

WHILE người dùng chưa đăng nhập
THE SYSTEM SHALL từ chối truy cập và trả về lỗi UNAUTHORIZED.
---

## 4. API Contract

### Endpoint

GET /api/v1/audit-logs

### Request

Query Parameters:

```json
{
  "entityType": "string",
  "action": "string",
  "createdBy": 1,
  "fromDate": "2026-01-01",
  "toDate": "2026-01-31",
  "page": 0,
  "size": 10
}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "auditLogId": 1,
        "entityType": "Tenant",
        "entityId": 15,
        "action": "UPDATE",
        "oldValue": "...",
        "newValue": "...",
        "ipAddress": "192.168.1.1",
        "comment": "Update tenant information",
        "createdBy": 1,
        "createdAt": "2026-06-01T10:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### Response 400

```json
{
  "success": false,
  "error": {
    "code": "INVALID_FILTER",
    "message": "Tham số lọc không hợp lệ"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "INVALID_DATE_RANGE",
    "message": "Khoảng thời gian không hợp lệ: fromDate không được lớn hơn toDate"
  }
}
```

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

### Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Vui lòng đăng nhập để tiếp tục"
  }
}
```

---

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 500ms (P95)
* Rate limit: 100 requests/phút/người dùng
* Danh sách nhật ký phải được sắp xếp theo createdAt giảm dần theo mặc định
* Phân trang là bắt buộc (page 0-based, mặc định size=10)
* Chỉ người dùng có vai trò ADMIN được truy cập tính năng này
* Audit Log không thể bị sửa đổi hoặc xóa qua API này

---

## 6. Out of Scope

* Tạo bản ghi nhật ký thủ công
* Chỉnh sửa bản ghi nhật ký
* Xóa bản ghi nhật ký
* Xuất nhật ký ra file Excel/PDF
* Theo dõi nhật ký thời gian thực