# PLAN: Kế hoạch Thực thi Đăng xuất

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** Medium  
**Estimated Duration:** 3-4 weeks

---

## 1. Tổng quan Giải pháp

Feature Đăng xuất đảm bảo phiên làm việc bị kết thúc an toàn, token bị thu hồi và dữ liệu nhạy cảm ở frontend được dọn sạch.

**Kiến trúc:**
- Backend API: `POST /api/v1/auth/logout`
- Token revocation: blacklist refresh token hoặc revoke token
- Frontend: clear local storage/session, purge client state
- HttpOnly cookie: xóa bằng header `Set-Cookie: Max-Age=0` nếu dùng cookie

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định cơ chế revoke token và client cleanup.

**Công việc:**
- Thiết kế API contract và blacklist flow
- Xác định cách xóa cookie nếu dùng HttpOnly
- Xác định front-end purge sequence

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** triển khai revoke token và logout endpoint.

**Công việc:**
- Implement token blacklist/revocation
- Implement logout endpoint
- Support expired token cleanup

---

### Giai đoạn 3: Frontend Integration (Tuần 3)

**Mục tiêu:** gọi API logout và dọn sạch state.

**Công việc:**
- Implement logout action
- Clear local/session storage
- Redirect to login
- Handle 401 response by purge state

---

### Giai đoạn 4: Testing & Deployment (Tuần 4)

**Mục tiêu:** kiểm thử logout success và error fallback.

**Công việc:**
- Unit tests token revocation
- Integration tests logout flow
- UI test for state cleanup

---

## 3. Key Technical Challenges

### Token Revocation
- Backend phải blacklist refresh token
- Logout yêu cầu hoàn thành trong 300ms (p95)

### Client Cleanup
- Phải xóa toàn bộ dữ liệu nhạy cảm dù API trả lỗi 401
- Nếu dùng HttpOnly cookie, backend phải trả header xóa cookie

### Error Handling
- Nếu API trả 401, frontend vẫn purge state và redirect

---

## 4. Success Criteria

- ✓ Logout API trả về thành công
- ✓ Token bị revoke/blacklist
- ✓ Frontend xóa sạch state sesion
- ✓ HttpOnly cookie bị xóa nếu dùng cookie
- ✓ Response time < 300ms (p95)
- ✓ >= 80% test coverage

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2:** Backend implementation
- **Week 3:** Frontend integration
- **Week 4:** Testing & deployment

**Total:** 4 weeks
