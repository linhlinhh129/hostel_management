# Tasks: updateServicePrice

**Input**: Design documents from `/specs/manager/updateServicePrice/`

**Prerequisites**: plan.md, spec.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(Project structure already exists. No setup tasks required.)*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T001 [P] Create `ServicePriceHistory` model in `src/main/java/com/quanlyphongtro/model/ServicePriceHistory.java` (without note field)
- [x] T002 [P] Create `ServicePriceHistoryDAO` in `src/main/java/com/quanlyphongtro/dao/ServicePriceHistoryDAO.java`
- [x] T003 Create `ServicePriceService` and `ServicePriceServiceImpl` in `src/main/java/com/quanlyphongtro/service/impl/ServicePriceServiceImpl.java`
- [x] T004 Create `ServicePricePageServlet` in `src/main/java/com/quanlyphongtro/controller/manager/ServicePricePageServlet.java`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 & 8 - View List & Access Control (Priority: P1) 🎯 MVP

**Goal**: Managers can view current prices for their facility securely.

**Independent Test**: Login as manager, navigate to `/manager/service-prices` and see prices. Login as tenant and get 403 Forbidden.

### Implementation for User Story 1 & 8

- [x] T005 [P] [US8] Implement `MANAGER` role check and authentication in `ServicePricePageServlet.java`
- [x] T006 [US1] Implement `getFacilityPrices` method in `ServicePriceService` to fetch current prices
- [x] T007 [US1] Update `ServicePricePageServlet` GET method to fetch prices and forward to `index.jsp`
- [x] T008 [US1] Implement UI in `src/main/webapp/WEB-INF/views/manager/service-prices/index.jsp` to display list of prices

**Checkpoint**: At this point, viewing current prices securely is functional.

---

## Phase 4: User Story 2, 3, 4, 5, 6 - Update Price & Validation (Priority: P1/P2)

**Goal**: Managers can update prices (Electricity, Water, Service Fee) via pop-up with robust validation.

**Independent Test**: Use pop-up to update Electricity price. Verify validation rejects commas/dots and same prices. Verify successful updates save to DB.

### Implementation for Update Price

- [x] T009 [P] [US2] Update `index.jsp` to include price update pop-up form (without note field, enforce HTML5 number input)
- [x] T010 [US3-5] Implement `updatePrice` method in `ServicePriceService` handling Transaction (update facility + insert history)
- [x] T011 [US6] Add backend validation in `ServicePricePageServlet` POST (must be positive integer, no commas, different from old price)
- [x] T012 [US3-5] Implement POST handler in `ServicePricePageServlet` for `action=update` to process update and handle errors

**Checkpoint**: Updating prices is now fully functional and validated.

---

## Phase 5: User Story 7 - View History (Priority: P2)

**Goal**: Managers can view historical price changes.

**Independent Test**: Click "Lịch sử" on a price row and view chronological changes without notes.

### Implementation for User Story 7

- [x] T013 [P] [US7] Implement `getHistoryByFacilityAndType` in `ServicePriceHistoryDAO`
- [x] T014 [US7] Implement `getPriceHistory` method in `ServicePriceService`
- [x] T015 [US7] Update `ServicePricePageServlet` GET method to handle `action=history`
- [x] T016 [US7] Implement UI in `src/main/webapp/WEB-INF/views/manager/service-prices/history.jsp` to display history table

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T017 [P] Run quickstart.md validation manually
- [x] T018 Code cleanup and refactoring (if needed)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Foundational (Phase 2)**: BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - Phase 4 depends on Phase 3 (Index page must exist to click update)
  - Phase 5 depends on Phase 3 and Phase 4 (History needs updates to occur first)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### Parallel Opportunities

- Model and DAO creation (T001, T002) can run in parallel
- UI implementation (T009) can run in parallel with Service implementation (T010)
- History DAO (T013) can run in parallel with Update Price implementation

---

## Implementation Strategy

### Incremental Delivery

1. Complete Foundational
2. Add View List (US1) → Test independently → MVP
3. Add Update Price (US2-6) → Test independently
4. Add View History (US7) → Test independently
