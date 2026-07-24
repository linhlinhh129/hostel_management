# Tasks: Dependent Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/dependentManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerTenantsServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` (add) successfully creates a new dependent for an ACTIVE tenant.
- [x] T003 Test `doGet` (detail) successfully masks the Identity Number (CCCD).
- [x] T004 Test `doPost` (remove) successfully performs soft deletion.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test `doPost` (add) rejects adding to an INACTIVE tenant.
- [x] T006 Test `doPost` (add) rejects request missing `fullName` or `relationship`.
- [x] T007 Test `doPost` (edit) throws HTTP 403 on cross-facility (IDOR) access.

## Phase 4: Boundary Values

- [x] T008 Test `doPost` validates CCCD length strictly (9 or 12).
- [x] T009 Test `doPost` validates Phone length strictly (10 digits).

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to verify Double-Strike Soft Delete race condition is handled gracefully.
