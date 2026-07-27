# Tasks: Logout

**Input**: Design documents from `my-project/sdd/specs/auth/Logout/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Source: `src/main/java/`
- Webapp: `src/main/webapp/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify project structure for Servlets and Filters exists in `src/main/java/com/quanlyphongtro/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

*(No foundational tasks required for this feature, assuming base authentication filter already exists)*

---

## Phase 3: User Story 1 - Standard Logout (Happy Path) 🎯 MVP

**Goal**: Allow users to click a logout button to invalidate their session and redirect to the login page.

**Independent Test**: Click "Đăng xuất" and verify redirection to `/login` with an invalidated session.

### Implementation for User Story 1

- [x] T002 [P] [US1] Ensure `LogoutServlet` exists and maps to `/logout` in `src/main/java/com/quanlyphongtro/controller/auth/LogoutServlet.java`
- [x] T003 [P] [US1] Implement `doGet` in `LogoutServlet.java` to call `session.invalidate()` and `resp.sendRedirect("/login")`
- [x] T004 [P] [US1] Update `topbar.jsp` in `src/main/webapp/WEB-INF/views/layout/topbar.jsp` to link the logout button to `/logout`

**Checkpoint**: At this point, clicking logout successfully ends the session and redirects to login.

---

## Phase 4: User Story 2 - Anti-Caching (Back Button Prevention)

**Goal**: Prevent users from using the browser's Back button to view cached authenticated pages after logging out.

**Independent Test**: Log out, click the Back button, and verify the browser does not display the cached page but redirects to `/login`.

### Implementation for User Story 2

- [x] T005 [P] [US2] Open `AuthFilter.java` in `src/main/java/com/quanlyphongtro/filter/AuthFilter.java`
- [x] T006 [P] [US2] Add `Cache-Control: no-cache, no-store, must-revalidate` header for all protected routes before `chain.doFilter`
- [x] T007 [P] [US2] Add `Pragma: no-cache` header for all protected routes
- [x] T008 [P] [US2] Add `Expires: 0` header for all protected routes

**Checkpoint**: At this point, all protected routes enforce anti-caching, solving the back button vulnerability.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T009 [P] Run `quickstart.md` validation to ensure end-to-end functionality works as expected.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **User Stories (Phase 3+)**: US1 and US2 can theoretically be implemented in parallel since they touch different files (Servlet vs Filter), but US1 is the MVP.
- **Polish (Final Phase)**: Depends on all user stories being complete.

### Parallel Opportunities

- T002/T003 and T005/T006/T007/T008 can be executed in parallel by different agents or developers.

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Implement `LogoutServlet` (US1)
2. Verify logout functionality works.
3. Deploy/Demo if ready.

### Incremental Delivery
1. Deliver US1 (Standard Logout).
2. Deliver US2 (Anti-Caching) to secure the application against local history leaks.
