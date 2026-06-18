# PLAN: Kế hoạch Thực thi Danh sách yêu cầu sửa chữa

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** High  
**Estimated Duration:** 5-6 weeks

---

## 1. Tổng quan Giải pháp

Feature Danh sách yêu cầu sửa chữa cho phép nhân viên vận hành xem nhanh ticket được giao, lọc theo trạng thái, thể loại, cơ sở và phòng, đồng thời điều hướng sang chi tiết khi cần.

**Kiến trúc:**
- Backend API: `GET /api/v1/requests`
- Server-side pagination
- Filters: status, category_id, facility_id, room_id
- Frontend UI: table/list, filters, empty state

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định bộ lọc, cấu trúc payload và paging.

**Công việc:**
- Thiết kế API contract
- Xác định index DB: `assignee_id`, `status`, `facility_id`
- Xác định default page size và sort order

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** triển khai API list request với pagination và filter.

**Công việc:**
- Implement list service
- Implement filter logic
- Implement server-side pagination
- Ensure response < 500ms

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** xây dựng UI list request và filter.

**Công việc:**
- Render request rows
- Implement filter controls
- Implement empty state
- Support navigation to detail

---

### Giai đoạn 4: Testing & Deployment (Tuần 5-6)

**Mục tiêu:** tối ưu performance và validate filter behaviour.

**Công việc:**
- Unit tests service logic
- Integration tests API with filters
- UI tests for no-data state

---

## 3. Key Technical Challenges

### Pagination & Performance
- Server-side pagination required
- Response under 500ms with filters
- Proper DB indexing

### Filter Logic
- Support filter combinations cleanly
- Maintain stable sort order by appointment_date

### Assignment Scope
- Only requests assigned to current user
- Filter by assignee by authorization

---

## 4. Success Criteria

- ✓ List loads with server-side pagination
- ✓ Filters work correctly
- ✓ Response time < 500ms
- ✓ Empty state shown when no data
- ✓ Access only own assigned requests
- ✓ >= 80% code coverage

---

## 5. Timeline

- **Week 1:** Design & preparation
- **Week 2:** Backend implementation
- **Week 3-4:** Frontend implementation
- **Week 5-6:** Testing & deployment

**Total:** 6 weeks
