# Feature Specification: Equalize Deposit Amount with Room Fee

**Feature Branch**: `009-deposit-equals-room-fee`

**Created**: 2026-07-31

**Status**: Draft

**Input**: User description: "thêm cho tôi tiền cọc sẽ bằng với tiến phòng @d:\Ki_5\hostel_management\my-project\sdd\specs\tenant\contract_management @d:\Ki_5\hostel_management\my-project\sdd\specs\manager\contractmanagement"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Auto-assign Deposit Equal to Room Fee Upon Contract & Room Creation (Priority: P1)

As a Hostel Manager, when I create a new room or generate a new lease contract, the system automatically sets the deposit amount equal to the monthly room fee so that I do not need to manually calculate or type the deposit amount.

**Why this priority**: Core business rule enforcement. Ensures consistency across room management and contract creation without manual errors.

**Independent Test**: Can be tested by initiating room/contract creation with a room fee of $2,500,000 VND and verifying that the deposit amount automatically defaults to $2,500,000 VND.

**Acceptance Scenarios**:

1. **Given** a manager is creating a room with a room fee of 3,000,000 VND, **When** the room details are loaded or saved, **Then** the deposit amount field defaults to 3,000,000 VND.
2. **Given** a manager is creating a lease contract for a room with a monthly rent of 2,700,000 VND, **When** the contract form is rendered or generated, **Then** the deposit amount is automatically populated as 2,700,000 VND.

---

### User Story 2 - Consistent Display of Deposit Amount in Manager & Tenant Contract Views (Priority: P2)

As a Tenant or Hostel Manager, when viewing contract details (or printable contract documents), the deposit text explicitly reflects that the deposit amount equals the monthly room fee.

**Why this priority**: Legal and transparent communication between landlord and tenant.

**Independent Test**: Can be tested by opening the contract detail view as both Manager and Tenant and confirming that the deposit amount matches the specified room fee in both numbers and formatted text.

**Acceptance Scenarios**:

1. **Given** an active contract with a room fee of 2,500,000 VND, **When** the Tenant views their contract detail page, **Then** Article 2 of the contract displays the deposit amount as 2,500,000 VND.
2. **Given** an active contract with a room fee of 3,200,000 VND, **When** the Manager views or prints the contract, **Then** the deposit amount listed matches 3,200,000 VND.

---

### User Story 3 - Synchronization When Room Fee is Updated (Priority: P3)

As a Manager, when updating the base room fee of an unoccupied room, the default deposit amount for future contracts on that room automatically updates to match the new room fee.

**Why this priority**: Maintains long-term data integrity for future lease agreements.

**Independent Test**: Can be tested by updating a room fee from 2,500,000 VND to 2,800,000 VND and verifying new contracts created for that room inherit 2,800,000 VND as the default deposit.

**Acceptance Scenarios**:

1. **Given** a room fee is updated to 2,800,000 VND, **When** a new contract is prepared for this room, **Then** the default deposit amount proposed by the system is 2,800,000 VND.

---

### Edge Cases

- What happens if the room fee is 0 or negative during input? System MUST validate that room fee is a positive number (> 0) before setting the equal deposit amount.
- What happens if an existing active contract has a historic deposit amount different from the updated room fee? Existing signed contracts MUST preserve their historic deposit amount as recorded at the time of signing.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST automatically calculate and set the default deposit amount to be equal to the room fee (`deposit_amount = room_fee`) when creating or initializing a room or lease contract.
- **FR-002**: System MUST validate that the room fee is a positive numeric value before assigning the equal deposit amount.
- **FR-003**: System MUST display the deposit amount equal to the room fee clearly in both numerical format and spelled-out words in contract templates for Manager and Tenant views.
- **FR-004**: System MUST preserve historical deposit amounts for previously signed contracts, ensuring retroactive changes to room fees do not alter active signed contracts.
- **FR-005**: System MUST present equal room fee and deposit values consistently across contract detail pages, print layouts, and PDF exports.

### Key Entities

- **Room**: Represents the rental unit, containing `room_fee` (monthly rent) and `deposit_amount` (security deposit required).
- **Contract**: Represents the legal agreement between Manager and Tenant, locking in `room_fee` and `deposit_amount` at the time of signing.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of newly generated contracts have their default deposit amount set equal to the room fee.
- **SC-002**: Zero discrepancies between room fee and deposit amount displayed in contract generation forms for new leases.
- **SC-003**: Managers complete contract setup in under 2 minutes without needing to manually calculate or adjust deposit figures.

## Assumptions

- Room fees are specified in VND and are positive integers.
- Existing signed contracts remain legally binding at their original deposit values recorded during contract execution.
- Standard deposit policy across all managed facilities is 1 month's rent (deposit = room fee).
