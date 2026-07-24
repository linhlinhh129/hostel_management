# Tasks: Tenant Dependent Management (Unit Test Only)

**Input**: Design documents from `my-project/test/tenant/dependent_management/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `TenantDependentsServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View My Dependents successfully.
- [x] T003 Test `doGet` View My Dependents empty.
- [x] T004 Test `doGet` View Dependent Detail successfully.
- [x] T005 Test `doGet` View Dependent Detail ensures CCCD is masked.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test `doGet` Cross-Tenant Access (IDOR) returns 403.
- [x] T007 Test `doGet` View Dependent Not Found or Soft Deleted returns 404.
- [x] T008 Test `doPost` Modify Dependent Forbidden returns 403/405.
- [x] T009 Test `doGet` Unauthorized Access returns 401/Redirect.
