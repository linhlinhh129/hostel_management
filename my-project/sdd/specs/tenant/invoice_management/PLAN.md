# Implementation Plan: Invoice Management & VNPAY Payment

**Branch**: `main` | **Date**: 2026-07-24 | **Spec**: [SPEC.md](SPEC.md)

**Input**: Feature specification from `SPEC.md`

## Summary

The feature enables Tenants to view invoices and pay them via VNPAY integration. It includes viewing invoice lists, invoice details, payment history, generating VNPAY payment URLs, and processing VNPAY IPN webhooks to automatically update invoice status and record payment transactions securely.

## Technical Context

**Language/Version**: Java 17, Jakarta EE

**Primary Dependencies**: VNPAY SDK / Cryptography Libraries (for SHA-512 Secure Hash)

**Storage**: SQL Server/MySQL (using JDBC)

**Testing**: JUnit 5, Mockito for Unit Tests

**Target Platform**: Web Browser

**Project Type**: Web Application

**Performance Goals**: API response < 300ms, VNPAY URL generation < 500ms

**Constraints**: Transactions required for Payment + Invoice updates. Idempotency for IPN. Secure Hash validation.

**Scale/Scope**: Moderate throughput, critical financial transactions.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Layered Architecture (MVC)**: Yes. Controllers handle HTTP (Servlet), Services handle business logic and VNPAY crypto, DAOs handle DB operations.
- **II. Consistent UI Design**: Yes. Follows `hostel-design.css`.
- **III. Role-Based Access Control (RBAC)**: Yes. All endpoints restricted to `TENANT`. IPN endpoint is open but secured via `SecureHash`.
- **IV. Safe Database Operations**: Yes. DB Transactions explicitly required for IPN processing.
- **V. Test-Driven and Code Quality**: Yes. Will include unit and integration tests.

## Project Structure

### Documentation (this feature)

```text
my-project/sdd/specs/tenant/invoice_management/
├── plan.md
├── data-model.md
├── quickstart.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/
├── java/com/quanlyphongtro/
│   ├── controller/tenant/
│   │   ├── TenantInvoiceServlet.java (List, Detail, Pay, Return)
│   │   ├── TenantPaymentHistoryServlet.java
│   │   └── VnpayIpnServlet.java
│   ├── service/
│   │   ├── InvoiceService.java
│   │   ├── PaymentService.java
│   │   └── VnpayService.java (Hashing, URL generation, IPN validation)
│   └── dao/
│       ├── InvoiceDao.java
│       └── PaymentDao.java
└── webapp/WEB-INF/views/tenant/
    ├── invoices/
    │   ├── list.jsp
    │   ├── detail.jsp
    │   └── payment-result.jsp
    └── payments/
        └── history.jsp
```

**Structure Decision**: Standard MVC layout for the Java Servlet application. Controllers mapped to routing, Service layer handles VNPAY integration logic, DAOs manage data access.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
