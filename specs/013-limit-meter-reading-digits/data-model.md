# Data Model: limit-meter-reading-digits

No changes to the data model. The `MeterStatusDTO` and the underlying database tables already use `int` for meter readings, which comfortably supports up to 2,147,483,647. The limit of 99999 fits perfectly without any schema adjustments.
