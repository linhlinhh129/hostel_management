# Feature: Quản lý Thông báo cho Ban quản lý

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** [YYYY-MM-DD]
**Priority:** High

## 1. Business Context

Tính năng Quản lý Thông báo cho Ban quản lý cho phép Manager tạo và quản lý các thông báo gửi đến người thuê trong phạm vi cơ sở mà Manager được Admin phân công quản lý.

Tính năng này giúp Ban quản lý truyền tải nhanh các thông tin quan trọng liên quan đến vận hành, bảo trì, quy định hoặc hoạt động của từng cơ sở nhà trọ. Đồng thời, hệ thống đảm bảo Manager chỉ được gửi thông báo trong phạm vi được phân quyền, không được gửi thông báo đến toàn bộ hệ thống như Admin.

Feature này hỗ trợ mục tiêu nâng cao hiệu quả vận hành tại từng cơ sở, giúp người thuê nhận được thông tin kịp thời và đúng phạm vi quản lý.

## 2. User Stories

### Story 1 (Happy Path)

As a Manager, I want to create a notification for my assigned facility so that I can send important information to tenants in that facility.

### Story 2 (Happy Path)

As a Manager, I want to send notifications to tenants in a specific room within my assigned facility so that I can notify the correct recipients.

### Story 3 (Happy Path)

As a Manager, I want to view the notification list created within my assigned facility so that I can track previously created notifications.

### Story 4 (Happy Path)

As a Manager, I want to view notification details so that I can review the content, recipient scope, and created information.

### Story 5 (Edge Case)

As a Manager, when I select a facility that is not assigned to me, I want the system to reject the action.

### Story 6 (Edge Case)

As a Manager, when I try to send a notification to all tenants in the system, I want the system to prevent this action.

### Story 7 (Edge Case)

As a Manager, when creating a notification with missing required information, I want the system to display validation errors.

## 3. Acceptance Criteria (EARS)

### 3.1 Tạo thông báo

#### AC-01 Tạo thông báo hợp lệ

WHEN Manager gửi biểu mẫu tạo thông báo với dữ liệu hợp lệ và phạm vi nhận thuộc cơ sở được phân công
THE SYSTEM SHALL tạo thông báo mới và lưu vào hệ thống.

#### AC-02 Kiểm tra tiêu đề

WHEN Manager nhập tiêu đề trống
THE SYSTEM SHALL trả về lỗi VALIDATION_ERROR.

#### AC-03 Kiểm tra nội dung

WHEN Manager nhập nội dung trống
THE SYSTEM SHALL trả về lỗi VALIDATION_ERROR.

#### AC-04 Kiểm tra đối tượng nhận

WHEN Manager không chọn đối tượng nhận
THE SYSTEM SHALL trả về lỗi RECIPIENT_REQUIRED.

#### AC-05 Gửi thông báo theo cơ sở được phân công

WHEN Manager chọn đối tượng nhận theo cơ sở được phân công
THE SYSTEM SHALL gửi thông báo đến người thuê thuộc cơ sở đó.

#### AC-06 Gửi thông báo theo phòng thuộc cơ sở được phân công

WHEN Manager chọn đối tượng nhận theo phòng thuộc cơ sở được phân công
THE SYSTEM SHALL gửi thông báo đến người thuê đang ACTIVE trong phòng đó.

#### AC-07 Không cho phép gửi toàn hệ thống

WHEN Manager chọn đối tượng nhận là toàn bộ người thuê trong hệ thống
THE SYSTEM SHALL từ chối thao tác và trả về lỗi MANAGER_GLOBAL_NOTIFICATION_NOT_ALLOWED.

#### AC-08 Không cho phép gửi ngoài phạm vi cơ sở được phân công

WHEN Manager chọn cơ sở không thuộc danh sách cơ sở được Admin phân công
THE SYSTEM SHALL từ chối thao tác và trả về lỗi FACILITY_ACCESS_DENIED.

### 3.2 Danh sách thông báo

#### AC-09 Xem danh sách thông báo

WHEN Manager truy cập màn hình danh sách thông báo
THE SYSTEM SHALL hiển thị danh sách các thông báo thuộc phạm vi cơ sở mà Manager được phân công.

#### AC-10 Tìm kiếm thông báo

WHEN Manager tìm kiếm theo tiêu đề thông báo
THE SYSTEM SHALL trả về danh sách thông báo phù hợp trong phạm vi cơ sở được phân công.

#### AC-11 Không có dữ liệu

WHEN không có thông báo phù hợp với điều kiện tìm kiếm
THE SYSTEM SHALL hiển thị trạng thái "Không có dữ liệu".

#### AC-12 Phân trang

WHEN số lượng thông báo vượt quá giới hạn hiển thị trên một trang
THE SYSTEM SHALL hỗ trợ phân trang.

### 3.3 Chi tiết thông báo

#### AC-13 Xem chi tiết thông báo

WHEN Manager chọn một thông báo từ danh sách
THE SYSTEM SHALL hiển thị thông tin chi tiết của thông báo.

THE SYSTEM SHALL hiển thị tối thiểu các thông tin sau:

* Mã thông báo
* Tiêu đề
* Nội dung
* Ngày tạo
* Người tạo
* Cơ sở nhận thông báo
* Phòng nhận thông báo nếu có
* Đối tượng nhận
* Trạng thái thông báo

#### AC-14 Thông báo không tồn tại

WHEN Manager truy cập thông báo không tồn tại
THE SYSTEM SHALL trả về lỗi NOTIFICATION_NOT_FOUND.

#### AC-15 Không được xem thông báo ngoài phạm vi

WHEN Manager truy cập thông báo thuộc cơ sở không được phân công
THE SYSTEM SHALL từ chối truy cập và trả về lỗi NOTIFICATION_ACCESS_DENIED.

### 3.4 Phân quyền

#### AC-16 Không có quyền quản lý thông báo

WHILE người dùng không có quyền quản lý thông báo
THE SYSTEM SHALL từ chối truy cập chức năng quản lý thông báo.

#### AC-17 Không có quyền tạo thông báo

WHILE người dùng không có quyền tạo thông báo
THE SYSTEM SHALL không cho phép thực hiện thao tác tạo thông báo.

#### AC-18 Không có quyền xem thông báo

WHILE người dùng không có quyền xem thông báo
THE SYSTEM SHALL không cho phép truy cập danh sách và chi tiết thông báo.

## 4. API Contract

### 4.1 Tạo thông báo

Endpoint

```http
POST /api/v1/manager/notifications
```

Request

```json
{
  "title": "Thông báo bảo trì điện",
  "content": "Cơ sở Hòa Lạc sẽ bảo trì điện từ 08:00 đến 10:00 ngày 01/01/2026.",
  "recipientType": "FACILITY",
  "recipientIds": [1]
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "notificationId": 1,
    "title": "Thông báo bảo trì điện",
    "recipientType": "FACILITY",
    "status": "SENT"
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

Response 400 - Chưa chọn người nhận

```json
{
  "success": false,
  "error": {
    "code": "RECIPIENT_REQUIRED",
    "message": "Bắt buộc chọn đối tượng nhận thông báo"
  }
}
```

Response 403 - Không được gửi toàn hệ thống

```json
{
  "success": false,
  "error": {
    "code": "MANAGER_GLOBAL_NOTIFICATION_NOT_ALLOWED",
    "message": "Ban quản lý không được gửi thông báo đến toàn bộ hệ thống"
  }
}
```

Response 403 - Không có quyền với cơ sở

```json
{
  "success": false,
  "error": {
    "code": "FACILITY_ACCESS_DENIED",
    "message": "Bạn không có quyền gửi thông báo đến cơ sở này"
  }
}
```

Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Chưa đăng nhập hoặc phiên đăng nhập không hợp lệ"
  }
}
```

### 4.2 Lấy danh sách thông báo

Endpoint

```http
GET /api/v1/manager/notifications?keyword=bao%20tri&page=1&size=10&facilityId=1
```

Query Parameters

| Trường     | Kiểu dữ liệu | Bắt buộc | Mô tả                                         |
| ---------- | ------------ | -------- | --------------------------------------------- |
| keyword    | string       | Không    | Tìm kiếm theo tiêu đề hoặc nội dung thông báo |
| page       | number       | Không    | Số trang                                      |
| size       | number       | Không    | Số lượng bản ghi trên một trang               |
| facilityId | number       | Không    | Lọc theo cơ sở được phân công                 |

Response 200

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "notificationId": 1,
        "title": "Thông báo bảo trì điện",
        "recipientType": "FACILITY",
        "facilityId": 1,
        "facilityName": "Cơ sở Hòa Lạc",
        "createdAt": "2026-01-01T08:00:00",
        "createdBy": "manager01",
        "status": "SENT"
      }
    ],
    "page": 1,
    "size": 10,
    "total": 1
  }
}
```

### 4.3 Xem chi tiết thông báo

Endpoint

```http
GET /api/v1/manager/notifications/{notificationId}
```

Response 200

```json
{
  "success": true,
  "data": {
    "notificationId": 1,
    "title": "Thông báo bảo trì điện",
    "content": "Cơ sở Hòa Lạc sẽ bảo trì điện từ 08:00 đến 10:00 ngày 01/01/2026.",
    "recipientType": "FACILITY",
    "recipients": [
      {
        "facilityId": 1,
        "facilityName": "Cơ sở Hòa Lạc"
      }
    ],
    "createdAt": "2026-01-01T08:00:00",
    "createdBy": "manager01",
    "status": "SENT"
  }
}
```

Response 403

```json
{
  "success": false,
  "error": {
    "code": "NOTIFICATION_ACCESS_DENIED",
    "message": "Bạn không có quyền xem thông báo này"
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

Max response time: 500ms tại P95.

Rate limit: 100 requests/phút/người dùng.

Danh sách thông báo phải hỗ trợ phân trang.

Danh sách thông báo phải hỗ trợ tìm kiếm theo tiêu đề hoặc nội dung.

Manager chỉ được xem và tạo thông báo trong phạm vi cơ sở được Admin phân công.

Manager không được phép gửi thông báo đến toàn bộ người thuê trong hệ thống.

Manager không được phép gửi thông báo đến cơ sở hoặc phòng ngoài phạm vi được phân công.

Hệ thống phải kiểm tra quyền truy cập cơ sở trước khi tạo thông báo.

Hệ thống phải ghi nhận Audit Log cho thao tác tạo và xem thông báo.

Chỉ người dùng được phân quyền mới được truy cập API.

Nội dung thông báo hỗ trợ tối đa 5000 ký tự.

Thông báo đã tạo không được chỉnh sửa nội dung nếu hệ thống không hỗ trợ cập nhật thông báo.

## 6. Dependencies

### Quản lý Cơ sở

Dùng để xác định cơ sở mà Manager được Admin phân công quản lý.

### Quản lý Phòng

Dùng để xác định danh sách phòng thuộc cơ sở mà Manager được phân công.

### Quản lý Người thuê

Dùng để xác định người thuê ACTIVE nhận thông báo theo cơ sở hoặc phòng.

### Quản lý Nhân sự

Dùng để xác định Manager và phạm vi cơ sở được phân công.

### Xác thực và Phân quyền

Dùng để kiểm tra quyền tạo, xem và quản lý thông báo.

### Audit Log

Dùng để ghi nhận thao tác tạo và xem thông báo.

## 7. Business Rules

### BR-01

Manager chỉ được tạo thông báo cho cơ sở được Admin phân công.

### BR-02

Manager không được gửi thông báo đến toàn bộ người thuê trong hệ thống.

### BR-03

Manager chỉ được gửi thông báo đến người thuê ACTIVE trong cơ sở hoặc phòng thuộc phạm vi quản lý.

### BR-04

Nếu Manager chọn cơ sở không được phân công, hệ thống phải từ chối thao tác.

### BR-05

Nếu Manager chọn phòng không thuộc cơ sở được phân công, hệ thống phải từ chối thao tác.

### BR-06

Thông báo được tạo bởi Manager phải lưu thông tin người tạo và phạm vi gửi.

### BR-07

Một thông báo sau khi được gửi thành công sẽ có trạng thái SENT.

### BR-08

Mọi thao tác tạo và xem thông báo phải được ghi nhận vào Audit Log.

## 8. Notification Recipient Type

| Recipient Type | Ý nghĩa                                   | Manager có được dùng không? |
| -------------- | ----------------------------------------- | --------------------------- |
| ALL            | Gửi toàn bộ người thuê trong hệ thống     | Không                       |
| FACILITY       | Gửi theo cơ sở được phân công             | Có                          |
| ROOM           | Gửi theo phòng thuộc cơ sở được phân công | Có                          |

## 9. Out of Scope

* Gửi thông báo toàn hệ thống.
* Chỉnh sửa thông báo sau khi tạo.
* Hủy hoặc xóa thông báo.
* Lập lịch gửi thông báo.
* Gửi email hoặc SMS.
* Gửi thông báo đẩy.
* Xác nhận đã đọc thông báo.
* Thống kê lượt xem thông báo.
* Gửi thông báo đến cơ sở không được phân công.
* Gửi thông báo đến phòng không thuộc cơ sở được phân công.
