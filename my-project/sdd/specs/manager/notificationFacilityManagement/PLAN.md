# PLAN: Kế hoạch Thực thi Quản lý Thông báo cho Ban quản lý (Manager)

**Status:** Completed  
**Date:** 2026-07-13  
**Priority:** High  
**Estimated Duration:** Completed

---

## 1. Tổng quan Giải pháp

Tính năng Quản lý Thông báo cho Ban quản lý cho phép Manager gửi thông báo chung (theo cơ sở/phòng), nhắc nợ quá hạn tiền phòng (gửi đến phòng có hóa đơn nợ), và báo lỗi chỉ số điện nước gửi Operator.

**Kiến trúc:**
- Backend API: Servlet Controller (`ManagerNotificationsServlet.java`) tiếp nhận các yêu cầu GET/POST gửi từ giao diện.
- Service & DAO: `NotificationServiceImpl.java` và `NotificationDAO.java` xử lý nghiệp vụ, giao dịch cập nhật trạng thái chỉ số điện nước và thêm yêu cầu sửa chỉ số cho Operator.
- Database: Tác động vào các bảng `dbo.notifications`, `dbo.requests`, `dbo.meter_readings` và `dbo.invoices`.
- Audit Log: Ghi nhận lịch sử hoạt động vào bảng `dbo.audit_logs`.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Hoàn thành)
- Thiết kế luồng phân loại thông báo theo tabs (`general`, `payment-reminder`, `incorrect-utility`).
- Thiết kế cấu trúc lưu trữ thông báo nhắc nợ (sử dụng mã tiền tố `NTF-DEBT-`).
- Thiết kế luồng báo lỗi chỉ số điện nước và bàn giao công việc cho Operator thông qua bảng `requests` với danh mục `UTILITY`.

### Giai đoạn 2: Backend Development (Hoàn thành)
- Implement `countManagerNotifications` và `getManagerNotifications` hỗ trợ phân trang, tìm kiếm và lọc theo tabs.
- Implement nghiệp vụ gửi nhắc nợ `sendDebtReminder()` và tạo mã tự động.
- Implement nghiệp vụ báo cáo chỉ số điện nước sai lệch bằng cơ chế Transaction: đổi trạng thái chỉ số điện nước thành `REPORTED` và chèn bản ghi yêu cầu hỗ trợ `'PENDING'` gán cho Operator.
- Enforce check phân quyền cơ sở (`verifyFacilityManager` / `verifyRoomManagerAndGetFacilityId`).

### Giai đoạn 3: Frontend Development (Hoàn thành)
- Xây dựng giao diện danh sách thông báo chia theo tabs và danh sách hóa đơn bị báo sai chỉ số.
- Thiết kế form gửi thông báo chung, form gửi nhắc nợ tiền phòng quá hạn và form gửi yêu cầu sửa chỉ số cho Operator.

### Giai đoạn 4: Testing & Deployment (Hoàn thành)

---

## 3. Key Technical Aspects

### Overdue Debt Reminders
- Generated using a specialized helper method `sendDebtReminder` in DAO.
- Generates notification code starting with `NTF-DEBT-` to easily classify them in the database.
- Targeted at a specific room (`target_type = 'ROOM'`).

### Utility Incorrect Report Transaction
- Executed as a database transaction (`sendOperatorRequestTransaction`).
- Inserts a request record under `UTILITY` category, status `PENDING`, assigned to the selected Operator.
- Updates the status of the related meter reading to `REPORTED` to lock it from billing actions.

### Scope and Authorization
- Manager is restricted to facilities where `manager_id` matches their own user ID.
- Attempts to target foreign rooms/facilities are verified and rejected by the service layer.

---

## 4. Success Criteria

- ✓ Manager can send announcements, debt reminders, and operator requests.
- ✓ Tabs filtration and pagination working.
- ✓ Scope restrictions strictly enforced.
- ✓ Database transactions commit/rollback safely on error.
- ✓ Actions logged in audit logs.

---

## 5. Timeline
- Completed.
