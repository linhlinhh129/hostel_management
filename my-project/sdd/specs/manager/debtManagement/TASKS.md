# Tasks: Quản lý công nợ

**Input**: Design documents from `/my-project/sdd/specs/manager/debtManagement/`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure
- [x] T001 Khởi tạo thư mục và xác nhận cấu trúc cho `DebtManagement`. (Đã có sẵn `Invoice` và Servlet cấu trúc chung).

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented
- [x] T002 Cập nhật file `AGENTS.md` (nếu cần thiết) và cấu hình lại RBAC trong `RoleFilter` cho `/manager/debts` (Role `MANAGER`) (US9).

## Phase 3: User Story 1-6 & 8 - Danh sách Công nợ (Priority: P1) 🎯 MVP

**Goal**: Hiển thị danh sách công nợ (hóa đơn UNPAID, OVERDUE), hiển thị đầy đủ thông tin (người thuê, phòng, số tiền, ngày nợ, phí phạt), và tính năng tìm kiếm/lọc.

### Implementation cho US1-6 & US8
- [x] T003 [P] [US1] Tạo DTO `DebtListItemDTO` trong `src/main/java/com/quanlynhatro/dto/DebtListItemDTO.java`.
- [x] T004 [US1] Cập nhật hoặc tạo `DebtDAO` với phương thức `getDebts(page, size, status, searchKeyword)` trong `src/main/java/com/quanlynhatro/dao/DebtDAO.java`.
- [x] T005 [US1] Tạo `DebtService` và `DebtServiceImpl` trong `src/main/java/com/quanlynhatro/service/DebtService.java`.
- [x] T006 [US1] Tạo `DebtPageServlet` tại `src/main/java/com/quanlynhatro/controller/manager/DebtPageServlet.java` xử lý logic `GET /manager/debts`.
- [x] T007 [US1] Tạo giao diện `list.jsp` tại `src/main/webapp/WEB-INF/views/manager/debts/list.jsp` hiển thị danh sách, phân trang, lọc và tính toán phí chậm nộp.

## Phase 4: User Story 7 - Xem chi tiết công nợ (Priority: P2)

**Goal**: Hiển thị chi tiết khoản nợ, tương tự như chi tiết hóa đơn nhưng tập trung vào số tiền còn nợ.

### Implementation cho US7
- [x] T008 [P] [US7] Tạo DTO `DebtDetailDTO` trong `src/main/java/com/quanlynhatro/dto/DebtDetailDTO.java`.
- [x] T009 [US7] Bổ sung phương thức `getDebtDetail(id)` vào `DebtDAO` và `DebtService`.
- [x] T010 [US7] Xử lý tham số `action=detail&id={id}` trong `DebtPageServlet` để forward sang trang chi tiết.
- [x] T011 [US7] Xây dựng giao diện `detail.jsp` tại `src/main/webapp/WEB-INF/views/manager/debts/detail.jsp`.

## Phase 5: User Story 10 - Gửi nhắc nhở thanh toán (Priority: P3)

**Goal**: Gửi thông báo In-app cho tài khoản người thuê khi hóa đơn quá hạn.

### Implementation cho US10
- [x] T012 [P] [US10] Đảm bảo bảng `notifications` và `NotificationDAO` đã sẵn sàng (hoặc tạo mới nếu chưa có).
- [x] T013 [US10] Viết phương thức `sendRemindNotification(invoiceId)` trong `DebtService`.
- [x] T014 [US10] Xử lý `POST /manager/debts?action=remind` trong `DebtPageServlet` để trigger tính năng nhắc nợ.
- [x] T015 [US10] Tích hợp nút "Nhắc nợ" vào màn hình danh sách và chi tiết công nợ.

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories
- [x] T016 Kiểm tra lại toàn bộ phân quyền cho các endpoint `/manager/debts`.
- [x] T017 Đảm bảo xử lý lỗi (Exception handling, 404 cho hóa đơn không tồn tại) được hiển thị thân thiện trên UI.
- [x] T018 Xác minh luồng In-app notification hiển thị đúng nội dung phía Tenant.
- [x] T019 Cập nhật DebtDetailDTO, DebtDAO, và detail.jsp để loại bỏ trường thuế (tax) và hiển thị Kỳ hợp đồng (contractPeriod).

---

## Dependencies & Execution Order

- Bắt buộc thực hiện Phase 2 (RBAC).
- Phase 3 (List view) là MVP và phải hoàn thành trước.
- Phase 4 (Detail view) có thể thực hiện sau Phase 3.
- Phase 5 (Notification) có thể làm độc lập với Phase 4 nhưng phụ thuộc vào danh sách công nợ (Phase 3).
