# Research: limit-meter-reading-digits

## Objective
Limit the maximum value for meter readings (electricity and water) to 99999 (5 digits) as per the updated operator spec.

## Affected Components

### 1. Backend Validation
**File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
- Currently, there is a validation check on lines 192-203 that limits `newElectric` and `newWater` to a maximum of 10000.
- Needs to be updated to `99999` and the error messages updated to reflect the new limit of "5 chữ số (tối đa 99999)".

### 2. Frontend UI
**File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
- The `input type="number"` for `newElectric` and `newWater` currently lacks `max="99999"`.
- Needs to add `max="99999"` (and `min="0"` if not already present) to the HTML inputs to prevent users from typing larger numbers.

### 3. SDD Specification
**File**: `my-project/sdd/specs/operator/README.md` (or the relevant spec file inside `operator`).
- Need to find where the operator limits are described and update it to reflect the max limit of 99999.
