# Quickstart Validation: Freeze Overdue Fee

## Scenarios to Validate

**Scenario 1: Freeze late fee upon tenant payment**
1. Wait until an invoice is `OVERDUE` by at least 2 days. The late fee shows X.
2. Log in as a tenant and create a payment for that invoice. The payment status becomes `PENDING`.
3. Wait another day (or simulate a time jump).
4. Log in as manager and view the invoice details.
5. **Expected Outcome**: The late fee remains X (frozen at the time the tenant made the payment).

**Scenario 2: Unfreeze late fee upon rejection**
1. From Scenario 1, the manager REJECTS the pending payment.
2. **Expected Outcome**: The late fee instantly jumps to reflect the actual current date (e.g., X + 1 day's fee).

**Scenario 3: Commit late fee upon approval**
1. From Scenario 1, the manager APPROVES the pending payment.
2. **Expected Outcome**: The invoice becomes `PAID`, and the frozen late fee X is saved to the database.
