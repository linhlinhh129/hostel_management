# Implementation Plan: Dependent Management (Test Strategy - 3 Tiers)

**Branch**: `test-dependent-management-3tiers` | **Spec**: [spec.md](file:///D:/Semester%205/SWP391/HostelManagement-main-a/HostelManagement-main-a%20%281%29%20%281%29/my-project/sdd/specs/test/tenant/dependent_manager/spec.md)

## Summary

This plan outlines the complete 3-tier testing implementation for the Tenant Dependent Management feature, ensuring strict compliance with `constitution.md`.
1. **Tier 1 (Unit)**: JUnit 5 + Mockito (Already Completed for `DependentServiceImpl`).
2. **Tier 2 (Integration)**: Testcontainers (SQL Server) for `DependentDAO` and REST Assured for Filter/Servlet integration.
3. **Tier 3 (End-to-End)**: Selenium WebDriver + JUnit 5 (Pure Java) for UI testing of JSP pages.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: JUnit 5, Mockito, AssertJ, Testcontainers, REST Assured, Selenium WebDriver

**Storage**: MS SQL Server (via Docker Testcontainers)

**Testing**: 3-Tier Testing Architecture (Unit, Integration, E2E)

**Target Platform**: Backend Web Service + JSP UI

**Project Type**: Comprehensive Testing

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **ENG-01**: Unit tests must maintain 80% minimum coverage. (✅ Done)
- **ENG-01**: Integration Tests are mandatory for all API endpoints / Servlets. (Will be implemented).
- **ENG-01**: E2E Tests are optional but recommended. (Will be implemented for the View UI).
- **DATA-01**: Soft-delete pattern must be tested accurately (returning HTTP 404/Empty equivalent).

## Project Structure

```text
src/test/java/
└── com/quanlyphongtro/
    ├── service/impl/
    │   └── DependentServiceImplTest.java    # Tier 1 (Done)
    ├── dao/
    │   ├── init-test-db.sql                 # Setup schema/seed for Testcontainers
    │   ├── BaseTestContainer.java           # Shared Docker container setup
    │   └── DependentDAOIT.java              # Tier 2 (SQL Queries)
    ├── servlet/
    │   └── DependentServletIT.java          # Tier 2 (REST Assured / Filter tests)
    └── e2e/
        └── DependentViewE2ETest.java        # Tier 3 (Selenium WebDriver + JUnit 5 tests)
```

## Verification Plan

- Run `mvn test` to execute Tier 1 & 2.
- E2E tests require Tomcat running locally, so a specialized profile or manual trigger will be documented.
