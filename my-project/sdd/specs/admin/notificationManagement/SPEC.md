# Feature: Quản lý Thông báo

**Status:** Draft
**Author:** [Tên] | **Reviewer:** [Tên] | **Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Quản lý Thông báo cho phép Admin tạo và quản lý các thông báo gửi đến tất cả user trong hệ thống.

Tính năng này giúp ban quản lý truyền tải thông tin nhanh chóng, đảm bảo user nhận được các thông báo quan trọng liên quan đến vận hành, bảo trì, quy định hoặc các hoạt động của cơ sở.

## 2. User Stories

### Story 1 (Happy Path)

As an Admin, I want to create a notification so that I can send information to users.

### Story 2 (Happy Path)

As an Admin, I want to view the notification list so that I can track previously created notifications.

### Story 3 (Happy Path)

As an Admin, I want to view notification details so that I can review the content and recipient information.

### Story 4 (Edge Case)

As an Admin, when no notification matches the search criteria, I want the system to return an empty result.

### Story 5 (Edge Case)

As an Admin, when creating a notification with missing required information, I want the system to display validation errors.

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

WHEN hệ thống không có cư dân đang hoạt động 
THE SYSTEM SHALL trả về lỗi NO_RECIPIENT_FOUND.

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

### 3.4 Phân quyền

WHILE người dùng không có quyền quản lý thông báo
THE SYSTEM SHALL từ chối truy cập chức năng quản lý thông báo.

WHILE người dùng không có quyền tạo thông báo
THE SYSTEM SHALL không cho phép thực hiện thao tác tạo thông báo.

WHILE người dùng không có quyền xem thông báo
THE SYSTEM SHALL không cho phép truy cập danh sách và chi tiết thông báo.

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
  "recipientType": "ALL",
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "id": 1,
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

### Danh sách thông báo

Endpoint

```http
GET /api/v1/notifications
```

Query Parameters

```json
{
  "keyword": "bảo trì",
  "page": 1,
  "size": 10
}
```

Response 200

```json
{
  "success": true,
  "data": {
    "items": [],
    "page": 1,
    "size": 10,
    "total": 0
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
    "title": "Thông báo bảo trì thang máy",
    "content": "Nội dung thông báo",
    "createdAt": "2026-01-01T08:00:00",
    "createdBy": "Admin"
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

Ghi nhận Audit Log cho thao tác tạo và xem thông báo

Chỉ người dùng được phân quyền mới được truy cập API

Nội dung thông báo hỗ trợ tối đa 5000 ký tự

## 6. Dependencies

### Quản lý Cơ sở

Dùng để xác định danh sách cư dân theo cơ sở nhận thông báo.

## 7. Out of Scope

* Chỉnh sửa thông báo sau khi tạo
* Hủy hoặc xóa thông báo
* Lập lịch gửi thông báo
* Gửi email hoặc SMS
* Gửi thông báo đẩy (Push Notification)
* Xác nhận đã đọc thông báo
* Thống kê lượt xem thông báo
