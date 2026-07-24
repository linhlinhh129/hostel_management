# Tasks: Tenant Invoice Management & Payment (Unit Test Only)

**Input**: Design documents from `my-project/test/tenant/invoice_management/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `TenantInvoiceServletTest`, `TenantPaymentServletTest`, `TenantPaymentReturnServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Invoice List successfully (sorted by period desc).
- [x] T003 Test `doGet` View Invoice Detail successfully.
- [x] T004 Test `doPost` Create VNPAY Payment URL successfully (UNPAID -> PROCESSING).
- [x] T005 Test `doGet` VNPAY Return Success (updates to PAID, saves payment).

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doGet` View Invoice Cross-Tenant Access returns 403.
- [x] T007 Test `doPost` Create Payment for already PAID/PROCESSING invoice returns 409 Conflict.
- [x] T008 Test `doGet` VNPAY Return with Invalid Signature (SecureHash).
- [x] T009 Test `doGet` VNPAY Return with Amount Mismatch.
- [x] T010 Test `doGet` VNPAY Return with Failed Transaction Code (!= 00).

## Phase 4: Boundary Values

- [x] T011 Test `doGet` VNPAY Return Idempotency (Ignore duplicate IPN requests).

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to verify VNPAY IPN Race Condition handling.
