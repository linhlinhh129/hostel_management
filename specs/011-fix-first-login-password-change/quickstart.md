# Quickstart: fix-first-login-password-change

## Validation Guide

This guide outlines how to validate the fix for the first login password change.

### Prerequisites

1. The application is running locally.
2. An account is created or modified to have `is_first_login = true` (or `force_change_pass = 1` in the database).

### Scenario 1: Unmatched Passwords Should Show Visual Error

1. Log into the system with the account. The system will redirect to `/first-login`.
2. In the "Mật khẩu mới" field, enter a valid password (e.g., `Admin@123`).
3. In the "Xác nhận mật khẩu" field, enter a different password (e.g., `Admin@1234`).
4. Click the "Cập nhật mật khẩu" button.
5. **Expected Outcome**: The form will not submit, and a clear alert will be shown to the user on the screen saying that the passwords do not match.

### Scenario 2: Properly Validated Password Should Submit

1. On the same page, change the "Xác nhận mật khẩu" field to exactly match "Mật khẩu mới" (e.g., `Admin@123`).
2. Click the "Cập nhật mật khẩu" button.
3. **Expected Outcome**: The form submits successfully, the user is redirected to the dashboard, and their password is changed in the database.

### Scenario 3: Backend and Frontend Consistency

1. In the database, manually set `force_change_pass = 1` for a user.
2. Log into the system with the user.
3. Enter `ADMIN123!` (no lowercase letters).
4. **Expected Outcome**: Since the frontend rules now match backend rules (and we removed the lowercase requirement), the UI checklist should correctly mark the password as passing (if it meets other criteria), and submitting it should succeed without backend validation errors.
