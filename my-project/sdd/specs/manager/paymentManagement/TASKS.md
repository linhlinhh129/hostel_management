# Tasks: Quản lý Thanh toán (Payment Management Snapshot Binding)

**Input**: Design documents from `/my-project/sdd/specs/manager/paymentManagement/`

## Phase 1: Setup & Data Models

- [x] T001 [P] Tạo entity `src/main/java/com/quanlyphongtro/model/PaymentTransaction.java` ánh xạ bảng `payments`.
- [x] T002 [P] Tạo DTO `src/main/java/com/quanlyphongtro/dto/PaymentListItemDTO.java` cho danh sách thanh toán.
- [x] T003 [P] Tạo DTO `src/main/java/com/quanlyphongtro/dto/PaymentDetailDTO.java` cho màn hình chi tiết thanh toán.

## Phase 2: Foundational DAO & Services (Snapshot Binding)

- [x] T004 Khởi tạo `src/main/java/com/quanlyphongtro/dao/PaymentDAO.java` kế thừa `BaseDAO`.
- [x] T005 [P] Viết Query UPDATE trạng thái `payments` (`SUCCESS` / `REJECTED`) và lưu Audit log vết trong `PaymentDAO.java`.
- [x] T006 [P] Viết Query UPDATE trạng thái `invoices` thành `PAID` khi duyệt trong `PaymentDAO.java`.
- [x] T007 [US1] [P] Cập nhật SQL Query trong `src/main/java/com/quanlyphongtro/dao/PaymentDAO.java` (`findPayments`, `countPayments`, `findById`) để `LEFT JOIN` lấy thông tin người nộp tiền qua `i.contract_id` và `p.created_by` (thay thế logic `COALESCE(r.tenant_id, ...)`), đảm bảo lịch sử giao dịch không bị nhảy tên hay bị rỗng khi cư dân trả phòng.

## Phase 3: Business Logic & Controllers

- [x] T008 [P] Định nghĩa Interface `src/main/java/com/quanlyphongtro/service/PaymentService.java`.
- [x] T009 Triển khai Service `src/main/java/com/quanlyphongtro/service/impl/PaymentServiceImpl.java` xử lý logic phân trang và State Machine.
- [x] T010 [P] Xây dựng Servlet `src/main/java/com/quanlyphongtro/controller/manager/PaymentServlet.java` (`GET /manager/payments`).
- [x] T011 [P] Xây dựng Servlet `src/main/java/com/quanlyphongtro/controller/manager/PaymentDetailServlet.java` (`GET /manager/payments/*`, `POST /approve`, `POST /reject`).

## Phase 4: Views & Testing

- [x] T012 [P] Xây dựng View `src/main/webapp/WEB-INF/views/manager/payments/list.jsp`.
- [x] T013 [P] Xây dựng View `src/main/webapp/WEB-INF/views/manager/payments/detail.jsp`.
- [x] T014 [US1] Kiểm thử Kịch bản Snapshot người nộp tiền: Tạo giao dịch thanh toán -> Cư dân A trả phòng (`INACTIVE`) -> Kiểm tra thông tin người nộp tiền A vẫn giữ nguyên 100% trên trang danh sách và chi tiết thanh toán.

---

## Dependencies & Execution Order
- Phase 1 & 2 (DAO SQL Updates) cần thực hiện trước.
- Phase 3 (Services & Servlets) phụ thuộc vào Phase 2.
- Phase 4 (Views & Testing) nghiệm thu độc lập sau khi hoàn thành Phase 2 & 3.

