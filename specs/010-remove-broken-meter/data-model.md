# Data Model Updates

## Entities

### `meter_readings` (Database Table)

This table stores monthly utility usage for each room. The following modifications will be made to remove the broken meter logic.

**Fields to drop:**
- `electric_old_final` (INT)
- `electric_new_start` (INT)
- `water_old_final` (INT)
- `water_new_start` (INT)

**Fields modified in logic:**
- `electric_status`: Will now only accept `NORMAL` or `ROLLOVER`.
- `water_status`: Will now only accept `NORMAL` or `ROLLOVER`.

### `MeterStatusDTO` (Java Object)

**Fields to remove:**
- `Integer electricOldFinal`
- `Integer electricNewStart`
- `Integer waterOldFinal`
- `Integer waterNewStart`
