# PLAN: Kế hoạch Thực thi Quản lý Yêu cầu (Tenant)

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 8-10 weeks

---

## 1. Tổng quan Giải pháp

Feature cho phép Tenant tạo yêu cầu hỗ trợ, xem danh sách yêu cầu, xem chi tiết, theo dõi trạng thái xử lý.

**Kiến trúc:**
- Backend API: Create, list, detail request
- States: PENDING → IN_PROGRESS → COMPLETED/REJECTED
- Frontend UI: Create form, list with status, detail with history
- Database: Request, RequestAttachment tables
- Audit Log: Log state changes

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1-2)

**Mục tiêu:** Design request schema, lifecycle, attachment handling

**Công việc:**
- Design Request entity & table
- Define API contract
- Plan request types/categories
- Plan attachment handling (JPG/PNG, max 5MB)
- Plan state machine

---

### Giai đoạn 2: Backend Development (Tuần 3-5)

**Mục tiêu:** Implement backend

**Công việc:**
- Implement Request entity & repository
- Implement create request service
- Implement list/detail services
- Implement attachment storage
- Implement state tracking

---

### Giai đoạn 3: Frontend Development (Tuần 6-8)

**Mục tiêu:** Implement UI

**Công việc:**
- Create request form with category selector
- Request list with status
- Detail view with history
- Attachment preview

---

### Giai đoạn 4: Testing & Deployment (Tuần 9-10)

**Mục tiêu:** Testing, UAT, deployment

---

## 3. Key Technical Challenges

### Request Lifecycle
- State machine: PENDING → IN_PROGRESS → COMPLETED/REJECTED
- Only manager can change state
- Tenant can only view

### Attachment Management
- Accept JPG, JPEG, PNG
- Max 5MB per file
- Store securely
- Validate on upload

### Status Tracking
- Show current status
- Show status history with timestamps

---

## 4. Dependencies

- Need request category/type definitions
- Need file storage infrastructure
- Need request management in manager system

---

## 5. Success Criteria

- ✓ Create request working
- ✓ List/detail working
- ✓ Attachment upload/preview working
- ✓ Status tracking correct
- ✓ Tenant can only see own requests
- ✓ Response time < 500ms (P95)
- ✓ >= 80% code coverage
- ✓ UAT passed

---

## 6. Timeline

- **Week 1-2:** Design & preparation
- **Week 3-5:** Backend development
- **Week 6-8:** Frontend development
- **Week 9-10:** Testing & deployment

**Total:** 10 weeks
