# PLAN: Kế hoạch Thực thi Danh sách điện nước

**Status:** Planning  
**Date:** 2026-06-11  
**Priority:** Medium  
**Estimated Duration:** 5-6 weeks

---

## 1. Tổng quan Giải pháp

Feature Danh sách điện nước giúp nhân viên vận hành kiểm tra số liệu tiêu thụ nước và điện cho các phòng hoặc cơ sở, với phân trang, bộ lọc và tổng hợp giá trị.

**Kiến trúc:**
- Backend API: `GET /api/v1/meter-readings`
- Server-side pagination
- Filters: facility_id, room_id, meter_type, date range
- Frontend UI: table with summary row, filter inputs

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** xác định dữ liệu meter reading và yêu cầu filter.

**Công việc:**
- Thiết kế API contract
- Xác định trường cần hiển thị
- Xác định chỉ số DB cho filter và ngày tháng

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** triển khai API list meter data.

**Công việc:**
- Implement list service
- Implement filters and pagination
- Add summary totals if cần

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** xây dựng UI và filter controls.

**Công việc:**
- Render reading rows
- Add filter controls
- Show summary values

---

### Giai đoạn 4: Testing & Deployment (Tuần 5-6)

**Mục tiêu:** kiểm tra performance và chính xác số liệu.

**Công việc:**
- Unit tests service logic
- Integration tests API filter
- Frontend validation

---

## 3. Key Technical Challenges

### Performance
- Response < 500ms p95
- Efficient aggregate queries

### Data Accuracy
- Correct range filtering by date
- Show correct totals for electricity/water

### User Scoping
- Only show readings for assigned facility/room

---

## 4. Success Criteria

- ✓ Meter list loads with pagination
- ✓ Filters work correctly by date/type/location
- ✓ Summary totals display accurately
- ✓ Response time < 500ms
- ✓ Access limited to operator scope
- ✓ Test coverage >= 80%

---

## 5. Timeline

- **Week 1:** Design
- **Week 2:** Backend implementation
- **Week 3-4:** Frontend implementation
- **Week 5-6:** Testing & deployment

**Total:** 6 weeks
