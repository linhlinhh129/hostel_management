# Feature: Notification Management (Quản lý Thông báo)

**Status:** Draft\
**Author:** Business Analyst\
**Date:** 2026-06-20\
**Priority:** Medium\
**Risk Level:** Low

---

# 1. Business Context & Goal

Tính năng **Notification Management** cho phép người thuê (Tenant) xem danh sách các thông báo được gửi từ Chủ nhà hoặc Ban quản lý, đồng thời xem nội dung chi tiết của từng thông báo.

Mục tiêu của tính năng là:

- Giúp người thuê cập nhật kịp thời các thông báo quan trọng.

- Đảm bảo mọi thông báo đều có thể được xem lại bất cứ lúc nào.

- Cung cấp trải nghiệm đơn giản, dễ theo dõi và dễ tra cứu.

---

# 2. User Story

**As a** Tenant,

**I want to** xem danh sách và nội dung chi tiết các thông báo,

**So that** tôi có thể cập nhật những thông tin mới nhất từ Ban quản lý hoặc Chủ nhà.

---

# 3. Actors & Roles

### Tenant (Authenticated)

- Xem danh sách thông báo.

- Xem nội dung chi tiết thông báo.

### System

- Trả về danh sách thông báo của Tenant.

- Trả về thông tin chi tiết của thông báo được chọn.

- Chỉ cho phép Tenant xem các thông báo thuộc quyền truy cập của mình.

---

# 4. Functional Requirements (EARS Notation)

## FR01 - Hiển thị danh sách thông báo

**WHEN** Tenant truy cập chức năng **Notification Management**

**THE SYSTEM SHALL**

- Hiển thị danh sách tất cả thông báo mà Tenant được phép xem.

---

## FR02 - Hiển thị thông tin tóm tắt

**WHEN** danh sách thông báo được tải thành công

**THE SYSTEM SHALL** hiển thị cho mỗi thông báo:

- Tiêu đề

- Ngày tạo

- Thời gian tạo

---

## FR03 - Sắp xếp thông báo

**WHEN** danh sách thông báo được hiển thị

**THE SYSTEM SHALL**

- Sắp xếp theo thời gian tạo giảm dần (Newest First).

---

## FR04 - Hiển thị thông báo mới nhất

**WHEN** có thông báo mới được tạo

**THE SYSTEM SHALL**

- Hiển thị thông báo đó ở đầu danh sách.

---

## FR05 - Xem chi tiết thông báo

**WHEN** Tenant chọn một thông báo

**THE SYSTEM SHALL**

- Chuyển đến màn hình Notification Detail.

---

## FR06 - Hiển thị chi tiết thông báo

**WHEN** màn hình Notification Detail được tải

**THE SYSTEM SHALL** hiển thị:

- Tiêu đề

- Nội dung

- Ngày tạo

- Thời gian tạo

---

## FR07 - Quay lại danh sách

**WHEN** Tenant chọn nút **Back**

**THE SYSTEM SHALL**

- Quay trở lại màn hình danh sách thông báo.

---

## FR08 - Không có dữ liệu

**WHERE** hệ thống không tìm thấy thông báo nào

**THE SYSTEM SHALL**

- Hiển thị Empty State với nội dung:

> "Hiện chưa có thông báo nào."

---

## FR09 - Lỗi tải dữ liệu

**WHERE** hệ thống không thể tải danh sách hoặc chi tiết thông báo

**THE SYSTEM SHALL**

- Hiển thị thông báo lỗi phù hợp.

- Cho phép người dùng thực hiện tải lại (Retry).

---

## FR10 - Người dùng chưa đăng nhập

**WHEN** người dùng truy cập Notification Management khi chưa xác thực

**THE SYSTEM SHALL**

- Chuyển hướng đến màn hình Login.

---

## FR11 - Thông báo không tồn tại

**WHERE** Tenant yêu cầu xem chi tiết một thông báo không tồn tại hoặc không có quyền truy cập

**THE SYSTEM SHALL**

- Trả về HTTP 404.

- Hiển thị thông báo:

> "Thông báo không tồn tại hoặc bạn không có quyền truy cập."

---

# 5. Non-functional Requirements

### Performance

- API lấy danh sách thông báo phản hồi dưới **300ms (P95)**.

- API lấy chi tiết thông báo phản hồi dưới **300ms (P95)**.

### Availability

- Dịch vụ Notification đạt **99.9% uptime**.

### Security

- Chỉ Tenant đã xác thực mới được truy cập.

- Tenant chỉ được xem thông báo thuộc quyền của mình hoặc thông báo công khai.

### Pagination

- Danh sách sử dụng phân trang.

- Mặc định **20 thông báo/trang**.

---

# 6. Technical Notes

## API

### Lấy danh sách

GET /api/v1/tenant/notifications?page=1&pageSize=20

---

### Lấy chi tiết

GET /api/v1/tenant/notifications/{notificationId}

---

## Database

Không thay đổi schema.

---

## Validation

- Người dùng đã đăng nhập.

- Vai trò phải là Tenant.

- notificationId là số nguyên dương.

- notificationId phải tồn tại.

- Chỉ được xem thông báo được gửi cho Tenant hoặc thông báo Public.

---

# 7. Response Data

## Notification List

```json
{
    "page":1,
    "pageSize":20,
    "totalItems":42,
    "items":[
        {
            "notificationId":1,
            "title":"Thông báo bảo trì hệ thống nước",
            "createdAt":"2026-06-10T08:00:00"
        },
        {
            "notificationId":2,
            "title":"Thông báo thu tiền phòng tháng 06",
            "createdAt":"2026-06-09T14:30:00"
        }
    ]
}
```

---

## Notification Detail

```json
{
    "notificationId":1,
    "title":"Thông báo bảo trì hệ thống nước",
    "content":"Hệ thống nước sẽ được bảo trì từ 08:00 đến 12:00 ngày 15/06/2026.",
    "createdAt":"2026-06-10T08:00:00"
}
```

---

# 8. Error Handling

| HTTP Code | Description | UI Action |
| --- | --- | --- |
| 401 | Unauthorized | Redirect Login |
| 403 | Forbidden | Hiển thị "Bạn không có quyền truy cập." |
| 404 | Notification Not Found | Hiển thị màn hình Not Found |
| 500 | Internal Server Error | Hiển thị thông báo lỗi và nút Retry |

---

# 9. Acceptance Criteria

- Tenant có thể xem danh sách thông báo.

- Danh sách được sắp xếp theo thời gian mới nhất.

- Mỗi thông báo hiển thị tiêu đề, ngày và giờ tạo.

- Có thể mở xem chi tiết thông báo.

- Chi tiết hiển thị đầy đủ tiêu đề, nội dung, ngày và giờ tạo.

- Có thể quay lại danh sách.

- Hiển thị Empty State khi không có dữ liệu.

- Hiển thị lỗi và Retry khi API thất bại.

- Truy cập khi chưa đăng nhập sẽ chuyển về Login.

- Truy cập notificationId không hợp lệ trả về 404.

---

# 10. UI Components

- Notification List

- Notification Card

- Notification Detail View

- Back Button

- Empty State

- Loading Indicator

- Error State

- Retry Button

---

# 11. Out of Scope

Không nằm trong phạm vi feature này:

- Tạo thông báo.

- Chỉnh sửa thông báo.

- Xóa thông báo.

- Đánh dấu đã đọc/chưa đọc.

- Push Notification (Firebase, APNs).

- Realtime Notification (WebSocket/SignalR).

- Gửi Email hoặc SMS.

- Quản lý thông báo của Ban quản lý/Admin.

---