# PLAN: Kế hoạch Thực thi Dashboard Tenant

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 4-5 weeks

---

## 1. Tổng quan Giải pháp

Feature Dashboard cung cấp màn hình tổng quan cho Tenant sau khi đăng nhập, hiển thị thống kê quan trọng và cung cấp điều hướng nhanh tới các chức năng chính.

**Kiến trúc:**
- Backend API: Get dashboard data (aggregated stats)
- Frontend UI: Dashboard widgets with quick links
- Data aggregation: Unpaid invoices, pending requests, recent notifications, dependents

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Design dashboard layout, API response format

**Công việc:**
- Define dashboard data requirements
- Design API response
- Plan widget layout
- Plan first-login flow (password change)

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** Implement dashboard data aggregation

**Công việc:**
- Implement dashboard service (aggregate data from multiple sources)
- Implement first-login check
- Implement caching for performance

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** Implement UI

**Công việc:**
- Dashboard layout with widgets
- First-login password change form
- Quick navigation links

---

### Giai đoạn 4: Testing & Deployment (Tuần 5)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Aspects

### First Login Flow
- Check password change flag
- Force password change before dashboard access
- Redirect to password change form if needed

### Data Aggregation
- Unpaid/overdue invoice count
- Pending/in-progress request count
- Recent notification count (30 days)
- Dependent count

### Performance
- Cache aggregated data
- Minimize database queries

---

## 4. Success Criteria

- ✓ Dashboard data aggregated correctly
- ✓ First-login flow working
- ✓ Quick links navigate correctly
- ✓ Stats updated in real-time
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
