# PLAN: Kế hoạch Thực thi Quản lý Người phụ thuộc (Tenant)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** Medium  
**Estimated Duration:** 4-5 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Tenant xem danh sách và chi tiết người phụ thuộc đã được Ban Quản Lý đăng ký (read-only).

**Kiến trúc:**
- Backend API: List, detail (read-only)
- Permission: Only own dependents visible
- Frontend UI: List, detail view
- Database: Using existing Dependent data

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Design API contract, permission model

**Công việc:**
- Define API endpoints
- Plan access control (only own dependents)
- Plan list/detail response formats

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** Implement backend APIs

**Công việc:**
- Implement list dependents service (tenant-scoped)
- Implement get detail service
- Implement access control

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** Implement UI

**Công việc:**
- Dependent list component
- Detail view
- Empty state handling

---

### Giai đoạn 4: Testing & Deployment (Tuần 5)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### Permission Enforcement
- Tenant can only see own dependents
- Filter by current tenant ID

### Read-Only Operations
- No create/edit/delete for tenant
- Only list & view

---

## 4. Success Criteria

- ✓ List/detail working
- ✓ Tenant-scoped filtering working
- ✓ Access control enforced
- ✓ Response time < 300ms (P95)
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2:** Backend development
- **Week 3-4:** Frontend development
- **Week 5:** Testing & deployment

**Total:** 5 weeks
