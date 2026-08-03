# Quickstart: strict-meter-reading-validation

This feature blocks negative numbers and decimals from being entered as meter readings.

To implement this feature:
1. Ensure the `update.jsp` in `WEB-INF/views/operator/meter_readings/` has `step="1"` for its number inputs.
2. Update the JavaScript in `update.jsp` to check for `.`, `,`, or values `< 0` and display an `alert()` if they occur, while preventing the input.
3. Update the backend `UpdateMeterReadingServlet.java` to return a specific flash message inside the `NumberFormatException` catch block, clarifying that decimals are not allowed.
4. Update the SDD spec in `my-project/sdd/specs/operator/` to reflect these strict integer-only rules.
