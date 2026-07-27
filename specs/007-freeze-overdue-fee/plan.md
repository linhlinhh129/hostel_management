# Implementation Plan: Freeze Overdue Fee

**Branch**: `007-freeze-overdue-fee` | **Date**: 2026-07-25 | **Spec**: [spec.md](file:///D:/FPT_University/Semester_5/dinh_SWP391/github/hostel_management/specs/007-freeze-overdue-fee/spec.md)

**Input**: Feature specification from `/specs/007-freeze-overdue-fee/spec.md`

## Summary

This plan outlines the technical approach to "freeze" the overdue penalty fee on an invoice when the tenant has created a payment that is currently in `PENDING` state.

## Technical Context

The logic for calculating the late fee is executed on-the-fly via the DAO layer (`DebtDAO` and `InvoiceDAO`). Instead of hardcoding `LocalDate.now()` as the end date for calculating the number of overdue days, the system retrieves the creation date of the latest `PENDING` payment for that invoice.

If a `PENDING` payment exists, the system uses its `created_at` timestamp as the end date.
If no `PENDING` payment exists (either none created, or the previous one was `REJECTED`), it falls back to `LocalDate.now()`.

## Phase 0: Research
- **Decision:** Use a subquery in the DAOs to fetch `pending_payment_date`.
- See [research.md](file:///D:/FPT_University/Semester_5/dinh_SWP391/github/hostel_management/specs/007-freeze-overdue-fee/research.md) for full context.

## Phase 1: Data Model & Contracts
- Data model remains unchanged. Logic operates on `invoices` and `payments` tables.
- See [data-model.md](file:///D:/FPT_University/Semester_5/dinh_SWP391/github/hostel_management/specs/007-freeze-overdue-fee/data-model.md).
- See [quickstart.md](file:///D:/FPT_University/Semester_5/dinh_SWP391/github/hostel_management/specs/007-freeze-overdue-fee/quickstart.md) for validation scenarios.

## Execution

Actually, the code changes for this feature have **already been fully implemented** in previous iterations within `DebtDAO.java` and `InvoiceDAO.java`. The `pending_payment_date` logic is already in place. The planning phase acts as a formalization of this already active logic.
