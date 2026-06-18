# PLAN: Kế hoạch Thực thi Tiếp nhận và xử lý yêu cầu người thuê

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 10-12 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép người thuê gửi yêu cầu hỗ trợ và cho phép Manager tiếp nhận, phân công, theo dõi, và cập nhật trạng thái xử lý yêu cầu.

**Kiến trúc:**
- Backend API: Create request, manage status flow, assign staff
- State machine: NEW → RECEIVED → ASSIGNED → IN_PROGRESS → RESOLVED/REJECTED
- Audit trail: Log all status changes
- Frontend UI: Tenant request form, manager dashboard, assignment UI
- Email notifications: Notify on status changes

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** Design state machine, database schema, API contract

**Công việc:**
- Design request status flow & validation
- Design database schema (Request, RequestHistory, RequestAssignment)
- Define API contract for each status transition
- Design audit logging strategy
- Plan notification triggers

---

### Giai đoạn 2: Backend Development - Core (Tuần 3-5)

**Mục tiêu:** Implement request creation & status management

**Công việc:**
- Implement Request entity & repository
- Implement RequestHistory & RequestAssignment
- Implement request creation service
- Implement status transition validators
- Implement audit logging

---

### Giai đoạn 3: Backend Development - Management (Tuần 6-7)

**Mục tiêu:** Implement manager operations

**Công việc:**
- Implement receive/accept request
- Implement assign staff to request
- Implement reject request (with reason)
- Implement list & search requests
- Implement get request detail

---

### Giai đoạn 4: Frontend Development (Tuần 8-9)

**Mục tiêu:** Implement UI for both tenant & manager

**Công việc:**
- Tenant request form (create request)
- Manager request dashboard (list, search, filter)
- Request detail view
- Assignment UI
- Status update UI

---

### Giai đoạn 5: Testing & Deployment (Tuần 10-12)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### State Machine
```
NEW → RECEIVED → ASSIGNED → IN_PROGRESS → RESOLVED
                ↓
              REJECTED
```

### Validation Rules
- Tenant must be ACTIVE to create
- Only NEW requests can be RECEIVED
- Only RECEIVED requests can be ASSIGNED
- Only ASSIGNED can go to IN_PROGRESS
- Only IN_PROGRESS can be RESOLVED
- NEW/RECEIVED can be REJECTED
- No transitions back from RESOLVED/REJECTED

### Staff Assignment
- One staff per request at a time
- Only assigned staff can update status
- Can reassign before IN_PROGRESS (needs review)

### Audit Trail
- Log all status changes
- Store who made the change & when
- Store old & new values

---

## 4. Dependencies

### External Dependencies
- Tenant service (tenant validation)
- Staff service (staff assignment)
- Email service (notifications)
- Room/Facility service (auto-linking)

### Blocking Issues
- None - can start parallel

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| State machine complexity | High | Implement validators at each transition |
| Permission bypass | High | Check assigned staff before updates |
| Data consistency | Medium | Use transactions for status updates |
| Lost notifications | Medium | Implement retry logic for emails |

---

## 6. Success Criteria

- ✓ All status transitions working correctly
- ✓ Staff assignment enforced
- ✓ Tenant can create requests
- ✓ Manager can manage request lifecycle
- ✓ Audit trail complete
- ✓ List, search, filter working
- ✓ Response time < 500ms
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 7. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-5:** Backend core development
- **Week 6-7:** Backend management development
- **Week 8-9:** Frontend development
- **Week 10-12:** Testing & deployment

**Total:** 12 weeks
