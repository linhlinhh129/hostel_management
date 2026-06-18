# ASKS: Phân chia Chi tiết Đầu Việc - Quản lý Cơ sở

**Total Story Points:** ~89 points  
**Sprint Duration:** 2 weeks × 5 sprints = 10 weeks  
**Velocity:** ~18 points/sprint

---

## Epic 1: Backend Infrastructure & Database (22 points)

### Task 1.1: Database Schema Design (5 points)
**Assignee:** Database Admin / Backend Lead  
**Duration:** 3-4 days  
**Description:**
- Design Facility table (id, code, name, address, maxFloors, maxRoomsPerFloor, status, createdAt, updatedAt)
- Design Room table (id, code, facilityId, floor, roomNumber, area, status, generatedBySystem, createdAt, updatedAt)
- Design AuditLog table
- Create ER diagram
- Define constraints, indexes, relationships

**Acceptance Criteria:**
- ✓ ER diagram documented
- ✓ All constraints defined (PK, FK, UNIQUE)
- ✓ Indexes for frequently queried columns identified
- ✓ Data type & length defined

**Dependencies:** None

---

### Task 1.2: Database Migration Scripts (3 points)
**Assignee:** Database Admin  
**Duration:** 2 days  
**Description:**
- Write migration scripts (create tables)
- Test rollback scripts
- Document migration process
- Create seed data (optional test facilities)

**Acceptance Criteria:**
- ✓ Migration scripts execute without error
- ✓ Rollback scripts tested & working
- ✓ Migration documentation complete

**Dependencies:** Task 1.1

---

### Task 1.3: Setup JPA/ORM Configuration (2 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Configure JPA entity mappings
- Setup Hibernate configuration
- Configure connection pooling
- Test ORM integration

**Acceptance Criteria:**
- ✓ Entities mapped correctly
- ✓ ORM queries working
- ✓ Connection pooling configured

**Dependencies:** Task 1.1, 1.2

---

### Task 1.4: Implement Facility Entity (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Create Facility JPA entity class
- Implement field validation (code format, length, uniqueness)
- Implement status enum
- Create repository interface with custom queries

**Acceptance Criteria:**
- ✓ Entity fields defined correctly
- ✓ Validation logic implemented
- ✓ Custom queries in repository work

**Dependencies:** Task 1.3

---

### Task 1.5: Implement Room Entity (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Create Room JPA entity class
- Implement relationships (Facility -> Room)
- Create room code composite uniqueness constraint
- Create repository with pagination & filtering

**Acceptance Criteria:**
- ✓ Entity relationships correct
- ✓ Composite constraints validated
- ✓ Repository queries working

**Dependencies:** Task 1.4

---

### Task 1.6: Implement AuditLog Entity (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Create AuditLog entity
- Implement aspect-oriented logging
- Create utility for logging user actions
- Test audit trail recording

**Acceptance Criteria:**
- ✓ Audit log captures all operations
- ✓ User information recorded
- ✓ Timestamps accurate

**Dependencies:** Task 1.4, 1.5

---

## Epic 2: Business Logic & API Layer (38 points)

### Task 2.1: Implement Facility Service - Create (6 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement createFacility() service method
- Validate all required fields (code, name, address, maxFloors, maxRoomsPerFloor)
- Enforce code uniqueness & uppercase conversion
- Handle FACILITY_001 to FACILITY_008 error codes
- Create audit log entry

**Acceptance Criteria:**
- ✓ Facility created with DRAFT status
- ✓ All validations working
- ✓ Audit log created
- ✓ Error responses correct

**Dependencies:** Task 1.4, 1.6

**Unit Tests:**
- Test valid facility creation
- Test duplicate facility code
- Test invalid code format
- Test number range validations

---

### Task 2.2: Implement Facility Service - Read (4 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Implement getFacilityList() with pagination
- Implement getFacilityDetail(id)
- Implement search by code, name, address
- Implement filter by status (DRAFT, ACTIVE, INACTIVE)

**Acceptance Criteria:**
- ✓ List pagination working
- ✓ Search working for all fields
- ✓ Filter by status working
- ✓ 404 for missing facility

**Dependencies:** Task 1.4

---

### Task 2.3: Implement Facility Service - Update DRAFT (6 points)
**Assignee:** Backend Developer  
**Duration:** 2-3 days  
**Description:**
- Implement updateFacilityDraft() service
- Allow updates to: code, name, address, maxFloors, maxRoomsPerFloor
- Validate all fields same as create
- Check facility is in DRAFT status
- Create audit log entry

**Acceptance Criteria:**
- ✓ Updates applied correctly
- ✓ Status check enforced
- ✓ All validations working
- ✓ Audit log created

**Dependencies:** Task 2.1

**Unit Tests:**
- Test valid updates
- Test status constraints
- Test validation rules

---

### Task 2.4: Implement Facility Service - Activate (8 points)
**Assignee:** Backend Developer  
**Duration:** 3-4 days  
**Description:**
- Implement activateFacility() service
- Validate facility is in DRAFT status (error FACILITY_013)
- Lock configuration fields (code, address, maxFloors, maxRoomsPerFloor)
- Generate rooms automatically (see Task 2.5)
- Set status to ACTIVE
- Use transaction to ensure atomicity
- Create audit log entry

**Acceptance Criteria:**
- ✓ Facility activated successfully
- ✓ Status validation enforced
- ✓ Rooms generated (see Task 2.5)
- ✓ Configuration locked
- ✓ Transaction handling correct
- ✓ Audit log created

**Dependencies:** Task 2.1, 2.5

**Unit Tests:**
- Test valid activation
- Test invalid status transitions
- Test room generation triggered

---

### Task 2.5: Implement Room Generation Algorithm (8 points)
**Assignee:** Backend Developer  
**Duration:** 3-4 days  
**Description:**
- Implement automatic room generation algorithm
- Generate codes using format: [facilityCode][floor 2-digit][roomNumber 2-digit]
- Create rooms for all combinations: floor (1 to maxFloors) × roomNumber (1 to maxRoomsPerFloor)
- Set default status to AVAILABLE
- Validate no duplicate room codes (error ROOM_001)
- Use batch insert for performance
- Implement rollback on failure (error FACILITY_020)

**Acceptance Criteria:**
- ✓ Room codes generated correctly
- ✓ Total rooms = maxFloors × maxRoomsPerFloor
- ✓ No duplicate codes
- ✓ Correct status set
- ✓ Transaction rollback working
- ✓ Performance acceptable (< 5s for large facilities)

**Dependencies:** Task 1.5

**Unit Tests:**
- Test room code generation
- Test for various floor/room combinations
- Test duplicate detection
- Test batch insert
- Test rollback scenario

**Edge Cases:**
- Single floor, single room (HL0101)
- Multi-floor, multi-room (HL0101 to HL0505)
- Large facility (100+ rooms)

---

### Task 2.6: Implement Facility Service - Deactivate (4 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Implement deactivateFacility() service
- Set status to INACTIVE
- Validate facility has no active contracts (future validation)
- Create audit log entry
- Keep existing data for history

**Acceptance Criteria:**
- ✓ Status changed to INACTIVE
- ✓ No new data can be created for facility
- ✓ History accessible
- ✓ Audit log created

**Dependencies:** Task 2.1

---

### Task 2.7: Implement Facility API Controller (3 points)
**Assignee:** Backend Developer  
**Duration:** 2 days  
**Description:**
- Create REST controller with endpoints:
  - POST /api/v1/facilities (create)
  - GET /api/v1/facilities (list with pagination)
  - GET /api/v1/facilities/{id} (detail)
  - PUT /api/v1/facilities/{id} (update)
  - POST /api/v1/facilities/{id}/activate (activate)
  - POST /api/v1/facilities/{id}/deactivate (deactivate)
- Implement request/response DTOs
- Handle exceptions & return proper error codes
- Add authorization checks (Admin only)

**Acceptance Criteria:**
- ✓ All endpoints working
- ✓ DTOs correct
- ✓ Authorization enforced
- ✓ Error handling complete

**Dependencies:** Task 2.1 - 2.6

---

### Task 2.8: Implement API Error Handling (3 points)
**Assignee:** Backend Developer  
**Duration:** 1-2 days  
**Description:**
- Create custom exception classes
- Implement global exception handler
- Map all error codes (FACILITY_001 to FACILITY_020, ROOM_001)
- Create error response DTO

**Acceptance Criteria:**
- ✓ All error codes mapped
- ✓ Proper HTTP status codes
- ✓ Consistent error response format

**Dependencies:** Task 2.7

---

## Epic 3: Frontend Development (20 points)

### Task 3.1: Implement Facility List Page (5 points)
**Assignee:** Frontend Developer  
**Duration:** 2-3 days  
**Description:**
- Create table component with columns: code, name, address, maxFloors, maxRoomsPerFloor, totalRooms, status
- Implement pagination (page, size)
- Implement search input (code, name, address)
- Implement status filter (DRAFT, ACTIVE, INACTIVE)
- Implement action buttons (view, edit, activate, deactivate)
- Add loading & error states

**Acceptance Criteria:**
- ✓ Table displays data correctly
- ✓ Pagination working
- ✓ Search filtering works
- ✓ Status filter works
- ✓ Responsive design
- ✓ Error messages displayed

**Dependencies:** Task 2.2

---

### Task 3.2: Implement Facility Create/Edit Form (6 points)
**Assignee:** Frontend Developer  
**Duration:** 2-3 days  
**Description:**
- Create form with fields: code, name, address, maxFloors, maxRoomsPerFloor
- Implement client-side validation (required, format, range)
- Show validation error messages
- Implement read-only mode for ACTIVE facilities
- Call create or update API based on mode
- Show success/error notifications

**Acceptance Criteria:**
- ✓ Form validation working
- ✓ Create/update API calls correct
- ✓ Read-only mode for ACTIVE
- ✓ Notifications showing
- ✓ Form reset after success
- ✓ Responsive design

**Dependencies:** Task 2.1, 2.3

---

### Task 3.3: Implement Facility Detail Page (4 points)
**Assignee:** Frontend Developer  
**Duration:** 2 days  
**Description:**
- Display facility information in read-only view
- Show facility code, name, address, max floors/rooms, total rooms, status
- Show timestamps (created, updated)
- Implement activate button for DRAFT facilities
- Implement deactivate button for ACTIVE facilities
- Implement edit button for DRAFT facilities

**Acceptance Criteria:**
- ✓ Detail view displays correctly
- ✓ Action buttons present & working
- ✓ Read-only fields locked
- ✓ Status badge shown
- ✓ Responsive design

**Dependencies:** Task 2.2, 2.7

---

### Task 3.4: Implement Activate/Deactivate Dialogs (3 points)
**Assignee:** Frontend Developer  
**Duration:** 1-2 days  
**Description:**
- Create confirmation dialogs
- Implement activate dialog (warning about room generation)
- Implement deactivate dialog (warning about inactive status)
- Show loading state during processing
- Handle success/error responses
- Show notification with result

**Acceptance Criteria:**
- ✓ Dialogs appear & behave correctly
- ✓ API calls made on confirmation
- ✓ Loading states shown
- ✓ Results handled properly
- ✓ Error messages clear

**Dependencies:** Task 2.4, 2.6

---

### Task 3.5: UI/UX Polish & Responsive Design (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 day  
**Description:**
- Review & improve UI consistency
- Test responsive design (mobile, tablet, desktop)
- Optimize load times
- Accessibility improvements (keyboard navigation, screen readers)
- Dark/light mode support if applicable

**Acceptance Criteria:**
- ✓ Consistent styling
- ✓ Responsive on all devices
- ✓ Fast load times (< 2s)
- ✓ WCAG AA compliance

**Dependencies:** Task 3.1 - 3.4

---

## Epic 4: Testing & Quality Assurance (9 points)

### Task 4.1: Unit Tests - Backend (3 points)
**Assignee:** QA Engineer / Backend Developer  
**Duration:** 2 days  
**Description:**
- Write unit tests for service methods
- Test validation logic
- Test room generation algorithm
- Test status transitions
- Aim for >= 80% code coverage

**Acceptance Criteria:**
- ✓ All service methods tested
- ✓ Coverage >= 80%
- ✓ All tests passing
- ✓ Edge cases covered

**Dependencies:** Task 2.1 - 2.6

---

### Task 4.2: Integration Tests (3 points)
**Assignee:** QA Engineer  
**Duration:** 2 days  
**Description:**
- Write API integration tests
- Test complete CRUD workflows
- Test status transition flows
- Test room generation end-to-end
- Test with various data sets

**Acceptance Criteria:**
- ✓ API tests comprehensive
- ✓ Workflows tested
- ✓ All tests passing
- ✓ Performance acceptable

**Dependencies:** Task 2.7

---

### Task 4.3: E2E & UAT Testing (2 points)
**Assignee:** QA Engineer  
**Duration:** 2-3 days  
**Description:**
- Write E2E test scenarios
- Test complete user workflows
- Test UI interactions
- Manual UAT with stakeholders
- Document any issues

**Acceptance Criteria:**
- ✓ E2E tests written & passing
- ✓ UAT completed
- ✓ No critical issues
- ✓ Sign-off from stakeholders

**Dependencies:** Task 3.1 - 3.4

---

### Task 4.4: Performance & Security Testing (1 point)
**Assignee:** QA Engineer  
**Duration:** 1 day  
**Description:**
- Load testing (100 req/min)
- Response time verification (< 500ms P95)
- SQL injection testing
- Authorization testing
- Input validation testing

**Acceptance Criteria:**
- ✓ Performance criteria met
- ✓ No security vulnerabilities
- ✓ Load test passed
- ✓ Report documented

**Dependencies:** Task 2.7, 3.1 - 3.4

---

## Epic 5: Documentation & Deployment (6 points)

### Task 5.1: API Documentation (2 points)
**Assignee:** Tech Lead / Backend Developer  
**Duration:** 1 day  
**Description:**
- Generate Swagger/OpenAPI documentation
- Document all endpoints, parameters, responses
- Document error codes & meanings
- Document authentication & authorization

**Acceptance Criteria:**
- ✓ API docs complete
- ✓ All endpoints documented
- ✓ Error codes explained
- ✓ Examples provided

**Dependencies:** Task 2.7

---

### Task 5.2: User Documentation (2 points)
**Assignee:** Tech Lead  
**Duration:** 1-2 days  
**Description:**
- Write admin user guide
- Document how to create facility
- Document status transitions
- Document search & filter usage
- Add screenshots

**Acceptance Criteria:**
- ✓ Guide complete & clear
- ✓ Screenshots included
- ✓ Examples provided
- ✓ Troubleshooting section

**Dependencies:** Task 3.1 - 3.4

---

### Task 5.3: Deployment & Go-Live (2 points)
**Assignee:** DevOps / Tech Lead  
**Duration:** 2-3 days  
**Description:**
- Prepare production environment
- Run database migrations
- Deploy backend & frontend
- Setup monitoring & logging
- Conduct go-live checklist
- Monitor for issues first week

**Acceptance Criteria:**
- ✓ Deployment successful
- ✓ Monitoring setup
- ✓ Team trained
- ✓ Go-live completed

**Dependencies:** All previous tasks

---

## Summary by Sprint

| Sprint | Duration | Points | Focus |
|--------|----------|--------|-------|
| Sprint 1 | Wk 1-2 | 18 | Design, DB schema, entity setup |
| Sprint 2 | Wk 3-4 | 20 | Create, activate, room generation |
| Sprint 3 | Wk 5 | 18 | Read, update, deactivate, API |
| Sprint 4 | Wk 6-7 | 19 | Frontend, testing, integration |
| Sprint 5 | Wk 8-10 | 14 | Testing completion, docs, deployment |

---

## Task Dependency Graph

```
Task 1.1 (DB Schema)
  ↓
Task 1.2 (Migration)
  ↓
Task 1.3 (ORM Config)
  ├→ Task 1.4 (Facility Entity)
  │   ├→ Task 1.6 (AuditLog)
  │   ├→ Task 2.1 (Create)
  │   └→ Task 2.3 (Update)
  │
  └→ Task 1.5 (Room Entity)
      ├→ Task 2.5 (Room Generation)
      └→ Task 2.4 (Activate)

Task 2.1 → Task 2.2, 2.3, 2.4, 2.6
Task 2.2 → Task 3.1
Task 2.1, 2.3 → Task 3.2
Task 2.7 → Task 3.3, 4.2
Task 3.1-3.4 → Task 4.3, 5.2
```

---

## Prioritization Guidelines

**Must Have (P0):**
- Task 1.1-1.6 (Database & entities)
- Task 2.1-2.7 (API implementation)
- Task 3.1-3.3 (Core UI)
- Task 4.1-4.2 (Testing)

**Should Have (P1):**
- Task 2.8 (Error handling)
- Task 3.4 (Dialogs)
- Task 4.3-4.4 (Additional testing)
- Task 5.1-5.2 (Documentation)

**Nice to Have (P2):**
- Task 3.5 (UI Polish)
- Audit log details
- Advanced filtering
