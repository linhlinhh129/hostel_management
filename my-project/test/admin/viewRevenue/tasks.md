# Tasks: View Revenue Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/viewRevenue/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `AdminRevenueServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` `/admin/revenue` (Index) fetches system revenue and 6-month trends.
- [x] T003 Test `doGet` `/admin/revenue/by-facility` fetches paginated facility revenue.
- [x] T004 Test `doGet` `/admin/revenue/by-period` fetches multi-period trends.
- [x] T005 Test `period` parsing logic (converts `YYYY-MM` to `MM/yyyy` correctly).

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test handling of `INVALID_DATE_RANGE` exception from mock service.
- [x] T007 Test security checks: Unauthorized (401/Redirect) and Forbidden (403 for non-admins).
- [x] T008 Test invalid path handling (`/admin/revenue/invalid`) returning 404.

## Phase 4: Boundary Values

- [x] T009 Test empty data handling (mock service returns 0 or empty arrays, UI fallback gracefully).
- [x] T010 Test fallback mechanism when the `period` parameter is completely omitted.
- [x] T011 Test pagination bounds (`page=-1` or invalid strings falling back to 1).

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` to test high-concurrency read requests to verify `AdminRevenueServlet` thread safety.
