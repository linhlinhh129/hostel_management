# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Thông báo (Tenant)

**Total Story Points:** ~28 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~9.3 points/sprint

---

## Epic 1: Backend Services (8 points)

### Task 1.1: List Notifications Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getNotifications(tenantId) service
- Include public notifications
- Include tenant-specific notifications
- Support pagination
- Order by date descending

---

### Task 1.2: Get Notification Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getNotification(notificationId, tenantId) service
- Validate visibility (public or for this tenant)
- Return full details

---

### Task 1.3: Notification Visibility Logic (2 points)
**Duration:** 1 day  
**Description:**
- Implement visibility check method
- Public notifications visible to all
- Targeted visible only to recipients

---

## Epic 2: APIs (6 points)

### Task 2.1: List Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/notifications
- Support pagination
- Implement DTO

---

### Task 2.2: Detail Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/notifications/{id}
- Implement DTO

---

### Task 2.3: Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- Standard response format
- Error handling

---

## Epic 3: Frontend Development (12 points)

### Task 3.1: Notification List Page (5 points)
**Duration:** 2-3 days  
**Description:**
- Create table: title, type, createdDate
- Pagination
- Search by title
- Click to view detail
- Empty state

---

### Task 3.2: Notification Detail View (4 points)
**Duration:** 2 days  
**Description:**
- Display full content (read-only)
- Show metadata (creator, date)
- Back button to list
- Show notification type

---

### Task 3.3: UI Polish (3 points)
**Duration:** 1-2 days  
**Description:**
- Review consistency
- Test responsive design
- Format long content

---

## Epic 4: Testing (2 points)

### Task 4.1: Unit & Integration Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test services & APIs
- >= 80% coverage

---

### Task 4.2: E2E & UAT (1 point)
**Duration:** 1 day  
**Description:**
- E2E tests
- Manual UAT

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 10 | List, detail services, API |
| Sprint 2 | 9 | Frontend list |
| Sprint 3 | 9 | Frontend detail, testing |

---

## Critical Dependencies

- Task 1.1-1.3 → Task 2.1-2.3 (services needed for API)
- Task 2.1-2.3 → Task 3.1-3.3 (API needed for frontend)
