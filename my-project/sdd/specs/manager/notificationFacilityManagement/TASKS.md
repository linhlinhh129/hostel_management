# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Thông báo cho Ban quản lý

**Total Story Points:** ~68 points  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~13.6 points/sprint

---

## Epic 1: Facility Access Control (16 points)

### Task 1.1: Facility Access Validation Service (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement checkFacilityAccess(managerId, facilityId) service
- Query EmployeeFacility table
- Return true if manager has access
- Return false otherwise
- Cache results for performance

---

### Task 1.2: Manager Facility List Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getManagerFacilities(managerId) service
- Return list of facilities assigned to manager
- Used for facility selector in UI

---

### Task 1.3: Recipient Resolution Service (5 points)
**Duration:** 2-3 days  
**Description:**
- Implement resolveRecipients(recipientType, recipientIds, managerId) service
- For FACILITY: Get tenants from facility
- For ROOM: Get tenant from room
- Validate manager has access
- Return list of recipient IDs

---

### Task 1.4: Permission Validation Interceptor (3 points)
**Duration:** 1-2 days  
**Description:**
- Create AOP aspect for facility access validation
- Apply to manager notification endpoints
- Return 403 for unauthorized access

---

## Epic 2: Notification Management Service (20 points)

### Task 2.1: Create Notification Service (6 points)
**Duration:** 2-3 days  
**Description:**
- Implement createNotification() service
- Validate title & content not empty
- Validate recipientType (FACILITY or ROOM, not ALL)
- Validate manager has access to facility/room
- Resolve actual recipients
- Store notification with status SENT
- Create audit log

**Error Codes:** 
- VALIDATION_ERROR
- RECIPIENT_REQUIRED
- MANAGER_GLOBAL_NOTIFICATION_NOT_ALLOWED
- FACILITY_ACCESS_DENIED

---

### Task 2.2: Get Notification List Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement getNotifications(managerId, facilityId) service
- Support pagination
- Support search by title/content
- Filter by facility (if manager assigned to multiple)
- Return only manager's notifications

---

### Task 2.3: Get Notification Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getNotificationDetail(notificationId, managerId) service
- Check access permission
- Return full notification info + recipient list
- Handle NOTIFICATION_NOT_FOUND & NOTIFICATION_ACCESS_DENIED

---

### Task 2.4: Notification API Controller (4 points)
**Duration:** 2 days  
**Description:**
- Create REST controller
- POST /api/v1/manager/notifications (create)
- GET /api/v1/manager/notifications (list)
- GET /api/v1/manager/notifications/{id} (detail)
- Implement DTOs
- Add authorization checks

---

### Task 2.5: Audit Logging (3 points)
**Duration:** 1-2 days  
**Description:**
- Log notification creation
- Log notification access
- Include user, timestamp, facility info

---

## Epic 3: Validation & Error Handling (12 points)

### Task 3.1: Recipient Type Validation (3 points)
**Duration:** 1 day  
**Description:**
- Validate recipientType is FACILITY or ROOM
- Reject ALL recipient type
- Return MANAGER_GLOBAL_NOTIFICATION_NOT_ALLOWED

---

### Task 3.2: Facility Access Validation (4 points)
**Duration:** 1-2 days  
**Description:**
- Implement facility access checks
- Check before creating notification
- Check before listing notifications
- Check before viewing detail

---

### Task 3.3: Data Validation (3 points)
**Duration:** 1 day  
**Description:**
- Validate title not empty
- Validate content not empty (max 5000 chars)
- Validate recipient IDs
- Return proper error messages

---

### Task 3.4: Global Exception Handler (2 points)
**Duration:** 1 day  
**Description:**
- Create exception handler for manager notifications
- Map all error codes
- Proper HTTP status codes

---

## Epic 4: Frontend Development (16 points)

### Task 4.1: Notification List Page (4 points)
**Duration:** 2 days  
**Description:**
- Create table: title, facility, recipientType, createdDate, createdBy, status
- Implement pagination
- Implement search by title
- Implement facility filter (if multiple facilities)
- Action buttons (view, delete)

---

### Task 4.2: Create Notification Form (5 points)
**Duration:** 2-3 days  
**Description:**
- Create form with: title, content, recipientType, recipientIds
- Load manager's facilities on form open
- Implement facility selector (dropdown)
- Implement recipient type selector (FACILITY or ROOM)
- Load rooms when ROOM type selected
- Implement recipient selector
- Show recipient count
- Character counter for content
- Validation on save

---

### Task 4.3: Notification Detail Page (4 points)
**Duration:** 2 days  
**Description:**
- Display notification info (read-only)
- Show facility, recipientType, recipient count
- Display recipient list
- Show metadata (creator, timestamp)
- Back button to list

---

### Task 4.4: Search & Filter UI (3 points)
**Duration:** 1 day  
**Description:**
- Create search input (title)
- Create facility filter
- Implement clear/reset button

---

## Epic 5: Testing & Quality (14 points)

### Task 5.1: Unit Tests - Access Control (3 points)
**Duration:** 1 day  
**Description:**
- Test facility access validation
- Test permission checks
- Test error codes

---

### Task 5.2: Unit Tests - Services (3 points)
**Duration:** 1 day  
**Description:**
- Test create service with various scenarios
- Test recipient resolution
- Test validation

---

### Task 5.3: Integration Tests (4 points)
**Duration:** 2 days  
**Description:**
- Test API endpoints
- Test permission enforcement
- Test complete workflows
- Test error scenarios

---

### Task 5.4: E2E & Security Testing (4 points)
**Duration:** 2 days  
**Description:**
- E2E tests for manager flows
- Security testing (permission bypass attempts)
- Performance testing (< 500ms)
- UAT with actual managers

---

## Epic 6: Documentation (4 points)

### Task 6.1: API Documentation (2 points)
**Duration:** 1 day  
**Description:**
- Generate Swagger docs
- Document all endpoints
- Document error codes
- Add examples

---

### Task 6.2: Deployment (2 points)
**Duration:** 1 day  
**Description:**
- Deploy code
- Setup monitoring
- Verify go-live

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | Facility access control, validation |
| Sprint 2 | 14 | Create, list, detail services |
| Sprint 3 | 14 | Frontend forms, list page |
| Sprint 4 | 14 | Testing, integration |
| Sprint 5 | 12 | E2E testing, deployment |

---

## Critical Dependencies

- Task 1.1 → Task 1.3 (access validation needed for recipient resolution)
- Task 1.3 → Task 2.1 (recipient resolution needed for create)
- Task 2.1-2.4 → Task 3.1-3.4 (validation & error handling)
- Task 2.1-2.4 → Task 4.2 (API needed for frontend form)
- Task 4.1-4.3 → Task 5.4 (frontend needed for E2E)

---

## Key Test Scenarios

1. Manager creates notification for assigned facility (PASS)
2. Manager tries to create for non-assigned facility (403 - FACILITY_ACCESS_DENIED)
3. Manager tries to create with ALL type (403 - MANAGER_GLOBAL_NOTIFICATION_NOT_ALLOWED)
4. Manager lists only notifications from assigned facilities
5. Manager cannot view notifications from other facilities (403)
6. Recipient resolution works correctly for FACILITY type
7. Recipient resolution works correctly for ROOM type
