<!-- 
Sync Impact Report:
- Version change: 1.0.0 → 1.1.0
- Modified principles: Initial Draft
- Added sections: Core Principles, Technology Stack & Constraints, Development Workflow
- Removed sections: N/A
- Templates requiring updates: N/A
- Follow-up TODOs: N/A
-->
# Hostel Management Constitution

## Core Principles

### I. Layered Architecture (MVC)
The application MUST strictly adhere to the Model-View-Controller (MVC) architecture. Controllers (Servlets) handle requests, Services handle business logic, DAOs handle data access, and JSPs handle views. Direct database access from Controllers or JSPs is strictly forbidden.

### II. Consistent UI Design
All new UI components MUST use the predefined design system (e.g., `hostel-design.css`, Mintlify tables, standardized badges). Avoid introducing ad-hoc CSS styles unless absolutely necessary. The aesthetic must feel premium, modern, and aligned across all actors (Admin, Manager, Operator, Tenant).

### III. Role-Based Access Control (RBAC)
Every endpoint and view MUST verify the user's role before processing. Do not assume authorization based on obfuscated URLs. The roles (`ADMIN`, `MANAGER`, `OPERATOR`, `TENANT`) have distinct responsibilities that must remain isolated.

### IV. Safe Database Operations
All database modifications MUST be transacted properly, particularly when dealing with related entities (e.g., Invoices, Contracts, Services). Read operations should utilize pagination and filtering at the database level to maintain performance.

### V. Test-Driven and Code Quality
Code should be highly testable, with minimal tight coupling. Maintain clean and readable code. All complex business logic must have clear documentation and be verifiable through unit testing where possible.

## Technology Stack & Constraints

- **Backend**: Java, Jakarta EE (Servlets, JSP), JDBC
- **Frontend**: HTML5, CSS3, Vanilla JavaScript. JSTL and EL for dynamic rendering.
- **Database**: Relational Database (SQL Server/MySQL)
- **Tooling**: Maven for dependency management and build.

## Development Workflow

- **Specification**: All major features MUST start with a specification document.
- **Implementation**: Changes should be implemented following the established conventions in the repository.
- **Review**: All PRs must be verified for compliance with this Constitution and the specific feature requirements. UI changes must include visual verification to ensure responsiveness and consistency.

## Governance

This Constitution supersedes all other practices. All changes must align with these principles.
Amendments require documentation, approval, and a corresponding version bump in this file. All PRs/reviews must verify compliance. Use `AGENTS.md` and standard project guidelines for runtime development guidance.

**Version**: 1.1.0 | **Ratified**: 2026-06-29 | **Last Amended**: 2026-06-29
