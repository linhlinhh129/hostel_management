# Feature: Danh sách yêu cầu sửa chữa

**Status:** Draft  
**Author:** Phạm Anh Tú  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-10  
**Priority:** High

---

## 1. Business Context

Tính năng này giúp nhân viên vận hành có cái nhìn tổng quan về các yêu cầu sửa chữa được giao. Việc tích hợp các bộ lọc đa dạng giúp họ dễ dàng sắp xếp, ưu tiên xử lý yêu cầu theo trạng thái, khu vực hoặc loại sự cố, qua đó nâng cao hiệu quả vận hành và đảm bảo tiến độ xử lý.

---

## 2. User Stories

### Story 1 (Happy Path – Xem danh sách)

**As a** nhân viên vận hành,

**I want to** xem danh sách các yêu cầu sửa chữa được giao cho tôi

**so that** tôi biết mình cần theo dõi và xử lý những công việc nào.

### Story 2 (Lọc dữ liệu)

**As a** nhân viên vận hành,

**when** tôi cần ưu tiên công việc,

**I want to** lọc yêu cầu theo trạng thái, thể loại và vị trí (cơ sở, phòng).

### Story 3 (Xem chi tiết)

**As a** nhân viên vận hành,

**I want to** nhấn vào một yêu cầu cụ thể

**so that** tôi có thể xem chi tiết vấn đề và thực hiện các bước xử lý tiếp theo.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách yêu cầu

**WHEN** user truy cập vào trang "Danh sách yêu cầu"

**THE SYSTEM SHALL**

- Hiển thị danh sách các yêu cầu được giao cho user đó, sắp xếp mặc định theo ngày hẹn gần nhất.
- Hiển thị thông tin tóm tắt cho mỗi yêu cầu gồm:
  - Tiêu đề
  - Phòng
  - Ngày hẹn
  - Trạng thái

### AC02 – Lọc dữ liệu

**WHEN** user chọn một hoặc nhiều điều kiện lọc (trạng thái, thể loại, cơ sở, phòng gửi)

**THE SYSTEM SHALL**

- Gọi lại API.
- Hiển thị danh sách kết quả khớp với điều kiện lọc.

### AC03 – Xem chi tiết yêu cầu

**WHEN** user nhấn vào một thẻ (card/row) yêu cầu

**THE SYSTEM SHALL**

- Chuyển hướng user sang trang "Chi tiết yêu cầu" của ID tương ứng.

### AC04 – Không có dữ liệu

**WHEN** hệ thống không có dữ liệu cho bộ lọc hiện tại

**THE SYSTEM SHALL** hiển thị thông báo:

```text
Không có yêu cầu nào phù hợp
```

---

## 4. API Contract

### Endpoint

```http
GET /api/v1/requests
```

### Request Query Parameters

| Tham số | Kiểu | Bắt buộc | Mô tả |
|----------|--------|----------|--------|
| `status` | string | Không | Enum: `pending`, `in_progress`, `completed` |
| `category_id` | number | Không | ID thể loại |
| `facility_id` | number | Không | ID cơ sở |
| `room_id` | number | Không | ID phòng |
| `page` | number | Không | Mặc định: `1` |
| `limit` | number | Không | Mặc định: `20` |

### Response 200 (OK)

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": 1,
        "title": "Sửa điều hòa phòng 202",
        "room_name": "Phòng 202",
        "appointment_date": "2026-06-12",
        "status": "pending",
        "category_name": "Điện lạnh"
      }
    ],
    "total": 50,
    "page": 1,
    "total_pages": 3
  }
}
```

### Response Errors

- **401 Unauthorized** – Token hết hạn hoặc không hợp lệ.
- **500 Internal Server Error** – Lỗi hệ thống.

---

## 5. Technical Constraints

- **Max response time:** Dưới 500ms cho việc tải danh sách có bộ lọc.
- **Pagination:** Bắt buộc áp dụng Server-side Pagination để tránh quá tải payload khi dữ liệu lớn.
- **Indexing DB:** Cần đánh index cho các trường `assignee_id`, `status`, `facility_id` để tối ưu truy vấn tìm kiếm.

---

## 6. Out of Scope

- Cập nhật trạng thái yêu cầu trực tiếp từ màn hình danh sách (sẽ nằm trong spec của màn hình Chi tiết yêu cầu).
- Real-time update (Push Notification/WebSocket) khi có yêu cầu mới được giao (sẽ thực hiện ở Sprint sau).
