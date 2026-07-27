# Tasks: Quản lý hóa đơn & Giao dịch thanh toán (Snapshot Định danh Người thuê & Dọn dẹp Code thừa)

**Input**: Design documents from `/my-project/sdd/specs/manager/invoiceManagement/`

## Phase 1: Database Migration & Schema Update (Data Layer)

**Purpose**: Nâng cấp CSDL bổ sung lưu vết `contract_id` và `tenant_id` cho bảng `dbo.invoices`
- [x] T001 [P] [US1] Chạy script SQL `ALTER TABLE dbo.invoices ADD contract_id INT NULL, tenant_id INT NULL;` và thêm khóa ngoại `FK_invoices_contracts`, `FK_invoices_tenants`.
- [x] T002 [US1] Chạy script SQL Migration map các Hóa đơn lịch sử hiện tại với `contract_id` tương ứng theo thời gian hợp đồng của phòng.

## Phase 2: DAO & Service Updates (Snapshot Binding)

**Purpose**: Cập nhật logic tạo hóa đơn và câu lệnh SQL JOIN định danh cố định
- [x] T003 [US1] Cập nhật `src/main/java/com/quanlyphongtro/service/impl/InvoiceServiceImpl.java` trong `createInvoice()` để tự động lấy `contract_id` và `tenant_id` của hợp đồng hiệu lực tại thời điểm chốt.
- [x] T004 [US1] Cập nhật `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java` trong `insertInvoice()` để lưu `contract_id` và `tenant_id` vào CSDL.
- [x] T005 [US4] Cập nhật các câu lệnh SQL trong `InvoiceDAO.java` (`findById`, `findInvoices`, `countInvoices`) thay thế JOIN động bằng `LEFT JOIN contracts c ON i.contract_id = c.contract_id` và `LEFT JOIN users u ON i.tenant_id = u.user_id`.
- [x] T006 [US4] Cập nhật các câu lệnh SQL trong `src/main/java/com/quanlyphongtro/dao/PaymentDAO.java` (`findPayments`, `countPayments`, `findById`) để JOIN người thuê cố định qua `i.contract_id` và `p.created_by`.
- [x] T007 [US4] Cập nhật câu SQL trong `NotificationDAO.java` và `DebtDAO.java` để JOIN theo `i.contract_id`.

## Phase 3: Dọn dẹp Code thừa (Legacy Function Cleanup)

**Purpose**: Xóa bỏ các hàm và endpoint cũ dư thừa không còn sử dụng
- [x] T008 [P] Xóa bỏ hàm `reportError()` trong `src/main/java/com/quanlyphongtro/service/InvoiceService.java` và `InvoiceServiceImpl.java`.
- [x] T009 [P] Xóa bỏ hàm `reportIncorrectInvoice()` trong `src/main/java/com/quanlyphongtro/service/NotificationService.java` và `NotificationServiceImpl.java`.
- [x] T010 [P] Xóa bỏ handler `handleReportIncorrect()` và nhánh `action=report-incorrect` trong `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`.

## Phase 4: Views & Verification (Testing & Quality Assurance)

**Purpose**: Xác minh giao diện và chạy các kịch bản kiểm thử
- [x] T011 [P] [US4] Đảm bảo `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp` hiển thị đầy đủ thông tin người thuê cố định (`tenantName`, `tenantPhone`, `tenantEmail`).
- [x] T012 Chạy kịch bản kiểm thử: Tạo hóa đơn -> Thanh lý hợp đồng / Đổi cư dân mới -> Kiểm tra Hóa đơn & Giao dịch cũ vẫn giữ nguyên 100% tên người thuê cũ ban đầu.

---

## Dependencies & Execution Order
- Phase 1 (DB Migration) cần thực hiện đầu tiên để bảng `dbo.invoices` có cột `contract_id` và `tenant_id`.
- Phase 2 (DAO/Service) phụ thuộc vào Phase 1.
- Phase 3 (Dọn dẹp code thừa) có thể thực hiện song song với Phase 2.
- Phase 4 (Views & Verification) thực hiện sau cùng để nghiệm thu toàn bộ tính năng.

