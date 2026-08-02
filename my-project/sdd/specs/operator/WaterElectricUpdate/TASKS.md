# Tasks: WaterElectricUpdate

**Input**: Design documents from `/specs/WaterElectricUpdate/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify project structure and branch checkouts

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Update Database Schema (ALTER TABLE `meter_readings`) to include metadata columns (`electric_status`, `electric_old_final`, v.v.)
- [x] T003 [P] Update `src/main/java/com/quanlyphongtro/dto/MeterStatusDTO.java` to add new DTO properties.

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 & 2 - Happy Path & Hiển thị dữ liệu cần sửa (Priority: P1) 🎯 MVP

**Goal**: Hiển thị lại số liệu cũ và cho phép cập nhật số điện nước lớn hơn kỳ trước.

**Independent Test**: Mở giao diện `update.jsp`, thấy dữ liệu được điền sẵn, nhập số lớn hơn và lưu thành công.

### Implementation for User Story 1 & 2

- [x] T004 [P] [US1] Update `src/main/java/com/quanlyphongtro/dao/MeterReadingDAO.java` to implement `getReadingForEdit` querying current and previous records.
- [x] T005 [P] [US1] Update `src/main/java/com/quanlyphongtro/service/MeterReadingService.java` to wrap DAO method.
- [x] T006 [US1] Update `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java` (GET) to pass DTO.
- [x] T007 [US2] Update `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp` to bind values and disable submission if `invoicePaid` is true.

**Checkpoint**: At this point, User Story 1 & 2 should be fully functional and testable independently

---

## Phase 4: User Story 3, 4, 5 - Xử lý Thay công tơ và Rollover (Priority: P2)

**Goal**: Hỗ trợ giao diện nhập lý do và tính toán logic tiêu thụ cho nghiệp vụ công tơ bất thường.

**Independent Test**: Nhập số điện mới < số cũ, form hiển thị dropdown, chọn Thay công tơ, lưu thành công với mức tiêu thụ chính xác.

### Implementation for User Story 3, 4, 5

- [x] T008 [P] [US3] Add JS logic to `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp` to detect `new < old` and show reasons dropdown.
- [x] T009 [US3] Update `UpdateMeterReadingServlet.java` (POST) to parse new form fields (`electricStatus`, `electricOldFinal`, etc.).
- [x] T010 [US4] Implement usage calculation logic in `src/main/java/com/quanlyphongtro/service/MeterReadingService.java` based on statuses.
- [x] T011 [US4] Update `MeterReadingDAO.java` update query to persist new metadata columns and computed usage.

**Checkpoint**: At this point, User Stories 3, 4 AND 5 should both work independently

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T012 Run manual validation following `quickstart.md` scenarios
- [x] T013 Update Links in `list.jsp` and `history.jsp` to point to the correct Update endpoint
- [x] T014 Remove JS auto-detect logic in `update.jsp` and show status block by default.
- [x] T015 Hardcode max limits to 10000 in frontend (`update.jsp`) and backend (`MeterReadingService.java`).

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1, 2 (P1)**: Can start after Foundational (Phase 2).
- **User Story 3, 4, 5 (P2)**: Depends on UI forms from US 1, 2.

## Implementation Strategy

### MVP First (User Story 1 & 2 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational 
3. Complete Phase 3: User Story 1 & 2
4. **STOP and VALIDATE**: Test Happy Path independently

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 & 2 → Deploy/Demo (MVP!)
3. Add User Story 3, 4, 5 → Deploy/Demo
