# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Yêu cầu (Tenant)

**Total Story Points:** ~60 points  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Database & Entities (10 points)

### Task 1.1: Schema Design (2 points)
**Duration:** 1 day  
**Description:**
- Design Request table (id, code, tenantId, category, title, content, status, createdAt)
- Design RequestAttachment table (id, requestId, fileUrl, filename)
- Create indexes

---

### Task 1.2: Entity Implementation (4 points)
**Duration:** 2 days  
**Description:**
- Create Request entity
- Create RequestAttachment entity
- Setup relationships
- Create repositories

---

### Task 1.3: Database Migration (2 points)
**Duration:** 1 day  
**Description:**
- Write migration scripts
- Test rollback

---

### Task 1.4: Validation (2 points)
**Duration:** 1 day  
**Description:**
- Implement field validators
- Implement file format/size validation

---

## Epic 2: Request Management Service (16 points)

### Task 2.1: Create Request Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement createRequest() service
- Validate category, title, content
- Auto-generate request code
- Store with PENDING status
- Handle attachments

---

### Task 2.2: List Requests Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement list service with pagination
- Filter by tenant ID
- Support search by title
- Support filter by status
- Order by date descending

---

### Task 2.3: Get Request Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement detail service
- Validate ownership (tenant can only see own)
- Return request + attachments
- Return status history

---

### Task 2.4: Attachment Management (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement file upload validation
- Implement file storage service
- Implement file retrieval

---

### Task 2.5: Status History Tracking (3 points)
**Duration:** 1-2 days  
**Description:**
- Track status changes (auto by manager)
- Store timestamp & changer info
- Return history in detail view

---

## Epic 3: Request API (14 points)

### Task 3.1: Create Request Endpoint (3 points)
**Duration:** 1-2 days  
**Description:**
- POST /api/v1/tenant/requests
- Accept category, title, content, attachments
- Implement request DTO
- Return created request

---

### Task 3.2: List & Detail Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- GET /api/v1/tenant/requests (list)
- GET /api/v1/tenant/requests/{id} (detail)
- Support pagination & filters
- Implement DTOs

---

### Task 3.3: Attachment Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- GET /api/v1/tenant/requests/{id}/attachments/{attachmentId} (download)
- POST multipart file handling
- Implement stream response

---

### Task 3.4: Response Formatting (3 points)
**Duration:** 1-2 days  
**Description:**
- Standard response format
- Error handling for file operations

---

## Epic 4: Frontend Development (14 points)

### Task 4.1: Create Request Form (5 points)
**Duration:** 2-3 days  
**Description:**
- Form with: category selector, title, content
- File upload input (accept JPG/PNG, max 5MB)
- Client validation
- Call create API on submit
- Show success & request code

---

### Task 4.2: Request List Page (4 points)
**Duration:** 2 days  
**Description:**
- Create table: code, category, title, status, createdDate
- Pagination
- Search by title
- Filter by status
- Click to view detail
- Color-code by status

---

### Task 4.3: Request Detail View (4 points)
**Duration:** 2 days  
**Description:**
- Display full request info (read-only)
- Show attachments with preview/download
- Show status history with timeline
- Show creator & dates
- Back button to list

---

### Task 4.4: UI Polish (1 point)
**Duration:** 1 day  
**Description:**
- Review consistency
- Test responsive

---

## Epic 5: Testing (6 points)

### Task 5.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test services
- Test validation
- >= 80% coverage

---

### Task 5.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test APIs
- Test file operations
- Test complete workflow

---

### Task 5.3: E2E & UAT (2 points)
**Duration:** 1 day  
**Description:**
- E2E tests
- Manual UAT
- File upload testing

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | DB schema, entities, create service |
| Sprint 2 | 12 | List, detail, attachment services |
| Sprint 3 | 12 | API endpoints, response formatting |
| Sprint 4 | 12 | Frontend form, list, detail |
| Sprint 5 | 12 | Testing, deployment |

---

## Critical Dependencies

- Task 1.1 → 1.2 (DB needed)
- Task 1.2 → 2.1-2.5 (entities needed for services)
- Task 2.1-2.5 → 3.1-3.4 (services needed for API)
- Task 3.1-3.4 → 4.1-4.4 (API needed for frontend)
