# Data Model: strict-meter-reading-validation

No changes to the data model. The `MeterStatusDTO` and the underlying database tables already use `int` for meter readings, which inherently does not support decimal values. This feature solely enforces these restrictions strictly at the UI and Controller layers.
