# Quickstart: remove-js-validation-first-login

## Validation Guide

This guide outlines how to validate that JavaScript password validation is fully removed and backend validation still correctly protects the password change on the first login screen.

### Prerequisites

1. The application is running locally.
2. An account is created or modified to have `is_first_login = true` (or `force_change_pass = 1` in the database).

### Scenario 1: Verify No Dynamic JS Validation

1. Log into the system with the account. The system will redirect to `/first-login`.
2. Observe the page UI. There should be NO dynamic password strength bar and NO checklist with dynamic green checkmarks. It should just display a static policy requirement.
3. **Expected Outcome**: Typing into the "Mật khẩu mới" field does not trigger any JS UI updates.

### Scenario 2: Test Native HTML5 Validation (Optional client-side block)

1. In the "Mật khẩu mới" field, enter an invalid password (e.g., `admin`).
2. Click the "Cập nhật mật khẩu" button.
3. **Expected Outcome**: The browser's native HTML5 tooltip should appear (e.g., "Please match the requested format..."), preventing submission, entirely without custom JavaScript alerts.

### Scenario 3: Test Backend Validation Fallback

1. Bypass HTML5 validation (e.g., by modifying the DOM in DevTools to remove `pattern="(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}"`).
2. Enter `admin` in both fields and submit the form.
3. **Expected Outcome**: The form submits to the server, the server's `PasswordValidator.java` catches the weakness, and the page is re-rendered showing an inline alert (from `inline_alerts.jsp`) stating the password is too weak.
