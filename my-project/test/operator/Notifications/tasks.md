# Tasks: Operator Notifications Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/Notifications/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorNotificationServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching system-wide notifications (target_type = ALL).
- [x] T003 Test `doGet` successfully fetching facility-specific notifications matching Operator's facility ID.

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test handling of unauthorized access (e.g. TENANT role).
- [x] T005 Test handling of POST request -> HTTP 405 Method Not Allowed.
- [x] T006 Test handling of corrupted session missing `facility_id` (Graceful error rendering).

## Phase 4: Boundary Values

- [x] T007 Test empty list behavior when no notifications exist.
- [x] T008 Test out-of-bounds pagination (e.g., `page=99999`).
- [x] T009 Test memory/DTO mapping stability with extremely long notification content (10,000 chars).

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to verify that concurrent GET requests maintain Thread Safety.
