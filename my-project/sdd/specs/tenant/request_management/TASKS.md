# TASKS: Phân chia Chi Tiết Đầu Việc - Quản lý Yêu cầu (Tenant)

**Date:** 2026-06-21  
**Total Story Points:** ~48 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Backend Core Services (14 points)

### Task 1.1: Request Data Model & Schema (3 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** None  
**Assignee:** Backend Developer

**Description:**
- Define `requests` table structure
- Define `request_attachments` table structure
- Define `request_status_history` table structure
- Include fields: requestId, tenantId, categoryId, title, content, status, createdAt, updatedAt, deletedAt
- Ensure soft delete support and tenant filtering

**Acceptance Criteria:**
- ✅ Data model documented
- ✅ Schema ready for migration
- ✅ Soft delete and ownership fields included
- ✅ Request categories referenced

---

### Task 1.2: Request Entity Implementation (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1  
**Assignee:** Backend Developer

**Description:**
- Implement Request entity and repository
- Implement RequestAttachment entity and repository
- Implement RequestStatusHistory entity and repository
- Build relations: one request → many attachments/history
- Add tenant ownership field to request

**Acceptance Criteria:**
- ✅ Entities compiled and mapped correctly
- ✅ Repositories support CRUD and tenant queries
- ✅ Relations work with ORM
- ✅ Unit tests validate entity mapping

---

### Task 1.3: Request Validation & Business Rules (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.2  
**Assignee:** Backend Developer

**Description:**
- Validate required fields: title, content
- Validate categoryId exists
- Validate attachment type JPG/JPEG/PNG
- Validate attachment max size 5MB
- Enforce `deleted_at IS NULL`
- Enforce tenant data isolation

**Acceptance Criteria:**
- ✅ Required field validation implemented
- ✅ Error codes REQ_001, REQ_002, REQ_003 returned correctly
- ✅ Soft delete excluded
- ✅ Unauthorized tenant access prevented
- ✅ Unit tests cover invalid scenarios

---

### Task 1.4: Create Request Service (3 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 1.2, 1.3  
**Assignee:** Backend Developer

**Description:**
- Implement createRequest(tenantId, requestDto)
- Generate request code
- Persist request with status `PENDING`
- Associate attachments metadata
- Return created request summary

**Acceptance Criteria:**
- ✅ Request created with PENDING status
- ✅ Attachments stored and linked
- ✅ Returns HTTP 201 structure
- ✅ Data isolation ensures tenant ownership
- ✅ Unit tests pass

---

### Task 1.5: Request List Service (2 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.2, 1.3  
**Assignee:** Backend Developer

**Description:**
- Implement getRequestList(tenantId, page, pageSize, statusFilter, search)
- Filter by tenantId only
- Sort by createdAt DESC
- Return fields: requestId, category, title, status, createdAt
- Support pagination

**Acceptance Criteria:**
- ✅ Returns only current tenant requests
- ✅ Sorted newest first
- ✅ Search and status filter work
- ✅ Pagination works
- ✅ Performance < 500ms
- ✅ Unit tests cover common cases

---

### Task 1.6: Request Detail Service (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.2, 1.3  
**Assignee:** Backend Developer

**Description:**
- Implement getRequestDetail(requestId, tenantId)
- Validate ownership and soft delete
- Return full request detail including attachments and status history
- Return 404 if not found or deleted
- Return 403 if tenant not owner

**Acceptance Criteria:**
- ✅ Detail includes attachments and history
- ✅ Authorization enforced
- ✅ Soft-deleted requests hidden
- ✅ Correct error codes for missing/forbidden
- ✅ Unit tests cover access scenarios

---

## Epic 2: API Endpoints & Error Handling (12 points)

### Task 2.1: List Requests API (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 1.5  
**Assignee:** Backend Developer

**Description:**
- Build `GET /api/v1/tenant/requests`
- Validate JWT auth
- Accept query params: page, pageSize, status, search
- Return success response with request list

**Acceptance Criteria:**
- ✅ Authenticated tenant only
- ✅ Pagination and filters work
- ✅ Response matches spec
- ✅ 401 for unauthenticated
- ✅ 500 on server errors handled

---

### Task 2.2: Create Request API (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.4  
**Assignee:** Backend Developer

**Description:**
- Build `POST /api/v1/tenant/requests`
- Accept body: categoryId, title, content, attachmentUrl(s)
- Validate inputs and return correct error codes
- Create request and return created summary

**Acceptance Criteria:**
- ✅ Returns 201 on success
- ✅ Error codes correct
- ✅ Request stored with correct status
- ✅ Attachments linked properly

---

### Task 2.3: Request Detail API (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 1.6  
**Assignee:** Backend Developer

**Description:**
- Build `GET /api/v1/tenant/requests/{requestId}`
- Validate `requestId` format
- Enforce tenant access
- Return full request detail DTO

**Acceptance Criteria:**
- ✅ Returns full detail
- ✅ 404 if not found/deleted
- ✅ 403 if not owner
- ✅ 401 if unauthenticated

---

### Task 2.4: Attachment Download API (2 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 1.6  
**Assignee:** Backend Developer

**Description:**
- Build `GET /api/v1/tenant/requests/{requestId}/attachments/{attachmentId}`
- Validate request ownership
- Return file stream or signed URL
- Secure file access to tenant only

**Acceptance Criteria:**
- ✅ Tenant can download own attachments
- ✅ 403 for unauthorized access
- ✅ 404 for missing attachment
- ✅ Correct content headers

---

### Task 2.5: Standard Error Handling (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1-2.4  
**Assignee:** Backend Developer

**Description:**
- Implement consistent API error format
- Map errors to HTTP codes
- Return spec error codes for validation failures
- Handle missing/invalid fields and auth errors

**Acceptance Criteria:**
- ✅ Consistent response schema
- ✅ REQ_001 / REQ_002 / REQ_003 implemented
- ✅ 401/403/404/500 handled
- ✅ Tests cover all error cases

---

## Epic 3: Frontend User Experience (12 points)

### Task 3.1: Request Creation Form (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 2.2  
**Assignee:** Frontend Developer

**Description:**
- Build form fields: category, title, content, attachment upload
- Client-side validation for required fields
- File upload validation for JPG/JPEG/PNG and max 5MB
- Call create API and show success/failure
- Display created requestId and PENDING status

**Acceptance Criteria:**
- ✅ Form validates input
- ✅ File upload restrictions enforced
- ✅ Success state shown after submission
- ✅ Error messages displayed clearly
- ✅ Experience works on desktop and mobile

---

### Task 3.2: Request List Page (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 2.1  
**Assignee:** Frontend Developer

**Description:**
- Display list with columns: requestId, category, title, status, createdAt
- Implement pagination, status filter, and search by title
- Sort by createdAt descending
- Clicking a request opens detail page
- Show empty state when no requests

**Acceptance Criteria:**
- ✅ List data loads correctly
- ✅ Pagination works
- ✅ Search and filter work
- ✅ Click navigates to detail
- ✅ Loading and empty states present

---

### Task 3.3: Request Detail Page (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.3  
**Assignee:** Frontend Developer

**Description:**
- Show full request detail and status
- Show attachments with preview/download links
- Show status history timeline
- Display back navigation
- Handle 403/404 errors gracefully

**Acceptance Criteria:**
- ✅ Detail view displays all fields
- ✅ Attachments accessible
- ✅ Status history shown
- ✅ Back button works
- ✅ Error states handled

---

### Task 3.4: UI Polish & Mobile (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 3.1-3.3  
**Assignee:** Frontend Developer

**Description:**
- Review UI consistency and spacing
- Ensure mobile responsiveness
- Validate color/typography
- Improve accessibility

**Acceptance Criteria:**
- ✅ UI consistent across pages
- ✅ Mobile layout works
- ✅ Accessible forms and buttons
- ✅ No major visual issues

---

## Epic 4: Testing & QA (10 points)

### Task 4.1: Backend Unit Tests (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 1.1-1.6  
**Assignee:** QA / Backend

**Description:**
- Test request creation service
- Test list/detail services
- Test validation and authorization
- Test attachment metadata handling
- Achieve ≥ 80% coverage

**Acceptance Criteria:**
- ✅ Unit tests pass
- ✅ Coverage target met
- ✅ Error paths tested
- ✅ Tenant isolation tested

---

### Task 4.2: API Integration Tests (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1-2.5  
**Assignee:** QA / Backend

**Description:**
- End-to-end API flows:
  - Create request
  - List requests
  - Get request detail
  - Download attachment
- Test auth and tenant isolation
- Test invalid requests and errors

**Acceptance Criteria:**
- ✅ API flows pass
- ✅ Auth/403 scenarios covered
- ✅ Error codes validated
- ✅ Attachment endpoint tested

---

### Task 4.3: Frontend E2E Tests (2 points)
**Priority:** MEDIUM  
**Duration:** 1.5 days  
**Dependencies:** Task 3.1-3.4  
**Assignee:** QA / Frontend

**Description:**
- Test from tenant perspective:
  - Create request
  - View list
  - Search/filter
  - Open detail
  - Download attachment
- Validate UI states and error handling

**Acceptance Criteria:**
- ✅ E2E flow passes
- ✅ UI handles errors
- ✅ Request status visible
- ✅ Mobile/responsive path tested

---

### Task 4.4: UAT & Sign-off (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 4.1-4.3  
**Assignee:** QA / Product Owner

**Description:**
- Execute acceptance criteria scenarios
- Confirm business goals met
- Validate with product owner
- Document issues and final sign-off

**Acceptance Criteria:**
- ✅ All ACs validated
- ✅ Product owner approves
- ✅ No critical issues remain
- ✅ UAT report available

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | Data model, validation, create/list/detail services |
| Sprint 2 | 12 | API endpoints, attachment download, errors |
| Sprint 3 | 12 | Frontend form, list, detail |
| Sprint 4 | 10 | Testing, E2E, UAT, deployment prep |

---

## Critical Dependencies

- Task 1.1-1.6 → Task 2.1-2.5 (backend services before APIs)
- Task 2.1-2.5 → Task 3.1-3.4 (frontend depends on APIs)
- Task 4.1-4.4 depends on completion of backend and frontend features

---

## Definition of Done

- Code reviewed and merged
- Unit tests pass with ≥ 80% coverage
- Integration tests pass
- API specs documented
- Frontend screens verified
- UAT sign-off obtained
- No critical bugs
- Deployment checklist completed
