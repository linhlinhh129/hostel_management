# Tasks: Tenant Post Management (Unit Test Only)

**Input**: Design documents from `my-project/test/tenant/post_management/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for Post Servlets with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View My Posts successfully.
- [x] T003 Test `doGet` View Post Detail successfully.
- [x] T004 Test `doPost` Create Post successfully (PENDING).
- [x] T005 Test `doPost` Delete Post successfully (PENDING).

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doPost` Create Post with Invalid Data returns Error.
- [x] T007 Test `doPost` Delete Cross-Tenant Post returns 403.
- [x] T008 Test `doPost` Delete Approved Post returns 403.
- [x] T009 Test `doGet` Unauthorized Access returns Redirect/401.

## Phase 4: Boundary Values

- [x] T010 Test `doPost` Create Post with maximum/excessive images.

## Phase 5: Concurrent Scenarios

- [x] T011 Setup `ExecutorService` to verify Race Condition (Delete vs Approve).
