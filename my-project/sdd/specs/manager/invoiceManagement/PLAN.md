# Implementation Plan: Quản lý hóa đơn & Giao dịch thanh toán (Permanent Tenant & Contract Snapshot Binding)

## 1. Technical Context
- **Feature**: Quản lý hóa đơn & Giao dịch (Invoice & Payment Transaction Management).
- **Core Strategy**: 
  - Gắn định danh Snapshot cố định `contract_id` và `tenant_id` cho Hóa đơn (`invoices`) và Giao dịch thanh toán (`payments`).
  - Khi chốt/tạo hóa đơn mới, hệ thống tự động tìm và ghi trực tiếp `contract_id` & `tenant_id` của hợp đồng hiệu lực vào bảng `invoices`.
  - Thay thế toàn bộ các câu lệnh SQL JOIN động theo `room_id` bằng JOIN cố định `LEFT JOIN contracts c ON i.contract_id = c.contract_id` và `LEFT JOIN users u ON i.tenant_id = u.user_id`.
  - Bảo toàn 100% lịch sử tên người thuê, SĐT, Email của hóa đơn & giao dịch khi cư dân trả phòng (`INACTIVE` / soft-deleted) hoặc có cư dân mới vào ở.
  - Xóa bỏ hoàn toàn các hàm cũ thừa không còn sử dụng (`reportError`, `reportIncorrectInvoice`, `handleReportIncorrect`).
- **Architecture**: Mô hình MVC truyền thống với Jakarta EE Servlets, Service, DAO và JSP (`InvoiceServlet`, `InvoiceDetailServlet`, `ManagerNotificationsServlet`, `PaymentDAO`).
- **Dependencies**: Bảng `invoices`, `payments`, `contracts`, `rooms`, `users`, `facilities`, `meter_readings`, `notifications`.

## 2. Constitution Check
- [x] **Core Principle I (Layered Architecture)**: Xử lý qua Servlet (`InvoiceServlet`, `InvoiceDetailServlet`, `ManagerNotificationsServlet`), gọi xuống Service (`InvoiceService`), DAO (`InvoiceDAO`, `PaymentDAO`) và trả view JSP.
- [x] **Core Principle II (Consistent UI)**: Giữ nguyên giao diện chuẩn Mintlify/Hero Sky Gradient.
- [x] **Core Principle III (RBAC)**: Phân quyền `MANAGER` / `ADMIN` hợp lệ.
- [x] **Core Principle IV (Safe DB Transactions)**: Migration và Insert sử dụng SQL Transaction an toàn.

## 3. Data Model & Technical Updates
- **Cơ sở dữ liệu (`dbo.invoices`)**:
  - Thêm cột `contract_id` (INT NULL REFERENCES `dbo.contracts(contract_id)`).
  - Thêm cột `tenant_id` (INT NULL REFERENCES `dbo.users(user_id)`).
- **Thực thể DTO (`InvoiceDetailDTO`, `InvoiceListItemDTO`)**:
  - Bổ sung `contractId`, `tenantId`, `contractCode` vào DTOs.
- **Truy vấn DAO (`InvoiceDAO`, `PaymentDAO`, `NotificationDAO`, `DebtDAO`)**:
  - `InvoiceDAO`: `INSERT INTO invoices` lưu `contract_id` và `tenant_id`. `findById` & `findInvoices` JOIN theo `i.contract_id`.
  - `PaymentDAO`: `findPayments` & `findById` JOIN theo `i.contract_id` và `p.created_by`.
  - `NotificationDAO` & `DebtDAO`: JOIN thông tin hóa đơn theo `i.contract_id`.
- **Dọn dẹp Code thừa**:
  - Xóa `reportError` trong `InvoiceService` & `InvoiceServiceImpl`.
  - Xóa `reportIncorrectInvoice` trong `NotificationService` & `NotificationServiceImpl`.
  - Xóa `handleReportIncorrect` và action `report-incorrect` trong `ManagerNotificationsServlet`.

## 4. Phases
### Phase 1: Database Migration & Schema Update
- [x] Tạo script ALTER TABLE bổ sung `contract_id` và `tenant_id` vào `dbo.invoices`.
- [x] Chạy Migration script map dữ liệu hợp đồng cho các hóa đơn hiện có trong CSDL.

### Phase 2: DAO & Service Snapshot Binding
- [x] Cập nhật `InvoiceDAO.insertInvoice()` và `InvoiceServiceImpl.createInvoice()` để lưu snapshot `contract_id` và `tenant_id`.
- [x] Cập nhật SQL JOIN trong `InvoiceDAO` (`findById`, `findInvoices`, `countInvoices`) theo `i.contract_id`.
- [x] Cập nhật SQL JOIN trong `PaymentDAO` (`findPayments`, `findById`) theo `i.contract_id` và `p.created_by`.
- [x] Cập nhật SQL JOIN trong `NotificationDAO` và `DebtDAO`.

### Phase 3: Dọn dẹp Code thừa (Legacy Function Cleanup)
- [x] Xóa `reportError()` trong `InvoiceService` & `InvoiceServiceImpl`.
- [x] Xóa `reportIncorrectInvoice()` trong `NotificationService` & `NotificationServiceImpl`.
- [x] Xóa `handleReportIncorrect()` trong `ManagerNotificationsServlet`.

### Phase 4: Views & Verification
- [x] Cập nhật `detail.jsp` hiển thị thông tin người thuê cố định từ `InvoiceDetailDTO`.
- [x] Kiểm thử luồng cư dân trả phòng / đổi cư dân mới: Hóa đơn cũ giữ nguyên 100% thông tin người thuê ban đầu.

