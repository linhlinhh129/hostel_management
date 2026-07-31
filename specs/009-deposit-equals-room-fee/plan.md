# Implementation Plan: Equalize Deposit Amount with Room Fee

**Branch**: `009-deposit-equals-room-fee` | **Date**: 2026-07-31 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/009-deposit-equals-room-fee/spec.md`

## Summary

Enforce business rule where the security deposit amount automatically equals the monthly room fee upon room initialization and contract creation. Ensure consistent rendering across Manager and Tenant contract views and PDF/print templates while preserving historic deposit records for active/past signed contracts.

## Technical Context

**Language/Version**: Java 17+, Jakarta EE 10 (Servlets, JSP)

**Primary Dependencies**: Jakarta Servlet API, JSTL (Core & FMT), JDBC

**Storage**: SQL Server (Tables: `dbo.rooms`, `dbo.contracts`)

**Testing**: JUnit, Manual UI Verification

**Target Platform**: Web Application (Tomcat / Servlet Container)

**Project Type**: Web Application (MVC)

**Performance Goals**: Sub-200ms page render time for contract views

**Constraints**: Strict MVC layering (no DB queries in JSP/Servlets), JSTL formatting for currency, RBAC validation

**Scale/Scope**: All managed facilities, rooms, and contracts across Manager and Tenant portals

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Layered Architecture (MVC)**: PASS - Changes isolated to Service layer (`RoomServiceImpl`, `ContractServiceImpl`), DAO layer (`RoomDAO`, `ContractDAO`), and View layer JSPs.
- **Consistent UI Design**: PASS - Use existing `hostel-design.css` and `<fmt:formatNumber>` formatting.
- **Role-Based Access Control (RBAC)**: PASS - Ensure Manager and Tenant authorizations are maintained.
- **Safe Database Operations**: PASS - Database transactions utilized when updating rooms/contracts.
- **Test-Driven & Code Quality**: PASS - Clear business logic validation.

## Project Structure

### Documentation (this feature)

```text
specs/009-deposit-equals-room-fee/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
    └── room-contract-rules.md
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/com/quanlyphongtro/
    │   ├── dao/
    │   │   ├── ContractDAO.java
    │   │   └── RoomDAO.java
    │   ├── model/
    │   │   ├── Contract.java
    │   │   └── Room.java
    │   └── service/impl/
    │       ├── ContractServiceImpl.java
    │       └── RoomServiceImpl.java
    └── webapp/WEB-INF/views/
        ├── manager/contracts/
        │   ├── create.jsp
        │   ├── detail.jsp
        │   └── print.jsp
        └── tenant/contracts/
            └── detail.jsp
```

**Structure Decision**: Standard Java Web MVC application with JSPs in `src/main/webapp/WEB-INF/views/` and Backend in `src/main/java/com/quanlyphongtro/`.

## Complexity Tracking

> *No Constitution violations detected.*
