# TASKS: Phân chia Chi tiết - Xem Báo Cáo Doanh Thu

**Updated:** 2026-07-23

---

## Epic 1: Thiết kế & Chuẩn bị ✅

### [x] Task 1.1: Xác nhận yêu cầu và API contract
- Output: Servlet contract `GET /admin/revenue`, `by-facility`, `by-period`
- Acceptance: Đã đồng bộ với `SPEC.md`

### [x] Task 1.2: Lập kế hoạch truy vấn dữ liệu
- Output: Single-query aggregation, không N+1, fill gap tháng rỗng
- Acceptance: Đã triển khai

---

## Epic 2: Backend Implementation ✅

### [x] Task 2.1: Implement service tổng hợp doanh thu
- `RevenueDAO.getSystemRevenue(period)` — KPI toàn hệ thống
- `RevenueDAO.getFacilityRevenues(period)` — danh sách không phân trang
- `RevenueDAO.getFacilityRevenuesPaged(period, page, size)` — có phân trang (OFFSET/FETCH)
- `RevenueDAO.countActiveFacilities()` — đếm facility ACTIVE cho pagination
- `RevenueDAO.getRevenueTrend(months)` — xu hướng N tháng, fill gap tháng rỗng
- Filter: `period` format `MM/yyyy`, fallback về tháng hiện tại
- Tính: `totalRevenue` (PAID), `totalOutstanding` (UNPAID+OVERDUE), `totalBilledAmount`, `collectionRate`

### [x] Task 2.2: Controller và URL routing
- `AdminRevenueServlet` với 3 sub-path: `/`, `/by-facility`, `/by-period`
- `resolvePeriod()` — chấp nhận `YYYY-MM` (input[type=month]) và `MM/yyyy`
- Phân quyền: `ADMIN` only qua `BaseServlet`



## Epic 3: Frontend (Admin UI)

### [x] Task 3.1: UI Filter & Summary (index.jsp)
- Filter theo `period` (`input[type=month]`)
- Hiển thị `SystemRevenueDTO` KPIs
- Hiển thị mini bảng facility (top) + xu hướng 6 tháng

### [x] Task 3.2: Bảng by-facility và pagination (by-facility.jsp)
- Danh sách tất cả facility ACTIVE, kể cả revenue = 0
- Phân trang `PAGE_SIZE = 10`
- Hiển thị empty state khi không có dữ liệu

### [x] Task 3.3: Biểu đồ by-period (by-period.jsp)
- Hiển thị xu hướng N tháng gần nhất (mặc định 12)
- Nhóm theo tháng, sắp xếp mới nhất trước

---

## Epic 4: Testing & Hoàn thiện

### [ ] Task 4.1: Unit tests backend *(chưa triển khai)*
- Test `parsePeriod()` với các format input
- Test tính toán `totalRevenue`, `totalOutstanding`, `collectionRate`
- Test fill gap tháng rỗng trong `getRevenueTrend`

### [ ] Task 4.2: Integration/UAT *(chưa triển khai)*
- E2E flow: Admin truy cập, lọc kỳ, xem summary và bảng
- Kiểm tra empty state, phân quyền

---

## Dependencies & Thứ tự thực hiện
- Task 2.1 → Task 2.2 → Task 3.x (đã hoàn thành)
- Task 4.1 → Task 4.2

---

## Còn lại cần làm
| # | Task | Mức độ |
|---|------|--------|
| 4.1 | Unit tests | 🟡 Nice-to-have |
| 4.2 | UAT | 🟡 Nice-to-have |
