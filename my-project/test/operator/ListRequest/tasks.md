# Tasks: ListRequest Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/ListRequest/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorListRequestServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching the default paginated list (page=1, limit=20).
- [x] T003 Test `doGet` successfully parsing and applying filters (status, category_id, room_id).

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test fallback parsing logic when passing invalid data types for filters (e.g., string instead of int).
- [x] T005 Test Role Mismatch (e.g. TENANT accesses `/operator/requests` -> 403).

## Phase 4: Boundary Values

- [x] T006 Test handling of out-of-bounds pagination (page=9999) resulting in an empty list.
- [x] T007 Test the 'No Data Found' state rendering correctly without NullPointerException.

## Phase 5: Concurrent Scenarios

- [x] T008 Setup `ExecutorService` to verify that concurrent GET requests do not corrupt shared state.
