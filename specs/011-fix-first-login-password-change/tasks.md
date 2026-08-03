---
description: "Task list for fix-first-login-password-change"
---

# Tasks: fix-first-login-password-change

**Input**: Design documents from `/specs/011-fix-first-login-password-change/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(No setup tasks required for this bug fix)*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

*(No foundational tasks required for this bug fix)*

---

## Phase 3: User Story 1 - Fix First Login Password Change (Priority: P1) 🎯 MVP

**Goal**: Ensure users can successfully submit their new password on the forced password change screen without silent failures, and validation rules match exactly between frontend and backend.

**Independent Test**: Can be fully tested by logging in as a user with `is_first_login = true`, filling out the new password form, submitting it, and verifying that the system accepts the change, updates the password, clears the first login flag, and redirects the user to the main dashboard. Unmatched passwords should display a clear alert message.

### Implementation for User Story 1

- [x] T001 [US1] Update JavaScript special character validation regex to match Java backend in `src/main/webapp/WEB-INF/views/auth/first_login.jsp`
- [x] T002 [US1] Remove lowercase character requirement from JavaScript validation to match Java backend in `src/main/webapp/WEB-INF/views/auth/first_login.jsp`
- [x] T003 [US1] Add visible alert to form submit listener to notify user when passwords do not match or form is invalid in `src/main/webapp/WEB-INF/views/auth/first_login.jsp`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T004 Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **User Stories (Phase 3+)**: US1 is independent.
- **Polish (Final Phase)**: Depends on US1 completion

### Parallel Opportunities

- Only one file is modified. Tasks are best done sequentially.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 3: User Story 1
2. **STOP and VALIDATE**: Test User Story 1 independently using quickstart.md
3. Deploy/demo if ready
