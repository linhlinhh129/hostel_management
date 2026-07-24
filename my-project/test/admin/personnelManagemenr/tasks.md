# Tasks: Personnel Management Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/personnelManagemenr/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `AdminPersonnelServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` to successfully create a MANAGER/OPERATOR (verify ID gen, temp password gen, and email sending mock).
- [x] T003 Test `doPost` to successfully update personnel role and assign valid active facility.
- [x] T004 Test `doGet` to fetch personnel list and details (enriched with facilityNames).
- [x] T005 Test `doPost` to successfully lock/unlock a valid account.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doPost` validation errors for missing fields.
- [x] T007 Test `doPost` to prevent creating an `ADMIN` role.
- [x] T008 Test `doPost` errors related to Facility (Not Active, Not Found, Already has Manager/Operator).
- [x] T009 Test `doPost` error for `CANNOT_DEACTIVATE_SELF` (Admin locking their own account).

## Phase 4: Boundary Values

- [x] T010 Test exact character boundaries for Phone (10 digits) and CCCD (12 digits).
- [x] T011 Test unique constraints (Duplicate Email, Phone, or CCCD).
- [x] T012 Test searching with trailing whitespaces (trim logic check).

## Phase 5: Concurrent Scenarios

- [x] T013 Test concurrent race condition where two threads attempt to assign a MANAGER to the same Facility at the exact same time.
