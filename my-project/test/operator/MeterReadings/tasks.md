# Tasks: MeterReading History Tests (Unit Test Only)

**Input**: Design documents from `my-project/test/operator/MeterReadings/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [x] T001 Initialize Unit Test base structure for `MeterReadingHistoryServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [x] T002 Test `doGet` successfully fetching history for the current month when no params are provided.
- [x] T003 Test `doGet` successfully parsing specific `month` and `year` query parameters.

## Phase 3: Error Cases (Unwanted)

- [x] T004 Test handling of invalid numeric formats (e.g. `month=abc`) falling back gracefully to default values.
- [x] T005 Test handling of invalid month ranges (e.g. `month=13`).
- [x] T006 Test that `doPost` returns 405 Method Not Allowed.

## Phase 4: Boundary Values

- [x] T007 Test rendering when history records lack image URLs (null/empty values).
- [x] T008 Test empty list behavior for months containing no historical data.

## Phase 5: Concurrent Scenarios

- [x] T009 Setup `ExecutorService` to verify that concurrent GET requests (Read-Only) maintain Thread Safety.
