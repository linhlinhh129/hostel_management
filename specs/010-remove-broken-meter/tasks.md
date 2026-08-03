# Tasks: Remove Broken Meter

**Input**: Design documents from `/specs/010-remove-broken-meter/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- No setup tasks needed for this feature removal.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- No foundational blocking tasks for this feature.

---

## Phase 3: User Story 1 - Remove broken meter fields and logic (Priority: P1) 🎯 MVP

**Goal**: Remove obsolete fields and UI elements related to broken meters from the database schema, DAOs, services, controllers, and views.

**Independent Test**: Can be fully tested by verifying that the broken meter checkboxes/fields are absent from the UI and their data is no longer saved.

### Implementation for User Story 1

- [x] T001 [P] [US1] Remove `electric_old_final`, `electric_new_start`, `water_old_final`, `water_new_start` columns from `database (1)/schema.sql` and `database (1)/seed.sql`.
- [x] T002 [P] [US1] Remove fields `electricOldFinal`, `electricNewStart`, `waterOldFinal`, `waterNewStart` and their getters/setters from `src/main/java/com/quanlyphongtro/dto/MeterStatusDTO.java`.
- [x] T003 [US1] Remove SQL queries mapping the dropped columns in `src/main/java/com/quanlyphongtro/dao/MeterReadingDAO.java`.
- [x] T004 [US1] Remove parsing and assignment logic for the dropped fields in `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`.
- [x] T005 [P] [US1] Remove old_final/new_start data attributes and rendering logic from `src/main/webapp/WEB-INF/views/operator/meter_readings/history.jsp`.

**Checkpoint**: At this point, User Story 1 should be fully functional. The broken meter feature is completely stripped out.

---

## Phase 4: User Story 2 - Meter reset logic at 10,000 units (Priority: P2)

**Goal**: Keep the reset (rollover) case for meters. When the meter reaches 10,000, provide a checkbox to calculate the rollover accurately.

**Independent Test**: Can be fully tested by entering an old index close to 10,000, a new index close to 0, checking the reset checkbox, and verifying the correct usage calculation.

### Implementation for User Story 2

- [x] T006 [P] [US2] Modify `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp` to replace `electricStatus`/`waterStatus` dropdowns with checkboxes for "Công tơ chạy hết vòng (reset về 0)" and remove JS toggle logic.
- [x] T007 [US2] Update `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java` to read the checkbox values and map them to `NORMAL` or `ROLLOVER`.
- [x] T008 [US2] Update calculation logic for `ROLLOVER` to `10000 - prev + current` in `src/main/java/com/quanlyphongtro/service/impl/MeterReadingServiceImpl.java`.

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently. The system properly resets the meter after 10,000 when checked.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T009 [P] Run quickstart.md validation to ensure the form and history pages function correctly after removals.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: No dependencies.
- **User Stories (Phase 3+)**: US1 is P1 (priority 1). US2 is P2 (priority 2). US2 touches some files that US1 touches, so it's recommended to do US1 then US2, but they can be parallelized carefully.
- **Polish (Final Phase)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Independent.
- **User Story 2 (P2)**: Independent, but builds upon the UI changes in `update.jsp` and `UpdateMeterReadingServlet.java`.

### Parallel Opportunities

- T001, T002, T005 can be done in parallel.
- T006 can be done in parallel with T001-T005.

---

## Parallel Example: User Story 1

```bash
# Launch independent UI and Database tasks together:
Task: "Remove old_final/new_start data attributes and rendering logic from history.jsp"
Task: "Remove columns from schema.sql and seed.sql"
Task: "Remove fields from MeterStatusDTO.java"
```

---

## Implementation Strategy

### Incremental Delivery

1. Complete User Story 1 → Remove broken meter → Test independently → Deliver.
2. Complete User Story 2 → Refactor reset logic → Test independently → Deliver.
