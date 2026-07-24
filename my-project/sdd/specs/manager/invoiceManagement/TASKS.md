# TASKS: Quản lý hóa đơn

## Phase 1: Setup & Foundational
- [x] T001 Create `InvoiceListItemDTO` with necessary fields in `src/main/java/com/quanlyphongtro/dto/InvoiceListItemDTO.java`
- [x] T002 Create `InvoiceDetailDTO` with necessary fields in `src/main/java/com/quanlyphongtro/dto/InvoiceDetailDTO.java`
- [x] T003 Create `InvoiceService` interface in `src/main/java/com/quanlyphongtro/service/InvoiceService.java`

## Phase 2: Danh sách và Tìm kiếm Hóa Đơn (US3, US7, US9)
**Goal:** Hiển thị danh sách hóa đơn với phân trang và lọc.
**Test Criteria:** 
- Phân quyền MANAGER thành công.
- Trả về danh sách chính xác với keyword và bộ lọc.
**Implementation:**
- [x] T004 [US3] Add `getInvoices` and `countInvoices` methods to `InvoiceDAO` in `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java`
- [x] T005 [US3] Implement `getInvoices` in `InvoiceServiceImpl` in `src/main/java/com/quanlyphongtro/service/impl/InvoiceServiceImpl.java`
- [x] T006 [US3] Create `InvoiceServlet` handling GET `/manager/invoices` in `src/main/java/com/quanlyphongtro/controller/manager/InvoiceServlet.java`
- [x] T007 [US3] Create UI `list.jsp` for displaying table of invoices with search/filters in `src/main/webapp/WEB-INF/views/manager/invoices/list.jsp`

## Phase 3: Tạo hóa đơn (US1, US2)
**Goal:** Cho phép quản lý tạo hóa đơn, tự động snapshot dữ liệu và tính tiền.
**Test Criteria:**
- Tự động lấy giá điện, nước từ `facilities`, và lấy chỉ số điện nước chốt trong kỳ từ `meter_readings`.
- Tính toán chính xác tổng tiền.
- Từ chối và ném exception nếu phòng chưa chốt số điện nước trong kỳ.
**Implementation:**
- [x] T008 [US1] Add `createInvoice` and methods to fetch facility prices and meter readings to `InvoiceDAO` in `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java`
- [x] T009 [US1] Implement `createInvoice` logic (snapshot price, calculate amounts) inside a transaction in `InvoiceServiceImpl` in `src/main/java/com/quanlyphongtro/service/impl/InvoiceServiceImpl.java`
- [x] T010 [US1] Update `InvoiceServlet` to handle GET and POST for `action=create` in `src/main/java/com/quanlyphongtro/controller/manager/InvoiceServlet.java`
- [x] T011 [US1] Create UI `create.jsp` in `src/main/webapp/WEB-INF/views/manager/invoices/create.jsp`

## Phase 4: Chi tiết, Điều chỉnh, và In hóa đơn (US4, US5, US6, US8)
**Goal:** Xem chi tiết, chỉnh sửa thông tin chưa thanh toán, xóa, đổi trạng thái và in hóa đơn (PDF browser print).
**Test Criteria:**
- 404 cho hóa đơn không tồn tại.
- 400 nếu cố sửa hóa đơn đã PAID.
- Bản in ẩn đi topbar và sidebar.
**Implementation:**
- [x] T012 [P] [US4] Add `getInvoiceDetail`, `updateInvoice`, `deleteInvoice`, `updateStatus` to `InvoiceDAO` in `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java`
- [x] T013 [US4] Implement detail, update, delete logic (recalculate amounts on update) in `InvoiceServiceImpl` in `src/main/java/com/quanlyphongtro/service/impl/InvoiceServiceImpl.java`
- [x] T014 [US4] Create `InvoiceDetailServlet` handling detail, edit, delete, update-status in `src/main/java/com/quanlyphongtro/controller/manager/InvoiceDetailServlet.java`
- [x] T015 [US4] Create UI `detail.jsp` displaying all info and including print styles (`@media print`) in `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp`
- [x] T016 [US5] Create UI `edit.jsp` for adjusting unpaid invoices in `src/main/webapp/WEB-INF/views/manager/invoices/edit.jsp`

## Dependencies
- Phase 1 must be completed first.
- Phase 2, Phase 3, Phase 4 can be developed in parallel for DAO/Service, but Servlets and UIs depend on respective Services.

## Implementation Strategy
- Hoàn thiện Phase 1 & Phase 2 để hiển thị được danh sách.
- Tập trung xử lý tính tiền phức tạp ở Phase 3.
- Hoàn thiện xem chi tiết và in ấn ở Phase 4.
