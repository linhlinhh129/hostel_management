# Quickstart: Validation Guide for Notification UI Sync

This guide provides the steps to manually verify the Notification UI sync for Operators and Tenants.

## Prerequisites
- The application must be running locally via Tomcat or equivalent Servlet container.
- You need active user accounts with `OPERATOR` and `TENANT` roles.
- The database should contain at least one system notification accessible by these users.

## 1. Verify Operator View
1. Log in using an `OPERATOR` account.
2. Navigate to the Notifications section (`/operator/notifications`).
3. Verify the layout uses a table matching the Manager's layout (`table-mintlify`).
4. Click the "Xem chi tiết" button on a notification.
5. Verify the row expands to show the full content in a consistent, readable format.

## 2. Verify Tenant View
1. Log in using a `TENANT` account.
2. Navigate to the Notifications section (`/tenant/notifications` or equivalent).
3. Verify the layout uses a table matching the Manager's layout.
4. Click the detail button on a notification.
5. Verify the expanded content appears correctly with the same UI layout as the Operator/Manager.

## Expected Outcomes
- The structure and styling are identical for Manager, Operator, and Tenant views.
- No database exceptions or access errors occur during this process.
