# Tasks: Invoice Management & VNPAY Payment

This document outlines the actionable tasks required to implement the Invoice Management and VNPAY Payment feature for Tenants.

## Dependencies & Strategy
- Phase 1 & 2 must be completed first to establish the VNPAY integration foundation.
- Phase 3, 4, 5 correspond to user stories and can be somewhat developed in parallel once foundational DAOs are ready.
- **MVP Scope**: Phase 1, Phase 2, Phase 3 (Viewing Invoices), and Phase 4 (Payment processing). Payment History (Phase 5) is secondary.

## Phase 1: Setup
- [ ] T001 Setup VNPAY configuration variables in `.env` (vnp_TmnCode, vnp_HashSecret, vnp_Url, vnp_ReturnUrl)
- [ ] T002 Update `payments` table schema (migration script) to include VNPAY fields (vnp_transaction_no, etc.)

## Phase 2: Foundational
- [ ] T003 Implement `VnpayConfig` class to load and provide VNPAY settings in `src/main/java/com/quanlyphongtro/config/VnpayConfig.java`
- [ ] T004 Implement `VnpayService` for Hash generation and URL building in `src/main/java/com/quanlyphongtro/service/VnpayService.java`

## Phase 3: [US1] View Invoices and Detail
- [ ] T005 [P] [US1] Implement `InvoiceDao` queries for tenant invoice list and detail in `src/main/java/com/quanlyphongtro/dao/InvoiceDao.java`
- [ ] T006 [US1] Implement `InvoiceService` logic for tenant invoices in `src/main/java/com/quanlyphongtro/service/InvoiceService.java`
- [ ] T007 [US1] Implement `TenantInvoiceServlet` for list and detail GET routes in `src/main/java/com/quanlyphongtro/controller/tenant/TenantInvoiceServlet.java`
- [ ] T008 [P] [US1] Create `list.jsp` view for invoices in `src/main/webapp/WEB-INF/views/tenant/invoices/list.jsp`
- [ ] T009 [P] [US1] Create `detail.jsp` view for invoice detail in `src/main/webapp/WEB-INF/views/tenant/invoices/detail.jsp`

## Phase 4: [US2] VNPAY Payment & Callback
- [ ] T010 [P] [US2] Add `POST /pay` route in `TenantInvoiceServlet` to generate VNPAY URL and redirect in `src/main/java/com/quanlyphongtro/controller/tenant/TenantInvoiceServlet.java`
- [ ] T011 [US2] Add `GET /vnpay-return` route in `TenantInvoiceServlet` to handle VNPAY Return URL in `src/main/java/com/quanlyphongtro/controller/tenant/TenantInvoiceServlet.java`
- [ ] T012 [P] [US2] Create `payment-result.jsp` view for VNPAY return in `src/main/webapp/WEB-INF/views/tenant/invoices/payment-result.jsp`
- [ ] T013 [P] [US2] Implement `PaymentDao` for inserting/updating payment records in `src/main/java/com/quanlyphongtro/dao/PaymentDao.java`
- [ ] T014 [US2] Implement transaction logic in `PaymentService` to save payment and update invoice status securely in `src/main/java/com/quanlyphongtro/service/PaymentService.java`
- [ ] T015 [US2] Implement `VnpayIpnServlet` for handling IPN webhook requests in `src/main/java/com/quanlyphongtro/controller/tenant/VnpayIpnServlet.java`

## Phase 5: [US3] Payment History
- [ ] T016 [P] [US3] Implement `PaymentDao` query for successful payment history in `src/main/java/com/quanlyphongtro/dao/PaymentDao.java`
- [ ] T017 [US3] Implement `TenantPaymentHistoryServlet` to serve `GET /tenant/payments/history` in `src/main/java/com/quanlyphongtro/controller/tenant/TenantPaymentHistoryServlet.java`
- [ ] T018 [P] [US3] Create `history.jsp` view for payment history in `src/main/webapp/WEB-INF/views/tenant/payments/history.jsp`

## Phase 6: Polish & Cross-Cutting
- [ ] T019 Implement unit tests for `VnpayService` hash validation in `src/test/java/com/quanlyphongtro/service/VnpayServiceTest.java`
- [ ] T020 Review and polish UI consistency with `hostel-design.css` across all new JSP files
