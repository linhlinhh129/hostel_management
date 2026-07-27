# Data Model: operator-utility-request-display

## Key Entities

### `requests` (table)
- `request_id`: INT, Primary Key
- `code`: VARCHAR, unique identifier (e.g. `REQ-UTL-001`)
- `sender_id`: INT (Foreign Key to `users` - Manager)
- `category`: VARCHAR (Value: `'UTILITY'`)
- `title`: NVARCHAR (Title of the report)
- `content`: NVARCHAR (Content of the report)
- `status`: VARCHAR (e.g. `'PENDING'`)
- `assigned_staff_id`: INT (Foreign Key to `users` - Operator)
- **`room_id`**: INT (Foreign Key to `rooms`) - *Newly populated field for this feature*
- **`facility_id`**: INT (Foreign Key to `facilities`) - *Newly populated field for this feature*
- `created_at`: DATETIME
- `updated_at`: DATETIME
- `deleted_at`: DATETIME (Soft delete)

*Validation Rules:*
- `room_id` and `facility_id` MUST be populated for `category = 'UTILITY'`.

*State Transitions:*
- Not altered by this feature. Remains standard: `PENDING` -> `RECEIVED` -> `IN_PROGRESS` -> `COMPLETED`/`REJECTED`.
