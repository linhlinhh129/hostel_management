# Quickstart Validation Guide

This document provides steps to manually validate the "Remove Broken Meter" feature.

## Prerequisites

1. Access to the Operator or Manager dashboard.
2. An existing room (`P101`) with previous electricity and water readings (e.g., previous reading of 9,990 kWh).

## Validation Scenarios

### Scenario 1: Verify broken meter UI and fields are completely removed

1. Navigate to `/operator/meter-readings/create`.
2. Inspect the form visually. 
3. **Expected Result**: There should be no dropdowns for "Nguyên nhân bất thường". Instead, there should be simple checkboxes for "Công tơ chạy hết vòng (reset về 0)" under both electricity and water inputs.
4. Try inspecting the HTML. Ensure there are no hidden fields for `oldFinal` or `newStart`.

### Scenario 2: Test Meter Rollover (Reset at 10,000)

1. Navigate to `/operator/meter-readings/create` for a room (e.g., Room P101) where the previous electricity reading was close to 10,000 (e.g., 9,990).
2. Enter the new reading: `10`.
3. Check the "Công tơ chạy hết vòng (reset về 0)" checkbox.
4. Fill in required images and submit.
5. Go to the history view or invoice view for that room.
6. **Expected Result**: The electricity usage should be calculated as `20` kWh (calculated as: 10,000 - 9,990 + 10).

### Scenario 3: Verify legacy data errors

1. Make sure no errors occur when navigating to the Meter Readings History or viewing old records that previously had "REPLACED" status.
2. The old fields will no longer be visible in the database.
