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

## Epic 2: REST APIs (8 points)

### Task 2.1: Get Notifications List Endpoint (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 1.2, 1.5  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/tenant/notifications`
- Query parameters: `page` (default 1), `pageSize` (default 20, max 100)
- Call NotificationService.getNotificationsList()
- Return paginated response with proper HTTP status

**Request:**
```
GET /api/v1/tenant/notifications?page=1&pageSize=20
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "page": 1,
  "pageSize": 20,
  "totalItems": 42,
  "items": [
    {
      "notificationId": 1,
      "title": "Thông báo bảo trì hệ thống nước",
      "createdAt": "2026-06-10T08:00:00"
    }
  ]
}
```

**Error Responses:**
- 400: Invalid pagination parameters
- 401: Unauthorized
- 403: Forbidden (non-tenant)
- 500: Server error

**Acceptance Criteria:**
- ✅ Valid parameters → 200 with data
- ✅ Invalid page/pageSize → 400
- ✅ No auth header → 401
- ✅ Non-tenant role → 403
- ✅ Response matches spec format
- ✅ Tests with multiple scenarios

---

### Task 2.2: Get Notification Detail Endpoint (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 1.3, 1.4, 1.5  
**Assignee:** Backend Developer

**Description:**
- Create endpoint: `GET /api/v1/tenant/notifications/{notificationId}`
- Path parameter: `notificationId` (must be positive integer)
- Call NotificationService.getNotificationDetail()
- Implement permission check
- Return full notification or error

**Request:**
```
GET /api/v1/tenant/notifications/1
Authorization: Bearer <token>
```

**Success Response (200 OK):**
```json
{
  "notificationId": 1,
  "title": "Thông báo bảo trì hệ thống nước",
  "content": "Hệ thống nước sẽ được bảo trì từ 08:00 đến 12:00 ngày 15/06/2026.",
  "createdAt": "2026-06-10T08:00:00"
}
```

**Error Responses:**
- 400: Invalid notificationId format
- 401: Unauthorized
- 403: Forbidden (non-tenant)
- 404: Notification not found or no access
- 500: Server error

**404 Response:**
```json
{
  "error": "Thông báo không tồn tại hoặc bạn không có quyền truy cập."
}
```

**Acceptance Criteria:**
- ✅ Valid notification owned by tenant → 200
- ✅ Valid notification not owned by tenant → 404
- ✅ Non-existent notification → 404
- ✅ Invalid notificationId → 400
- ✅ No auth → 401
- ✅ Non-tenant role → 403
- ✅ Response matches spec format
- ✅ Comprehensive test coverage

---

### Task 2.3: Error Response & Exception Handling (2 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 2.1, 2.2  
**Assignee:** Backend Developer

**Description:**
- Create consistent error response format
- Implement exception handlers for:
  - NotificationNotFoundException (404)
  - PermissionDeniedException (403)
  - ValidationException (400)
  - InternalException (500)
- Add logging for errors
- Return proper HTTP status codes

**Standard Error Format:**
```json
{
  "statusCode": 404,
  "message": "Thông báo không tồn tại hoặc bạn không có quyền truy cập.",
  "timestamp": "2026-06-21T10:30:00Z"
}
```

**Acceptance Criteria:**
- ✅ All errors return consistent format
- ✅ Correct HTTP status codes
- ✅ Helpful error messages in Vietnamese
- ✅ No sensitive info exposed
- ✅ All scenarios tested

---

### Task 2.4: API Documentation & Contract (1 point)
**Priority:** MEDIUM  
**Duration:** 0.5 days  
**Dependencies:** Task 2.1, 2.2, 2.3  
**Assignee:** Tech Lead

**Description:**
- Generate Swagger/OpenAPI documentation
- Document both endpoints:
  - List endpoint with pagination
  - Detail endpoint with permission check
- Include request/response examples
- Document error codes and meanings
- Create API specification document

**Deliverables:**
- Swagger YAML/JSON file
- API spec document (Markdown)
- Request/response examples

**Acceptance Criteria:**
- ✅ All endpoints documented
- ✅ Clear parameter descriptions
- ✅ Examples match actual responses
- ✅ Error codes explained
- ✅ Ready for frontend integration

---

## Epic 3: Frontend Development (12 points)

### Task 3.1: Notification List Page - Component Setup (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1, 2.4  
**Assignee:** Frontend Developer

**Description:**
- Create NotificationList component/page
- Setup page structure:
  - Header with page title
  - Content area for notification list
  - Pagination controls
  - Loading/error states
- Initialize routing to this page
- Setup API integration skeleton

**UI Layout:**
```
┌──────────────────────────────────┐
│ Quản lý Thông báo                │
├──────────────────────────────────┤
│ [Loading State or List]           │
│                                  │
│                                  │
├──────────────────────────────────┤
│ < 1 | 2 | 3 | ... | 42 >         │
└──────────────────────────────────┘
```

**Acceptance Criteria:**
- ✅ Component renders without errors
- ✅ Page layout matches design
- ✅ Pagination controls present
- ✅ Loading/error state areas prepared
- ✅ Routing configured

---

### Task 3.2: Notification List Display (3 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 3.1, 2.1  
**Assignee:** Frontend Developer

**Description:**
- Implement notification list table/card display:
  - Column 1: Title
  - Column 2: Created Date (formatted)
  - Row clickable to view detail
  - Visual feedback on hover
- Call `/api/v1/tenant/notifications` API
- Display notifications from response
- Handle pagination navigation
- Sort newest first (already done on backend)

**Each Row Shows:**
- Title (clickable)
- Date formatted: "21/06/2026 08:00"
- Hover effect (background color change)

**Requirements:**
- Responsive design (mobile-friendly)
- Click → navigate to detail page
- Support pagination (page numbers)
- Loading spinner while fetching

**Acceptance Criteria:**
- ✅ List displays correctly
- ✅ Date formatted properly (Vietnamese locale)
- ✅ Clickable rows navigate to detail
- ✅ Pagination working
- ✅ Loading indicator shown
- ✅ Responsive on mobile/tablet/desktop

---

### Task 3.3: Empty State & Error States (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 3.2  
**Assignee:** Frontend Developer

**Description:**
- Implement Empty State:
  - Show when API returns 0 items
  - Message: "Hiện chưa có thông báo nào."
  - Optional icon/illustration
  - Center aligned
- Implement Error State:
  - Show when API fails (4xx/5xx)
  - Error message displayed
  - "Retry" button to reload
  - Connection error handling

**Empty State:**
```
┌──────────────────────────────────┐
│                                  │
│       [📬 Icon]                   │
│  Hiện chưa có thông báo nào.    │
│                                  │
└──────────────────────────────────┘
```

**Error State:**
```
┌──────────────────────────────────┐
│       Tải dữ liệu thất bại        │
│                                  │
│  [⚠️ Icon]                        │
│  Vui lòng thử lại sau.           │
│  [Tải lại]                       │
└──────────────────────────────────┘
```

**Acceptance Criteria:**
- ✅ Empty state shows when no items
- ✅ Error state shows on API error
- ✅ Retry button reloads data
- ✅ Messages clear and helpful
- ✅ Styling consistent with design

---

### Task 3.4: Notification Detail Page (3 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 2.2, 2.4  
**Assignee:** Frontend Developer

**Description:**
- Create NotificationDetail component/page
- Display full notification:
  - Title (large, bold)
  - Created date/time
  - Full content (paragraphs, formatting preserved)
  - Back button to return to list
- Call `/api/v1/tenant/notifications/{notificationId}` API
- Handle loading state while fetching
- Handle error state (404, 403, 500)

**Detail Page Layout:**
```
┌──────────────────────────────────┐
│ < Quay lại                       │
├──────────────────────────────────┤
│ Tiêu đề thông báo                │
│                                  │
│ 21/06/2026 08:00                 │
│                                  │
├──────────────────────────────────┤
│ Nội dung đầy đủ của thông báo... │
│                                  │
│ Có thể là nhiều dòng, nhiều      │
│ đoạn văn tùy theo nội dung       │
│                                  │
│                                  │
└──────────────────────────────────┘
```

**Error Handling:**
- 401 → Redirect to login
- 403 → Show "Bạn không có quyền truy cập."
- 404 → Show "Thông báo không tồn tại hoặc bạn không có quyền truy cập."
- 500 → Show error + Retry button

**Acceptance Criteria:**
- ✅ Page displays notification content
- ✅ Date formatted correctly
- ✅ Content formatting preserved
- ✅ Back button works
- ✅ Loading state shown
- ✅ Error states handled
- ✅ 404 message user-friendly

---

### Task 3.5: Pagination Component & Navigation (2 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 3.2  
**Assignee:** Frontend Developer

**Description:**
- Create reusable Pagination component
- Display page numbers: 1, 2, 3... N
- Show current page highlighted
- Previous/Next buttons
- Jump to page functionality
- Update URL/state when page changes
- Re-fetch data when page changes

**Pagination UI:**
```
< Previous | 1 | 2 | 3 | ... | 10 | Next >
           ↑ Current page highlighted
```

**Features:**
- Disable Previous on page 1
- Disable Next on last page
- Click page number → fetch that page
- Preserve scroll position
- URL state (optional: ?page=2)

**Acceptance Criteria:**
- ✅ Pagination renders correctly
- ✅ Navigation between pages works
- ✅ Data re-fetches on page change
- ✅ Current page highlighted
- ✅ Next/Previous buttons disable appropriately
- ✅ Responsive on mobile

---

### Task 3.6: UI Polish & Responsive Design (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 3.1, 3.2, 3.4, 3.5  
**Assignee:** Frontend Developer

**Description:**
- Review styling consistency across pages
- Ensure responsive design:
  - Mobile (320px - 480px)
  - Tablet (481px - 1024px)
  - Desktop (1025px+)
- Test on various screen sizes
- Optimize typography & spacing
- Ensure accessibility (WCAG basics)
- Cross-browser testing

**Checklist:**
- ✅ Consistent color scheme
- ✅ Font sizes readable
- ✅ Touch targets ≥ 44px on mobile
- ✅ No horizontal scroll
- ✅ Tables/content scrollable on small screens
- ✅ Dark mode compatibility (if applicable)

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
