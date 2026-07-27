# Implementation Plan: operator-utility-request-display

**Branch**: `[###-operator-utility-request-display]` | **Date**: 2026-07-27 | **Spec**: [Spec.md](file:///f:/SU26/New%20folder/hostel_management/my-project/sdd/specs/operator/UtilityRequestDisplay/Spec.md)

**Input**: Feature specification from `my-project/sdd/specs/operator/UtilityRequestDisplay/Spec.md`

## Summary

This feature resolves an issue where "Wrong Utility Number Reports" created by Managers lack Room and Facility mapping in the system. The missing relations cause the Operator's request list and request detail screens to show empty (`—`) room identifiers and untranslated English labels (`UTILITY`). We will fix this by persisting `room_id` and `facility_id` in the `requests` table during report creation, and updating the UI templates to localize the `UTILITY` category.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Servlet API, JSP, JSTL, JDBC

**Storage**: SQL Server (tables: `requests`, `invoices`)

**Testing**: Manual UI verification and End-to-End browser flow

**Project Type**: Monolithic Web Application

## Constitution Check

- Passes all project guidelines.
- No DB migration scripts needed (existing table `requests` already has `room_id` and `facility_id` columns, they just were not being populated).
- Uses standard PreparedStatement methods.

## Proposed Changes

### Database Access Layer (DAO)
#### [MODIFY] [NotificationDAO.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/dao/NotificationDAO.java)
- Update `sendOperatorRequestTransaction` signature to accept `Integer roomId` and `Integer facilityId`.
- Update the `insertReqSql` to: `INSERT INTO dbo.requests (code, sender_id, category, title, content, status, assigned_staff_id, created_at, updated_at, room_id, facility_id) VALUES (?, ?, 'UTILITY', ?, ?, 'PENDING', ?, GETDATE(), GETDATE(), ?, ?)`.
- Set parameters for `roomId` and `facilityId`. If they are null, insert `Types.INTEGER`.

### Service Layer
#### [MODIFY] [NotificationServiceImpl.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/service/impl/NotificationServiceImpl.java)
- In `sendOperatorRequest`, extract `roomId` (via `roomCode` lookup if needed, or by ensuring `getInvoiceDetailsForSendOperator` returns `roomId`) and `facilityId`.
  - Looking at `NotificationDAO.getInvoiceDetailsForSendOperator`, it currently selects `f.facility_id` and `r.room_code`, but does it select `r.room_id`? 
  - Actually, let's verify if `roomId` is returned by `getInvoiceDetailsForSendOperator`. If not, we can add it to the SQL in `NotificationDAO` or query it. Let's just retrieve `roomId` and `facilityId` and pass them down.

### View Layer (JSP)
#### [MODIFY] [list.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/operator/requests/list.jsp)
- Add `<c:when test="${cat == 'UTILITY'}">Điện nước</c:when>` inside the category `<select>` loop.
- Add `<c:when test="${req.category == 'UTILITY'}">SỰ CỐ ĐIỆN NƯỚC</c:when>` in the table row's `<c:choose>`.

#### [MODIFY] [detail.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/operator/requests/detail.jsp)
- No actual changes needed in `detail.jsp` since fixing the database insertion will naturally populate `${reqDetail.roomCode}` and `${reqDetail.facilityName}`.

## Verification Plan

### Manual Verification
1. Login as a Manager.
2. Go to Invoices and Report a Wrong Utility Number to an Operator.
3. Login as the Operator.
4. Go to Requests List. Verify the category shows "SỰ CỐ ĐIỆN NƯỚC" (or Điện Nước) and the room number is visible.
5. Click on the Request detail. Verify the "Phòng / Cơ sở" section shows the correct Room and Facility.
