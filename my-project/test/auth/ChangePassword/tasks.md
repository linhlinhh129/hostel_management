# Tasks: Change Password Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/auth/ChangePassword/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ChangePasswordServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` to successfully change password given correct old password and compliant new password.

## Phase 3: Error Cases (Unwanted)

- [x] T003 Test `doPost` failure when old password does not match (ValidationException).
- [x] T004 Test `doPost` failure when new password violates the 7 complexity rules.
- [x] T005 Test unauthorized access (user session is null or expired).

## Phase 4: Boundary Values

- [x] T006 Test `doPost` handling of empty, null, or whitespace-only inputs.
- [x] T007 Test `doPost` handling of excessively long strings for passwords (e.g. >1000 characters).

## Phase 5: Concurrent Scenarios

- [x] T008 Setup `ExecutorService` to verify thread-safety during parallel password changes by the same user.
