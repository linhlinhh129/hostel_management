# Feature Specification: Operator Detail UI Sync

**Feature Branch**: `[002-operator-detail-ui]`

**Created**: 2026-06-29

**Status**: Draft

**Input**: User description: "t check lại thì thấy phần tenat có giống thôi manager, còn phần operator chưa giống đâu chỉnh lại giao diện thông cho giống đi"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Operator views Notification Detail on Dedicated Page (Priority: P1)

As an Operator, I want to click on a notification and be taken to a dedicated detail page that perfectly mirrors the layout of the Manager detail page (with a main content area and a metadata sidebar), so that my experience is identical to the Manager's.

**Why this priority**: The user specifically requested that the Operator UI matches the Manager UI exactly. An inline row expansion does not meet the visual standard set by the Manager's dedicated page.

**Independent Test**: Log in as an Operator, go to notifications, click "Xem chi tiết", and verify that a new page loads with the `row g-3`, `col-lg-8`, and `col-lg-4` structure mimicking the manager's view.

**Acceptance Scenarios**:

1. **Given** I am on the Operator notifications list, **When** I click "Xem chi tiết", **Then** I am navigated to `/operator/notifications/{id}`.
2. **Given** I am on the Operator notification detail page, **When** I view the page, **Then** I see the title, code, back button, content area, and a sidebar with metadata (Code, Creator, Date, etc.) exactly matching the manager's UI.

### Edge Cases

- User tries to access a non-existent notification ID -> Should redirect or show a 404/Not Found gracefully.
- User tries to access a notification not intended for Operators -> Should deny access (403 Forbidden).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a dedicated detail view URL for Operator notifications (`/operator/notifications/{id}`).
- **FR-002**: System MUST implement a Servlet (e.g., `OperatorNotificationDetailServlet`) in Java to handle the routing and data fetching for the new detail page, utilizing existing DAOs.
- **FR-003**: The Operator detail JSP MUST use the exact same HTML structure and CSS classes as the Manager's `detail.jsp` (e.g., `widget-surface`, `col-lg-8`, `col-lg-4`).
- **FR-004**: System MUST NOT modify the database schema or SQL queries.

### Key Entities 

- **Notification**: Existing entity. No schema changes allowed.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% visual consistency between Operator detail page and Manager detail page.
- **SC-002**: 0 database changes.
- **SC-003**: Operator can successfully navigate back and forth between the list and the detail page.

## Assumptions

- We assume the existing `NotificationDAO` has methods to fetch a single notification by ID (e.g., `findById(int id)`), which we can reuse in the new Servlet.
