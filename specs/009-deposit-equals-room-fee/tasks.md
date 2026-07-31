# Tasks: Equalize Deposit Amount with Room Fee

**Input**: Design documents from `/specs/009-deposit-equals-room-fee/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Includes exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and artifact verification

- [x] T001 Review feature requirements and data model in `specs/009-deposit-equals-room-fee/spec.md` and `specs/009-deposit-equals-room-fee/data-model.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core model verification that MUST be complete before user story work begins

- [x] T002 Verify `depositAmount` and `roomFee` field accessors in `src/main/java/com/quanlyphongtro/model/Room.java` and `src/main/java/com/quanlyphongtro/model/Contract.java`

**Checkpoint**: Foundation ready - user story implementation can begin

---

## Phase 3: User Story 1 - Auto-assign Deposit Equal to Room Fee Upon Contract & Room Creation (Priority: P1) 🎯 MVP

**Goal**: Automatically default `deposit_amount` equal to `room_fee` when initializing or saving room records and generating lease contracts.

**Independent Test**: Create a room or draft contract with room fee = 2,500,000 VND and verify deposit defaults to 2,500,000 VND.

### Implementation for User Story 1

- [x] T003 [P] [US1] Implement default deposit calculation logic (`deposit_amount = room_fee`) in `src/main/java/com/quanlyphongtro/dao/RoomDAO.java`
- [x] T004 [P] [US1] Update room service validation to enforce positive room fee and equal default deposit in `src/main/java/com/quanlyphongtro/service/impl/RoomServiceImpl.java`
- [x] T005 [P] [US1] Enforce deposit equalization when initializing new contracts in `src/main/java/com/quanlyphongtro/service/impl/ContractServiceImpl.java`
- [x] T006 [US1] Auto-populate default deposit equal to room fee in contract creation form `src/main/webapp/WEB-INF/views/manager/contracts/create.jsp`

**Checkpoint**: At this point, User Story 1 is fully functional and testable independently.

---

## Phase 4: User Story 2 - Consistent Display of Deposit Amount in Manager & Tenant Contract Views (Priority: P2)

**Goal**: Display formatted deposit amount matching the room fee in Manager and Tenant contract detail and print templates.

**Independent Test**: Open contract details as Tenant and Manager and verify Article 2 displays formatted deposit matching room fee.

### Implementation for User Story 2

- [x] T007 [P] [US2] Verify and format deposit display in Manager contract detail view `src/main/webapp/WEB-INF/views/manager/contracts/detail.jsp`
- [x] T008 [P] [US2] Verify and format deposit display in Tenant contract detail view `src/main/webapp/WEB-INF/views/tenant/contracts/detail.jsp`
- [x] T009 [P] [US2] Verify and format deposit display in Manager contract print template `src/main/webapp/WEB-INF/views/manager/contracts/print.jsp`

**Checkpoint**: User Stories 1 and 2 work independently and consistently across all UI views.

---

## Phase 5: User Story 3 - Synchronization When Room Fee is Updated (Priority: P3)

**Goal**: Synchronize default deposit amount when room rent is updated while preserving historic signed contract deposit snapshots.

**Independent Test**: Update a room's fee and confirm new contracts inherit the new fee as default deposit while old contracts remain unchanged.

### Implementation for User Story 3

- [x] T010 [US3] Ensure room fee update operations in `src/main/java/com/quanlyphongtro/dao/RoomDAO.java` update default deposit for future contracts without affecting signed historical records in `src/main/java/com/quanlyphongtro/dao/ContractDAO.java`

**Checkpoint**: All user stories are independently functional and synchronized.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final verification and end-to-end testing

- [x] T011 [P] Execute end-to-end scenario validation following `specs/009-deposit-equals-room-fee/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - Sequential priority order: US1 (P1) → US2 (P2) → US3 (P3)
- **Polish (Phase 6)**: Depends on all user stories being complete

### Parallel Opportunities

- T003, T004, T005 in US1 can be worked on in parallel across DAO and Service files.
- T007, T008, T009 in US2 can be worked on in parallel across different JSP view templates.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 & Phase 2.
2. Implement Phase 3 (US1).
3. Validate US1 independently with room/contract creation.

### Incremental Delivery

1. Deliver US1 → Verify auto-defaulting deposit on room and contract creation (MVP).
2. Deliver US2 → Verify contract details and print formatting.
3. Deliver US3 → Verify room fee updates and historical preservation.
