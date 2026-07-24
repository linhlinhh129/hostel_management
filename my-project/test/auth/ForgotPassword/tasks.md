# Tasks: Forgot Password Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/auth/ForgotPassword/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ForgotPasswordServletTest` and `ResetPasswordServletTest`.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` in ForgotPassword to trigger mock email dispatch.
- [x] T003 Test `doGet` in ResetPassword to render form given a valid token.
- [x] T004 Test `doPost` in ResetPassword to successfully reset password AND invoke `SessionRegistry` invalidation.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test Anti-User Enumeration: Invalid email silently mimics success.
- [x] T006 Test `doPost` with expired or completely invalid token.
- [x] T007 Test `doPost` when `newPassword` and `confirmPassword` do not match.

## Phase 4: Boundary Values

- [x] T008 Test token validation precisely at the 15-minute expiry boundary.
- [x] T009 Test empty parameters (empty email, empty token).

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to test concurrent invalidation requests inside the mock `SessionRegistry` wrapper.
