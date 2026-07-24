# Tasks: Tenant Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/tenantManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `ManagerTenantsServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Tenants List successfully.
- [x] T003 Test `doGet` View Tenant Detail successfully.
- [x] T004 Test `doPost` Update Tenant Profile successfully.
- [x] T005 Test `doPost` Lock Tenant successfully.
- [x] T006 Test `doPost` Unlock Tenant successfully.
- [x] T007 Test `doPost` End Rental successfully (free room).

## Phase 3: Error Cases (Unwanted)

- [x] T008 Test `doGet` Create Tenant Directly Redirects to Contracts.
- [x] T009 Test `doPost` Action Cross-Facility returns 403 Forbidden.
- [x] T010 Test `doPost` Update Profile with Duplicate Email/CCCD fails.
- [x] T011 Test `doPost` Update Profile with Invalid Format fails.
- [x] T012 Test `doPost` End Rental before Contract Date fails.

## Phase 4: Boundary Values

- [x] T013 Test `doPost` Update Profile with max Length Boundaries.

## Phase 5: Concurrent Scenarios

- [x] T014 Setup `ExecutorService` to verify End Rental and Lock Race Condition.
