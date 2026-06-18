# PLAN: Kế hoạch Thực thi Quên mật khẩu (Forgot Password)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 6-7 weeks

---

## 1. Tổng quan Giải pháp

Feature Quên mật khẩu cung cấp luồng tự phục vụ an toàn để người dùng yêu cầu link khôi phục và đặt mật khẩu mới khi họ quên mật khẩu hiện tại.

**Kiến trúc:**
- Backend API: `POST /api/v1/auth/forgot-password` và `POST /api/v1/auth/reset-password`
- Security: token chỉ dùng một lần, TTL 15 phút, response uniform để chống email enumeration
- Rate limiting: tối đa 3 request/giờ cho mỗi email
- Password hashing: bcrypt/argon2
- SQL Sanitization: sanitize dữ liệu đầu vào trước khi dùng Basic SQL Statement

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** định nghĩa luồng token, bảo mật email enumeration và các constraint rate limit.

**Công việc:**
- Design API contract cho forgot-password và reset-password
- Thiết kế lưu token (token table hoặc cache TTL)
- Thiết kế thông điệp uniform để tránh spam enumeration
- Define sanitize utility for SQL inputs

---

### Giai đoạn 2: Backend Development (Tuần 2-4)

**Mục tiêu:** triển khai token lifecycle, rate-limiter, email send, reset password.

**Công việc:**
- Implement forgot-password service
- Implement reset-password service
- Implement token creation, validation, expiration, one-time use
- Implement rate limiting per email
- Implement password hashing with bcrypt/argon2

---

### Giai đoạn 3: Frontend Integration (Tuần 5)

**Mục tiêu:** tích hợp form yêu cầu và form reset password với link token.

**Công việc:**
- Forgot-password form
- Reset-password form
- Common success page flow
- Handle invalid/expired token gracefully

---

### Giai đoạn 4: Testing & Deployment (Tuần 6-7)

**Mục tiêu:** kiểm thử bảo mật, token expiry, email enum response, rollback.

**Công việc:**
- Unit tests cho business logic
- Integration tests cho token flow
- Security tests cho SQL sanitization

---

## 3. Key Technical Challenges

### Email Enumeration Prevention
- Response unify success message cho email tồn tại và không tồn tại
- Không tiết lộ trạng thái user existence qua response

### Token Lifecycle
- Token TTL = 15 phút
- One-time use token
- Invalidate token khi đã sử dụng

### SQL Injection Protection
- Dữ liệu input phải sanitize trước khi build Basic SQL string
- Dùng utility chung cho tất cả truy vấn basic SQL

---

## 4. Success Criteria

- ✓ Forgot-password gửi message thành công đồng nhất
- ✓ Reset-password chỉ hoạt động với token hợp lệ và chưa hết hạn
- ✓ Token bị hủy sau khi dùng
- ✓ Rate limit 3 requests/giờ
- ✓ Password mới được hash an toàn
- ✓ Response time < 500ms (p95)
- ✓ >= 80% test coverage

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2-4:** Backend implementation
- **Week 5:** Frontend integration
- **Week 6-7:** Testing & deployment

**Total:** 7 weeks
