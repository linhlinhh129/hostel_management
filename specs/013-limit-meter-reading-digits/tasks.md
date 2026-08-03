# Implementation Tasks: limit-meter-reading-digits

**Feature**: [spec.md](spec.md)
**Plan**: [plan.md](plan.md)

## Phase 1: SDD Updates

- [x] `T001`: **Update Operator SDD**
  - **File**: `my-project/sdd/specs/operator/` (locate relevant spec file, likely `WaterElectricUpdate/Spec.md` or `MeterReadings/Spec.md`)
  - **Action**: Update the constraints on electricity and water meter reading inputs to explicitly state the limit is `99999` (5 digits).
  - **Validation**: Verify the markdown file correctly reflects the new constraints.

## Phase 2: Frontend Implementation

- [x] `T002`: **Update Meter Reading Form UI**
  - **File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
  - **Action**: Add `max="99999"` to the `<input type="number">` elements for `newElectric` and `newWater`. (Add `min="0"` if not present).
  - **Validation**: Verify that the browser's native HTML5 validation prevents submitting values greater than 99999 or negative values.

## Phase 3: Backend Implementation

- [x] `T003`: **Update Servlet Validation Logic**
  - **File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
  - **Action**: In the `doPost` method, locate the validation checking if `newElectric` and `newWater` are greater than `10000`. Update these checks to use `99999` as the upper limit. Update the corresponding `flashMessage` string to read: "Chỉ số điện/nước không hợp lệ. Số mới không được âm và tối đa là 99999 (tối đa 5 chữ số)."
  - **Validation**: Deploy the server locally, attempt to bypass client-side UI limitations by modifying DOM/Postman to submit a value > 99999, and ensure the Servlet safely rejects it with the updated error message.
