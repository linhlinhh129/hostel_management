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

WHEN hệ thống không có user đang hoạt động 
THE SYSTEM SHALL trả về lỗi NO_RECIPIENT_FOUND.

WHEN tiêu đề vượt quá 100 ký tự
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

### 3.5 Phân quyền

WHILE người dùng không có quyền quản lý thông báo
THE SYSTEM SHALL từ chối truy cập chức năng quản lý thông báo.

WHILE người dùng chưa được xác thực
THE SYSTEM SHALL trả về lỗi UNAUTHORIZED.

WHILE người dùng đã được xác thực nhưng không có quyền quản lý thông báo
THE SYSTEM SHALL trả về lỗi FORBIDDEN.

## 4. Servlet Contract

### 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `AdminNotificationServlet` |
| **URL Pattern** | `GET /admin/notifications` — danh sách |
| **URL Pattern** | `GET /admin/notifications/create` — form tạo |
| **URL Pattern** | `POST /admin/notifications/create` — lưu tạo mới |
| **URL Pattern** | `GET /admin/notifications/{id}` — chi tiết |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `BaseServlet`) |

---

### 4.2 Request Attributes — Danh sách (list.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `page` | `PageDTO<Notification>` | `NotificationDAO.findAll(keyword, page, 10)` | Dữ liệu phân trang, `PAGE_SIZE = 10` |
| `keyword` | `String` | Query param `keyword` | Giữ lại giá trị tìm kiếm trên form |

**Lưu ý:** `NotificationDAO.findAll()` chỉ lấy thông báo có `target_type = 'ALL'`.

---

### 4.3 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `notification` | `Notification` | `NotificationDAO.findById(id)` | Thông tin đầy đủ thông báo |

---

### 4.4 Request Attributes — Form tạo (create.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `errorMessage` | `String` | `ValidationException.getMessage()` | Thông báo lỗi khi submit thất bại |

---

### 4.5 Validation — POST /admin/notifications/create

| Form param | Điều kiện hợp lệ | Lỗi ném ra |
|---|---|---|
| `title` | Không rỗng | `ValidationException` |
| `title` | Tối đa 255 ký tự | `ValidationException` |
| `content` | Không rỗng | `ValidationException` |
| `content` | Tối đa 1000 ký tự | `ValidationException` |
| `recipientType` | Phải là `"ALL"` | `ValidationException` |

**Khi tạo thành công:** mã thông báo tự động sinh qua `NotificationDAO.generateCode("ALL")`, format `NTF-ALL-001`, status = `SENT`, `createdBy` = ID user hiện tại từ session.

---

### 4.6 Notification Model

| Field | Type | Mô tả |
|---|---|---|
| `id` | `int` | ID bản ghi (`notification_id`) |
| `code` | `String` | Mã tự sinh (`NTF-ALL-001`) |
| `title` | `String` | Tiêu đề |
| `content` | `String` | Nội dung |
| `targetType` | `String` | Loại đối tượng (`ALL`) |
| `facilityId` | `Integer` | ID cơ sở (null với loại `ALL`) |
| `roomId` | `Integer` | ID phòng (null với loại `ALL`) |
| `status` | `String` | Trạng thái (`DRAFT`, `SENT`) |
| `createdBy` | `Integer` | `user_id` người tạo |
| `createdByName` | `String` | `full_name` người tạo (từ JOIN) |
| `createdAt` | `LocalDateTime` | Thời gian tạo |
| `sentAt` | `LocalDateTime` | Thời gian gửi |

---

### 4.7 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| `{id}` không tồn tại | `NotFoundException` → HTTP 404 |
| Validation thất bại (POST) | Forward về `create.jsp` với `errorMessage` |

## 5. Technical Constraints

Max response time: 500ms (P95)

Rate limit: 100 requests/phút/người dùng

Hỗ trợ phân trang cho danh sách thông báo

Ghi nhận Audit Log cho thao tác tạo và xem thông báo

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
