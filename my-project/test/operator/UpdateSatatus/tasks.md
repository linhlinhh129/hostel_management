# Tasks: Operator Update Status (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/UpdateSatatus/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `OperatorUpdateStatusServletTest` with Mockito extensions.
- [x] T002 Mock `Part` objects to simulate multipart file uploads.

## Phase 2: Happy Path 🎯 MVP

- [x] T003 Test `doPost` successfully marking a request as COMPLETED with 1 valid image and note.
- [x] T004 Test `doPost` successfully handling multiple uploaded images (concat filenames into `attachment_urls2`).

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test submission rejection when no image files are attached.
- [x] T006 Test submission rejection when notes (rejection_reason workaround) are empty.
- [x] T007 Test submission rejection when target incident is NOT in `IN_PROGRESS` state.
- [x] T008 Test submission rejection when uploaded files are not valid images (e.g., .pdf).

## Phase 4: Boundary Values

- [x] T009 Test submission with exact maximum allowed characters for notes (e.g., 1000).
- [x] T010 Test handling of File Size Exceeded exception (simulated Tomcat `IllegalStateException`).

## Phase 5: Concurrent Scenarios

- [x] T011 Setup `ExecutorService` to simulate 2 operators reporting completion simultaneously, verifying Race Condition protection (Optimistic Lock).
