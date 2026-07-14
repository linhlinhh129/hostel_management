# PLAN: Kế hoạch Thực thi Quản lý Người thuê (Manager)

**Status:** Completed  
**Date:** 2026-07-13  
**Priority:** High  
**Estimated Duration:** Completed

---

## 1. Tổng quan Giải pháp

Tính năng Quản lý Người thuê cho phép Manager quản lý thông tin cư dân chính (đại diện cho phòng). Chức năng tạo người thuê được tích hợp trực tiếp vào luồng tạo Hợp đồng mới.

**Kiến trúc:**
- Backend API: Servlet Controller (`ManagerTenantsServlet.java`) điều phối các thao tác cập nhật hồ sơ, khóa tài khoản, mở khóa và kết thúc thuê.
- Service & DAO: `TenantServiceImpl.java` xử lý các quy tắc nghiệp vụ validate số điện thoại/CCCD, khóa đăng nhập và gỡ liên kết phòng cư dân.
- Database: Tương tác với bảng `dbo.users` (với vai trò `role = 'TENANT'`), bảng `dbo.rooms` (gỡ liên kết `tenant_id`) và ghi nhật ký vào bảng `dbo.audit_logs`.
- Giao diện JSP: `list.jsp` hiển thị danh sách phân trang và các bộ lọc tìm kiếm; `detail.jsp` hiển thị chi tiết hồ sơ cá nhân, các nút chức năng (Sửa thông tin, Khóa/Mở khóa, Kết thúc thuê, Xóa mềm), và danh sách người phụ thuộc đi kèm.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Hoàn thành)
- Thiết kế luồng gán tài khoản cư dân tự động khi tạo hợp đồng phòng.
- Thiết kế Servlet Controller `/manager/tenants` điều khiển các luồng sửa thông tin, khóa tài khoản, mở khóa tài khoản, kết thúc thuê, xóa mềm.
- Thiết kế bộ validate định dạng thông tin liên lạc (số điện thoại và CCCD Việt Nam hợp lệ).

### Giai đoạn 2: Backend Development (Hoàn thành)
- Implement `countTenants` và `getTenants` hỗ trợ tìm kiếm và phân trang người thuê thuộc các cơ sở được phân công.
- Implement các phương thức quản lý tài khoản: `lockTenantAccount`, `unlockTenantAccount` (reset số lần nhập sai mật khẩu), và `softDeleteTenant` (cập nhật `deleted_at`).
- Implement nghiệp vụ kết thúc thuê phòng `endRental()` (gỡ bỏ liên kết `tenant_id` tại bảng `rooms`).

### Giai đoạn 3: Frontend Development (Hoàn thành)
- Xây dựng giao diện danh sách người thuê kèm phân trang và tìm kiếm.
- Phát triển trang chi tiết hiển thị hồ sơ cá nhân và tích hợp các modal (modal sửa thông tin người thuê, modal sửa thông tin người phụ thuộc).
- Tích hợp form xác nhận ngày kết thúc và lý do trả phòng.

### Giai đoạn 4: Testing & Deployment (Hoàn thành)

---

## 3. Key Technical Aspects

### Contracts Redirection
- Explicitly blocks direct tenant creation by redirecting requests to the contracts page.
- Tenant accounts are automatically created during contract signature.

### Account Lock/Unlock Mechanics
- Locking sets user account status to LOCKED in `dbo.users`.
- Unlocking restores status to ACTIVE and clears fail counts in the login attempt tracker.

### Soft Delete of Inactive Tenants
- Hard deletion is prevented.
- Sets `deleted_at = GETDATE()` in database to hide from list screens, while preserving past transaction history.

---

## 4. Success Criteria

- ✓ Manager can list, filter, search tenants within scope.
- ✓ Tenant profile details can be updated with phone/CCCD validations.
- ✓ Lock/Unlock functions work immediately.
- ✓ End rental successfully frees up the room.
- ✓ Operations logged in audit logs.

---

## 5. Timeline
- Completed.
