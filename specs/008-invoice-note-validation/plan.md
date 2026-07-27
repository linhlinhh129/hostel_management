# Implementation Plan: Invoice Note Validation

**Branch**: `[008-invoice-note-validation]` | **Date**: 2026-07-27 | **Spec**: [spec.md](file:///f:/SU26/New%20folder/hostel_management/specs/008-invoice-note-validation/spec.md)

**Input**: Feature specification from `/specs/008-invoice-note-validation/spec.md`

## Summary

Add a 1000 character limit to the invoice note field when creating a new invoice, including UI guidance (helper text), frontend validation (maxlength constraint), and backend validation (rejecting >1000 characters).

## Technical Context

**Language/Version**: Java 17 + Jakarta Servlet 6.0

**Primary Dependencies**: JSP, JSTL, Bootstrap 5

**Storage**: SQL Server 2022, JDBC

**Testing**: Manual UI Testing + JUnit/Mockito

**Target Platform**: Tomcat 10.1 Web Server

**Project Type**: Web application

**Performance Goals**: N/A

**Constraints**: MVC architecture, no direct DB access from JSP.

**Scale/Scope**: Limit to `create.jsp` UI and `InvoiceServlet` / `InvoiceService`.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*
- [x] **Core Principle I (Layered Architecture)**: Validation occurs in UI (JSP) and Service layer.
- [x] **Core Principle II (Consistent UI)**: Helper text will use standard Bootstrap 5 classes (e.g., `form-text text-muted`).
- [x] **Core Principle III (RBAC)**: Already handled for Manager invoice creation.
- [x] **Core Principle IV (Safe DB Transactions)**: Handled.
- [x] **Core Principle V (Test-Driven and Code Quality)**: Backend validation returns clear, readable error messages.

## Project Structure

### Documentation (this feature)

```text
specs/008-invoice-note-validation/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
src/main/webapp/WEB-INF/views/manager/invoices/
└── create.jsp

src/main/java/com/quanlyphongtro/service/impl/
└── InvoiceServiceImpl.java
```

**Structure Decision**: This is a minor enhancement to existing files in a single Java web project structure.

## Complexity Tracking

N/A
