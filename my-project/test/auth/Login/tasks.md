# Tasks: Login Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/auth/Login/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `LoginServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test normal login (`force_change_pass = 0`) routing to Dashboard.
- [x] T003 Test first-time login (`force_change_pass = 1`) forcing redirect to change password screen.

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test invalid credentials triggering error message.
- [x] T005 Test login attempt on a `LOCKED` account is rejected.
- [x] T006 Test 5 consecutive failed logins triggering account lock (`UPDATE` query via MockDAO).

## Phase 4: Boundary Values

- [x] T007 Test the boundary condition of exactly 4 vs 5 failed attempts.
- [x] T008 Test empty inputs handling.

## Phase 5: Concurrent Scenarios

- [x] T009 Setup `ExecutorService` to simulate 50 concurrent failed login attempts to verify thread-safe brute-force prevention locking.
