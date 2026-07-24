# Tasks: Dashboard Operator Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/Dashboard/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorDashboardServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching and setting Dashboard Summary metrics and Today's Appointments.

## Phase 3: Error Cases (Unwanted)

- [x] T003 Test Role Mismatch (e.g. TENANT accesses `/operator/dashboard` -> 403).
- [x] T004 Test graceful exception handling when Mock DAO throws `SQLException`.

## Phase 4: Boundary Values

- [x] T005 Test Zero Data rendering (Counts = 0, Appointments List is empty).
- [x] T006 Test boundary limit ensuring MAXIMUM 5 appointments are attached to the Request.

## Phase 5: Concurrent Scenarios

- [x] T007 Setup `ExecutorService` to verify multiple threads fetching the Dashboard Summary without altering global state (Thread-safe reading).
