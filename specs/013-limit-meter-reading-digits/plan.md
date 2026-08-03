# Implementation Plan: limit-meter-reading-digits

## Overview
This plan implements the feature to limit operator meter readings (electricity and water) to a maximum of 99999 (5 digits), reflecting physical 5-digit mechanical meters. It updates the UI, the backend Servlet validation, and the relevant SDD documents.

## Phase 1: SDD Updates
- Locate the specific meter reading spec in `my-project/sdd/specs/operator/` (e.g. `WaterElectricUpdate/Spec.md` or `MeterReadings/Spec.md`).
- Update the constraints on the electricity and water input fields from their current limits to `99999` (5 digits).

## Phase 2: Frontend Implementation
- **File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
- Update the HTML inputs for both `newElectric` and `newWater`.
- Add `max="99999"` (and `min="0"` if not already present) to the `type="number"` fields to enforce the 5-digit limit on the client side natively.

## Phase 3: Backend Implementation
- **File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
- In the `doPost` method, locate the validation blocks (around lines 192-203).
- Change the upper limit check from `10000` to `99999`.
- Update the error message to explicitly state the limit is "99999 (tối đa 5 chữ số)".

## Dependencies
- No new libraries or schema changes are required.

## Testing Strategy
- Manual test on the `update.jsp` page: try typing `100000` or pasting a larger number, and attempt to submit. The browser should block it.
- If bypass client-side validation, ensure the Servlet catches the `> 99999` value and returns an error without crashing.
- Normal case: test entering `99999` and submit, ensuring it saves correctly.
