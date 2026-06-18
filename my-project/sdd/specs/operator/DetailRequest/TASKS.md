# TASKS: Phân chia Chi tiết Đầu Việc - Chi tiết yêu cầu sửa chữa

**Total Story Points:** ~44 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8 weeks  
**Velocity:** ~11 points/sprint

---

## Epic 1: Backend Detail Service (14 points)

### Task 1.1: Request Detail API Design (2 points)
**Duration:** 1 day  
**Description:**
- Define GET /api/v1/requests/{id}
- Define response fields and status mapping

---

### Task 1.2: Get Request Detail Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement retrieval of request with attachments
- Include facility, room, category, dates, status
- Include image URLs

---

### Task 1.3: Accept Request Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement assign request action
- Validate current status is PENDING
- Apply optimistic concurrency on update
- Return updated request status

---

### Task 1.4: Reject Request Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement reject action with `reject_reason`
- Validate current status is PENDING
- Store reject reason
- Redirect user after success

---

## Epic 2: API & Security (10 points)

### Task 2.1: GET Request Detail Endpoint (3 points)
**Duration:** 1 day  
**Description:**
- Implement endpoint
- Add authorization/ownership checks
- Return read-only data

---

### Task 2.2: PUT Accept Endpoint (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement accepting API call
- Return success/failure with conflict handling

---

### Task 2.3: PUT Reject Endpoint (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement rejecting API call
- Enforce `reject_reason`
- Return validation errors

---

### Task 2.4: Response Standardization (1 point)
**Duration:** 1 day  
**Description:**
- Standard success/error payloads
- Map status codes properly

---

## Epic 3: Frontend Detail Page (12 points)

### Task 3.1: Detail Layout & Fields (4 points)
**Duration:** 2 days  
**Description:**
- Render title, category, room, facility
- Render appointment/completion dates
- Render status and metadata

---

### Task 3.2: Image Thumbnails & Lightbox (3 points)
**Duration:** 2 days  
**Description:**
- Display thumbnails
- Open full-size lightbox on click
- Optimize loading behavior

---

### Task 3.3: Accept/Reject Buttons (3 points)
**Duration:** 2 days  
**Description:**
- Show buttons only if status = PENDING
- Handle accept action
- Handle reject modal with reason

---

### Task 3.4: Redirect & State Update (2 points)
**Duration:** 1 day  
**Description:**
- Redirect to list after action
- Refresh detail state on accept/reject

---

## Epic 4: Testing (8 points)

### Task 4.1: Unit Tests (3 points)
**Duration:** 2 days  
**Description:**
- Test detail retrieval service
- Test accept/reject logic
- Test concurrency failure scenarios

---

### Task 4.2: Integration Tests (3 points)
**Duration:** 2 days  
**Description:**
- Test API flows end-to-end
- Test validation of reject reason

---

### Task 4.3: UI Verification (2 points)
**Duration:** 1 day  
**Description:**
- Validate button visibility
- Validate lightbox and redirect

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 14 | Request detail service |
| Sprint 2 | 10 | Accept/reject API & security |
| Sprint 3 | 12 | Frontend detail page |
| Sprint 4 | 8 | Testing & polish |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (detail service required by endpoint)
- Task 1.3/1.4 → Task 2.2/2.3 (service required by API)
- Task 2.2/2.3 → Task 3.3/3.4 (frontend action buttons depend on API)
