# Tasks: Payment Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/paymentManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `PaymentServletTest` and `PaymentDetailServletTest`.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `PaymentServlet.doGet` successfully lists payments.
- [x] T003 Test `PaymentDetailServlet.doGet` successfully views payment details.
- [x] T004 Test `PaymentDetailServlet.doPost` successfully approves PENDING payment.
- [x] T005 Test `PaymentDetailServlet.doPost` successfully rejects PENDING payment.
- [x] T006 Test `PaymentDetailServlet.doPost` successfully re-approves REJECTED payment.

## Phase 3: Error Cases (Unwanted)

- [x] T007 Test `doPost` rejects approval of non-existent payment (404).
- [x] T008 Test `doPost` rejects approval of already SUCCESS payment (400).
- [x] T009 Test `doPost` rejects rejection of already SUCCESS payment (400).
- [x] T010 Test unauthorized access (TENANT/Unauthenticated) receives 403/401.

## Phase 4: Boundary Values

- [x] T011 Test approval works when payment amount exactly matches invoice total.

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to verify Double-Approve race condition handling.
- [x] T013 Setup `ExecutorService` to verify Approve-vs-Reject race condition handling.
