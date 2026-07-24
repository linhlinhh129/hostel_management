# Tasks: DetailRequest Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/DetailRequest/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorDetailRequestServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching and displaying Incident Request Details.
- [x] T003 Test `doPost` (action=accept) successfully accepting a PENDING request.
- [x] T004 Test `doPost` (action=reject) successfully rejecting a request with a valid reason.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test rejection failure when no reason is provided.
- [x] T006 Test acceptance failure when the request is NOT in PENDING state.
- [x] T007 Test 404 Not Found handling when requesting a non-existent Incident ID.

## Phase 4: Boundary Values

- [x] T008 Test rejection reason exactly at the maximum character limit (e.g. 1000 chars).
- [x] T009 Test UI state flags ensuring Accept/Reject buttons are hidden for IN_PROGRESS/COMPLETED statuses.

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to test Race Condition where 2 operators try to accept the same request concurrently (mocking Optimistic Lock).
