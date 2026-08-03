---
description: "Task list for remove-js-validation-first-login"
---

# Tasks: remove-js-validation-first-login

**Input**: Design documents from `/specs/012-remove-js-validation-first-login/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(No setup tasks required)*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

*(No foundational tasks required)*

---

## Phase 3: User Story 1 - Remove Client-Side Password Validation (Priority: P1) 🎯 MVP

**Goal**: Remove all dynamic JavaScript password validation logic and UI from `first_login.jsp`, relying entirely on native HTML5 `pattern` attributes and backend server-side validation.

**Independent Test**: Can be tested by verifying that entering an invalid password does not trigger JS alerts or color changes, and that bypassing HTML5 validation causes the server to return an error rendered by JSP.

### Implementation for User Story 1

- [x] T001 [US1] Remove dynamic JavaScript logic (functions `updateChecklist`, `allPassed`, the submit event listener, and input event listeners) from `src/main/webapp/WEB-INF/views/auth/first_login.jsp`
- [x] T002 [US1] Remove dynamic HTML elements (`<ul id="pwChecklist">` and `<div class="pw-strength-track">`) from `src/main/webapp/WEB-INF/views/auth/first_login.jsp`
- [x] T003 [US1] Add a static helper text describing password complexity requirements below the "Mật khẩu mới" input field in `src/main/webapp/WEB-INF/views/auth/first_login.jsp`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T004 Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **User Stories (Phase 3+)**: US1 is independent.
- **Polish (Final Phase)**: Depends on US1 completion.

### Parallel Opportunities

- Only one file is modified. Tasks must be executed sequentially.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 3: User Story 1
2. **STOP and VALIDATE**: Test User Story 1 independently using quickstart.md
3. Deploy/demo if ready
