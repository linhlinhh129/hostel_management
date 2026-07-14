# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Thông báo cho Ban quản lý

**Total Story Points:** ~68 points (Completed)  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~13.6 points/sprint

---

## Epic 1: Facility Access Control (16 points) - Completed

### Task 1.1: Facility Access Validation Service (5 points)
**Duration:** 2-3 days  
**Description:**
- [x] Implement checkFacilityAccess(managerId, facilityId) service via `verifyFacilityManager`
- [x] Query `dbo.facilities` table
- [x] Return true if manager has access (matches `manager_id`)

---

### Task 1.2: Manager Facility List Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `getAssignedFacilitiesForManager(managerId)` service
- [x] Return list of facilities assigned to manager
- [x] Used for facility dropdown/filters in UI

---

### Task 1.3: Recipient Resolution Service (5 points)
**Duration:** 2-3 days  
**Description:**
- [x] Implement recipient type checks (FACILITY or ROOM)
- [x] Verify target room is within manager's assigned facilities via `verifyRoomManagerAndGetFacilityId`

---

### Task 1.4: Permission Validation (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement check in Controller for path and resource access
- [x] Restrict access with Forbidden (403) error page if manager accesses foreign notification details

---

## Epic 2: Notification & Debt Reminder Service (20 points) - Completed

### Task 2.1: Create Notification Service (6 points)
**Duration:** 2-3 days  
**Description:**
- [x] Implement general notification creation (`insertNotificationAndGetId`)
- [x] Validate title & content form parameters in servlet
- [x] Validate recipientType (FACILITY or ROOM)
- [x] Store notification with status SENT
- [x] Create audit log upon creation

---

### Task 2.2: Get Notification List Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `getManagerNotifications` with tabs support
- [x] Support pagination
- [x] Support search by title/content
- [x] Filter by facility and tab type (general/received/sent, payment-reminder)

---

### Task 2.3: Get Notification Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `getNotificationDetail(notificationId)` service
- [x] Check manager ownership
- [x] Return detail including recipient name and sender details

---

### Task 2.4: Debt Reminder Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `sendDebtReminder()` service
- [x] Auto-generate notification code `NTF-DEBT-xxx`
- [x] Link to specific room and record with status SENT
- [x] Log in audit log

---

### Task 2.5: Utility Incorrect Reading Transaction (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement transaction logic `sendOperatorRequestTransaction`
- [x] Update meter reading status to REPORTED
- [x] Create a ticket request with category UTILITY and status PENDING assigned to Operator
- [x] Rollback database transaction on SQL errors

---

## Epic 3: Controller & Routing (12 points) - Completed

### Task 3.1: Servlet Mapping and Actions (3 points)
**Duration:** 1 day  
**Description:**
- [x] Map `/manager/notifications` and `/manager/notifications/*` in `ManagerNotificationsServlet.java`
- [x] Implement GET action handlers for list, create form, debt form, operator form, and detail
- [x] Implement POST action submit handlers for create, debt, and operator requests

---

### Task 3.2: CSRF Validation (4 points)
**Duration:** 1-2 days  
**Description:**
- [x] Integrate CSRF Token validation on all POST form submits

---

### Task 3.3: Redirects & Flash Messages (3 points)
**Duration:** 1 day  
**Description:**
- [x] Standardize alerts redirection using flash messages (`setFlashMessage`)
- [x] Handle validation error redirect by preserving inputs

---

## Epic 4: Frontend Development (16 points) - Completed

### Task 4.1: Notification List Page (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create JSP view `list.jsp` with tabs (general, payment-reminder, incorrect-utility)
- [x] Render list table for general notifications and debt reminders
- [x] Render table for reported incorrect invoices in the utility tab
- [x] Implement pagination & keyword search fields

---

### Task 4.2: Create Form JSP (5 points)
**Duration:** 2-3 days  
**Description:**
- [x] Create announcement form at `create.jsp`
- [x] Support recipient type selectors (dropdowns for facilities and active rooms)

---

### Task 4.3: Detail & Actions JSP (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create `detail.jsp` displaying notification parameters
- [x] Create `send_debt_reminder.jsp` for debt reminders
- [x] Create `send_operator.jsp` for sending requests to Operator

---

## Epic 5: Testing & Quality (14 points) - Completed

- [x] Verify checkFacilityAccess behavior
- [x] Verify create and sendDebtReminder services
- [x] Verify database transaction rollback for utility report
- [x] Manual E2E testing of the notification flows

---

## Summary by Sprint

| Sprint | Points | Focus | Status |
|--------|--------|-------|--------|
| Sprint 1 | 14 | Access control, database schema | Completed |
| Sprint 2 | 14 | General, debt, and operator services | Completed |
| Sprint 3 | 14 | Frontend forms and list tabs JSP | Completed |
| Sprint 4 | 14 | Testing & security validations | Completed |
| Sprint 5 | 12 | E2E verification, deployment | Completed |

---

## Critical Dependencies

- Task 1.1 → Task 2.1 (access validation needed for recipient checks)
- Task 2.1-2.5 → Task 3.1 (service functions needed for servlet)
- Task 3.1 → Task 4.1-4.3 (servlet routing needed for frontend JSP pages)
