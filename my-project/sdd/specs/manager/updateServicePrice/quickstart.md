# Quickstart: updateServicePrice Validation

This guide provides runnable validation scenarios to prove the feature works end-to-end.

## Scenario 1: Valid Price Update

**Setup**: Login as a MANAGER who manages facility ID = 1.
**Action**:
- Navigate to `/manager/service-prices`.
- Open pop-up for `ELECTRICITY`.
- Input `40000` as the new price.
- Submit the form (POST to `/manager/service-prices?action=update`).
**Expected**:
- Success redirect to `/manager/service-prices`.
- The new electricity price displayed is 40000 VNĐ.
- The history list (click "Lịch sử") shows a new entry with old price, new price = 40000, and no note column.

## Scenario 2: Invalid Format (Contains Comma)

**Setup**: Same as above.
**Action**:
- Open pop-up for `WATER`.
- Input `20,000` as the new price.
- Submit the form.
**Expected**:
- The form is rejected (either client-side HTML5 validation or server-side).
- Error message displays: "Yêu cầu nhập số nguyên, ví dụ: 40000".

## Scenario 3: Price Same as Current

**Setup**: Current `SERVICE_FEE` is 100000.
**Action**:
- Open pop-up for `SERVICE_FEE`.
- Input `100000` as the new price.
- Submit the form.
**Expected**:
- The form is rejected.
- Error message displays: "Giá mới phải khác giá hiện tại".

## Scenario 4: Price is Negative

**Setup**: Same as above.
**Action**:
- Open pop-up for `SERVICE_FEE`.
- Input `-5000` as the new price.
- Submit the form.
**Expected**:
- The form is rejected.
- Error message displays that the price must be greater than 0.
