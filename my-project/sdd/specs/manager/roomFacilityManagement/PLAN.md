# PLAN: Kế hoạch Thực thi Vận hành Cơ sở được phân công

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** Medium  
**Estimated Duration:** 4-5 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Manager xem dashboard chỉ-đọc (read-only) của các cơ sở được phân công và danh sách phòng trong đó.

**Kiến trúc:**
- Backend API: List facilities, get facility detail, list rooms, get room detail
- Permission enforcement: Only assigned facilities visible
- Frontend UI: Facility dashboard, room list, room detail
- No create/edit/delete operations

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Design API contract, permission model

**Công việc:**
- Design facility access checks
- Define API contract (read-only)
- Design response formats
- Plan error scenarios

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** Implement backend APIs

**Công việc:**
- Implement facility access validation
- Implement get assigned facilities service
- Implement get facility detail service
- Implement list rooms service
- Implement get room detail service
- Add authorization checks

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** Implement UI dashboard

**Công việc:**
- Facility list/dashboard page
- Room list within facility
- Room detail view
- Search & filter

---

### Giai đoạn 4: Testing & Deployment (Tuần 5)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Permission Enforcement
- Every API call checks manager's facility access
- Return only assigned facilities
- Return only rooms from assigned facilities

### Read-Only Operations
- No create endpoints
- No update endpoints
- No delete endpoints
- GET operations only

### Data Scope
- Manager sees only assigned facilities
- Manager sees only rooms in assigned facilities
- No cross-facility access

---

## 4. Dependencies

### External Dependencies
- Employee facility assignment (EmployeeFacility)
- Facility data (from facilityManagement)
- Room data (from roomManagement)

### Blocking
- Need Employee facility assignment complete

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Permission bypass | High | Implement checks at service & API layer |
| Performance issues | Low | Optimize queries with proper joins |

---

## 6. Success Criteria

- ✓ Manager can view only assigned facilities
- ✓ Manager can view rooms only in assigned facilities
- ✓ All GET operations working
- ✓ Permission enforcement working
- ✓ Response time < 300ms
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 7. Timeline

- **Week 1:** Design & preparation
- **Week 2:** Backend development
- **Week 3-4:** Frontend development
- **Week 5:** Testing & deployment

**Total:** 5 weeks
