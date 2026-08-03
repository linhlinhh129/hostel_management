# Quickstart: Debt Management

## Prerequisites
- A user account with the `Management Board` (`MANAGER`) role.
- Existing invoices in the database with `status` = `UNPAID` or `OVERDUE` or `FROZEN`.

## Validation Scenarios

### Scenario 1: View Debt List
1. Login as a Manager.
2. Navigate to `GET /manager/debts`.
3. Verify that the list only displays invoices with `UNPAID` or `OVERDUE` or `FROZEN` statuses.
4. Verify that the default sorting shows the oldest `dueDate` first.
5. Verify that `Tổng tiền phải nộp` displays the original total amount (excluding the temporary late fee).
6. Verify that the `temporaryLateFee` column calculates 1% of the room fee multiplied by the number of overdue days.

### Scenario 2: Filter by Status
1. From the Debt List, select the filter for `OVERDUE`.
2. Verify that only overdue invoices are displayed.

### Scenario 3: View Debt Detail
1. Click "View Detail" on a debt record.
2. Verify that `GET /manager/debts?action=detail&id={id}` returns HTTP 200.
3. Check the calculated `remainingAmount` ensuring it subtracts any successful payments.
