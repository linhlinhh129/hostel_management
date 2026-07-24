# Tasks: Notification Facility Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/notificationFacilityManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerNotificationsServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` send general notification successfully.
- [x] T003 Test `doPost` send debt reminder successfully.
- [x] T004 Test `doPost` report utility issue successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test `doPost` rejects sending global notification (ALL) with 403 Forbidden.
- [x] T006 Test `doPost` rejects cross-facility notification with 403 Forbidden.
- [x] T007 Test `doPost` fails gracefully on missing title/content (Validation).
- [x] T008 Test `doPost` utility issue report correctly rolls back on DB failure.

## Phase 4: Boundary Values

- [x] T009 Test notification creation with maximum allowed length.
- [x] T010 Test debt reminder allowed when exactly 1 day overdue.

## Phase 5: Concurrent Scenarios

- [x] T011 Setup `ExecutorService` to verify Double-Strike utility issue reporting.
