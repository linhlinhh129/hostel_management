# Feature: Xem Nhật ký Hệ thống (Audit Log)

**Status:** Draft
**Author:** Admin
**Reviewer:** Tech Lead
**Date:** 2026-06-29
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

WHEN Admin lọc nhật ký theo người thực hiện (actor)
THE SYSTEM SHALL trả về các bản ghi có tên người thực hiện (full_name) khớp một phần với từ khóa.

WHEN Admin lọc nhật ký theo vai trò (role)
THE SYSTEM SHALL chỉ trả về các bản ghi do người dùng có vai trò tương ứng thực hiện.

WHEN không có bộ lọc vai trò nào được chọn
THE SYSTEM SHALL mặc định chỉ hiển thị nhật ký do MANAGER và OPERATOR thực hiện.

WHEN không có bản ghi nhật ký nào khớp với điều kiện lọc
THE SYSTEM SHALL trả về danh sách rỗng để UI hiển thị trạng thái "Không có dữ liệu".

### 3.2 Hiển thị nội dung nhật ký

WHILE hiển thị danh sách nhật ký
THE SYSTEM SHALL hiển thị các thông tin cơ bản sau trên bảng:

* Mã nhật ký (Log ID)
* Thời gian (Created At)
* Người thực hiện (Created By)
* Đối tượng (Entity Type & Tên)
* Hành động (Action)

WHILE hiển thị chi tiết bản ghi nhật ký
THE SYSTEM SHALL hiển thị đầy đủ các thông tin:

* Mã nhật ký
* Thời gian
* Người thực hiện
* Tên đối tượng và Loại đối tượng (Entity ID)
* Hành động
* Giá trị trước thay đổi (Old Value)
* Giá trị sau thay đổi (New Value)
* Địa chỉ IP
* Ghi chú (Comment)

WHEN hiển thị bản ghi nhật ký
THE SYSTEM SHALL hiển thị oldValue và newValue theo quy tắc sau:

* CREATE: oldValue = null, newValue = dữ liệu được tạo
* UPDATE: oldValue = dữ liệu trước khi sửa, newValue = dữ liệu sau khi sửa
* DELETE: oldValue = dữ liệu bị xóa, newValue = null

### 3.3 Validation tham số lọc

WHEN fromDate lớn hơn toDate
THE SYSTEM SHALL xử lý an toàn và trả về danh sách rỗng (không ném lỗi).

WHEN tham số `page` nhỏ hơn 1
THE SYSTEM SHALL mặc định về `page = 1`.

### 3.4 Phân quyền

WHILE người dùng không phải Admin
THE SYSTEM SHALL từ chối truy cập và trả về lỗi FORBIDDEN.

WHILE người dùng chưa đăng nhập
THE SYSTEM SHALL từ chối truy cập và trả về lỗi UNAUTHORIZED.
---

## 4. Servlet Contract

### 4.1 Servlet Entry Point

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `AdminAuditLogServlet` |
| **URL Pattern** | `GET /admin/audit-logs` — danh sách |
| **URL Pattern** | `GET /admin/audit-logs/*` — chi tiết |
| **Forward đến (list)** | `/WEB-INF/views/admin/audit-logs/list.jsp` |
| **Forward đến (detail)** | `/WEB-INF/views/admin/audit-logs/detail.jsp` |
| **Phân quyền** | Role = `ADMIN` (kiểm tra qua `RoleFilter`) |

---

### 4.2 Query Parameters (GET /admin/audit-logs)

| Tham số | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `actor` | `String` | Không | Tên người thực hiện — khớp một phần (`LIKE %actor%`) |
| `role` | `String` | Không | Vai trò người thực hiện (`MANAGER`, `OPERATOR`). Mặc định lọc `IN ('MANAGER','OPERATOR')` nếu bỏ trống |
| `entityType` | `String` | Không | Loại đối tượng (`facilities`, `rooms`, `users`, `notifications`, `invoices`, `payments`) |
| `action` | `String` | Không | Loại hành động (`CREATE`, `UPDATE`, `DELETE`, `ACTIVATE`...) |
| `dateFrom` | `String` | Không | Từ ngày, định dạng `YYYY-MM-DD` |
| `dateTo` | `String` | Không | Đến ngày, định dạng `YYYY-MM-DD` |
| `page` | `int` | Không | Trang hiện tại, mặc định `1` |

---

### 4.3 Request Attributes — Danh sách (list.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `auditLogs` | `List<AuditLog>` | `AuditLogService.list(actor, role, entityType, action, dateFrom, dateTo, page, 10)` | Danh sách nhật ký trang hiện tại |
| `totalCount` | `int` | `AuditLogService.count(actor, role, entityType, action, dateFrom, dateTo)` | Tổng số bản ghi khớp filter |
| `currentPage` | `int` | Parse từ query param `page` | Trang hiện tại |
| `hasNextPage` | `boolean` | `currentPage * PAGE_SIZE < totalCount` | Có trang kế tiếp không |
| `filterActor` | `String` | Query param `actor` | Giữ lại giá trị filter trên form |
| `filterRole` | `String` | Query param `role` | Giữ lại giá trị filter trên form |
| `filterEntityType` | `String` | Query param `entityType` | Giữ lại giá trị filter trên form |
| `filterAction` | `String` | Query param `action` | Giữ lại giá trị filter trên form |
| `filterDateFrom` | `String` | Query param `dateFrom` | Giữ lại giá trị filter trên form |
| `filterDateTo` | `String` | Query param `dateTo` | Giữ lại giá trị filter trên form |

**Hằng số:** `PAGE_SIZE = 10`

---

### 4.4 Request Attributes — Chi tiết (detail.jsp)

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|---|---|---|---|
| `auditLog` | `AuditLog` | `AuditLogService.getById(id)` | Bản ghi nhật ký đầy đủ, kèm `entityName` được lookup |

---

### 4.5 AuditLog Model

| Field | Type | Mô tả |
|---|---|---|
| `id` | `int` | ID bản ghi (`audit_log_id`) |
| `entityType` | `String` | Loại đối tượng (`facilities`, `rooms`, `users`...) |
| `entityId` | `Integer` | ID đối tượng bị tác động |
| `entityName` | `String` | Tên hiển thị của đối tượng (lookup batch, nullable) |
| `action` | `String` | Hành động (`CREATE`, `UPDATE`, `DELETE`...) |
| `oldValue` | `String` | Giá trị trước thay đổi (null với CREATE) |
| `newValue` | `String` | Giá trị sau thay đổi (null với DELETE) |
| `ipAddress` | `String` | Địa chỉ IP thực hiện |
| `comment` | `String` | Ghi chú bổ sung (nullable) |
| `createdBy` | `Integer` | `user_id` người thực hiện |
| `createdByName` | `String` | `full_name` người thực hiện (từ JOIN `dbo.users`) |
| `createdAt` | `LocalDateTime` | Thời gian thực hiện |

---

### 4.6 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| `{id}` không tồn tại | `NotFoundException` → HTTP 404 |
| Path không hợp lệ | HTTP 404 |
| DAO lỗi (exception) | Log `ERROR`, gọi `handleException` xử lý response chung |
---

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 500ms (P95)
* Rate limit: 100 requests/phút/người dùng
* Danh sách nhật ký phải được sắp xếp theo createdAt giảm dần theo mặc định
* Phân trang là bắt buộc (page 1-based, mặc định page=1, size=10)
* Chỉ người dùng có vai trò ADMIN được truy cập tính năng này
* Audit Log không thể bị sửa đổi hoặc xóa qua API này

---

## 6. Out of Scope

* Tạo bản ghi nhật ký thủ công
* Chỉnh sửa bản ghi nhật ký
* Xóa bản ghi nhật ký
* Xuất nhật ký ra file Excel/PDF
* Theo dõi nhật ký thời gian thực