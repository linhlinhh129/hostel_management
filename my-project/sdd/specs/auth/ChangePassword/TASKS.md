# TASKS: Phân chia Chi tiết Đầu Việc - Quên mật khẩu

**Total Story Points:** ~40 points  
**Sprint Duration:** 2 weeks × 4 sprints = 8 weeks  
**Velocity:** ~10 points/sprint

---

## Epic 1: Token & Email Flow (16 points)

### Task 1.1: Forgot Password API Contract (2 points)
**Duration:** 1 day  
**Description:**
- Define request payload and response message
- Ensure uniform response for all emails

---

### Task 1.2: Reset Password API Contract (2 points)
**Duration:** 1 day  
**Description:**
- Define request/response
- Define token invalid/expired error payload

---

### Task 1.3: Token Storage Design (4 points)
**Duration:** 2 days  
**Description:**
- Choose storage model (table or cache)
- Token TTL 15 minutes
- One-time use invalidation
- Rate-limit metadata storage

---

### Task 1.4: Email Enumeration Prevention (4 points)
**Duration:** 2 days  
**Description:**
- Implement uniform success response
- Avoid leaking user existence
- Log only internal diagnostics

---

### Task 1.5: Rate Limit per Email (4 points)
**Duration:** 2 days  
**Description:**
- Enforce max 3 request/hour per email
- Return generic success despite exceed
- Protect email delivery service

---

## Epic 2: Business Logic (12 points)

### Task 2.1: Forgot Password Service (4 points)
**Duration:** 2 days  
**Description:**
- Generate recovery token when email exists
- Send email with reset link
- Store token securely

---

### Task 2.2: Reset Password Service (4 points)
**Duration:** 2 days  
**Description:**
- Validate token and expiry
- Hash new password with bcrypt/argon2
- Invalidate token after use
- Return success

---

### Task 2.3: SQL Sanitization Utility (4 points)
**Duration:** 2 days  
**Description:**
- Build sanitize helper for Basic SQL
- Apply to email, token, password inputs
- Review for common SQLi patterns

---

## Epic 3: API Endpoint Implementation (8 points)

### Task 3.1: `POST /api/v1/auth/forgot-password` (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement endpoint
- Call forgot-password service
- Return uniform success response

---

### Task 3.2: `POST /api/v1/auth/reset-password` (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement endpoint
- Call reset-password service
- Return clear validation errors

---

### Task 3.3: Response Standardization (2 points)
**Duration:** 1 day  
**Description:**
- Standard success/error schema
- Map errors to codes: INVALID_TOKEN, TOKEN_EXPIRED, INVALID_EMAIL

---

## Epic 4: Testing (4 points)

### Task 4.1: Unit Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test token creation, expiry, one-time use
- Test rate limiting

---

### Task 4.2: Integration Tests (2 points)
**Duration:** 1 day  
**Description:**
- Test forgot/reset end-to-end
- Test uniform response for missing email

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 12 | Token design, email enumeration, rate-limit |
| Sprint 2 | 12 | Forgot-password + reset-password service |
| Sprint 3 | 10 | API endpoints, SQL sanitization |
| Sprint 4 | 6 | Testing, polish |

---

## Critical Dependencies

- Task 1.3 → Task 2.1/2.2 (token storage needed)
- Task 1.5 → Task 3.1 (rate limit needed before sending email)
- Task 2.2 → Task 3.2 (reset endpoint depends on service)
