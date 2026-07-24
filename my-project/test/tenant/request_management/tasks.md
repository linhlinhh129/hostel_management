# Tasks: Tenant Request Management (Unit Test Only)

**Input**: Design documents from `my-project/test/tenant/request_management/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `TenantRequestServletTest` with Mockito.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` View Requests List successfully.
- [x] T003 Test `doGet` View Request Detail successfully.
- [x] T004 Test `doPost` Create Request successfully (PENDING).

## Phase 3: Error Cases (Unwanted)

- [x] T005 Test `doPost` Create Request Missing Title/Content returns 400.
- [x] T006 Test `doPost` Create Request Invalid Category returns 400.
- [x] T007 Test `doPost` Create Request Invalid Attachment Type returns 400.
- [x] T008 Test `doGet` View Request Detail Cross-Tenant/Not Found returns 404.
- [x] T009 Test `doGet` Unauthorized Access returns Redirect/401.

## Phase 4: Boundary Values

- [x] T010 Test `doPost` Create Request Max Attachment Size (>5MB).
