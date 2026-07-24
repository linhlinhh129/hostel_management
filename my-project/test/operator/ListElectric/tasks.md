# Tasks: ListElectric Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/ListElectric/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorListElectricServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching and mapping `DA_CAP_NHAT` and `CHUA_CAP_NHAT` statuses correctly.
- [x] T003 Test `doGet` gracefully handling an empty list of rooms.

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test handling of `SQLException` from DAO (Graceful degradation).
- [x] T005 Test unauthorized access (User is not OPERATOR).

## Phase 4: Boundary Values

- [x] T006 Test exact `0` values for previous readings are correctly processed and not treated as nulls.
- [x] T007 Test processing performance/logic for a highly populated list (e.g. 10,000 rooms simulated).

## Phase 5: Concurrent Scenarios

- [x] T008 Setup `ExecutorService` to verify that concurrent GET requests do not cause state corruption or `ConcurrentModificationException`.
