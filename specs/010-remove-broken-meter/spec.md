# Feature Specification: Remove Broken Meter

**Feature Branch**: `[010-remove-broken-meter]`

**Created**: 2026-08-03

**Status**: Draft

**Input**: User description: "xóa cho mình phần công tơ điện nước hỏng nhé, giờ mình không cần nữa, những gì liên quan đến nó như code và database thì xóa giúp mình nhé. Hơn nữa giữ cho mình trường hợp reseat nếu công tơ đến max 10000 số điện và số nước. Khi gặp trường reset ở trên màn có dấu tích thì code chuyển sang trường hợp reseat nhé."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Remove broken meter fields and logic (Priority: P1)

As a user, I should not see or interact with the broken electricity and water meter functionality on the UI, so that the interface is simplified and unnecessary features are removed.

**Why this priority**: Removing obsolete features prevents user confusion and cleans up the codebase.

**Independent Test**: Can be fully tested by verifying that the broken meter checkboxes/fields are absent from the UI and their data is no longer saved.

**Acceptance Scenarios**:

1. **Given** the user is on the meter entry screen, **When** they view the form, **Then** the broken meter options are completely absent.
2. **Given** a historical record with broken meter data, **When** viewed, **Then** the system does not display broken meter information and handles the legacy data gracefully or it is completely migrated/deleted.

---

### User Story 2 - Meter reset logic at 10,000 units (Priority: P2)

As a user, when an electricity or water meter reaches the maximum value of 10,000 units, I need to check the reset option on the screen so that the system correctly calculates usage after the meter resets back to 0.

**Why this priority**: Accurate utility calculation is critical for billing when meters roll over.

**Independent Test**: Can be fully tested by entering old index close to 10,000, new index close to 0, and checking the reset checkbox, then verifying the correct usage calculation.

**Acceptance Scenarios**:

1. **Given** an old meter reading of 9,990 and a new reading of 10, **When** the reset checkbox is ticked, **Then** the system calculates the usage as 20 units (10,000 - 9,990 + 10).
2. **Given** the reset checkbox is not ticked, **When** a user enters a new index lower than the old index, **Then** the system returns an error.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST permanently remove all user interface elements related to recording "broken" electricity and water meters.
- **FR-002**: System MUST remove all backend logic and database schema columns related to the broken meter functionality.
- **FR-003**: System MUST retain the "reset" (rollover) functionality for meters.
- **FR-004**: System MUST calculate usage considering a max meter value of 10,000 units when the reset checkmark is enabled.
- **FR-005**: System MUST properly switch calculation logic to the reset case (calculating `10000 - old_index + new_index`) when the reset checkbox is checked on the UI.

### Key Entities

- **MeterReading**: Represents the electricity and water readings. Attributes related to "is_broken" will be removed, while "is_reset" (or similar) will be retained.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of UI elements and code paths related to "broken meter" are removed from the application.
- **SC-002**: Users can successfully input a meter reset scenario and the system correctly calculates usage across the 10,000 boundary with 100% accuracy.
- **SC-003**: No data integrity errors occur for legacy records that previously used the broken meter feature.

## Assumptions

- Legacy data for broken meters can be safely dropped or ignored without affecting past finalized invoices.
- The maximum value for both electricity and water meters is exactly 10,000 before they reset to 0.
- The reset condition is explicitly indicated by the user via a checkbox on the UI.
