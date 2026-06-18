# TASKS: Phân chia Chi tiết Đầu Việc - Đăng nhập

**Total Story Points:** ~30 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~10 points/sprint

---

## Epic 1: Authentication Service (12 points)

### Task 1.1: Login Request Validation (2 points)
**Duration:** 1 day  
**Description:**
- Validate `username` and `password` presence
- Validate request schema
- Return `INVALID_CREDENTIALS` for invalid format only if needed

---

### Task 1.2: Wrong Password Counter & Lockout (4 points)
**Duration:** 2 days  
**Description:**
- Implement counter by `username`
- Lock account after 5 consecutive failures
- Store lockout timestamp
- Return HTTP 403 + `LOGIN_DISABLED_1MIN`

---

### Task 1.3: Login Logic (4 points)
**Duration:** 2 days  
**Description:**
- Authenticate credentials safely
- Handle user not found → 404 `USER_NOT_FOUND`
- Handle invalid credentials → 401 `INVALID_CREDENTIALS`
- Check lockout before auth

---

### Task 1.4: Token Issuance (2 points)
**Duration:** 1 day  
**Description:**
- Issue restricted token for first-login temporary password
- Issue standard access token for normal login
- Include `requirePasswordChange` and `expiresIn`

---

## Epic 2: Backend API & Security (10 points)

### Task 2.1: API Endpoint Implementation (3 points)
**Duration:** 1-2 days  
**Description:**
- POST /api/v1/auth/login
- Implement request/response DTOs
- Add HTTP status mapping

---

### Task 2.2: Authorization/Scope Flagging (3 points)
**Duration:** 1-2 days  
**Description:**
- Flag restricted token scope for password reset only
- Ensure it cannot access business APIs

---

### Task 2.3: Response Standardization (2 points)
**Duration:** 1 day  
**Description:**
- Standard success/error payloads
- Return success object with token and flags

---

### Task 2.4: Rate-Limit Observation (2 points)
**Duration:** 1 day  
**Description:**
- Record failed login attempts per minute if needed
- Integrate with existing rate limiter architecture

---

## Epic 3: Frontend Integration & Error Handling (6 points)

### Task 3.1: Login Form Integration (2 points)
**Duration:** 1 day  
**Description:**
- Submit username/password to API
- Handle response success/failure

---

### Task 3.2: Redirect on requirePasswordChange (2 points)
**Duration:** 1 day  
**Description:**
- Redirect user to change password page
- Block access to dashboard when true

---

### Task 3.3: Error Display (2 points)
**Duration:** 1 day  
**Description:**
- Show messages for 401, 403, 404
- Show generic error for other failures

---

## Epic 4: Testing (2 points)

### Task 4.1: Unit Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test login service, lockout, token issuance

---

### Task 4.2: Integration Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test API responses for success and failure scenarios

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | Auth service, lockout logic |
| Sprint 2 | 10 | API endpoint, token scope, frontend flow |
| Sprint 3 | 8 | Testing, polish |

---

## Critical Dependencies

- Task 1.2 → Task 1.3 (lockout required before auth)
- Task 1.4 → Task 2.1 (token generation needed by endpoint)
- Task 2.2 → frontend redirect logic in Task 3.2
