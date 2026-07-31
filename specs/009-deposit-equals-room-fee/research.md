# Phase 0 Research: Equalize Deposit Amount with Room Fee

## Technical Decisions & Rationale

### 1. Default Deposit Calculation at Room Creation & Update
- **Decision**: Automatically set `deposit_amount = room_fee` in `RoomDAO` / `RoomServiceImpl` when creating a new room or updating room rent if `deposit_amount` is not explicitly overridden.
- **Rationale**: Ensures data integrity at the foundational Room entity level, preventing zero or uninitialized deposit values in the database.
- **Alternatives Considered**: Calculating deposit dynamically on the fly without storing it in `rooms`. Rejected because historical contracts need a locked deposit snapshot.

### 2. Auto-Populating Deposit in Contract Creation Workflow
- **Decision**: In `ContractServiceImpl` / `ContractDAO` and `manager/contracts/create.jsp`, automatically load `room.room_fee` as the default `depositAmount`.
- **Rationale**: Eliminates manual input errors by managers when drafting contracts for tenants.
- **Alternatives Considered**: Hardcoding deposit values in JSP templates. Rejected because room fees vary across facilities and rooms.

### 3. Displaying Deposit Amount in Contract Views
- **Decision**: Use `<fmt:formatNumber value="${contract.room.depositAmount}" pattern="#,##0"/> đ` consistently across `manager/contracts/detail.jsp`, `tenant/contracts/detail.jsp`, and `manager/contracts/print.jsp`.
- **Rationale**: Ensures formatted currency representation with thousand separators across all roles and print formats.

### 4. Preserving Historic Contract Records
- **Decision**: Contract table stores `deposit_amount` at the time of contract execution. Updating a room's fee for future tenants will NOT modify existing signed active contracts.
- **Rationale**: Maintains legal compliance and historical accuracy for past agreements.
