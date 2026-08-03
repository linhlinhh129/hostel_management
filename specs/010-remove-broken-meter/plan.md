# Implementation Plan: Remove Broken Meter

**Branch**: `[010-remove-broken-meter]` | **Date**: 2026-08-03 | **Spec**: [spec.md](file:///F:/SU26/New%20folder/hostel_management/specs/010-remove-broken-meter/spec.md)

**Input**: Feature specification from `/specs/010-remove-broken-meter/spec.md`

## Summary

Remove broken meter fields (`old_final`, `new_start`) and logic from the UI, Database, and Backend logic. Change the anomaly dropdown on the operator UI into simple checkboxes for meter rollover (reset) and handle the rollover calculation exactly up to 10,000 units.

## Technical Context

**Language/Version**: Java 17, Jakarta EE (Servlets, JSP)

**Primary Dependencies**: JDBC, JSP, JSTL, Servlets

**Storage**: MySQL Database (table `meter_readings`)

**Testing**: JUnit

**Target Platform**: Web browsers (Chrome, Edge, Safari)

**Project Type**: Web Application

**Constraints**: Adhere to Model-View-Controller, no direct database calls from JSP. Must provide safe DB transaction handling.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Follows MVC architecture.
- [x] No unauthorized access changes.
- [x] Database changes mapped via DAO properly.
- [x] Tested manually.

## Project Structure

### Documentation (this feature)

```text
specs/010-remove-broken-meter/
├── plan.md              
├── research.md          
├── data-model.md        
└── quickstart.md        
```

### Source Code (repository root)

```text
src/main/
├── java/com/quanlyphongtro/
│   ├── controller/operator/
│   │   ├── CreateMeterReadingServlet.java
│   │   └── UpdateMeterReadingServlet.java
│   ├── dao/
│   │   └── MeterReadingDAO.java
│   ├── dto/
│   │   └── MeterStatusDTO.java
│   └── service/impl/
│       └── MeterReadingServiceImpl.java
└── webapp/WEB-INF/views/operator/meter_readings/
    ├── create.jsp
    └── update.jsp
```

**Structure Decision**: The feature changes touch existing files across the MVC stack: UI (JSP), Controller (Servlets), DTOs, Services, and DAOs. We will remove code rather than add new structures.
