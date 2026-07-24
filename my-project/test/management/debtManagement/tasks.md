# Tasks: Debt Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/debtManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `DebtPageServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` loads unpaid/overdue debt list successfully.
- [x] T003 Test `doGet` (action=detail) loads debt detail successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test `doGet` rejects request with `INVALID_DEBT_STATUS` (HTTP 400).
- [x] T005 Test `doGet` detail view throws HTTP 404 on cross-facility (IDOR) access.
- [x] T006 Test `doGet` throws HTTP 403 for non-manager roles.

## Phase 4: Boundary Values

- [x] T007 Test `doGet` calculations: Debt remaining locked to 0 when overpaid.
- [x] T008 Test `doGet` calculations: 3 days overdue -> late fee = 0.
- [x] T009 Test `doGet` calculations: 4 days overdue -> late fee = 1%.

## Phase 5: Concurrent Scenarios

- [x] T010 Setup `ExecutorService` to verify Servlet is Thread-Safe for concurrent list searching.
