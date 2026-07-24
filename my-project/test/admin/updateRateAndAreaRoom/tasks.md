# Tasks: Update Room Rate & Area Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/updateRateAndAreaRoom/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `AdminRoomServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` `/admin/rooms/{roomId}` to fetch and display room detail successfully.
- [x] T003 Test `doPost` to update room with positive valid `area` and `roomFee`.
- [x] T004 Test `doPost` to update room with empty `area` and `roomFee` (null assignment).

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test `doPost` failure when updating a room whose facility is `INACTIVE` (ValidationException).
- [x] T006 Test `doPost` validation errors for negative numbers or invalid characters.
- [x] T007 Test `doGet` and `doPost` when the room ID does not exist (NotFoundException 404).
- [x] T008 Test security checks: Unauthorized (401) and Forbidden (403 for non-admins).

## Phase 4: Boundary Values

- [x] T009 Test `doPost` with exactly `0` as the value for area and roomFee.
- [x] T010 Test `doPost` with extremely large values (e.g. 9999999999) to verify `BigDecimal` parsing safety.

## Phase 5: Concurrent Scenarios

- [x] T011 Setup `ExecutorService` with multiple threads to verify the `AdminRoomServlet`'s Thread-Safety during POST requests.
