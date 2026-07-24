# TASKS: Quản lý công nợ

## Phase 1: Setup
- [x] T001 Create `DebtListItemDTO` with necessary fields in `src/main/java/com/quanlyphongtro/dto/DebtListItemDTO.java`
- [x] T002 Create `DebtDetailDTO` with necessary fields in `src/main/java/com/quanlyphongtro/dto/DebtDetailDTO.java`
- [x] T003 Create `DebtService` interface in `src/main/java/com/quanlyphongtro/service/DebtService.java`

## Phase 2: Danh sách công nợ (US1, US2, US3, US4, US5, US6, US8, US9)
**Goal:** Hiển thị danh sách hóa đơn `UNPAID` và `OVERDUE` với các thông tin tính toán (số ngày nợ, phí chậm nộp).
**Test Criteria:** 
- Phân quyền MANAGER thành công (403 nếu không phải MANAGER).
- Truy vấn đúng các hóa đơn trạng thái UNPAID/OVERDUE, nối đúng bảng `rooms`, `users`, `facilities`.
- Tính toán đúng phí chậm nộp cho các hóa đơn quá 3 ngày.

**Implementation:**
- [x] T004 [US1] Add `getDebts` and `countDebts` to `InvoiceDAO` with complex JOIN query in `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java`
- [x] T005 [US1] Implement `getDebts` logic in `DebtServiceImpl` including late fee calculation in `src/main/java/com/quanlyphongtro/service/impl/DebtServiceImpl.java`
- [x] T006 [US1] Create `DebtPageServlet` handling GET `/manager/debts` with pagination/filtering in `src/main/java/com/quanlyphongtro/controller/manager/DebtPageServlet.java`
- [x] T007 [US1] Implement UI `list.jsp` displaying table of debts with search/filter in `src/main/webapp/WEB-INF/views/manager/debts/list.jsp`

## Phase 3: Chi tiết công nợ (US7)
**Goal:** Xem chi tiết một hóa đơn nợ cụ thể.
**Test Criteria:**
- Trả về đúng 404 nếu hóa đơn không tồn tại hoặc không phải là UNPAID/OVERDUE.
- Hiển thị đầy đủ chi phí thành phần (tiền phòng, điện, nước).

**Implementation:**
- [x] T008 [US7] Add `getDebtDetail` query in `InvoiceDAO` in `src/main/java/com/quanlyphongtro/dao/InvoiceDAO.java`
- [x] T009 [US7] Implement `getDebtDetail` mapping in `DebtServiceImpl` in `src/main/java/com/quanlyphongtro/service/impl/DebtServiceImpl.java`
- [x] T010 [US7] Update `DebtPageServlet` to handle `action=detail` in `src/main/java/com/quanlyphongtro/controller/manager/DebtPageServlet.java`
- [x] T011 [US7] Implement UI `detail.jsp` displaying breakdown of costs in `src/main/webapp/WEB-INF/views/manager/debts/detail.jsp`

## Phase 4: Nhắc nhở thanh toán (US10)
**Goal:** Cho phép quản lý nhắc nợ từ danh sách.
**Implementation:**
- [x] T012 [P] [US10] Add "Nhắc nợ" button for `OVERDUE` rows navigating to notifications in `src/main/webapp/WEB-INF/views/manager/debts/list.jsp`

## Dependencies
- Phase 1 must be completed before Phase 2 and 3.
- Phase 2 and 3 can be developed in parallel for DAO/Service, but Servlet depends on Service.

## Implementation Strategy
- Bắt đầu với MVP: Hoàn thành Phase 2 để ban quản lý có thể xem được ngay danh sách người nợ.
- Sau đó phát triển tiếp màn hình chi tiết và nhắc nợ.
