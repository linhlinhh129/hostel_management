# Tasks: Quản lý Thông báo & Báo cáo sai số điện nước (Manager & Operator Integration)

**Input**: Design documents from `my-project/sdd/specs/manager/notificationFacilityManagement/` and `my-project/sdd/specs/operator/`  
**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `quickstart.md`

## Format: `- [x]` or `- [ ]` `[ID] [P?] [Story] Description with file path`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Target user story (US1, US2, US3, US4)

---

## Phase 1: Setup (Shared Infrastructure)

- [x] T001 Verify database table definitions for `dbo.notifications`, `dbo.requests`, `dbo.meter_readings`, and `dbo.invoices` in database scripts
- [x] T002 Configure Flash Message utility in `src/main/java/com/quanlyphongtro/util/SessionUtil.java` for notification alerts

---

## Phase 2: Foundational (Blocking Prerequisites)

- [x] T003 [P] Implement `verifyFacilityManager` check in `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java`
- [x] T004 [P] Implement `verifyRoomManagerAndGetFacilityId` check in `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java`
- [x] T005 Setup base request routing and authorization check in `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`

---

## Phase 3: User Story 1 - Báo cáo sai số điện nước từ trang Xem Chi tiết Hóa đơn & Chuẩn hóa Nút Thao tác (Priority: P1) 🎯 MVP

**Goal**: Bấm nút "Xem" tại danh sách Hóa đơn (`/manager/invoices`) để vào Chi tiết Hóa đơn (`/manager/invoices/{id}`), từ đó bấm "Báo cáo sai số", nạp Tiêu đề/Nội dung và gửi công việc sang Module Danh sách Yêu cầu của Operator (`/operator/requests`), đồng thời loại bỏ luồng xóa hóa đơn.

- [x] T006 [P] [US1] Remove extra buttons from action column in `src/main/webapp/WEB-INF/views/manager/invoices/list.jsp` leaving ONLY the "Xem" button
- [x] T007 [P] [US1] Place "Báo cáo sai số" action button inside `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp` linking to `${ctx}/manager/notifications/send-operator?invoiceId=${invoice.invoiceId}` and remove Delete Invoice form
- [x] T008 [P] [US1] Update `getInvoiceDetailsForOperatorRequest` query method in `src/main/java/com/quanlyphongtro/dao/NotificationDAO.java`
- [x] T009 [US1] Implement `GET /manager/notifications/send-operator` handler to auto-fill invoice details in `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`
- [x] T010 [P] [US1] Ensure form view layout always displays Title, Content, and Operator dropdown in `src/main/webapp/WEB-INF/views/manager/notifications/send_operator.jsp`
- [x] T011 [US1] Implement `sendOperatorRequestTransaction` in `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java` with Database Transaction rollback
- [x] T012 [US1] Implement `POST /manager/notifications/send-operator` handler to redirect to `/manager/invoices` with success Flash Message in `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`
- [x] T013 [P] [US1] Ensure Operator request list query in `src/main/java/com/quanlyphongtro/dao/RequestDAO.java` fetches `UTILITY` tasks for `GET /operator/requests`
- [x] T014 [US1] Implement Tenant Payment Lock check for `REPORTED` meter readings in `src/main/java/com/quanlyphongtro/controller/tenant/TenantPaymentServlet.java` (FR-007)
- [x] T015 [P] [US1] Update Tenant invoice views `src/main/webapp/WEB-INF/views/tenant/invoices/list.jsp` & `detail.jsp` to disable payment button and display "Đang xử lý sai số điện nước" badge when status is `REPORTED`

---

## Phase 4: User Story 2 - Operator tiếp nhận, Đặt lịch & Báo cáo hoàn thành (Priority: P1)

**Goal**: Cho phép Operator xem Tiêu đề và Nội dung chi tiết tại `/operator/requests/detail?id={id}`, tiếp nhận/đặt lịch (`IN_PROGRESS`), và xác nhận hoàn thành (`COMPLETED`) với ghi chú hoàn thành tùy chọn (Optional).

- [x] T016 [P] [US2] Update `GET /operator/requests/detail` handler in `src/main/java/com/quanlyphongtro/controller/operator/DetailRequestServlet.java` to render UTILITY request details
- [x] T017 [US2] Implement `POST /operator/requests/detail` action `"schedule"` / `"accept"` to set request status to `IN_PROGRESS` in `src/main/java/com/quanlyphongtro/controller/operator/DetailRequestServlet.java`
- [x] T018 [US2] Implement `POST /operator/requests/detail` action `"complete"` to set request status to `COMPLETED` and save optional completion notes (`rejection_reason` / `notes`) in `src/main/java/com/quanlyphongtro/controller/operator/DetailRequestServlet.java`
- [x] T019 [P] [US2] Update `src/main/webapp/WEB-INF/views/operator/requests/detail.jsp` to display Title, Content, and Action buttons ("Xác nhận & Đặt lịch", "Xác nhận hoàn thành" with optional notes)

---

## Phase 5: User Story 3 - Gửi thông báo chung cho Cơ sở / Phòng (Priority: P2)

- [x] T020 [P] [US3] Implement `insertNotificationAndGetId` service in `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java`
- [x] T021 [US3] Implement `GET /manager/notifications/create` and `POST /manager/notifications/create` handlers in `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`
- [x] T022 [P] [US3] Create general announcement form view in `src/main/webapp/WEB-INF/views/manager/notifications/create.jsp`
- [x] T023 [US3] Render general notifications list in tab `general` in `src/main/webapp/WEB-INF/views/manager/notifications/list.jsp`

---

## Phase 6: User Story 4 - Gửi thông báo nhắc nợ quá hạn (Priority: P3)

- [x] T024 [P] [US4] Implement `sendDebtReminder` helper method with `NTF-DEBT-` code generator in `src/main/java/com/quanlyphongtro/dao/NotificationDAO.java`
- [x] T025 [US4] Implement `GET /manager/notifications/send-debt-reminder` and `POST /manager/notifications/send-debt-reminder` handlers in `src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java`
- [x] T026 [P] [US4] Create debt reminder form view in `src/main/webapp/WEB-INF/views/manager/notifications/send_debt_reminder.jsp`
- [x] T027 [US4] Render debt reminders list in tab `payment-reminder` in `src/main/webapp/WEB-INF/views/manager/notifications/list.jsp`

---

## Phase 7: Polish & Cross-Cutting Concerns

- [x] T028 Implement Audit Log recording for all notification and report actions in `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java`
- [x] T029 Add CSRF token validation to all POST forms in `send_operator.jsp`, `create.jsp`, and `send_debt_reminder.jsp`
- [x] T030 Run quickstart validation scenarios defined in `quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies
- **Setup (Phase 1)** -> **Foundational (Phase 2)** -> **User Story 1 (Phase 3)** & **User Story 2 (Phase 4)** -> **User Story 3 & 4 (Phase 5 & 6)** -> **Polish (Phase 7)**.
