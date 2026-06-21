# TASKS: Phân chia Chi Tiết Đầu Việc - Quản lý Người phụ thuộc (Tenant)

**Date:** 2026-06-21  
**Total Story Points:** ~36 points  
**Sprint Duration:** 2 weeks × 2 sprints = 4 weeks  
**Velocity:** ~18 points/sprint

---

## Epic 1: Requirements & Security Design (5 points)

### Task 1.1: Confirm Authorization Model (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Description:**
- Review Tenant/Manager/Admin access rules
- Confirm tenant-scoped dependent ownership mapping
- Document `deleted_at IS NULL` soft-delete policy
- Define PII masking rules for CCCD/CMND

**Acceptance Criteria:**
- ✅ Authorization matrix documented
- ✅ Tenant-only and admin/manager access clarified
- ✅ Soft delete and masking requirements captured

---

### Task 1.2: Define API Contract & Response Schema (3 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Description:**
- Define list endpoint contract and response payload
- Define detail endpoint contract and response payload
- Define error responses for 401, 403, 404, 500
- Document empty state behavior

**Acceptance Criteria:**
- ✅ API contracts documented
- ✅ Response examples match SPEC
- ✅ Error handling schema defined

---

## Epic 2: Backend Implementation (14 points)

### Task 2.1: Dependent List Service (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 1.1, 1.2  
**Description:**
- Implement service to query dependents by tenantId
- Include only `deleted_at IS NULL`
- Sort by `full_name` ascending
- Return fields: dependentId, fullName, relationship, phoneNumber, isVerified
- Support pagination

**Acceptance Criteria:**
- ✅ Tenant sees only own dependents
- ✅ Sorted ascending by full name
- ✅ Pagination works
- ✅ Response fits defined schema

---

### Task 2.2: Dependent Detail Service (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 1.1, 1.2  
**Description:**
- Implement service to load dependent detail by dependentId and tenantId
- Validate tenant ownership
- Exclude soft deleted records
- Mask citizenId/CCCD before returning
- Return full tenant detail payload

**Acceptance Criteria:**
- ✅ Detail returned only for owned dependent
- ✅ 404 when missing or soft deleted
- ✅ 403 when accessing another tenant's dependent
- ✅ citizenId masked correctly

---

### Task 2.3: Data Protection & Filtering (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1, 2.2  
**Description:**
- Enforce soft-delete filter globally for dependent queries
- Mask PII fields server-side
- Ensure email and phone display only when allowed
- Add tenant ownership check at query layer

**Acceptance Criteria:**
- ✅ Soft-deleted dependents never appear
- ✅ PII masking executed before response
- ✅ Tenant isolation enforced in all queries

---

### Task 2.4: Performance & Index Review (3 points)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 2.1  
**Description:**
- Review schema indexes for dependent queries
- Add/verify indexes on `tenant_id`, `deleted_at`, `full_name`
- Validate list/detail query performance

**Acceptance Criteria:**
- ✅ Query performance targets documented
- ✅ Index recommendations captured
- ✅ No full table scan for tenant list

---

## Epic 3: API Endpoints (8 points)

### Task 3.1: Implement List Endpoint (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.1  
**Description:**
- Build `GET /api/v1/tenant/dependents`
- Enforce authentication and tenant context
- Accept pagination parameters
- Return structured JSON response

**Acceptance Criteria:**
- ✅ Endpoint returns tenant-scoped list
- ✅ 401 for missing auth
- ✅ 500 handled gracefully
- ✅ Matches API contract

---

### Task 3.2: Implement Detail Endpoint (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 2.2  
**Description:**
- Build `GET /api/v1/tenant/dependents/{dependentId}`
- Validate path parameter and tenant ownership
- Return detailed dependent data
- Return 403/404 as defined

**Acceptance Criteria:**
- ✅ Detail endpoint works for valid request
- ✅ 403 and 404 responses implemented
- ✅ Response conforming to SPEC example

---

### Task 3.3: API Error Handling & Logging (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 3.1, 3.2  
**Description:**
- Implement standard error response format
- Log access denial and missing resource events
- Provide retry-friendly messaging for front-end

**Acceptance Criteria:**
- ✅ Error response format consistent
- ✅ Authentication and authorization errors logged
- ✅ Frontend-friendly messages available

---

## Epic 4: Frontend Experience (8 points)

### Task 4.1: Dependent List Screen (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Dependencies:** Task 3.1  
**Description:**
- Implement list view for dependents
- Display columns: dependentId, fullName, relationship, phoneNumber, isVerified
- Show sort order by name A→Z
- Show empty state copy per SPEC
- Navigate to detail view on selection

**Acceptance Criteria:**
- ✅ List displays expected fields
- ✅ Empty state shown when no dependents
- ✅ Sorting and navigation work
- ✅ Loading/error states handled

---

### Task 4.2: Dependent Detail Screen (3 points)
**Priority:** HIGH  
**Duration:** 1.5 days  
**Dependencies:** Task 3.2  
**Description:**
- Implement detail page with read-only information
- Display avatar, full name, dateOfBirth, gender, relationship, phoneNumber, email, masked citizenId, sponsoredBy, registeredDate, isVerified
- Add back navigation and error state handling

**Acceptance Criteria:**
- ✅ Detail screen matches SPEC fields
- ✅ citizenId is masked correctly
- ✅ 403/404 handled gracefully
- ✅ Back navigation works

---

### Task 4.3: UI Polish & Mobile Review (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 4.1, 4.2  
**Description:**
- Review visual consistency and spacing
- Verify responsive layout
- Ensure accessibility for text, buttons, and labels

**Acceptance Criteria:**
- ✅ UI is consistent across screens
- ✅ Responsive layout validated
- ✅ No major accessibility issues

---

## Epic 5: Testing & Validation (5 points)

### Task 5.1: Backend Unit Tests (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 2.1-2.4  
**Description:**
- Test list/detail service logic
- Test authorization and soft-delete filtering
- Test PII masking behavior

**Acceptance Criteria:**
- ✅ Unit tests pass
- ✅ Key negative scenarios covered
- ✅ PII masking validated

---

### Task 5.2: API Integration Tests (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 3.1-3.3  
**Description:**
- Test endpoint flows for tenant list/detail
- Test 401, 403, 404 responses
- Test empty state and valid response payloads

**Acceptance Criteria:**
- ✅ Integration tests pass
- ✅ Auth and access control verified
- ✅ Response schema validated

---

### Task 5.3: UAT & Sign-off (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 4.1-4.3, Task 5.1-5.2  
**Description:**
- Execute acceptance criteria from SPEC
- Validate tenant experience with product owner
- Capture issues and approve release readiness

**Acceptance Criteria:**
- ✅ UAT checklist completed
- ✅ No critical issues remaining
- ✅ Product owner approves

---

## Sprint Plan

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 15 | Authorization, backend list/detail, API contract |
| Sprint 2 | 15 | API endpoints, frontend list/detail |
| Sprint 3 | 6 | UI polish, testing, UAT |

---

## Critical Dependencies

- Task 1.1-1.2 → Task 2.1-2.4 (design before implementation)
- Task 2.1-2.4 → Task 3.1-3.3 (backend services before API)
- Task 3.1-3.3 → Task 4.1-4.2 (API before frontend)
- Task 4.1-4.2 → Task 5.1-5.3 (features before testing)

---

## Definition of Done

- Tenant can view dependents list and detail through API and UI
- CCCD/CMND masking is enforced server-side
- Tenant can only access their own dependent records
- `deleted_at IS NULL` rule is enforced
- API responds with correct 401/403/404 behavior
- UI shows empty state and error states
- Tests pass and UAT signed off

