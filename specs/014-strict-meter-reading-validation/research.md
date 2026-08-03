# Research: strict-meter-reading-validation

## Objective
Prevent the entry of negative numbers and decimal values in meter readings, showing clear error messages when such inputs are attempted.

## Affected Components

### 1. Frontend UI
**File**: `src/main/webapp/WEB-INF/views/operator/meter_readings/update.jsp`
- Currently, `<input type="number">` has `min="0"`, which natively blocks negative numbers on form submit.
- It does not have `step="1"`, meaning browsers *might* allow entering decimals, or at least they won't explicitly block it until submit. 
- We can add `step="1"` to enforce integers via native HTML5 validation.
- In the Javascript `input` event listener, we can add a check for decimals (e.g. using `includes('.')` or `includes(',')`) and negatives (`< 0`) to show a real-time `alert()` and clear the input.

### 2. Backend Validation
**File**: `src/main/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServlet.java`
- Currently, `newElectric < 0` and `newWater < 0` are already checked and show an error message: "Số mới không được âm...".
- If a decimal is sent (e.g. `10.5`), `Integer.parseInt()` throws a `NumberFormatException`, which is caught at the end of the `doPost` method. The current catch block sets a generic flash message: `"Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại."`
- We should update the `NumberFormatException` catch block message to explicitly say: `"Dữ liệu nhập vào không hợp lệ. Chỉ số phải là số nguyên (không được chứa dấu thập phân)."`

### 3. SDD Specification
**File**: `my-project/sdd/specs/operator/WaterElectricUpdate/Spec.md` and `PLAN.md`
- Need to update the validation rules to explicitly state: `Chỉ số (số mới) phải là số nguyên không âm (không được nhập số âm, không được nhập số thập phân)`.
