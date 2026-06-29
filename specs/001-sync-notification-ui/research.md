# Research: Sync Notification UI

## Findings

- **No unknown dependencies or technologies**: The project utilizes standard Java Server Pages (JSP) and custom CSS (`hostel-design.css`). 
- **Target view**: The manager view at `manager/notifications/list.jsp` uses a `table-mintlify` layout for notifications. 
- **Action**: Operator and Tenant views just need to replicate this HTML structure without any changes to the servlets or DAOs.

## Decisions

- **Decision**: Update Operator and Tenant JSPs to use `table-mintlify` with inline expanding rows.
- **Rationale**: Meets the user requirement of UI consistency and strictly avoids database schema changes, while preserving existing controller logic.
- **Alternatives considered**: Creating dedicated `detail.jsp` pages for Operator and Tenant (rejected because it might require adding new Servlet mappings which increases backend risk, whereas an inline expanding row in a table matches the visual feel with zero backend changes).
