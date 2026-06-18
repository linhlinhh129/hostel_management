# TASKS: Phân chia Chi tiết Đầu Việc - Cập nhật trạng thái yêu cầu sửa chữa

**Total Story Points:** ~36 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Backend Status Update Service (14 points)

### Task 1.1: Status Update API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define PUT /api/v1/requests/{id}/status payload
- Specify valid state transitions and reason field

---

### Task 1.2: Status Transition Validation (4 points)
**Duration:** 2 days  
**Description:**
- Implement transition rule engine
- Validate current state and target state
- Enforce reject reason when rejecting

---

### Task 1.3: Status Update Service Implementation (5 points)
**Duration:** 2 days  
**Description:**
- Apply conditional update based on current status
- Persist audit fields and rejection metadata
- Return updated state

---

### Task 1.4: Concurrency Handling (3 points)
**Duration:** 1 day  
**Description:**
- Detect concurrent state changes
- Return conflict error when state mismatches

---

## Epic 2: API & Security (10 points)

### Task 2.1: PUT Status Endpoint (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint
- Authorize operator and ensure correct request scope

---

### Task 2.2: Payload Validation (3 points)
**Duration:** 1 day  
**Description:**
- Validate target status and optional reason
- Reject bad requests with clear errors

---

### Task 2.3: Response Standardization (2 points)
**Duration:** 1 day  
**Description:**
- Standardize success/failure payloads
- Map validation and conflict errors

---

### Task 2.4: Security Review (2 points)
**Duration:** 1 day  
**Description:**
- Review access control for status updates
- Ensure no unauthorized status churn

---

## Epic 3: Frontend Status Controls (8 points)

### Task 3.1: Status Action UI (3 points)
**Duration:** 2 days  
**Description:**
- Render action buttons for allowed transitions
- Show status labels and progress hints

---

### Task 3.2: Confirm / Reason Dialog (3 points)
**Duration:** 2 days  
**Description:**
- Add reject reason modal when needed
- Confirm terminal transitions

---

### Task 3.3: State Refresh & Feedback (2 points)
**Duration:** 1 day  
**Description:**
- Refresh page on status change
- Show success/error alerts

---

## Epic 4: Testing (4 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test transition validation rules
- Test reject reason enforcement

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Validate status update API end-to-end
- Test invalid transitions and conflict handling

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | Backend transition rule engine |
| Sprint 2 | 10 | API and security |
| Sprint 3 | 12 | Frontend status controls and tests |

---

## Critical Dependencies

- Task 1.2 → Task 1.3 (validation required before update)
- Task 2.1 → Task 3.1 (frontend depends on endpoint contract)
- Task 3.2 → Task 2.2 (UI validation messages map to API rules)
