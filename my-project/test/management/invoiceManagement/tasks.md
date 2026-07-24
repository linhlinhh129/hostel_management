# Tasks: Invoice Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/invoiceManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [ ] T001 Initialize Unit Test base structure for `InvoiceServletTest` and `InvoiceDetailServletTest`.

## Phase 2: Happy Path 🎯 MVP

- [ ] T002 Test `InvoiceServlet.doPost` successfully creates a new invoice.
- [ ] T003 Test `InvoiceServlet.doGet` successfully lists invoices.
- [ ] T004 Test `InvoiceDetailServlet.doGet` successfully displays invoice details.
- [ ] T005 Test `InvoiceDetailServlet.doPost` successfully updates an invoice.

## Phase 3: Error Cases (Unwanted)

- [ ] T006 Test `InvoiceServlet.doPost` fails gracefully on ROOM_NOT_FOUND (404).
- [ ] T007 Test `InvoiceServlet.doPost` fails gracefully on INVOICE_ALREADY_EXISTS (400).
- [ ] T008 Test `InvoiceDetailServlet.doPost` fails gracefully when updating a PAID invoice (400).
- [ ] T009 Test unauthorized access (role = TENANT) receives 403.

## Phase 4: Boundary Values

- [ ] T010 Test invoice creation with DueDate = Today.
- [ ] T011 Test invoice creation with Utility Usage = 0.
- [ ] T012 Test invoice creation with Tax = 0 and Other Fee = 0.

## Phase 5: Concurrent Scenarios

- [ ] T013 Setup `ExecutorService` to test duplicate invoice creation race condition.
- [ ] T014 Setup `ExecutorService` to test update vs payment race condition.
