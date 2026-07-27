# Tasks: Invoice Note Validation

**Input**: Design documents from `/specs/008-invoice-note-validation/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- No setup tasks required for this minor enhancement feature.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- No foundational tasks required. Project structure is already established.

---

## Phase 3: User Story 1 - Validate Note Length (Priority: P1) 🎯 MVP

**Goal**: Đảm bảo trường ghi chú (note) trên form tạo hóa đơn không vượt quá 1000 ký tự và hiển thị hướng dẫn rõ ràng.

**Independent Test**:
- Open the Create Invoice page, observe the hint, try to input >1000 characters, and verify both frontend limit and backend rejection on bypass.

### Implementation for User Story 1

- [x] T001 [P] [US1] Update `src/main/webapp/WEB-INF/views/manager/invoices/create.jsp` to add `maxlength="1000"` to the note textarea and display a helper text.
- [x] T002 [P] [US1] Update `src/main/java/com/quanlyphongtro/service/impl/InvoiceServiceImpl.java` to throw `IllegalArgumentException` if `note` length > 1000 in `createInvoice` method.

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T003 [P] Run quickstart.md validation locally to verify feature works end-to-end.

---

## Dependencies & Execution Order

### Phase Dependencies

- **User Stories (Phase 3)**: Can start immediately.
- **Polish (Final Phase)**: Depends on all user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Can start immediately.

### Parallel Opportunities

- T001 and T002 modify different files and can be executed in parallel.
