# Phase 1 Data Model: Equalize Deposit Amount with Room Fee

## Entities & Relationships

```mermaid
erDiagram
    FACILITY ||--o{ ROOM : contains
    ROOM ||--o{ CONTRACT : has
    USER ||--o{ CONTRACT : signs

    ROOM {
        int room_id PK
        string code
        decimal room_fee
        decimal deposit_amount "Default = room_fee"
        string status
    }

    CONTRACT {
        int contract_id PK
        string code
        int room_id FK
        int tenant_id FK
        decimal deposit_amount "Locked snapshot at contract creation"
        date start_date
        date end_date
        string status
    }
```

## Validation Rules

1. **Room Creation/Update**:
   - `room_fee` MUST be a positive number (`room_fee > 0`).
   - `deposit_amount` defaults to `room_fee` unless explicitly specified.

2. **Contract Creation**:
   - `contract.deposit_amount` is initialized to `room.room_fee` (or `room.deposit_amount`).
   - Must be locked into `dbo.contracts` table upon creation.

3. **Contract View & Print**:
   - `deposit_amount` MUST be rendered with thousand separators (e.g., `2,500,000 đ`).
