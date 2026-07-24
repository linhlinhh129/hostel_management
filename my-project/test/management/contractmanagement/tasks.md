# Tasks: Contract Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/contractmanagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ContractServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` loads contract list successfully for the assigned facility.
- [x] T003 Test `doPost` creates a contract successfully with valid inputs.
- [x] T004 Test `doPost` adds a tenant account successfully.
- [x] T005 Test `doPost` soft-deletes an `INACTIVE` contract successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doPost` create contract rejects request when required fields are missing.
- [x] T007 Test `doPost` create contract rejects request when room is already rented (has `ACTIVE` contract).
- [x] T008 Test `doPost` create contract rejects request for unauthorized facility access (IDOR).
- [x] T009 Test `doPost` delete contract rejects request when contract is still `ACTIVE`.

## Phase 4: Boundary Values

- [x] T010 Test `doPost` create contract strictly validates CCCD length (9 or 12 digits).
- [x] T011 Test `doPost` create contract accepts identical signed date and start date.

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to verify Double Booking prevention when two managers create a contract for the same room simultaneously.
