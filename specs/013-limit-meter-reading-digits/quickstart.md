# Quickstart: limit-meter-reading-digits

This feature introduces a limit to the maximum meter reading an operator can enter, restricting it to `99999` (5 digits).

To implement this feature:
1. Ensure the `update.jsp` in `WEB-INF/views/operator/meter_readings/` has `min="0"` and `max="99999"` for its number inputs.
2. Update the backend `UpdateMeterReadingServlet.java` to validate that `newElectric` and `newWater` are less than or equal to `99999`, and display appropriate error messages if not.
3. Update the SDD spec in `my-project/sdd/specs/operator/` that governs meter reading updates to reflect the new 5-digit limit.
