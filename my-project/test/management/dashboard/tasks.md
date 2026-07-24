# Tasks: Manager Dashboard (Unit Test Only)

**Input**: Design documents from `my-project/test/management/dashboard/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerDashboardServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully loading dashboard stats when assigned a valid facility.

## Phase 3: Error Cases (Unwanted)

- [x] T003 Test `doGet` rejecting access for unauthorized roles (Tenant).
- [x] T004 Test `doGet` handling Manager without an assigned facility safely (no DB call, default values).
- [x] T005 Test `doGet` gracefully handling Database exceptions (mocked).

## Phase 4: Boundary Values

- [x] T006 Test `doGet` properly avoiding Division-by-Zero when `totalRooms` is 0.

## Phase 5: Concurrent Scenarios

- [x] T007 Setup `ExecutorService` to verify Servlet is Thread-Safe when multiple managers access dashboard concurrently.
