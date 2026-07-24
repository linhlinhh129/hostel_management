# Tasks: Tenant Contract Management (Unit Test Only)

**Input**: Design documents from `my-project/test/tenant/contract_management/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `TenantContractsServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View My Contracts successfully.
- [x] T003 Test `doGet` View My Contracts when empty.
- [x] T004 Test `doGet` View Contract Detail successfully.

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test `doGet` Cross-Tenant Access returns 403.
- [x] T006 Test `doGet` View Contract Not Found returns 404.
- [x] T007 Test `doPost` Update Contract Forbidden returns 403/405.
- [x] T008 Test `doGet` Unauthorized Access returns 401/Redirect.
