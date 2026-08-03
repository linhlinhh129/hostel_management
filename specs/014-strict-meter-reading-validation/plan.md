# Implementation Plan: strict-meter-reading-validation

## Overview
This plan implements strict validation to block negative and decimal values for meter readings, improving data integrity and user experience through real-time feedback.

## Phase 1: Frontend Implementation
- **File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
- Add `step="1"` to both `newElectric` and `newWater` inputs to enforce integer entry natively.
- Enhance the existing JS `input` event listener:
  - Check if the value contains a dot `.` or comma `,`. If so, show an alert: "Chỉ số không được chứa số thập phân." and revert the input.
  - Check if the value is less than 0. If so, show an alert: "Chỉ số không được là số âm." and revert the input.

## Phase 2: Backend Implementation
- **File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
- Modify the `catch (NumberFormatException e)` block at the end of the `doPost` method.
- Change the generic error message to a more specific one: `"Dữ liệu nhập vào không hợp lệ. Chỉ số phải là số nguyên không âm (không chứa dấu thập phân)."`

## Phase 3: SDD Updates
- Locate the specific meter reading spec in `my-project/sdd/specs/operator/WaterElectricUpdate/Spec.md`.
- Update the constraints on electricity and water input fields to explicitly forbid negative and decimal numbers.

## Testing Strategy
- Manual test on the `update.jsp` page: try typing `10.5` or `-5`, and ensure the Javascript `alert()` pops up and clears the invalid character.
- Try bypassing client-side validation by sending `newElectric=10.5` via Postman, and ensure the Servlet returns the updated `NumberFormatException` error message.
