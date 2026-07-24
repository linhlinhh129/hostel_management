# Tasks: Profile Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/auth/Profile/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ProfileServletTest` with Mockito extensions.
- [x] T002 Implement helper methods to Mock `jakarta.servlet.http.Part` for Avatar file uploads.

## Phase 2: Happy Path 🎯 MVP

- [x] T003 Test `doGet` to successfully render the Profile View.
- [x] T004 Test `doPost` (`action=update_profile`) to successfully update text fields (Phone, FullName, DOB).
- [x] T005 Test `doPost` to successfully upload a valid Avatar image and generate the `avatar_url`.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test ID Spoofing prevention: Verify system ignores any hidden `userId` parameters and only relies on the Session.
- [x] T007 Test `doPost` with invalid phone/identity format (ValidationException).
- [x] T008 Test `doPost` handling of Duplicate Unique Constraints (Email/Phone already exists).

## Phase 4: Boundary Values

- [x] T009 Test Avatar upload exceeding 5MB max size (if possible to mock).
- [x] T010 Test `doPost` gracefully handling empty/blank optional fields (e.g., DOB).

## Phase 5: Concurrent Scenarios

- [x] T011 Setup `ExecutorService` to test concurrent Profile update requests resolving Unique Constraints gracefully.
