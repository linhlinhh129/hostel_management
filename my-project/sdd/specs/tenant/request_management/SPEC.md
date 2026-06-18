# Feature: Request Management

**Status:** Draft

**Author:** Business Analyst

**Reviewer:** Product Owner

**Date:** 2026-06-10

**Priority:** High

---

# 1. Business Context

Người thuê cần một kênh chính thức để gửi các yêu cầu hỗ trợ, phản ánh sự cố hoặc đề xuất đến Ban quản lý trong suốt thời gian thuê phòng.

Tính năng này giúp chuẩn hóa quy trình tiếp nhận và xử lý yêu cầu, giảm trao đổi thủ công qua điện thoại hoặc tin nhắn, đồng thời tăng khả năng theo dõi tiến độ xử lý và nâng cao chất lượng dịch vụ.

Feature này hỗ trợ mục tiêu kinh doanh của hệ thống là số hóa quy trình vận hành và cải thiện trải nghiệm của người thuê.

---

# 2. User Stories

### Story 1 (Happy Path)

As a Tenant,

I want to tạo và gửi yêu cầu hỗ trợ,

so that Ban quản lý có thể tiếp nhận và xử lý vấn đề của tôi.

---

### Story 2 (Happy Path)

As a Tenant,

I want to xem danh sách và trạng thái các yêu cầu đã gửi,

so that tôi có thể theo dõi tiến độ xử lý.

---

### Story 3 (Edge Case)

As a Tenant,

when yêu cầu của tôi chưa được xử lý,

I want to xem trạng thái hiện tại của yêu cầu.

---

### Story 4 (Edge Case)

As a Tenant,

when tôi nhập thiếu hoặc sai thông tin bắt buộc,

I want to nhận được thông báo lỗi để chỉnh sửa trước khi gửi.

---

# 3. Acceptance Criteria (EARS)

## AC01 - Hiển thị danh sách yêu cầu

WHEN người thuê truy cập chức năng Quản lý yêu cầu

THE SYSTEM SHALL hiển thị danh sách các yêu cầu do người thuê hiện tại tạo.

---

## AC02 - Hiển thị thông tin tóm tắt yêu cầu

WHEN danh sách yêu cầu được tải thành công

THE SYSTEM SHALL hiển thị:

* Mã yêu cầu
* Thể loại yêu cầu
* Tiêu đề
* Ngày tạo
* Trạng thái

---

## AC03 - Sắp xếp yêu cầu theo thời gian

WHEN danh sách yêu cầu được hiển thị

THE SYSTEM SHALL sắp xếp các yêu cầu theo ngày tạo giảm dần.

---

## AC04 - Tạo yêu cầu thành công

WHEN người thuê gửi yêu cầu với dữ liệu hợp lệ

THE SYSTEM SHALL tạo yêu cầu mới với trạng thái mặc định là "Chờ xử lý"

AND return HTTP 201.

---

## AC05 - Đính kèm hình ảnh

WHEN người thuê tải lên hình ảnh hợp lệ

THE SYSTEM SHALL lưu hình ảnh và liên kết với yêu cầu tương ứng.

---

## AC06 - Xem chi tiết yêu cầu

WHEN người thuê chọn một yêu cầu trong danh sách

THE SYSTEM SHALL hiển thị đầy đủ thông tin của yêu cầu.

---

## AC07 - Hiển thị trạng thái xử lý

WHEN người thuê xem chi tiết yêu cầu

THE SYSTEM SHALL hiển thị trạng thái hiện tại của yêu cầu.

Các trạng thái bao gồm:

* Chờ xử lý
* Đang xử lý
* Hoàn thành
* Từ chối

---

## AC08 - Thiếu thông tin bắt buộc

WHEN người thuê gửi yêu cầu mà thiếu Tiêu đề hoặc Nội dung

THE SYSTEM SHALL return HTTP 400

WITH error code REQ_001.

---

## AC09 - Thể loại yêu cầu không hợp lệ

WHEN người thuê gửi yêu cầu với thể loại không tồn tại trong hệ thống

THE SYSTEM SHALL return HTTP 400

WITH error code REQ_002.

---

## AC10 - Tệp đính kèm không hợp lệ

WHEN người thuê tải lên tệp không thuộc định dạng cho phép

THE SYSTEM SHALL return HTTP 400

WITH error code REQ_003.

---

## AC11 - Truy cập trái phép

WHEN người dùng chưa đăng nhập truy cập chức năng Quản lý yêu cầu

THE SYSTEM SHALL return HTTP 401 Unauthorized.

---

## AC12 - Cách ly dữ liệu người thuê

WHILE người thuê đang sử dụng hệ thống

THE SYSTEM SHALL chỉ cho phép truy cập các yêu cầu do chính người thuê đó tạo.

---

# 4. API Contract

## Lấy danh sách yêu cầu

Endpoint:

GET /api/v1/tenant/requests

Response 200

```json
{
  "success": true,
  "data": [
    {
      "requestId": "REQ001",
      "category": "Bảo trì",
      "title": "Máy lạnh không hoạt động",
      "status": "PENDING",
      "createdAt": "2026-06-10T08:00:00"
    }
  ]
}
```

---

## Tạo yêu cầu

Endpoint:

POST /api/v1/tenant/requests

Request

```json
{
  "categoryId": 1,
  "title": "Máy lạnh không hoạt động",
  "content": "Máy lạnh không thể khởi động",
  "attachmentUrl": "image.jpg"
}
```

Response 201

```json
{
  "success": true,
  "data": {
    "requestId": "REQ001",
    "status": "PENDING"
  }
}
```

Response 400

```json
{
  "success": false,
  "error": {
    "code": "REQ_001",
    "message": "Title is required"
  }
}
```

Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Authentication required"
  }
}
```

---

## Xem chi tiết yêu cầu

Endpoint:

GET /api/v1/tenant/requests/{requestId}

Response 200

```json
{
  "success": true,
  "data": {
    "requestId": "REQ001",
    "category": "Bảo trì",
    "title": "Máy lạnh không hoạt động",
    "content": "Máy lạnh không thể khởi động",
    "attachmentUrl": "image.jpg",
    "roomCode": "A101",
    "status": "PENDING",
    "createdAt": "2026-06-10T08:00:00"
  }
}
```

---

# 5. Technical Constraints

* Max response time: 500ms (p95)
* Rate limit: 100 requests/minute/user
* Chỉ chấp nhận file JPG, JPEG, PNG.
* Kích thước tệp tối đa: 5MB.
* Hệ thống phải lưu lịch sử thay đổi trạng thái yêu cầu.
* Dữ liệu phải được phân quyền theo Tenant ID.

---

# 6. Out of Scope

* Chat trực tiếp giữa Tenant và Ban quản lý.
* Bình luận trên yêu cầu.
* Đánh giá chất lượng xử lý yêu cầu.
* Push Notification thời gian thực.
* Email thông báo khi trạng thái yêu cầu thay đổi.
* SLA Monitoring và Escalation Workflow.



