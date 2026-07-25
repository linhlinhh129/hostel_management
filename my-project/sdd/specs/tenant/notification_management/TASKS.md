# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Thông báo (Tenant)

**Date:** 2026-06-21  
**Total Story Points:** ~32 points  
**Sprint Duration:** 2 weeks × 2 sprints = 4-5 weeks  
**Velocity:** ~16 points/sprint

---

## Epic 1: Backend Services (10 points)

### Task 1.1: Database & Data Model Analysis (1 point)
**Priority:** HIGH  
**Duration:** 0.5 days  
**Dependencies:** None  
**Assignee:** Backend Lead

**Description:**
- Analyze existing Notification table schema
- Identify columns: notificationId, title, content, createdAt, type (public/private), tenantId
- Verify data exists and is correct
- Document schema version

**Acceptance Criteria:**
- ✅ Schema documented
- ✅ Sample data retrieved
- ✅ Data quality verified

---

### Task 1.2: Notification Service - List (3 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `NotificationService.getNotificationsList(tenantId, page, pageSize)` method
- Support pagination (page, pageSize parameters)
- Filter notifications visible to tenant:
  - Public notifications (send to all)
  - Private notifications for specific tenant
- Sort by createdAt DESC (newest first)
- Handle empty list gracefully

**Technical Requirements:**
- Query should use indexes for performance
- Response time < 300ms (P95)
- Support pageSize 1-100 (default 20)
- Return DTO with: notificationId, title, createdAt

**Acceptance Criteria:**
- ✅ List service returns correct notifications
- ✅ Pagination works (page, pageSize)
- ✅ Public notifications included
- ✅ Private notifications filtered correctly
- ✅ Sorted newest first
- ✅ Performance < 300ms
- ✅ Unit tests ≥ 85% coverage

---

### Task 1.3: Notification Service - Get Detail (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `NotificationService.getNotificationDetail(notificationId, tenantId)` method
- Return full notification content (title, content, createdAt)
- Validate notification visibility:
  - Check if public (visible to all)
  - Check if notification is for this specific tenant
  - Reject if tenant doesn't have access
- Throw exception with 404 error if not found or no access

**Technical Requirements:**
- Check visibility BEFORE returning data (security critical)
- Response time < 300ms (P95)
- Return DTO with: notificationId, title, content, createdAt
- Handle invalid notificationId gracefully

**Acceptance Criteria:**
- ✅ Detail service returns full content for authorized tenant
- ✅ 404 exception for unauthorized access
- ✅ 404 exception for non-existent notification
- ✅ Performance < 300ms
- ✅ Security: no data leakage
- ✅ Unit tests ≥ 85% coverage

---

### Task 1.4: Notification Visibility & Permission Logic (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement `NotificationPermissionChecker.canView(notificationId, tenantId)` method
- Business logic:
  - Public notification → all tenants can view
  - Private notification with tenantId = current tenant → can view
  - Private notification with different tenantId → cannot view
  - Non-existent notification → cannot view
- Create reusable permission validator

**Technical Requirements:**
- Fail-secure approach (deny by default)
- Single database query for efficiency
- Cacheable for performance

**Acceptance Criteria:**
- ✅ Public notifications accessible to all
- ✅ Private notifications only for recipient
- ✅ No cross-tenant data access
- ✅ Consistent logic in both list & detail
- ✅ Unit tests with 10+ scenarios

---

### Task 1.5: Authentication & Authorization Middleware (1 point)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** None  
**Assignee:** Backend Lead

**Description:**
- Verify authentication middleware exists for tenant role
- Ensure endpoints require valid JWT token
- Validate tenantId from token matches request
- Return 401 if not authenticated, 403 if role mismatch

**Acceptance Criteria:**
- ✅ Unauthenticated request → 401
- ✅ Non-tenant role → 403
- ✅ Valid tenant → proceeds to business logic
- ✅ Tests for all scenarios

---

## Epic 2: Controller Servlets (8 points)

### Task 2.1: TenantNotificationListServlet (4 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.2, 1.5  
**Assignee:** Backend Developer

**Description:**
- Tạo `TenantNotificationListServlet` mapped với `@WebServlet("/tenant/notifications")`.
- Đọc tham số `page` (mặc định 1) và `pageSize` (mặc định 10).
- Lấy `tenantId` từ Session.
- Gọi `NotificationService.getNotificationsList()`, gán `request.setAttribute("notificationPage", pageDTO)` và forward sang `/WEB-INF/views/tenant/notification-list.jsp`.

**Acceptance Criteria:**
- ✅ Servlet điều hướng đúng route.
- ✅ Phân trang danh sách thông báo theo tenantId.

---

### Task 2.2: TenantNotificationDetailServlet (4 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.3, 1.4, 1.5  
**Assignee:** Backend Developer

**Description:**
- Tạo `TenantNotificationDetailServlet` mapped với `@WebServlet("/tenant/notification-detail")`.
- Nhận tham số `id`, validate quyền sở hữu thông báo (công khai hoặc khớp với tenantId từ Session).
- Gọi `NotificationService.getNotificationDetail()`, gán `request.setAttribute("notification", dto)` và forward sang `/WEB-INF/views/tenant/notification-detail.jsp`.
- Nếu không có quyền: Forward tới trang lỗi 403 hoặc 404.

---

## Epic 3: Frontend & Views Development (12 points)

### Task 3.1: Notification List JSP View (6 points)
**Priority:** HIGH  
**Duration:** 2.5 days  
**Dependencies:** Task 2.1  
**Assignee:** Frontend Developer

**Description:**
- Tạo file JSP `/WEB-INF/views/tenant/notification-list.jsp`.
- Render danh sách thông báo (Tiêu đề, ngày tạo formatted).
- Tích hợp thanh phân trang (Pagination).
- Hiển thị Empty State khi không có thông báo ("Hiện chưa có thông báo nào.").

---

### Task 3.2: Notification Detail JSP View (6 points)
**Priority:** HIGH  
**Duration:** 2.5 days  
**Dependencies:** Task 2.2  
**Assignee:** Frontend Developer

**Description:**
- Tạo file JSP `/WEB-INF/views/tenant/notification-detail.jsp`.
- Render đầy đủ nội dung thông báo, tiêu đề, ngày tạo.
- Nút "Quay lại" dẫn tới `/tenant/notifications`.

---

## Epic 4: Testing & QA (2 points)

### Task 4.1: Unit & Integration Tests - Backend (1 point)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Tasks 1.1-2.4  
**Assignee:** QA / Backend

**Description:**
- Unit tests for services (getList, getDetail)
- Unit tests for permission checker
- Integration tests for APIs
- Test with mock data covering:
  - Public notifications
  - Private notifications
  - Cross-tenant access (should deny)
  - Pagination edge cases
  - Error scenarios

**Test Coverage Target:**
- ✅ Services: ≥ 85%
- ✅ APIs: ≥ 80%
- ✅ Permission logic: 100%

**Test Scenarios:**
- Normal flow (list, detail)
- Unauthorized access (denied)
- Invalid parameters
- Empty list
- Large pagination
- Concurrent requests

---

### Task 4.2: Frontend Component & Integration Tests (0.5 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Tasks 3.1-3.6  
**Assignee:** QA / Frontend

**Description:**
- Component tests for list, detail, pagination
- Mock API responses
- Test user interactions:
  - Click row → navigate to detail
  - Click page number → fetch page
  - Click back → return to list
  - Click retry → retry failed request
- Test responsive design breakpoints

**Test Coverage:**
- List component: ≥ 75%
- Detail component: ≥ 75%

---

### Task 4.3: End-to-End & UAT (0.5 point)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** All backend & frontend tasks  
**Assignee:** QA

**Description:**
- E2E test scenarios:
  1. Login → List notifications → View detail → Back
  2. Pagination navigation
  3. Error state & recovery
  4. Cross-tenant access attempt (should fail)
  5. Empty state
  6. Mobile responsive view
- User acceptance testing with stakeholders
- Performance testing (< 300ms)
- Security testing (no data leakage)

**Success Criteria:**
- ✅ All E2E tests pass
- ✅ Product owner signs off
- ✅ No critical bugs
- ✅ Performance meets targets
- ✅ No security issues

---

## Summary by Sprint

| Sprint | Focus | Points | Duration |
|--------|-------|--------|----------|
| **Sprint 1** | Design + Backend Services + APIs | 16 | Weeks 1-2 |
| **Sprint 2** | Frontend + Testing + Deployment | 16 | Weeks 3-4 |

---

## Task Dependencies Graph

```
1.1 (Analysis)
 ↓
1.2 (List Service) ──→ 2.1 (List API) ──→ 3.2 (List UI)
1.3 (Detail Service) → 2.2 (Detail API) → 3.4 (Detail UI)
1.4 (Permissions) ──→ 2.2 (Detail API) ──→ 3.4 (Detail UI)
1.5 (Auth) ─────────→ 2.1, 2.2 APIs
                         ↓
                      2.3 (Error Handling)
                      2.4 (Documentation)
                         ↓
                      3.5 (Pagination)
                      3.6 (Polish)
                         ↓
                      4.1-4.3 (Testing)
```

---

## Definition of Done for Each Task

✅ **Code:**
- Written & peer-reviewed
- Follows project coding standards
- No code smells or technical debt

✅ **Testing:**
- Unit tests pass (≥80% coverage)
- Integration tests pass
- No critical/blocker bugs

✅ **Documentation:**
- Inline code comments (complex logic)
- API documentation (if applicable)
- Task description updated

✅ **QA Sign-off:**
- QA reviewed and approved
- Staging environment tested
- Ready for production

✅ **Product Sign-off:**
- Product owner reviewed (if applicable)
- Acceptance criteria met
- Requirements satisfied
