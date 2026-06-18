# ASKS: Phân chia Chi tiết Đầu Việc - Tiếp nhận và xử lý yêu cầu người thuê

**Total Story Points:** ~96 points  
**Sprint Duration:** 2 weeks × 6 sprints = 12 weeks  
**Velocity:** ~16 points/sprint

---

## Epic 1: Database & Entity Design (14 points)

### Task 1.1: Database Schema Design (3 points)
**Duration:** 1-2 days  
**Description:**
- Design Request table (id, title, description, category, status, tenantId, roomId, facilityId, assignedStaffId, rejectionReason, createdAt, updatedAt)
- Design RequestHistory table (id, requestId, oldStatus, newStatus, changedBy, changedAt, comment)
- Design RequestAttachment table (id, requestId, fileUrl)
- Create ER diagram

---

### Task 1.2: Entity Implementation (4 points)
**Duration:** 2 days  
**Description:**
- Create Request JPA entity
- Create RequestHistory JPA entity
- Create RequestAttachment JPA entity
- Setup relationships
- Create repositories

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- Write migration scripts
- Test migration & rollback

---

### Task 1.4: Validation & Error Handling (3 points)
**Duration:** 1 day  
**Description:**
- Implement validation for each field
- Create custom validators (status transitions)
- Map error codes

---

### Task 1.5: Audit Logging (2 points)
**Duration:** 1 day  
**Description:**
- Setup RequestHistory logging
- Implement automatic history recording on status changes

---

## Epic 2: Request Submission (12 points)

### Task 2.1: Create Request Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement createRequest() service
- Validate tenant is ACTIVE
- Auto-link to room, facility
- Extract tenant from context
- Validate required fields
- Create audit log
- Return NEW status

**Error Codes:** TENANT_NOT_FOUND, TENANT_NOT_ACTIVE, VALIDATION_ERROR, REQUEST_VALIDATION_ERROR

---

### Task 2.2: Attachment Handling (4 points)
**Duration:** 2 days  
**Description:**
- Implement file upload to cloud storage
- Store file URL in RequestAttachment
- Validate file type/size
- Limit number of attachments

---

### Task 2.3: Auto-Linking Tenant Context (2 points)
**Duration:** 1 day  
**Description:**
- Extract tenant from JWT token
- Get current room from tenant
- Get facility from room
- Link automatically to request

---

### Task 2.4: Request API - Create Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- POST /api/v1/requests
- Implement request DTO
- Call createRequest service
- Return response

---

## Epic 3: Manager Request Management (24 points)

### Task 3.1: Receive Request Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement receiveRequest(requestId, managerId) service
- Check status is NEW
- Change status to RECEIVED
- Create history entry

**Error Codes:** INVALID_REQUEST_STATUS

---

### Task 3.2: Assign Staff Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement assignStaff(requestId, staffId, managerId) service
- Check status is RECEIVED
- Validate staff exists
- Check staff belongs to same facility
- Change status to ASSIGNED
- Store staff assignment
- Create history entry

**Error Codes:** INVALID_REQUEST_STATUS, STAFF_NOT_FOUND, STAFF_ACCESS_DENIED

---

### Task 3.3: Reject Request Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement rejectRequest(requestId, reason, managerId) service
- Check status is NEW or RECEIVED
- Validate reason provided
- Change status to REJECTED
- Store reason
- Create history entry

---

### Task 3.4: Get Request List Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement getRequests(facilityId, filters) service
- Support pagination
- Support search by keyword
- Support filter by status, category, facility, room, assignedStaff
- Return summary + history

---

### Task 3.5: Get Request Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getRequestDetail(requestId) service
- Return full request info
- Include full history
- Include attachments
- Handle REQUEST_NOT_FOUND

---

### Task 3.6: Update Status Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement updateRequestStatus(requestId, newStatus, staffId) service
- Validate assigned staff matches
- Validate status transition
- ASSIGNED → IN_PROGRESS
- IN_PROGRESS → RESOLVED
- Create history entry

---

### Task 3.7: Manager APIs (4 points)
**Duration:** 2 days  
**Description:**
- PUT /api/v1/requests/{id}/receive
- PUT /api/v1/requests/{id}/assign
- PUT /api/v1/requests/{id}/reject
- PUT /api/v1/requests/{id}/status
- GET /api/v1/requests (list)
- GET /api/v1/requests/{id} (detail)
- Implement DTOs
- Add authorization

---

## Epic 4: Staff Operations (12 points)

### Task 4.1: Staff Status Update Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement updateByStaff(requestId, newStatus, staffId, comment) service
- Validate staff is assigned
- Validate status transition (ASSIGNED→IN_PROGRESS, IN_PROGRESS→RESOLVED)
- Store comment
- Create history

---

### Task 4.2: Staff Request List Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getAssignedRequests(staffId) service
- Return requests assigned to staff
- Filter by status (defaults to IN_PROGRESS, ASSIGNED)
- Support search & filter

---

### Task 4.3: Staff APIs (3 points)
**Duration:** 1-2 days  
**Description:**
- GET /api/v1/staff/requests (list assigned)
- PUT /api/v1/requests/{id}/update-progress (update status)
- Implement DTOs

---

### Task 4.4: Notification Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement notification triggers on status changes
- Notify tenant on status change
- Notify assigned staff on assignment
- Notify manager on completion
- Async email sending

---

## Epic 5: Frontend - Tenant (16 points)

### Task 5.1: Request Creation Form (5 points)
**Duration:** 2-3 days  
**Description:**
- Create form with: title, description, category, attachments
- Implement category selector (MAINTENANCE, REPAIR, COMPLAINT, OTHER)
- File upload component (multi-file)
- Client-side validation
- Call create API
- Show success/error

---

### Task 5.2: Request Status Tracking (4 points)
**Duration:** 2 days  
**Description:**
- Show my requests page
- Table with: ID, title, status, createdDate, updatedDate
- Implement pagination
- Show request detail (read-only)
- Show status history timeline

---

### Task 5.3: Request Detail View (4 points)
**Duration:** 2 days  
**Description:**
- Display request info (read-only)
- Show status history with timeline
- Show assigned staff info
- Show rejection reason if rejected
- Show attachments

---

### Task 5.4: Notification Badge (3 points)
**Duration:** 1 day  
**Description:**
- Show notification badge when request status changes
- Real-time update (polling or websocket)

---

## Epic 6: Frontend - Manager (18 points)

### Task 6.1: Request Dashboard (5 points)
**Duration:** 2-3 days  
**Description:**
- Create manager dashboard
- Table with: ID, tenant, title, status, category, facility, createdDate
- Implement pagination
- Implement search by keyword
- Implement filters: status, category, facility, room, assignedStaff
- Action buttons: receive, assign, reject, view detail

---

### Task 6.2: Assignment UI (4 points)
**Duration:** 2 days  
**Description:**
- Create assignment dialog
- Staff selector (dropdown from facility)
- Confirmation prompt
- Call assign API on confirm

---

### Task 6.3: Rejection UI (3 points)
**Duration:** 1-2 days  
**Description:**
- Create rejection dialog
- Reason text area
- Validation (required)
- Call reject API on confirm

---

### Task 6.4: Request Detail View (3 points)
**Duration:** 1-2 days  
**Description:**
- Display full request info
- Show status history
- Show assigned staff info
- Show action buttons (receive, assign, reject)
- Editable based on status

---

### Task 6.5: Request History Timeline (3 points)
**Duration:** 1-2 days  
**Description:**
- Create timeline component
- Show all status changes
- Show who made change & when
- Show comments if any

---

## Epic 7: Testing (14 points)

### Task 7.1: Unit Tests - Services (4 points)
**Duration:** 2 days  
**Description:**
- Test request creation
- Test status transitions
- Test validation
- Test permission checks
- Aim for >= 80% coverage

---

### Task 7.2: Integration Tests (4 points)
**Duration:** 2 days  
**Description:**
- Test complete workflow (create → receive → assign → progress → resolved)
- Test rejection flow
- Test validation errors
- Test permission enforcement

---

### Task 7.3: E2E Tests (3 points)
**Duration:** 1-2 days  
**Description:**
- Test tenant creates request
- Test manager receives & assigns
- Test staff updates status
- Test notifications sent

---

### Task 7.4: Security Testing (3 points)
**Duration:** 1 day  
**Description:**
- Test unauthorized status transitions
- Test staff cannot update unassigned requests
- Test facility access control
- Test no data leaks

---

## Epic 8: Documentation & Deployment (6 points)

### Task 8.1: API Documentation (2 points)
**Duration:** 1 day  
**Description:**
- Generate Swagger docs
- Document all endpoints
- Document error codes
- Document state machine

---

### Task 8.2: User Documentation (2 points)
**Duration:** 1 day  
**Description:**
- Tenant guide for submitting requests
- Manager guide for managing requests
- Staff guide for updating status
- Screenshots & examples

---

### Task 8.3: Deployment (2 points)
**Duration:** 1 day  
**Description:**
- Deploy database migrations
- Deploy backend
- Deploy frontend
- Setup monitoring
- Verify go-live

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 16 | DB schema, entities, request creation |
| Sprint 2 | 16 | Manager operations (receive, assign, reject, list) |
| Sprint 3 | 16 | Staff operations, notifications |
| Sprint 4 | 16 | Frontend tenant & manager dashboards |
| Sprint 5 | 16 | Testing, history, timeline |
| Sprint 6 | 16 | E2E testing, deployment |

---

## Critical State Transitions

```
Story: Tenant submits request
- Create in NEW status
- Auto-link tenant/room/facility

Story: Manager receives request
- NEW → RECEIVED

Story: Manager assigns staff
- RECEIVED → ASSIGNED
- Notify staff

Story: Staff updates progress
- ASSIGNED → IN_PROGRESS
- Notify manager/tenant

Story: Staff resolves
- IN_PROGRESS → RESOLVED
- Notify tenant

Story: Manager rejects request
- NEW/RECEIVED → REJECTED
- Store reason
- Notify tenant

All transitions logged with history + audit
```

---

## Test Scenarios

1. ✓ Tenant creates request (auto-linked to tenant/room/facility)
2. ✓ Manager receives request (NEW → RECEIVED)
3. ✓ Manager assigns staff (RECEIVED → ASSIGNED)
4. ✓ Staff updates status to in-progress
5. ✓ Staff resolves request
6. ✓ Manager rejects request with reason
7. ✗ Unassigned staff cannot update request
8. ✗ Invalid status transitions rejected
9. ✗ Staff from wrong facility cannot be assigned
10. ✓ All transitions create history entries
