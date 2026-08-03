# Phase 0: Research

## Summary of Findings

1. **How "Broken Meter" is currently represented**:
   - The UI provides dropdowns (`electricStatus` and `waterStatus`) with three options: `NORMAL`, `REPLACED`, `ROLLOVER`.
   - The `REPLACED` status ("Thay công tơ mới") corresponds to the "broken meter" scenario where operators must input `old_final` and `new_start` numbers.
   - The Database table `meter_readings` has columns `electric_old_final`, `electric_new_start`, `water_old_final`, and `water_new_start` to store this data.
   - The code tracks these fields in `MeterStatusDTO`, `MeterReadingDAO`, `MeterReadingService`, and Servlet layers.

2. **How "Reset" is currently represented**:
   - The `ROLLOVER` status ("Công tơ chạy hết vòng") handles the reset case (currently up to 10,000 max).

## Decisions for Implementation

- **Decision 1: UI Changes**: 
  - Remove the dropdowns for `electricStatus` / `waterStatus` entirely.
  - Replace them with simple checkboxes: "Công tơ chạy hết vòng (reset về 0)" for electricity and water.
  - Remove the input fields for `oldFinal` and `newStart`.
- **Decision 2: Logic & Calculation Changes**:
  - If the checkbox is checked, map the status to `ROLLOVER`. Otherwise, `NORMAL`.
  - In the service layer (`MeterReadingService`), when calculating usage, if `ROLLOVER`, apply the formula: `usage = 10000 - previousReading + currentReading`.
- **Decision 3: Database & Code Cleanup**:
  - Drop the columns `electric_old_final`, `electric_new_start`, `water_old_final`, `water_new_start` from the `meter_readings` table.
  - Remove these fields from `MeterStatusDTO` and any `MeterReadingDAO` queries.
  - Remove related logic from Servlets (`CreateMeterReadingServlet`, `UpdateMeterReadingServlet`).

## Resolution of NEEDS CLARIFICATION

- All unknowns are resolved. The database schema and controller layers have been analyzed. We know exactly which fields to drop and which logic to modify.
