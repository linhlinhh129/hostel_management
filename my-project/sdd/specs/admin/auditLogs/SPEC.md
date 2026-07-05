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

WHEN tham số lọc không hợp lệ được gửi lên
THE SYSTEM SHALL trả về HTTP 400 với lỗi INVALID_FILTER.

WHEN fromDate lớn hơn toDate
THE SYSTEM SHALL xử lý an toàn và trả về danh sách rỗng (không ném lỗi 400).

### 3.4 Phân quyền

WHILE người dùng không phải Admin
THE SYSTEM SHALL từ chối truy cập và trả về lỗi FORBIDDEN.

WHILE người dùng chưa đăng nhập
THE SYSTEM SHALL từ chối truy cập và trả về lỗi UNAUTHORIZED.
---

## 4. Giao tiếp Dữ liệu (SSR)

Tính năng này được triển khai theo kiến trúc Server-Side Rendering (SSR) sử dụng Servlet và JSP.

### Request Lọc dữ liệu

**HTTP Method:** GET
**URL:** `/admin/audit-logs`

**Query Parameters:**
* `actor` (string, optional): Tên người thực hiện (khớp một phần).
* `role` (string, optional): Vai trò người thực hiện (VD: MANAGER, OPERATOR).
* `entityType` (string, optional): Phân loại chức năng/đối tượng (facilities, rooms, users...).
* `action` (string, optional): Loại hành động (CREATE, UPDATE, DELETE...).
* `dateFrom` (string, optional): Từ ngày (YYYY-MM-DD).
* `dateTo` (string, optional): Đến ngày (YYYY-MM-DD).
* `page` (int, optional): Trang hiện tại (mặc định 1).

### Phản hồi

*   **Thành công (200):** Forward dữ liệu danh sách `List<AuditLog>` (kèm thông tin phân trang) xuống view `/WEB-INF/views/admin/audit-logs/list.jsp` để render HTML.
*   **Chi tiết (200):** Khi truy cập `/admin/audit-logs/{id}`, hệ thống query chi tiết (bao gồm việc parse `oldValue`, `newValue`) và forward xuống `/WEB-INF/views/admin/audit-logs/detail.jsp`.
*   **Lỗi 403/401:** Quản lý tập trung qua Filter/Interceptor, redirect về trang đăng nhập hoặc hiển thị lỗi không có quyền truy cập.
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