# TASKS: Phân chia Chi tiết Đầu Việc - Đăng xuất

**Total Story Points:** ~18 points  
**Sprint Duration:** 2 weeks × 2 sprints = 4 weeks  
**Velocity:** ~9 points/sprint

---

## Epic 1: Backend Logout Service (8 points)

### Task 1.1: Logout API Contract (2 points)
**Duration:** 1 day  
**Description:**
- Define `POST /api/v1/auth/logout`
- Define standard response and error handling
- Include cookie clear behavior if needed

---

### Task 1.2: Token Revocation/Blacklist (4 points)
**Duration:** 2 days  
**Description:**
- Implement refresh token revoke/blacklist
- Ensure revoked tokens cannot be reused
- Support cleanup of expired entries

---

### Task 1.3: Logout Response Performance (2 points)
**Duration:** 1 day  
**Description:**
- Ensure endpoint responds < 300ms (p95)
- Optimize lookup/invalidation logic

---

## Epic 2: Frontend Integration (6 points)

### Task 2.1: Logout Action (3 points)
**Duration:** 1-2 days  
**Description:**
- Call backend logout endpoint
- Clear localStorage/sessionStorage
- Clear auth-related app state
- Redirect to login

---

### Task 2.2: Error Path Cleanup (2 points)
**Duration:** 1 day  
**Description:**
- If logout API returns 401 or 500, still purge client state
- Redirect to login

---

### Task 2.3: HttpOnly Cookie Handling (1 point)
**Duration:** 1 day  
**Description:**
- If using cookies, backend returns `Set-Cookie: Max-Age=0`
- Ensure client code handles logout success

---

## Epic 3: Testing (4 points)

### Task 3.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test backend token revocation
- Test response format

---

### Task 3.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test logout flow end-to-end
- Test state purge on 401/500 fallback

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | Backend logout service, revoke logic |
| Sprint 2 | 6 | Frontend integration, testing |

---

## Critical Dependencies

- Task 1.2 → Task 2.1 (backend revoke required before frontend call)
- Task 2.2 must be handled even when Task 1.1 fails
