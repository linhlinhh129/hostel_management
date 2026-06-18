# TASKS: Phân chia Chi tiết Đầu Việc - Cập nhật đồng hồ điện nước

**Total Story Points:** ~32 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~10 points/sprint

---

## Epic 1: Backend Meter Update Service (14 points)

### Task 1.1: Meter Update API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define POST /api/v1/meter-readings and/or PUT /api/v1/meter-readings/{id}
- Specify payload fields and validation rules

---

### Task 1.2: Meter Create/Update Service (5 points)
**Duration:** 2 days  
**Description:**
- Implement create/update logic
- Validate positive reading, date window, operator scope
- Apply audit metadata

---

### Task 1.3: Monotonic Reading Validation (4 points)
**Duration:** 2 days  
**Description:**
- Prevent regressive or duplicate readings
- Validate current reading against latest stored value

---

### Task 1.4: Audit & Logging (3 points)
**Duration:** 1 day  
**Description:**
- Log operator and timestamps
- Ensure audit fields populate on insert/update

---

## Epic 2: API & Security (8 points)

### Task 2.1: Endpoint Implementation (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint with authorization
- Validate room/facility ownership

---

### Task 2.2: Payload Validation (3 points)
**Duration:** 1 day  
**Description:**
- Validate meter type, reading, and date
- Reject invalid or missing values

---

### Task 2.3: Error Handling (2 points)
**Duration:** 1 day  
**Description:**
- Standardize errors for invalid data
- Ensure user-facing messages are clear

---

## Epic 3: Frontend Update Form (6 points)

### Task 3.1: Meter Entry Form (3 points)
**Duration:** 2 days  
**Description:**
- Create form for new reading entry
- Include room/facility and meter type selector

---

### Task 3.2: Inline Validation (2 points)
**Duration:** 1 day  
**Description:**
- Validate positive numbers and required fields
- Display actionable messages

---

### Task 3.3: Submission Feedback (1 point)
**Duration:** 1 day  
**Description:**
- Show success or error outcome
- Reset form on success

---

## Epic 4: Testing (4 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test positive/monotonic validation
- Test authorization checks

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test API with valid and invalid inputs
- Validate audit metadata is persisted

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | Backend create/update service |
| Sprint 2 | 8 | API validation and security |
| Sprint 3 | 10 | Frontend and tests |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (service required by endpoint)
- Task 1.3 → Task 2.2 (validation rules must enforce monotonicity)
- Task 3.2 → Task 2.3 (UI displays API validation messages)
