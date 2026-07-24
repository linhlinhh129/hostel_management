# Tasks: Audit Logs Test (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/auditLogs/`
**Prerequisites**: plan.md, spec.md

## Phase 1: Setup

**Purpose**: Test infrastructure setup.

- [x] T001 Initialize Unit Test base structure for `AdminAuditLogServletTest`

## Phase 2: Happy Path 🎯 MVP

**Goal**: Đảm bảo Controller `AdminAuditLogServlet` trả về dữ liệu chuẩn, parse tham số filter đúng.

- [x] T002 [US1] Create test cases for successful `doGet` without filters in `AdminAuditLogServletTest`
- [x] T003 [US1] Create test cases for `doGet` with action/entityType filters in `AdminAuditLogServletTest`
- [x] T004 [US1] Create test cases for date range and pagination in `AdminAuditLogServletTest`
- [x] T005 [US1] Create test cases for viewing log details by ID in `AdminAuditLogServletTest`

## Phase 3: Error Cases (Unwanted)

**Goal**: Đảm bảo Controller xử lý các ngoại lệ và lỗi HTTP chuẩn xác.

- [x] T006 [US2] Create test cases for INVALID_FILTER and invalid date ranges (400 or safe empty list)
- [x] T007 [US2] Create test cases for UNAUTHORIZED (no session) and FORBIDDEN (non-admin role)
- [x] T008 [US2] Create test cases for NotFoundException (404) and SQLException (500 handling)

## Phase 4: Boundary Values

**Goal**: Xử lý các giá trị biên của hệ thống.

- [x] T009 [US3] Create test cases for `page` < 1 and extremely large `page` values
- [x] T010 [US3] Create test cases for empty/whitespace string filters
- [x] T011 [US3] Create test cases for boundary ID values (negative/zero)

## Phase 5: Concurrent Scenarios

**Goal**: Đảm bảo Servlet/Service an toàn trong môi trường đa luồng (Thread-safety).

- [x] T012 [US4] Setup `ExecutorService` with 50 concurrent threads in `AdminAuditLogServletTest`
- [x] T013 [US4] Test concurrent `doGet` calls to verify no state leakage between requests
