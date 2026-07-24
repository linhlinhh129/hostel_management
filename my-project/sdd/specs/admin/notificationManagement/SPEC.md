# Feature: Quản lý Thông báo

**Status:** Draft
**Author:** [Tên] | **Reviewer:** [Tên] | **Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Quản lý Thông báo cho phép Admin tạo và quản lý các thông báo gửi đến tất cả user trong hệ thống.

Tính năng này giúp ban quản lý truyền tải thông tin nhanh chóng, đảm bảo user nhận được các thông báo quan trọng liên quan đến vận hành, bảo trì, quy định hoặc các hoạt động của cơ sở.

## 2. User Stories

### Story 1 (Happy Path)

Là Admin, tôi muốn tạo thông báo để gửi thông tin đến người dùng.

### Story 2 (Happy Path)

Là Admin, tôi muốn xem danh sách thông báo để theo dõi các thông báo đã tạo trước đó.

### Story 3 (Happy Path)

Là Admin, tôi muốn xem chi tiết thông báo để xem lại nội dung và thông tin người nhận.

### Story 4 (Edge Case)

Là Admin, khi không có thông báo nào khớp với tiêu chí tìm kiếm, tôi muốn hệ thống trả về kết quả rỗng.

### Story 5 (Edge Case)

Là Admin, khi tạo thông báo thiếu thông tin bắt buộc, tôi muốn hệ thống hiển thị lỗi xác thực.

## 3. Acceptance Criteria (EARS)

### 3.1 Tạo thông báo

WHEN Admin gửi biểu mẫu tạo thông báo với dữ liệu hợp lệ
THE SYSTEM SHALL tạo thông báo mới và lưu vào hệ thống.

WHEN Admin nhập tiêu đề trống
THE SYSTEM SHALL trả về lỗi VALIDATION_ERROR.

WHEN Admin nhập nội dung trống
THE SYSTEM SHALL trả về lỗi VALIDATION_ERROR.

WHEN Admin chọn đối tượng nhận khác "ALL" 
THE SYSTEM SHALL trả về lỗi VALIDATION_ERROR.

WHEN tiêu đề vượt quá 255 ký tự
THE SYSTEM SHALL trả về lỗi TITLE_TOO_LONG.

WHEN nội dung thông báo vượt quá 1000 ký tự
THE SYSTEM SHALL trả về lỗi CONTENT_TOO_LONG.

### 3.2 Danh sách thông báo

WHEN Admin truy cập màn hình danh sách thông báo
THE SYSTEM SHALL hiển thị danh sách các thông báo đã được tạo.

WHEN Admin tìm kiếm theo tiêu đề thông báo
THE SYSTEM SHALL trả về danh sách thông báo phù hợp.

WHEN không có thông báo phù hợp với điều kiện tìm kiếm
THE SYSTEM SHALL hiển thị trạng thái "Không có dữ liệu".

WHEN số lượng thông báo vượt quá giới hạn hiển thị trên một trang
THE SYSTEM SHALL hỗ trợ phân trang.

### 3.3 Chi tiết thông báo

WHEN Admin chọn một thông báo từ danh sách
THE SYSTEM SHALL hiển thị thông tin chi tiết của thông báo.

THE SYSTEM SHALL hiển thị tối thiểu các thông tin sau:

* Mã thông báo
* Tiêu đề
* Nội dung
* Ngày tạo
* Người tạo
* Đối tượng nhận

WHEN Admin truy cập thông báo không tồn tại
THE SYSTEM SHALL trả về lỗi NOTIFICATION_NOT_FOUND.

### 3.4 Tìm kiếm thông báo

WHEN Admin tìm kiếm theo từ khóa tiêu đề thông báo
THE SYSTEM SHALL trả về danh sách thông báo có tiêu đề chứa từ khóa được nhập.

## 4. API Contract

### Tạo thông báo

Endpoint

```http
POST /api/v1/notifications
```

Request

```json
{
  "title": "Thông báo bảo trì thang máy",
  "content": "Thang máy A sẽ bảo trì từ 08:00 đến 12:00 ngày 01/01/2026.",
  "recipientType": "ALL"
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "id": 1,
    "code": "NTF-ALL-001",
    "title": "Thông báo bảo trì thang máy"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Dữ liệu không hợp lệ"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "TITLE_TOO_LONG",
    "message": "Tiêu đề không được vượt quá 255 ký tự"
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "CONTENT_TOO_LONG",
    "message": "Nội dung không được vượt quá 1000 ký tự"
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
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

### Danh sách thông báo

Endpoint

```http
GET /api/v1/notifications
```

Query Parameters

| Tham số | Kiểu | Bắt buộc | Mô tả |
|---------|------|----------|-------|
| keyword | string | Không | Tìm kiếm theo tiêu đề |
| page | number | Không | Số trang (0-based, mặc định 0) |
| size | number | Không | Số bản ghi/trang (mặc định 10) |

Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": 1,
        "code": "NTF-ALL-001",
        "title": "Thông báo bảo trì thang máy",
        "recipientType": "ALL",
        "createdAt": "28/06/2026 09:00:00",
        "createdBy": "Admin"
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
    "items": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

### Chi tiết thông báo

Endpoint

```http
GET /api/v1/notifications/{notificationId}
```

Response 200

```json
{
  "success": true,
  "data": {
    "id": 1,
    "code": "NTF-ALL-001",
    "title": "Thông báo bảo trì thang máy",
    "content": "Nội dung thông báo",
    "createdAt": "28/06/2026 09:00:00",
    "createdBy": "Admin",
    "recipientType": "ALL"
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
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

Response 404

```json
{
  "success": false,
  "error": {
    "code": "NOTIFICATION_NOT_FOUND",
    "message": "Không tìm thấy thông báo"
  }
}
```

## 5. Technical Constraints

Max response time: 500ms (P95)

Rate limit: 100 requests/phút/người dùng

Hỗ trợ phân trang cho danh sách thông báo

Chỉ người dùng được phân quyền mới được truy cập API

Nội dung thông báo hỗ trợ tối đa 1000 ký tự

## 6. Out of Scope

* Chỉnh sửa thông báo sau khi tạo
* Hủy hoặc xóa thông báo
* Lập lịch gửi thông báo
* Gửi email hoặc SMS
* Gửi thông báo đẩy (Push Notification)
* Xác nhận đã đọc thông báo
* Thống kê lượt xem thông báo
