# Tasks: Room & Facility Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/roomFacilityManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerFacilitiesServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Assigned Facilities successfully.
- [x] T003 Test `doGet` View Facilities when empty (unassigned).
- [x] T004 Test `doGet` View Rooms in Facility successfully.
- [x] T005 Test `doGet` View Room Detail successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doGet` View Rooms Cross-Facility returns 403.
- [x] T007 Test `doGet` View Room Detail Cross-Facility returns 403.
- [x] T008 Test `doGet` Facility Not Found returns 404.
- [x] T009 Test `doGet` Room Not Found returns 404.
- [x] T010 Test `doGet` Unauthorized Access (TENANT) returns 403.

## Phase 4: Boundary Values

- [x] T011 Test `doGet` Pagination Bounds for Rooms List.

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to verify Rate Limiting (100 req/min).
