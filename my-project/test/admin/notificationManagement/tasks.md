# Tasks: Notification Management Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/admin/notificationManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `AdminNotificationServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doPost` to create a notification successfully (Valid title, content, recipientType).
- [x] T003 Test `doGet` to fetch paginated list of notifications.
- [x] T004 Test `doGet` to fetch detail of a specific valid notification ID.
- [x] T005 Test `doGet` to search with a valid keyword and return filtered data.

## Phase 3: Error Cases (Unwanted)

- [x] T006 Test validation errors on `doPost` for empty title, content, or invalid recipientType.
- [x] T007 Test `doGet` returning 404 NOT_FOUND for an invalid notification ID.
- [x] T008 Test security checks: Unauthorized (401/Redirect) and Forbidden (403) for non-admin users.

## Phase 4: Boundary Values

- [x] T009 Test title length limits (255 valid vs 256 invalid).
- [x] T010 Test content length limits (1000 valid vs 1001 invalid).
- [x] T011 Test pagination boundary logic (page < 0).

## Phase 5: Concurrent Scenarios

- [x] T012 Setup `ExecutorService` with multiple threads to verify the Servlet's Thread-Safety (No state leakage across requests).
