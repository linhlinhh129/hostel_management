# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Hồ sơ Cá nhân (Tenant)

**Total Story Points:** ~28 points  
**Sprint Duration:** 2 weeks × 2.5 sprints = 5 weeks  
**Velocity:** ~11.2 points/sprint

---

## Epic 1: Backend Services (8 points)

### Task 1.1: Get Profile Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getProfile(tenantId) service
- Return all profile info
- Include room info
- Include dependents

---

### Task 1.2: Update Profile Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement updateProfile(tenantId, email, phone) service
- Validate email unique
- Validate phone format
- Return updated profile

---

### Task 1.3: Validation Service (2 points)
**Duration:** 1 day  
**Description:**
- Implement email format & uniqueness validation
- Implement phone format validation

---

## Epic 2: APIs (6 points)

### Task 2.1: Get Profile Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- GET /api/v1/tenant/profile
- Implement DTO with full info

---

### Task 2.2: Update Endpoint (2 points)
**Duration:** 1 day  
**Description:**
- PUT /api/v1/tenant/profile
- Accept email, phone only
- Implement DTO

---

### Task 2.3: Response Formatting (2 points)
**Duration:** 1 day  
**Description:**
- Standard response format
- Error handling

---

## Epic 3: Frontend Development (12 points)

### Task 3.1: Profile View (4 points)
**Duration:** 2 days  
**Description:**
- Display all profile info (read-only)
- Show: tenantId, name, DOB, CCCD, email, phone
- Show room info
- Show dependents link

---

### Task 3.2: Edit Form (5 points)
**Duration:** 2-3 days  
**Description:**
- Form for email & phone only
- Email format validation
- Phone format validation
- Unique email check (async)
- Save button
- Error handling

---

### Task 3.3: Linked Data Display (3 points)
**Duration:** 1-2 days  
**Description:**
- Display current room (read-only)
- Link to dependents list
- UI polish

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
| Sprint 1 | 11 | Get/update service, API |
| Sprint 2 | 11 | Frontend view, edit form |
| Sprint 3 | 6 | Testing, deployment |

---

## Critical Dependencies

- Task 1.1-1.3 → Task 2.1-2.3 (services needed for API)
- Task 2.1-2.3 → Task 3.1-3.3 (API needed for frontend)
