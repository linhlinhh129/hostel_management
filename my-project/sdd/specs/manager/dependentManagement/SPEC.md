# **Feature: Quản lý người phụ thuộc**

Status: Completed

Author: Antigravity

Reviewer: [Tên]

Date: 2026-07-14

Priority: High

---

## **1. Business Context**

Tính năng Quản lý Người phụ thuộc giúp lưu giữ thông tin những thành viên cư trú cùng người thuê chính (như người thân, vợ/chồng, con cái). Việc này nhằm giải quyết nỗi đau của Ban quản lý khi không nắm rõ nhân khẩu cư trú thực tế trong phòng trọ, hỗ trợ các cơ quan chức năng khi cần đối soát nhân khẩu, và duy trì dữ liệu cư trú lịch sử sau khi người thuê chính kết thúc hợp đồng thuê.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Manager, I want to thêm thông tin người phụ thuộc vào tài khoản người thuê chính đang hoạt động so that tôi ghi nhận đầy đủ nhân khẩu đang sinh sống tại phòng.

### **Story 2 (Happy Path)**

As a Manager, I want to xem danh sách người phụ thuộc trên hồ sơ chi tiết của người thuê so that tôi nắm được tổng số người đang ở trong phòng.

### **Story 3 (Happy Path)**

As a Manager, I want to chỉnh sửa thông tin cá nhân của người phụ thuộc so that dữ liệu lưu trữ luôn chính xác khi có thay đổi.

### **Story 4 (Happy Path)**

As a Manager, I want to xóa mềm người phụ thuộc khi họ không còn cư trú tại phòng so that danh sách hiển thị chỉ chứa những người đang ở thực tế nhưng vẫn giữ lại lịch sử đối soát.

---

## **3. Acceptance Criteria (EARS)**

### **Thêm người phụ thuộc**

WHEN Manager submits valid dependent info for an ACTIVE tenant THE SYSTEM SHALL insert a new record in `dbo.dependents` AND redirect to the tenant's profile.

WHEN Manager submits dependent info missing `fullName` or `relationship` THE SYSTEM SHALL reject and return validation error.

### **Xem thông tin người phụ thuộc**

WHEN Manager views tenant detail page THE SYSTEM SHALL load and display all ACTIVE dependents of that tenant.

WHEN Manager clicks on a dependent THE SYSTEM SHALL display dependent details, edit form, and delete button.

### **Cập nhật người phụ thuộc**

WHEN Manager submits valid updated dependent info THE SYSTEM SHALL update the record in `dbo.dependents` AND redirect to the dependent detail page.

### **Xóa mềm người phụ thuộc**

WHEN Manager submits deletion request for a dependent THE SYSTEM SHALL set `deleted_at = GETDATE()` in `dbo.dependents` AND redirect to the tenant's profile.

---

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ManagerTenantsServlet` |
| **URL Pattern** | `POST /manager/tenants/{tenantId}/dependents/add` — thêm người phụ thuộc |
| **URL Pattern** | `GET /manager/dependents/{id}` — xem chi tiết người phụ thuộc |
| **URL Pattern** | `POST /manager/dependents/{id}/edit` — lưu chỉnh sửa người phụ thuộc |
| **URL Pattern** | `POST /manager/dependents/{id}/remove` — xóa mềm người phụ thuộc |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

*(Danh sách người phụ thuộc được hiển thị trực tiếp trong trang chi tiết người thuê `/manager/tenants/{id}`)*

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `dependents` | `List<Map<String, Object>>` | `tenantService.getTenantDependents(tenantId)` | Danh sách người phụ thuộc đang hoạt động của người thuê |

---

### **4.3 Request Attributes — Chi tiết (detail.jsp)**

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `dependent` | `Map<String, Object>` | `tenantService.getDependentDetail(dependentId, managerId)` | Thông tin chi tiết người phụ thuộc (gồm các thông tin cá nhân và người thuê chính) |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Người thuê chính không ở trạng thái ACTIVE | Redirect về trang chi tiết người thuê kèm lỗi `Người thuê chính phải ở trạng thái ACTIVE` |
| Thiếu họ tên hoặc quan hệ | Gán `error` message vào Session và redirect về lại trang điền form |
| Số điện thoại hoặc CCCD sai định dạng | Gán `error` message và redirect về lại trang điền form |
| Người phụ thuộc không tồn tại | Trả về lỗi `404 Not Found` |
| Thao tác ngoài cơ sở được phân quyền | Trả về lỗi `403 Forbidden` |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Manager chỉ được quản lý người phụ thuộc thuộc phòng của cơ sở được phân quyền quản lý (`manager_id` trong `dbo.facilities`).
  - Số CCCD của người phụ thuộc hiển thị trên giao diện phải được che phần giữa bằng phương thức `getMaskedIdentityNumber()` (chỉ hiển thị 3 số đầu và 3 số cuối).
- **Tính toàn vẹn dữ liệu (Transaction):**
  - Mọi thao tác Thêm, Cập nhật, Xóa mềm đều phải được ghi log vào hệ thống thông qua `AuditLogHelper.log`.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải chi tiết thông tin người phụ thuộc không vượt quá **200 ms (p95)**.
  - Thời gian xử lý ghi nhận Thêm/Sửa/Xóa mềm không vượt quá **300 ms (p95)**.

---

## **6. Out of Scope**

- Gửi tin nhắn thông báo tự động (SMS/Zalo/Email) cho cư dân khi thêm người phụ thuộc.
- Cho phép gán một người phụ thuộc vào nhiều người thuê chính khác nhau.
- Tạo tài khoản đăng nhập hệ thống cho người phụ thuộc.
