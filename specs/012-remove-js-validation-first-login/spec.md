# Feature Specification: remove-js-validation-first-login

**Feature Branch**: `[012-remove-js-validation-first-login]`

**Created**: 2026-08-03

**Status**: Draft

**Input**: User description: "tại sao để đây dùng javascript vậy, ban đầu kiến trúc hệ thống có phải dùng jsp cơ mà"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Remove Client-Side Password Validation (Priority: P1)

As a user required to change my password on first login, I want to submit my new password and have it validated entirely by the server (JSP/Servlet backend), so that the application adheres to its core server-side rendering architecture without relying on complex client-side JavaScript.

**Why this priority**: The current implementation relies heavily on JavaScript to validate password complexity and enable/disable the submit button. The user requested to align this with the project's JSP architecture, meaning validation should happen on the server, and errors should be returned via the rendered JSP view.

**Independent Test**: Can be fully tested by submitting a password that fails complexity rules; the server should catch the error and reload the page with a clear error message rendered by JSP.

**Acceptance Scenarios**:

1. **Given** a user on the first login password change page, **When** they enter a password that does not meet complexity requirements and submit the form, **Then** the server validates the input, rejects it, and re-renders the JSP page displaying a clear error message.
2. **Given** a user on the first login password change page, **When** they enter a valid password and submit, **Then** the server processes it successfully and redirects them to the dashboard.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST remove all custom JavaScript password validation logic (e.g., `allPassed`, `updateChecklist`, and manual DOM manipulation for password strength) from the `first_login.jsp` file.
- **FR-002**: System MUST rely solely on HTML5 form validation (e.g., `pattern`, `required`, `minlength`) and backend Servlet validation (using `PasswordValidator.java`) for password complexity.
- **FR-003**: System MUST handle backend validation errors gracefully by forwarding the user back to the `first_login.jsp` page and displaying the error messages through the standard `inline_alerts.jsp` mechanism.
- **FR-004**: System MUST ensure that the UI still clearly communicates the password policy requirements (e.g., minimum 8 characters, uppercase, number, special character) using static text or tooltips, replacing dynamic JS updates.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The `first_login.jsp` file is stripped of all custom password validation JavaScript.
- **SC-002**: Password complexity validation operates correctly via backend processing without JS intervention.
- **SC-003**: Invalid password submissions correctly display an error message returned from the server-side.

## Assumptions

- The existing `PasswordValidator.java` and `FirstLoginServlet.java` already have the necessary backend logic to validate passwords and return error messages. We only need to remove the frontend JS and ensure the HTML is correctly wired to show the backend errors.
- HTML5 `pattern` attributes can still be used for basic client-side defense (as this is native browser behavior, not custom JS script logic).
