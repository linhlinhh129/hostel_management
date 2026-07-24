# Tasks: Update Service Price (Unit Test Only)

**Input**: Design documents from `my-project/test/management/updateServicePrice/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ServicePricePageServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Service Prices successfully.
- [x] T003 Test `doGet` View Service Prices when empty (no facility).
- [x] T004 Test `doGet` View History successfully (`action=history`).
- [x] T005 Test `doPost` Update Price successfully (`action=update`).

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doGet` Cross-Facility Access returns 403.
- [x] T007 Test `doPost` Update Price with Invalid Number (Forwards Error).
- [x] T008 Test `doPost` Update Price with Zero or Negative (Forwards Error).
- [x] T009 Test `doPost` Update Price Missing Field (Forwards Error).
- [x] T010 Test `doPost` Update Price Invalid Type (Forwards Error).
- [x] T011 Test `doPost` Invalid Action returns 400 Bad Request.
- [x] T012 Test `doGet` Unauthorized Access returns 403.

## Phase 4: Boundary Values

- [x] T013 Test `doPost` Update Price with Max Int value.

## Phase 5: Concurrent Scenarios

- [x] T014 Setup `ExecutorService` to verify Update Price Race Condition.
