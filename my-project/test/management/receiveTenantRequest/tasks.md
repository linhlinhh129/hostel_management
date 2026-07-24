# Tasks: Receive Tenant Request (Unit Test Only)

**Input**: Design documents from `my-project/test/management/receiveTenantRequest/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerTicketsServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Tickets List successfully.
- [x] T003 Test `doGet` View Ticket Detail and Timeline successfully.
- [x] T004 Test `doPost` Receive Ticket successfully.
- [x] T005 Test `doPost` Schedule Ticket successfully.
- [x] T006 Test `doPost` Complete Ticket successfully.
- [x] T007 Test `doPost` Reject Ticket successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T008 Test `doPost` action on closed (DONE/REJECTED) ticket fails gracefully.
- [x] T009 Test `doPost` Complete ticket without notes fails gracefully.
- [x] T010 Test `doPost` Reject ticket without reason fails gracefully.
- [x] T011 Test `doPost` action on cross-facility ticket fails with 403.
- [x] T012 Test `doPost` upload invalid image format/size fails gracefully.

## Phase 4: Boundary Values

- [x] T013 Test `doPost` upload exactly 10MB image limit.

## Phase 5: Concurrent Scenarios

- [x] T014 Setup `ExecutorService` to verify Receive-vs-Reject race condition handling.
