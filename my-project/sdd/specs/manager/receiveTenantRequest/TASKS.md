# ASKS: Phân chia Chi tiết Đầu Việc - Tiếp nhận và xử lý yêu cầu người thuê

**Total Story Points:** ~96 points (Completed)  
**Sprint Duration:** 2 weeks × 6 sprints = 12 weeks  
**Velocity:** ~16 points/sprint

---

## Epic 1: Database & Entity Design (14 points) - Completed

### Task 1.1: Database Schema Design (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Design `dbo.requests` table columns (`request_id`, `code`, `sender_id`, `category`, `title`, `content`, `status`, `attachment_urls1`, `attachment_urls2`, `assigned_staff_id`, `rejection_reason`, `appoint_schedule`, `created_at`, `updated_at`, `deleted_at`)
- [x] Establish foreign keys linking to users (sender, operator)

---

### Task 1.2: Entity Implementation (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create Request model class
- [x] Map database columns to Java fields (dob, dates, status strings)
- [x] Create RequestDAO for CRUD and status transaction operations

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- [x] Write SQL scripts for requests table creation
- [x] Run migrations and populate seed data

---

### Task 1.4: Validation & Error Handling (3 points)
**Duration:** 1 day  
**Description:**
- [x] Implement validation for fields in controllers
- [x] Restrict disallowed transitions (e.g. modify closed requests)
- [x] Map exception messages to front-end flash alerts

---

### Task 1.5: Audit Logging (2 points)
**Duration:** 1 day  
**Description:**
- [x] Setup log actions for request updates in Servlet

---

## Epic 2: Request Submission (12 points) - Completed

### Task 2.1: Create Request Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `createRequest()` service for Tenants
- [x] Verify tenant is active and automatically resolve facility and room context
- [x] Set initial request status to `PENDING`

---

### Task 2.2: Attachment Handling (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement multipart file upload for completion verification image (`after_images`) in `ManagerTicketsServlet`
- [x] Validate file sizes (up to 10MB) and formats (JPG, PNG, PDF)
- [x] Store file paths as comma-separated values in `attachment_urls2`

---

### Task 2.4: Request API - Create Endpoint (4 points)
**Duration:** 1 day  
**Description:**
- [x] Map servlet endpoints for tenant submissions

---

## Epic 3: Manager Request Management (24 points) - Completed

### Task 3.1: Receive Request Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `receiveTicket(ticketId)` service
- [x] Check status is `PENDING` or `NEW`
- [x] Change status to `RECEIVED`

---

### Task 3.2: Schedule Appointment Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `scheduleTicket(ticketId, appointSchedule)` service
- [x] Check status is active (not resolved/rejected)
- [x] Store appointment schedule time
- [x] Change status to `IN_PROGRESS`

---

### Task 3.3: Reject Request Service (3 points)
**Duration:** 1-2 days  
**Description:**
- [x] Implement `rejectTicket(ticketId, reason)` service
- [x] Require non-empty rejection reason input
- [x] Change status to `REJECTED` and store reason

---

### Task 3.4: Complete Request Service (4 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `completeTicket(ticketId, notes, attachmentUrls2)` service
- [x] Require non-empty completion notes
- [x] Change status to `DONE`

---

### Task 3.5: Get Request List & Detail Service (6 points)
**Duration:** 2 days  
**Description:**
- [x] Implement `getManagerTickets` and `countManagerTickets` in service layer
- [x] Implement pagination (10 records/page), keyword search, filters by status and type
- [x] Implement `getManagerTicketDetail(ticketId)` and verify facility manager scope
- [x] Dynamically generate history timeline nodes based on request statuses

---

### Task 3.6: Manager Servlet Controller (4 points)
**Duration:** 2 days  
**Description:**
- [x] Map `/manager/tickets` and `/manager/tickets/*` in `ManagerTicketsServlet.java`
- [x] Dispatch actions based on path parts (`/receive`, `/reject`, `/schedule`, `/complete`)

---

## Epic 4: Frontend Development (16 points) - Completed

### Task 4.1: Request List View (4 points)
**Duration:** 2 days  
**Description:**
- [x] Create list JSP view `list.jsp` containing tables, pagination, and keyword search
- [x] Support filter by status and sender type (Tenant/Operator)

---

### Task 4.2: Detail and Action Modals (6 points)
**Duration:** 2 days  
**Description:**
- [x] Create detail JSP view `detail.jsp`
- [x] Display timeline history and file attachments
- [x] Create appointment date input form
- [x] Create rejection reason text area form
- [x] Create completion upload form (`enctype="multipart/form-data"`) with notes field

---

## Epic 5: Testing (14 points) - Completed

- [x] Verify status transition rules (PENDING -> RECEIVED -> IN_PROGRESS -> DONE)
- [x] Verify rejection reason validation
- [x] Verify completion notes validation
- [x] Verify security bypass check (unauthorized manager access rejected)

---

## Summary by Sprint

| Sprint | Focus | Status |
|--------|-------|--------|
| Sprint 1 | DB schema, entities, request creation | Completed |
| Sprint 2 | Manager operations (receive, reject, schedule, complete) | Completed |
| Sprint 3 | Timeline generation and scope validation services | Completed |
| Sprint 4 | Frontend list and detail JSP views | Completed |
| Sprint 5 | Multipart validation and upload configs | Completed |
| Sprint 6 | Testing, security audit, deployment | Completed |

---

## Critical State Transitions

```
Story: Tenant submits request
- Created in PENDING status
- Auto-linked to tenant, room, facility

Story: Manager receives request
- PENDING -> RECEIVED

Story: Manager schedules appointment
- RECEIVED -> IN_PROGRESS
- Stores appoint_schedule time

Story: Manager completes request
- IN_PROGRESS -> DONE
- Stores notes and attachment_urls2

Story: Manager rejects request
- PENDING/RECEIVED -> REJECTED
- Stores rejection_reason
```
