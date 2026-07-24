# Tasks: System Config Test (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/configSystem/`
**Prerequisites**: plan.md, spec.md

## Phase 1: Setup

**Purpose**: Test infrastructure setup.

- [x] T001 Initialize Unit Test base structure for `AdminSystemConfigServletTest`, `EmailConfigDTOTest`, `VNPayConfigDTOTest`

## Phase 2: Happy Path & DTOs 🎯 MVP

**Goal**: Đảm bảo DTO masking đúng và Servlet xử lý form config thành công.

- [x] T002 [US1] Implement password and secretKey masking tests in `EmailConfigDTOTest` and `VNPayConfigDTOTest`
- [x] T003 [US1] Create test cases for displaying config forms (GET) in `AdminSystemConfigServletTest`
- [x] T004 [US1] Create test cases for successful Email/VNPay update flows (POST) in `AdminSystemConfigServletTest`

## Phase 3: Error Cases (Unwanted)

**Goal**: Đảm bảo validate form chặn lưu rác và báo lỗi chuẩn.

- [x] T005 [US2] Create test cases for missing required fields returning validation errors
- [x] T006 [US2] Create test cases for invalid port format (non-integer)
- [x] T007 [US2] Create test cases for DB Exception handling from mocked Service
- [x] T008 [US2] Create test cases for FORBIDDEN access (non-admin role)

## Phase 4: Boundary Values

**Goal**: Xử lý các giá trị biên của cấu hình.

- [x] T009 [US3] Create test cases for boundary ports (1, 65535, 0, 65536)
- [x] T010 [US3] Create test cases for whitespace-only input validation
- [x] T011 [US3] Create test cases for excessively long string inputs

## Phase 5: Concurrent Scenarios

**Goal**: Đảm bảo luồng an toàn khi cập nhật cấu hình đồng thời.

- [x] T012 [US4] Setup `ExecutorService` with 20 threads pushing POST config updates concurrently
- [x] T013 [US4] Test read-write conflict (GET and POST simultaneously) using mock assertions
