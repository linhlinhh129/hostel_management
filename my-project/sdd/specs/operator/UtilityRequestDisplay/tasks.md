---
description: "Task list for operator-utility-request-display feature implementation"
---

# Tasks: operator-utility-request-display

**Input**: Design documents from `my-project/sdd/specs/operator/UtilityRequestDisplay/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Paths shown below assume single project root (`src/main/java`, `src/main/webapp`).

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify local environment is running and compile ready

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Update SQL insert query and method signature in `src/main/java/com/quanlyphongtro/dao/NotificationDAO.java` to persist `room_id` and `facility_id` in `requests` table.
- [x] T003 Update `src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java` to extract and pass `roomId` and `facilityId` into `NotificationDAO.sendOperatorRequestTransaction`.

**Checkpoint**: Foundation ready - requests are now created with proper relations.

---

## Phase 3: User Story 1 - Hiển thị đúng thể loại và thông tin phòng trên Danh sách yêu cầu (Priority: P1) 🎯 MVP

**Goal**: Hiển thị thể loại "SỰ CỐ ĐIỆN NƯỚC" và thông tin Số phòng thay vì P. — trên trang Danh sách yêu cầu.

**Independent Test**: Login as Operator, view Request List, verify UTILITY items show translated labels and room numbers.

### Implementation for User Story 1

- [x] T004 [US1] Update `src/main/webapp/WEB-INF/views/operator/requests/list.jsp` to translate `UTILITY` in the category filter dropdown and table row rendering.

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Hiển thị thông tin phòng và cơ sở trong Chi tiết yêu cầu (Priority: P1)

**Goal**: Xem được thông tin Phòng và Cơ sở trong trang Chi tiết yêu cầu của một báo cáo sai số điện nước.

**Independent Test**: View detail of a UTILITY request and verify room/facility is shown correctly.

### Implementation for User Story 2

- *(No code changes needed here. The UI already renders `reqDetail.roomCode` and `reqDetail.facilityName` properly when the database relation is present, which was fixed in Phase 2)*.

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T005 Run manual validation following `quickstart.md` scenarios to ensure end-to-end functionality.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: BLOCKS all user stories (Without the backend fix, UI won't have data).
- **User Stories (Phase 3+)**: All depend on Foundational phase completion.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2).
- **User Story 2 (P1)**: Completes automatically once Foundational (Phase 2) is done.

### Within Each User Story

- Foundational Backend (Phase 2) before UI (Phase 3).

### Parallel Opportunities

- T004 can theoretically run in parallel with T002/T003 since they touch different parts (JSP vs Java backend).

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 and 2 independently
5. Run End-to-End test.
