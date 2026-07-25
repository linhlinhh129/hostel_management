# Data Model: Debt Management

This feature does not introduce new database tables. It reuses existing tables to dynamically query and calculate debts.

## Source Entities

- **Invoices**: Core entity for debts. An invoice is considered a debt if its `status` is `UNPAID` or `OVERDUE`.
- **Rooms**: Associated with the invoice.
- **Users**: The tenant associated with the room.
- **Facilities**: The facility the room belongs to.
- **Payments**: Transactions related to the invoice, used to calculate `paid_amount` and active `PENDING` transactions to freeze late fees.

## DTOs (Data Transfer Objects)

### DebtListItemDTO
- `invoiceId` (Long)
- `invoiceCode` (String)
- `roomCode` (String)
- `tenantName` (String)
- `tenantPhone` (String)
- `facilityName` (String)
- `billingPeriod` (String)
- `totalAmount` (BigDecimal): Original total amount of the invoice + temporaryLateFee (if applicable).
- `paidAmount` (BigDecimal): Total successful payments.
- `remainingAmount` (BigDecimal): Calculated as `MAX(0, totalAmount - paidAmount)`.
- `dueDate` (Date)
- `debtDays` (Integer): `MAX(0, CurrentDate - dueDate)`
- `temporaryLateFee` (BigDecimal): `debtDays * (roomFee * 0.01)` (1% is hardcoded).
- `status` (String): `UNPAID` or `OVERDUE`.

### DebtDetailDTO
- Includes all fields from `DebtListItemDTO`.
- `contractPeriod` (String): Thể hiện ngày bắt đầu và kết thúc hợp đồng hiện tại.
- `roomFee`, `electricityFee`, `waterFee`, `serviceFee`, `internetFee`, `otherFee` (BigDecimal).
- `electricityUsage`, `waterUsage` (Integer) and their respective rates.
- `note` (String).
- Tenant details (`email`).
