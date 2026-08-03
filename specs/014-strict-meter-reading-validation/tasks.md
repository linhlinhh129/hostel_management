# Implementation Tasks: strict-meter-reading-validation

**Feature**: [spec.md](spec.md)
**Plan**: [plan.md](plan.md)

## Phase 1: SDD Updates

- [x] `T001`: **Update Operator SDD**
  - **File**: `my-project/sdd/specs/operator/WaterElectricUpdate/Spec.md`
  - **Action**: Update the constraints on electricity and water meter reading inputs to explicitly state that the inputs must be non-negative integers (no decimals, no negative numbers).
  - **Validation**: Verify the markdown file correctly reflects the new constraints.

## Phase 2: Frontend Implementation

- [x] `T002`: **Update Meter Reading Form UI**
  - **File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
  - **Action**: Add `step="1"` to the `<input type="number">` elements for `newElectric` and `newWater`. Update the Javascript `input` event listeners to check if the value contains a dot `.` or comma `,` (decimal), or is less than 0. If so, display an alert and revert the input.
  - **Validation**: Type `10.5` or `-5` in the UI and verify that the Javascript alert pops up and prevents the input.

## Phase 3: Backend Implementation

- [x] `T003`: **Update Servlet Validation Logic**
  - **File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
  - **Action**: Update the `NumberFormatException` catch block at the end of the `doPost` method. Change the generic message to: `"Dữ liệu nhập vào không hợp lệ. Chỉ số phải là số nguyên không âm (không chứa dấu thập phân)."`
  - **Validation**: Send a POST request with `newElectric=10.5` via Postman, and ensure the Servlet safely rejects it with the updated error message.
