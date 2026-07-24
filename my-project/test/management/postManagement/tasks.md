# Tasks: Post Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/postManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `CommunityPostServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Post List successfully.
- [x] T003 Test `doGet` View Post Detail successfully.
- [x] T004 Test `doPost` Create Post successfully (PENDING).
- [x] T005 Test `doPost` Approve Post successfully (AJAX).
- [x] T006 Test `doPost` Delete Post successfully (Soft Delete).

## Phase 3: Error Cases (Unwanted)

- [x] T007 Test `doPost` create post fails gracefully on empty title/content.
- [x] T008 Test `doGet` view detail redirects on Post NotFound.
- [x] T009 Test unauthorized access (TENANT/Unauthenticated) receives 403/401.

## Phase 4: Boundary Values

- [x] T010 Test `doPost` title creation with maximum 250 characters.
- [x] T011 Test `doPost` image upload with maximum 5MB size limit.

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to verify Double-Approve race condition handling.
- [x] T013 Setup `ExecutorService` to verify Rate Limiting (100 req/min).
