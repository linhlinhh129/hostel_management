# PLAN.md — Kế hoạch Thực thi Admin Dashboard

**Status:** Planning
**Date:** 2026-06-19
**Priority:** High
**Estimated Duration:** 3–4 tuần

---

## 1. Tổng quan Giải pháp

Admin Dashboard là màn hình tổng hợp dữ liệu từ nhiều module — không có business logic riêng, chủ yếu là aggregation query và caching.

**Kiến trúc:**
- Backend API: 1 endpoint duy nhất `GET /api/v1/dashboard` trả về toàn bộ dữ liệu
- Caching: Cache response tối đa 60 giây (in-memory hoặc Redis)
- Frontend UI: KPI Cards, widgets thống kê, bảng hoạt động gần đây, bảng doanh thu theo cơ sở
- Resilience: Partial failure — nếu một nguồn lỗi, trả về null cho phần đó thay vì 500

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Xác định câu query, thiết kế response, caching strategy

**Công việc:**
- Xác định các aggregation query cần thiết (doanh thu, nhân sự, cơ sở, audit log)
- Thiết kế response schema đầy đủ
- Chọn cơ chế cache (in-memory vs Redis)
- Xác định timeout và fallback cho từng nguồn dữ liệu

**Deliverables:**
- Response schema
- Query plan
- Caching design document

---

### Giai đoạn 2: Backend Development (Tuần 2)

**Mục tiêu:** Implement API và business logic

**Công việc:**
- Implement DashboardService (aggregate từ FacilityService, EmployeeService, RevenueService, AuditLogService, NotificationService)
- Implement caching layer
- Implement partial failure handling
- Implement authorization (ADMIN only)
- Implement `GET /api/v1/dashboard` controller

**Key Features:**
- KPI aggregation
- Facility stats (total, active, draft, inactive)
- Employee stats (total, manager, operator)
- Recent activities (top 10 audit logs)
- Revenue by facility (current month, PAID only)

---

### Giai đoạn 3: Frontend Development (Tuần 3)

**Mục tiêu:** Implement UI Dashboard

**Công việc:**
- KPI Cards component (5 cards)
- Facility stats widget
- Employee stats widget
- Recent activities table
- Revenue by facility table
- Navigation links từ mỗi widget/card
- Empty state và error state cho từng widget

---

### Giai đoạn 4: Testing & Deployment (Tuần 4)

**Mục tiêu:** Testing, UAT, deployment

**Công việc:**
- Unit tests cho DashboardService
- Integration tests cho API endpoint
- Performance tests (response time < 1s)
- UAT
- Deployment

---

## 3. Key Technical Challenges

### Aggregation Performance
- Dashboard gọi nhiều service/query cùng lúc — cần chạy song song (async/parallel)
- Dùng cache 60 giây để giảm tải database

### Partial Failure Handling
- Nếu một service timeout hoặc lỗi, không được trả về 500
- Mỗi phần dữ liệu phải có fallback về `null` hoặc `0`

### Cache Invalidation
- Cache không cần invalidate theo sự kiện — TTL 60 giây là đủ cho dashboard overview
- Không cache theo user (tất cả admin thấy cùng dữ liệu)

---

## 4. Dependencies

### Phụ thuộc vào các module đã có
- FacilityService — thống kê cơ sở
- EmployeeService — thống kê nhân sự
- RevenueService (viewRevenue) — doanh thu tháng
- AuditLogService — hoạt động gần đây
- NotificationService — tổng thông báo

### Điều kiện tiên quyết
- Tất cả các module trên phải được implement trước hoặc song song
- Database schema đã có đủ bảng cần thiết

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Query aggregation chậm | High | Chạy song song, cache 60s |
| Một service lỗi làm hỏng toàn Dashboard | High | Partial failure pattern |
| Dữ liệu stale do cache | Low | TTL 60s là chấp nhận được với overview |
| Nhiều Admin truy cập đồng thời | Medium | Cache dùng chung, không per-user |

---

## 6. Success Criteria

- ✓ Dashboard load đủ 5 KPI Cards
- ✓ Thống kê cơ sở và nhân sự chính xác
- ✓ Hoạt động gần đây hiển thị đúng 10 bản ghi mới nhất
- ✓ Doanh thu theo cơ sở đúng tháng hiện tại, chỉ tính PAID
- ✓ Partial failure không làm trắng toàn bộ Dashboard
- ✓ Response time < 1 giây (P95)
- ✓ UAT passed

---

## 7. Timeline

- **Tuần 1:** Thiết kế & chuẩn bị
- **Tuần 2:** Backend development
- **Tuần 3:** Frontend development
- **Tuần 4:** Testing & deployment

**Tổng:** 4 tuần
