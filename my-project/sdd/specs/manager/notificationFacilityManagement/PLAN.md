# PLAN: Kế hoạch Thực thi Quản lý Thông báo cho Ban quản lý

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 8-10 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Manager tạo thông báo chỉ cho cơ sở được phân công (không được gửi toàn hệ thống). Thông báo có thể gửi theo cơ sở hoặc phòng cụ thể.

**Kiến trúc:**
- Backend API: Create, list, detail notification (with facility scope)
- Facility access control: Validate manager assignment
- Frontend UI: Create form, list, detail, search
- Audit Log: Log notification creation & access
- Recipient resolution: Query tenants in facility/room

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** Design permission model, API contract

**Công việc:**
- Design facility access control
- Define API contract with scope checks
- Design recipient resolution by facility/room
- Plan validation rules

---

### Giai đoạn 2: Backend Development (Tuần 3-5)

**Mục tiêu:** Implement backend with access control

**Công việc:**
- Implement facility access validation service
- Implement create notification with scope enforcement
- Implement list notifications (facility-scoped)
- Implement get detail with access control
- Implement recipient resolution
- Implement audit logging

**Key Features:**
- Prevent ALL (global) notifications
- Prevent access to facilities not assigned
- Validate recipient scope
- Log all operations

---

### Giai đoạn 3: Frontend Development (Tuần 6-7)

**Mục tiêu:** Implement UI with scope enforcement

**Công việc:**
- Notification list (facility-scoped)
- Create form (facility/room selectors)
- Detail view
- Search & pagination

---

### Giai đoạn 4: Testing & Deployment (Tuần 8-10)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Challenges

### Facility Access Control
- Must enforce at API layer
- Check manager's assigned facilities
- Prevent access to other facilities

### Recipient Type Restrictions
- ALL: Not allowed for manager
- FACILITY: Only allowed if in assigned list
- ROOM: Only allowed if room's facility is assigned

### Permission Validation
- Every API call must check facility access
- Return 403 for unauthorized access
- Clear error messages

---

## 4. Dependencies

### External Dependencies
- Employee facility assignment (EmployeeFacility table)
- Tenant list (for recipient resolution)
- Room data (for room-scoped notifications)

### Blocking
- Need Employee facility assignment to be complete

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Permission bypass | High | Implement checks at service & API layer |
| Recipient resolution errors | Medium | Comprehensive testing with various scenarios |
| Performance issues | Medium | Optimize queries with proper joins |

---

## 6. Success Criteria

- ✓ Facility access enforced
- ✓ ALL notifications prevented
- ✓ Recipient resolution accurate
- ✓ Create/list/detail working
- ✓ Search & pagination working
- ✓ Response time < 500ms
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 7. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-5:** Backend development
- **Week 6-7:** Frontend development
- **Week 8-10:** Testing & deployment

**Total:** 10 weeks
