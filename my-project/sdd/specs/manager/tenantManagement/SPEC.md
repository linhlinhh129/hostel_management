# **Feature: Quản lý người thuê**

Status: Completed

Author: Antigravity

Reviewer: [Tên]

Date: 2026-07-14

Priority: High

---

## **1. Business Context**

Tính năng Quản lý Người thuê tập trung vào việc quản lý hồ sơ và tài khoản đăng nhập của người thuê chính đại diện cho từng phòng trọ. Việc này giải quyết khó khăn của Manager khi kiểm tra thông tin cư dân, đôn đốc bàn giao phòng, khóa/mở khóa tài khoản khi có vi phạm điều khoản thuê trọ, và quản lý trả phòng (`end-rental`) giải phóng phòng trống cho người khác thuê mà vẫn đảm bảo lưu trữ lịch sử thuê trọ để phục vụ đối soát công nợ.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Manager, I want to xem danh sách và tìm kiếm người thuê theo tên, số điện thoại hoặc mã số so that tôi dễ dàng tra cứu thông tin cư dân khi cần thiết.

### **Story 2 (Happy Path)**

As a Manager, I want to cập nhật các thông tin cá nhân của người thuê (Tên, SĐT, CCCD, Email...) so that dữ liệu lưu trú luôn phản ánh thông tin chính xác nhất.

### **Story 3 (Happy Path)**

As a Manager, I want to khóa hoặc mở khóa quyền đăng nhập của người thuê so that tôi xử lý kịp thời các trường hợp vi phạm quy định hoặc khôi phục lại quyền truy cập sau khi giải quyết xong.

### **Story 4 (Happy Path)**

As a Manager, I want to xác nhận kết thúc hợp đồng thuê và trả phòng cho cư dân so that tôi giải phóng phòng trống trên hệ thống và chuyển tài khoản cư dân sang lưu trữ lịch sử (`INACTIVE`).

---

## **3. Acceptance Criteria (EARS)**

### **Ngăn chặn tạo trực tiếp**

WHEN Manager attempts to open create page directly THE SYSTEM SHALL redirect to `/manager/contracts`.

### **Xem và cập nhật hồ sơ**

WHEN Manager filters or searches tenants THE SYSTEM SHALL return matching tenants within manager's assigned facilities.

WHEN Manager submits valid profile updates for a tenant THE SYSTEM SHALL update user record in `dbo.users`.

### **Khóa/Mở khóa tài khoản**

WHEN Manager locks active tenant account THE SYSTEM SHALL set status to `LOCKED`.

WHEN Manager unlocks locked tenant account THE SYSTEM SHALL set status to `ACTIVE` AND clear login attempts.

### **Kết thúc thuê phòng**

WHEN Manager ends rental for an active tenant with valid end date THE SYSTEM SHALL set status to `INACTIVE` AND set room `tenant_id = NULL` to free the room.

---

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ManagerTenantsServlet` |
| **URL Pattern** | `GET /manager/tenants` — danh sách người thuê |
| **URL Pattern** | `GET /manager/tenants/{id}` — chi tiết người thuê |
| **URL Pattern** | `POST /manager/tenants/{id}/edit` — submit cập nhật hồ sơ người thuê |
| **URL Pattern** | `POST /manager/tenants/{id}/lock` — khóa tài khoản người thuê |
| **URL Pattern** | `POST /manager/tenants/{id}/unlock` — mở khóa tài khoản người thuê |
| **URL Pattern** | `POST /manager/tenants/{id}/end-rental` — kết thúc hợp đồng trả phòng |
| **URL Pattern** | `POST /manager/tenants/{id}/delete` — xóa mềm tài khoản người thuê |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `tenants` | `List<User>` | `tenantService.getTenants(...)` | Danh sách người thuê chính |
| `currentPage`, `totalPages` | `int` | Xử lý logic phân trang | Phục vụ điều hướng phân trang |
| `keyword`, `status` | `String` | Query Params | Giữ lại các bộ lọc trên giao diện (form) |

---

### **4.3 Request Attributes — Chi tiết (detail.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `tenant` | `User` | `tenantService.getTenantDetail(tenantId)` | Thông tin chi tiết cá nhân cư dân |
| `room` | `Room` | `roomService.findById(tenant.getRoomId())`| Thông tin phòng cư dân đang thuê |
| `contract` | `Contract` | `contractService.findActiveByRoom(room.getId())` | Hợp đồng thuê phòng đang có hiệu lực |
| `dependents` | `List<Map<String, Object>>` | `tenantService.getTenantDependents(tenantId)` | Danh sách người phụ thuộc ở cùng phòng |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Cố tình truy cập trang tạo mới người thuê | Redirect về `/manager/contracts` kèm thông báo báo lỗi |
| Cập nhật trùng lặp email hoặc CCCD | Gán `error` message vào Session và redirect về trang chi tiết |
| Số điện thoại (SĐT) hoặc CCCD sai định dạng | Gán `error` message và redirect về trang chi tiết |
| Ngày trả phòng trước ngày ký hợp đồng | Gán `error` message và redirect về trang chi tiết |
| Thao tác cư dân ngoài cơ sở quản lý | Trả về lỗi `403 Forbidden` |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Manager chỉ được quản lý cư dân thuộc cơ sở mình được giao làm đại diện (`manager_id` trong `dbo.facilities`).
- **Tính toàn vẹn dữ liệu (Transaction):**
  - Mọi thao tác cập nhật trạng thái cư dân, khóa tài khoản, và kết thúc thuê đều phải lưu vết vào bảng `dbo.audit_logs`.
  - Kết thúc thuê phòng phải giải phóng phòng bằng cách gỡ bỏ `tenant_id = NULL` ở bảng `dbo.rooms`.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải danh sách người thuê kèm bộ lọc và tìm kiếm không vượt quá **250 ms (p95)**.

---

## **6. Out of Scope**

- Manager tự gán phòng trực tiếp cho cư dân tại phân hệ này (phải qua luồng hợp đồng).
- Khôi phục mật khẩu tài khoản người thuê bằng tay (cư dân tự khôi phục qua Email/OTP).
- Manager tự ý thay đổi vai trò (Role) của người thuê thành vai trò khác.
