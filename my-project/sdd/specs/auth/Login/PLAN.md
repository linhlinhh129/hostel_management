# PLAN: Kế hoạch Thực thi Đăng nhập

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 4-5 weeks

---

## 1. Tổng quan Giải pháp

Feature Đăng nhập kiểm soát truy cập người dùng, bảo vệ khỏi brute-force, hỗ trợ mật khẩu tạm thời và yêu cầu đổi mật khẩu khi cần thiết.

**Kiến trúc:**
- Backend API: `POST /api/v1/auth/login`
- Logic bảo mật: lockout sau 5 lần sai liên tiếp, 1 phút khóa, yêu cầu đổi mật khẩu tạm
- Token: restricted token cho đổi mật khẩu lần đầu, token truy cập chính thức cho hoạt động nghiệp vụ
- Frontend: form đăng nhập, phản hồi lỗi, điều hướng đổi mật khẩu khi requirePasswordChange=true

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định cơ chế lockout, yêu cầu đổi mật khẩu, cấu trúc token.

**Công việc:**
- Thiết kế API request/response
- Thiết kế cơ chế đếm sai password và khóa theo `username`
- Thiết kế restricted token cho lần đăng nhập đầu tiên bằng mật khẩu tạm
- Phân tích các luồng lỗi và mã lỗi chuẩn

---

### Giai đoạn 2: Backend Development (Tuần 2-3)

**Mục tiêu:** triển khai xác thực, lockout, token và logic yêu cầu đổi mật khẩu.

**Công việc:**
- Implement login service
- Implement wrong-password counter và lockout 60s
- Implement restricted token generation
- Implement response payload theo spec

---

### Giai đoạn 3: Frontend & Integration (Tuần 4)

**Mục tiêu:** chạy luồng đăng nhập, handle requirePasswordChange, show errors.

**Công việc:**
- Form đăng nhập client-side
- Handle 401, 403, 404 và requirePasswordChange
- Redirect tới đổi mật khẩu khi token hạn chế

---

### Giai đoạn 4: Testing & Deployment (Tuần 5)

**Mục tiêu:** kiểm thử bảo mật, performance, flow success.

**Công việc:**
- Unit tests service logic
- Integration tests API
- QA validate lockout và first-login flow

---

## 3. Key Technical Challenges

### Lockout logic
- Đếm sai password theo `username`
- Khóa truy cập 60 giây sau 5 lần sai
- Trả về 403 với mã `LOGIN_DISABLED_1MIN`

### Restricted token
- Token tạm thời chỉ dùng để đổi mật khẩu mới
- TTL = 600 giây
- Không được phép truy cập tài nguyên nghiệp vụ khác

### Performance
- API response p95 <= 500ms
- Cơ chế lockout và xác thực nhẹ

---

## 4. Success Criteria

- ✓ Đăng nhập thành công với token chính thức
- ✓ Đăng nhập lần đầu bằng mật khẩu tạm buộc đổi mật khẩu
- ✓ Khóa sau 5 lần sai, 1 phút
- ✓ Response đúng mã lỗi/HTTP status
- ✓ Thực thi dưới 500ms (p95)
- ✓ Coverage test >= 80%

---

## 5. Timeline

- **Week 1:** Thiết kế & workflow
- **Week 2-3:** Backend implementation
- **Week 4:** Frontend/integration
- **Week 5:** Testing & deployment

**Total:** 5 weeks
