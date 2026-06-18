# TASKS: Phân chia Chi tiết Đầu Việc - Dashboard Tenant

**Total Story Points:** ~24 points  
**Sprint Duration:** 2 weeks × 2.5 sprints = 5 weeks  
**Velocity:** ~9.6 points/sprint

---

## Epic 1: Backend Services (8 points)

### Task 1.1: Dashboard Data Aggregation Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement getDashboard(tenantId) service
- Get unpaid/overdue invoice count
- Get pending/in-progress request count
- Get recent notification count (30 days)
- Get dependent count
- Cache results

---

### Task 1.2: First Login Check Service (2 points)
**Duration:** 1 day  
**Description:**
- Implement isFirstLogin(tenantId) check
- Check password change flag

---

### Task 1.3: Caching Strategy (2 points)
**Duration:** 1 day  
**Description:**
- Implement cache invalidation
- Short TTL for frequently changing data

---

## Epic 2: API (6 points)

### Task 2.1: Dashboard Data Endpoint (3 points)
**Duration:** 1-2 days  
**Description:**
- GET /api/v1/tenant/dashboard
- Return aggregated stats DTO
- First-login flag

---

### Task 2.2: First Login Check (2 points)
**Duration:** 1 day  
**Description:**
- Check in middleware or endpoint
- Return flag to client

---

### Task 2.3: Response Formatting (1 point)
**Duration:** 1 day  
**Description:**
- Standard response format

---

## Epic 3: Frontend Development (8 points)

### Task 3.1: First Login Redirect (2 points)
**Duration:** 1 day  
**Description:**
- Check first-login flag on app load
- Redirect to password change form if needed
- Block dashboard access until password changed

---

### Task 3.2: Dashboard Layout (3 points)
**Duration:** 1-2 days  
**Description:**
- Create dashboard main page
- Welcome card with tenant name
- Stat widgets: unpaid invoices, pending requests, notifications, dependents

---

### Task 3.3: Quick Navigation Links (2 points)
**Duration:** 1 day  
**Description:**
- Create navigation cards to main features
- Clicking card navigates to feature page

---

### Task 3.4: UI Polish (1 point)
**Duration:** 1 day  
**Description:**
- Review consistency
- Test responsive

---

## Epic 4: Testing (2 points)

### Task 4.1: Unit & Integration Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test aggregation service
- >= 80% coverage

---

### Task 4.2: E2E & UAT (1 point)
**Duration:** 1 day  
**Description:**
- E2E tests
- Test first-login flow
- Manual UAT

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 10 | Aggregation service, API, first-login |
| Sprint 2 | 10 | Dashboard layout, navigation |
| Sprint 3 | 4 | Testing, deployment |

---

## Critical Dependencies

- Task 1.1-1.3 → Task 2.1-2.3 (services needed for API)
- Task 2.1-2.3 → Task 3.1-3.4 (API needed for frontend)
- Task 3.1 must be done before Task 3.2 (first-login check required)
