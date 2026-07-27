# Tasks: Danh sách yêu cầu sửa chữa (Operator)

**Input**: Design documents from `specs/operator/ListRequest/`
**Prerequisites**: plan.md, spec.md, data-model.md

## Phase 1: Setup & Foundational

- [x] T001 Kiểm tra thư mục và files hiện có cho trang danh sách yêu cầu operator.

---

## Phase 2: User Story 1 & 2 - Danh sách & Lọc dữ liệu (Priority: P1)

**Goal**: Cho phép xem danh sách yêu cầu, có hỗ trợ lọc và hiển thị thông tin rõ ràng bằng tiếng Việt (Thể loại, Trạng thái) cùng số phòng.

### Implementation

- [x] T002 [US1] Update DAO in `src/main/java/com/quanlyphongtro/dao/RequestDAO.java` to add `getRequests(status, category, offset, limit)` and `countRequests(status, category)`. Make sure it joins with rooms table.
- [x] T003 [US1] Implement Servlet in `src/main/java/com/quanlyphongtro/controller/operator/ListRequestServlet.java` to handle pagination and filtering.
- [x] T004 [US1] Update JSP in `src/main/webapp/WEB-INF/views/operator/requests/list.jsp` to display list with `table-mintlify`, filter dropdowns in Vietnamese, and translate categories and statuses in the table cells.

---

## Phase 3: User Story 3 - Sửa lỗi hiển thị ảnh (Cross-cutting / Polish)

**Goal**: Sửa lỗi hardcode chuỗi đường dẫn ảnh `/uploads/` trên màn hình xem chi tiết của Manager theo đúng plan đề ra.

### Implementation

- [x] T005 [US3] Modify JSP in `src/main/webapp/WEB-INF/views/manager/tickets/detail.jsp` to use `<c:choose>` for safe image URL rendering.

---

## Phase 4: Polish & Cross-Cutting Concerns

- [x] T006 Chạy ứng dụng và kiểm tra chức năng theo `quickstart.md`.

---

## Phase 5: AC04 - Validate Lịch Hẹn (Priority: P1)

**Goal**: Validate thời gian hẹn để không chọn ngày hôm qua và chỉ chọn trong giờ làm việc từ 08:00 - 18:00.

### Implementation

- [x] T007 [P] Update JSP in `src/main/webapp/WEB-INF/views/operator/requests/detail.jsp` to add `min` attribute and JS validation for hours.
- [x] T008 [P] Update JSP in `src/main/webapp/WEB-INF/views/manager/tickets/detail.jsp` to add `min` attribute and JS validation for hours.
- [x] T009 [P] Update Servlet in `src/main/java/com/quanlyphongtro/controller/operator/DetailRequestServlet.java` to parse and validate `appointmentDate` backend.
- [x] T010 [P] Update Servlet in `src/main/java/com/quanlyphongtro/controller/manager/ManagerTicketsServlet.java` to parse and validate `appointmentDate` backend.

---

## Dependencies & Execution Order

- **US1 & US2**: Bắt buộc phải làm DAO (T002) trước, sau đó đến Servlet (T003), và cuối cùng là JSP (T004).
- **US3**: Độc lập hoàn toàn, có thể thực hiện song song (T005).
- **AC04 (Validate Lịch Hẹn)**: Có thể thực hiện song song trên tất cả các file (T007, T008, T009, T010).
