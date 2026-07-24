# Tasks: Operator Update Meter Reading (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/WaterElectricUpdate/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `UpdateMeterReadingServletTest` with Mockito extensions.
- [x] T002 Mock `Part` objects to simulate multipart file uploads for electric and water images.

## Phase 2: Happy Path 🎯 MVP

- [x] T003 Test `doPost` successfully handling first-time insert for the current month.
- [x] T004 Test `doPost` successfully updating existing reading for the current month.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test submission rejection when `roomCode` is missing or empty.
- [x] T006 Test submission rejection when `roomCode` does not exist in DB (Room Not Found).
- [x] T007 Test submission rejection when new electric reading < previous reading.
- [x] T008 Test submission rejection when new water reading < previous reading.
- [x] T009 Test submission rejection when electric meter image is missing.
- [x] T010 Test submission rejection when water meter image is missing.
- [x] T011 Test handling of `NumberFormatException` when parsing non-numeric inputs.

## Phase 4: Boundary Values

- [x] T012 Test successful submission when new readings are exactly equal to previous readings.
- [x] T013 Test successful submission for the very first reading in the system (prev readings assumed 0).
- [x] T014 Test handling of File Size Exceeded exception (simulated Tomcat `IllegalStateException`).

## Phase 5: Concurrent Scenarios

- [x] T015 Setup `ExecutorService` to simulate 2 operators reporting readings simultaneously, verifying Thread Safety.
