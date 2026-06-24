# TASKS.md — Phân chia Chi tiết Đầu Việc - Admin Dashboard

**Total Story Points:** ~38 points
**Sprint Duration:** 2 tuần × 2 sprints = 4 tuần
**Velocity:** ~19 points/sprint

---

## Epic 1: Backend Infrastructure (10 points)

### Task 1.1: Dashboard Query Design (3 points)
**Assignee:** Backend Developer
**Duration:** 2 ngày
**Mô tả:**
- Thiết kế và tối ưu các aggregation query:
  - KPI: doanh thu tháng, tổng cơ sở, tổng nhân sự ACTIVE, tổng thông báo, audit log hôm nay
  - Facility stats: COUNT theo status (ACTIVE, DRAFT, INACTIVE)
  - Employee stats: COUNT theo role (MANAGER, OPERATOR)
  - Recent activities: TOP 10 audit logs ORDER BY createdAt DESC
  - Revenue by facility: SUM(amount) WHERE status = PAID AND tháng hiện tại GROUP BY facilityId
- Đánh giá EXPLAIN PLAN cho từng query
- Xác định index cần thiết

**Acceptance Criteria:**
- ✓ Tất cả query chạy đúng kết quả
- ✓ Mỗi query hoàn thành trong < 200ms
- ✓ Index được tạo đầy đủ

---

### Task 1.2: DashboardService Implementation (4 points)
**Assignee:** Backend Developer
**Duration:** 2–3 ngày
**Mô tả:**
- Implement `DashboardService.getDashboardData()`
- Gọi song song các service: FacilityService, EmployeeService, RevenueService, AuditLogService, NotificationService
- Implement partial failure handling: nếu một service lỗi, trả về `null` cho phần đó, không throw exception
- Map kết quả sang `DashboardResponse` DTO

**Acceptance Criteria:**
- ✓ Service tổng hợp đủ 5 phần dữ liệu
- ✓ Các call chạy song song (không tuần tự)
- ✓ Partial failure không ảnh hưởng các phần còn lại
- ✓ DTO mapping đúng schema

**Unit Tests:**
- Test tổng hợp đủ 5 phần
- Test partial failure (mock một service throw exception)
- Test empty data (tất cả module chưa có dữ liệu)

---

### Task 1.3: Caching Layer (3 points)
**Assignee:** Backend Developer
**Duration:** 1–2 ngày
**Mô tả:**
- Implement cache với TTL = 60 giây cho `getDashboardData()`
- Cache dùng chung cho tất cả Admin (không per-user)
- Cache key: `dashboard:admin`
- Implement cache eviction khi TTL hết

**Acceptance Criteria:**
- ✓ Response được cache 60 giây
- ✓ Cache hit không query lại database
- ✓ Cache miss query đầy đủ các nguồn

---

## Epic 2: Dashboard API (8 points)

### Task 2.1: Dashboard Controller (3 points)
**Assignee:** Backend Developer
**Duration:** 1–2 ngày
**Mô tả:**
- Implement `GET /api/v1/dashboard`
- Authorization: chỉ ADMIN được truy cập
- Gọi `DashboardService.getDashboardData()`
- Map sang response format chuẩn `{"success": true, "data": {...}}`
- Xử lý lỗi: 401 UNAUTHORIZED, 403 FORBIDDEN

**Acceptance Criteria:**
- ✓ Endpoint trả về đúng schema
- ✓ Authorization enforced
- ✓ Response format chuẩn
- ✓ Response time < 1s (P95)

---

### Task 2.2: Response DTO & Validation (2 points)
**Assignee:** Backend Developer
**Duration:** 1 ngày
**Mô tả:**
- Tạo `DashboardResponse` DTO với đầy đủ fields
- Tạo nested DTOs: `KpiDTO`, `FacilityStatsDTO`, `EmployeeStatsDTO`, `RecentActivityDTO`, `FacilityRevenueDTO`
- Đảm bảo giá trị numeric không bao giờ là `null` — default về `0`

**Acceptance Criteria:**
- ✓ DTOs đúng với schema trong SPEC
- ✓ Numeric fields default = 0 khi không có dữ liệu
- ✓ Serialization đúng format JSON

---

### Task 2.3: Integration Tests (3 points)
**Assignee:** Backend Developer / QA
**Duration:** 1–2 ngày
**Mô tả:**
- Test `GET /api/v1/dashboard` với dữ liệu đầy đủ
- Test với dữ liệu rỗng (hệ thống mới)
- Test partial failure (mock service lỗi)
- Test authorization (401, 403)
- Test response time < 1s

**Acceptance Criteria:**
- ✓ Tất cả test case pass
- ✓ Partial failure test pass
- ✓ Performance test pass

---

## Epic 3: Frontend Development (14 points)

### Task 3.1: Dashboard Layout & KPI Cards (4 points)
**Assignee:** Frontend Developer
**Duration:** 2–3 ngày
**Mô tả:**
- Tạo trang Dashboard với layout grid
- Implement 5 KPI Cards:
  - Tổng doanh thu tháng
  - Tổng số cơ sở
  - Tổng nhân sự ACTIVE
  - Tổng thông báo
  - Audit Log hôm nay
- Mỗi card có icon, label, giá trị số, link điều hướng
- Empty state: hiển thị 0 thay vì trống

**Acceptance Criteria:**
- ✓ 5 KPI Cards hiển thị đúng
- ✓ Click card → điều hướng đúng module
- ✓ Giá trị 0 hiển thị đúng (không trống)
- ✓ Responsive design

---

### Task 3.2: Widget Thống kê Cơ sở & Nhân sự (3 points)
**Assignee:** Frontend Developer
**Duration:** 1–2 ngày
**Mô tả:**
- Implement widget thống kê cơ sở (total, active, draft, inactive)
- Implement widget thống kê nhân sự (total, manager, operator)
- Hiển thị dạng summary card với breakdown

**Acceptance Criteria:**
- ✓ Số liệu hiển thị đúng
- ✓ Breakdown rõ ràng
- ✓ Responsive design

---

### Task 3.3: Widget Hoạt động Gần đây (3 points)
**Assignee:** Frontend Developer
**Duration:** 1–2 ngày
**Mô tả:**
- Implement bảng hiển thị 10 hoạt động gần nhất
- Columns: Thời gian, Người thực hiện, Hành động
- Format thời gian: `dd/MM/yyyy HH:mm`
- Empty state: "Chưa có hoạt động nào"
- Link "Xem tất cả" → điều hướng tới module Audit Log

**Acceptance Criteria:**
- ✓ Bảng hiển thị đúng tối đa 10 bản ghi
- ✓ Format thời gian đúng
- ✓ Empty state hiển thị đúng
- ✓ Link điều hướng hoạt động

---

### Task 3.4: Widget Doanh thu theo Cơ sở (4 points)
**Assignee:** Frontend Developer
**Duration:** 2 ngày
**Mô tả:**
- Implement bảng doanh thu tháng hiện tại theo cơ sở
- Columns: Tên cơ sở, Doanh thu tháng (format tiền VNĐ)
- Hiển thị label tháng hiện tại (vd: "Doanh thu tháng 6/2026")
- Empty state: "Chưa có dữ liệu doanh thu"
- Link "Xem chi tiết" → điều hướng tới module Báo cáo doanh thu

**Acceptance Criteria:**
- ✓ Bảng hiển thị đúng dữ liệu
- ✓ Format tiền đúng (vd: 20.000.000 đ)
- ✓ Label tháng đúng tháng hiện tại
- ✓ Empty state và link điều hướng hoạt động

---

## Epic 4: Testing & Deployment (6 points)

### Task 4.1: E2E Testing (2 points)
**Assignee:** QA Engineer
**Duration:** 1–2 ngày
**Mô tả:**
- Test toàn bộ Dashboard end-to-end với dữ liệu thực
- Test partial failure scenario
- Test empty state (hệ thống mới không có dữ liệu)
- Test authorization (login vs không login, ADMIN vs MANAGER)

**Acceptance Criteria:**
- ✓ Tất cả scenario pass
- ✓ Không có lỗi giao diện

---

### Task 4.2: Performance Testing (2 points)
**Assignee:** QA Engineer / Backend Developer
**Duration:** 1 ngày
**Mô tả:**
- Test response time với dataset thực tế
- Test với nhiều Admin truy cập đồng thời (10 concurrent users)
- Verify cache hoạt động đúng (cache hit vs miss)

**Acceptance Criteria:**
- ✓ P95 response time < 1 giây
- ✓ Cache hit giảm load database rõ rệt

---

### Task 4.3: Deployment (2 points)
**Assignee:** DevOps / Tech Lead
**Duration:** 1–2 ngày
**Mô tả:**
- Deploy backend API
- Deploy frontend
- Verify cache configuration trên môi trường production
- Go-live verification

**Acceptance Criteria:**
- ✓ Deployment thành công
- ✓ Dashboard hoạt động đúng trên production

---

## Summary by Sprint

| Sprint | Duration | Points | Focus |
|--------|----------|--------|-------|
| Sprint 1 | Tuần 1–2 | 21 | Backend: queries, service, cache, API |
| Sprint 2 | Tuần 3–4 | 17 | Frontend, testing, deployment |

---

## Critical Dependencies

- Task 1.1 → 1.2 → 1.3 → 2.1
- Task 2.1 → 3.1, 3.2, 3.3, 3.4
- Tất cả Epic 3 tasks có thể chạy song song sau khi Task 2.1 xong
- Task 4.1, 4.2 phụ thuộc vào Epic 3 hoàn thành

### Phụ thuộc bên ngoài
- FacilityService (Epic 1 — facilityManagement) phải available
- EmployeeService (Epic 1 — personnelManagement) phải available
- RevenueService (viewRevenue) phải available
- AuditLogService (auditLogs) phải available
- NotificationService (notificationManagement) phải available

---

## Resource Allocation

| Vai trò | Giờ ước tính | Thời gian |
|---------|-------------|-----------|
| Backend Developer | 60 | 2 tuần |
| Frontend Developer | 80 | 2 tuần |
| QA Engineer | 30 | 1 tuần |
| Tech Lead | 15 | 0.5 tuần |
