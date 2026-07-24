# Tasks: Báo cáo sự cố Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/Baocaosuco/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorIncidentReportServletTest` with Mockito extensions.
- [x] T002 Implement Helper method for mocking `jakarta.servlet.http.Part` to test image uploads.

## Phase 2: Happy Path 🎯 MVP

- [x] T003 Test `doPost` generating a report for a specific Room.
- [x] T004 Test `doPost` generating a report for a Public Area (verifying `room_id` is null).

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test missing required fields handling (Validation Error).
- [x] T006 Test unauthorized access (User is not OPERATOR).
- [x] T007 Test invalid file type upload rejection.

## Phase 4: Boundary Values

- [x] T008 Test form submission with ZERO images attached.
- [x] T009 Test extremely long text in `description` (Validation Boundary).

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to test Double-Submit prevention (Idempotency) when user spams the submit button.
