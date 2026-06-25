# Feature: Danh sách thông báo hệ thống

**Status:** Draft  
**Author:** Antigravity (AI Assistant)  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-25  
**Priority:** Medium

---

## 1. Business Context

Tính năng này giúp nhân viên vận hành (Operator) có thể nhận và xem các thông báo được gửi từ hệ thống hoặc từ cấp quản lý. Việc tích hợp danh sách thông báo giúp đảm bảo luồng thông tin thông suốt, nhân viên vận hành không bị bỏ lỡ các chủ trương, thay đổi chính sách hoặc các sự kiện quan trọng trong khu trọ mà họ quản lý.

---

## 2. User Stories

### Story 1 (Happy Path – Xem danh sách thông báo)

**As a** nhân viên vận hành,

**I want to** xem danh sách các thông báo hệ thống được gửi cho tôi hoặc khu trọ của tôi

**so that** tôi có thể nắm bắt kịp thời các thông tin và chỉ đạo quan trọng.

### Story 2 (Đọc chi tiết thông báo)

**As a** nhân viên vận hành,

**when** tôi thấy một thông báo có nội dung dài,

**I want to** nhấn vào thông báo đó

**so that** tôi có thể xem toàn văn chi tiết của nội dung.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách thông báo

**WHEN** user truy cập vào trang "Thông báo hệ thống" từ Sidebar

**THE SYSTEM SHALL**

- Lấy `facility_id` mà user đang quản lý.
- Hiển thị danh sách các thông báo thỏa mãn:
  - `status = 'SENT'`
  - `target_type = 'ALL'` HOẶC (`target_type = 'FACILITY'` và `facility_id` khớp với user).
  - Không bị xóa mềm (`deleted_at IS NULL`).
- Sắp xếp mặc định theo ngày gửi (`sent_at` hoặc `created_at` giảm dần).
- Hiển thị tóm tắt cho mỗi thông báo gồm:
  - Tiêu đề
  - Nội dung tóm tắt
  - Ngày gửi
  - Phạm vi (Toàn hệ thống / Khu trọ)

### AC02 – Không có dữ liệu

**WHEN** hệ thống không có thông báo nào dành cho user hiện tại

**THE SYSTEM SHALL** hiển thị thông báo:

```text
Chưa có thông báo nào.
```

### AC03 – Xem chi tiết

**WHEN** user nhấn vào một thẻ thông báo

**THE SYSTEM SHALL**

- Mở một Modal (Popup) hiển thị toàn bộ nội dung chi tiết của thông báo đó, hoặc tự động xổ (Accordion) nội dung xuống.

---

## 4. API Contract (Data Fetch)

*Note: Chức năng này được render qua Servlet JSP (Server-side rendering), dữ liệu được truyền thẳng vào Request Attribute thay vì API JSON.*

### Endpoint

```http
GET /operator/notifications
```

### Request Parameters

| Tham số | Kiểu | Bắt buộc | Mô tả |
|----------|--------|----------|--------|
| `page` | number | Không | Trang hiện tại (Mặc định: `1`) |

### Dữ liệu truyền cho View (JSP Attributes)

| Attribute | Kiểu | Mô tả |
|----------|--------|--------|
| `notifications` | List<Notification> | Danh sách các đối tượng thông báo |
| `totalPages` | Integer | Tổng số trang |
| `currentPage` | Integer | Trang hiện tại |

---

## 5. Technical Constraints

- **Max response time:** Dưới 500ms cho việc load danh sách.
- **Data Access:** Truy vấn lấy dữ liệu phải kiểm tra chính xác `facility_id` của Operator để tránh rò rỉ dữ liệu thông báo của khu trọ khác.
- **Indexing DB:** Đảm bảo có index cho cột `status`, `target_type`, `facility_id`, và `created_at` trong bảng `notifications`.

---

## 6. Out of Scope

- Không bao gồm chức năng tạo, chỉnh sửa hay xóa thông báo (Quyền của Admin).
- Không bao gồm đánh dấu "Đã đọc / Chưa đọc" (vì DB chưa hỗ trợ bảng phụ lưu trạng thái đọc).
- Không bao gồm Real-time update (Push Notification / WebSocket / Badge số lượng thông báo chưa đọc).
