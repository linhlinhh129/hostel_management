# Data Model: Sync Notification UI

## Entities

No new database entities or changes to existing entities are required. The existing `Notification` model will continue to be used.

### `Notification` (Existing)

- **id**: Integer
- **code**: String
- **title**: String
- **content**: String
- **targetType**: String (ALL / FACILITY / ROOM)
- **facilityId**: Integer
- **roomId**: Integer
- **status**: String (DRAFT / SENT)
- **createdBy**: Integer
- **createdByName**: String
- **createdAt**: LocalDateTime
- **sentAt**: LocalDateTime

## Validation Rules

N/A (UI-only change)

## State Transitions

N/A (UI-only change)
