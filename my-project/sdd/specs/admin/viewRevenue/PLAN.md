# PLAN: Kế hoạch Thực thi - Xem Báo Cáo Doanh Thu

**Status:** Implemented  
**Date:** 2026-06-13  
**Updated:** 2026-07-23  
**Priority:** High  

---

## 1. Tổng quan

Tính năng cho phép Admin xem báo cáo doanh thu tổng hợp của toàn bộ cơ sở theo từng kỳ tháng, lọc theo `period` (MM/yyyy), hiển thị các chỉ số: tổng doanh thu, dư nợ, tỷ lệ thu tiền theo từng cơ sở và theo xu hướng thời gian.

Kiến trúc: **MVC Servlet**.

Không bao gồm: xuất Excel/PDF, biểu đồ nâng cao, dự báo, Audit Log.

---

## 2. Kiến trúc đã triển khai

### Entry Point
| Servlet | `AdminRevenueServlet` |
|---|---|
| URL | `GET /admin/revenue` — tổng quan |
| URL | `GET /admin/revenue/by-facility` — doanh thu theo cơ sở (có phân trang) |
| URL | `GET /admin/revenue/by-period` — xu hướng theo tháng |
| Phân quyền | Role = `ADMIN` (kiểm tra qua `BaseServlet`) |

### Filter Parameter
| Param | Dùng ở | Mô tả |
|---|---|---|
| `period` | `/admin/revenue`, `by-facility` | Chấp nhận `YYYY-MM` hoặc `MM/yyyy`. Mặc định tháng hiện tại |
| `page` | `by-facility` | Trang hiện tại, mặc định `1` |
| `months` | `by-period` | Số tháng gần nhất, mặc định `12` |

### Layer Stack
```
AdminRevenueServlet
    └── RevenueService (interface)
            └── RevenueServiceImpl
                    └── RevenueDAO
```

### Các method đã triển khai
| Method | Mô tả |
|---|---|
| `getSystemRevenue(period)` | KPI tổng hợp toàn hệ thống trong kỳ |
| `getFacilityRevenues(period)` | Danh sách tất cả facility (không phân trang) |
| `getFacilityRevenuesPaged(period, page, size)` | Danh sách facility có phân trang |
| `countActiveFacilities()` | Đếm facility ACTIVE (dùng cho phân trang) |
| `getRevenueTrend(months)` | Xu hướng N tháng gần nhất |

### DTOs
- `SystemRevenueDTO`: `totalRevenue`, `totalOutstanding`, `totalBilledAmount`, `paidCount`, `unpaidCount`, `overdueCount`, `collectionRate`
- `FacilityRevenueStatDTO`: tương tự + `facilityId`, `facilityCode`, `facilityName`

---

## 3. Yêu cầu kỹ thuật và ràng buộc
- Thời gian phản hồi tối đa: 500ms (P95)
- Rate limit: 100 req/phút/người dùng
- Chỉ tính hóa đơn có status: `PAID`, `UNPAID`, `OVERDUE`
- Hóa đơn `CANCELLED` / `DELETED` không tính vào báo cáo
- by-facility hiển thị **tất cả facility ACTIVE** kể cả revenue = 0 (LEFT JOIN)
- `countActiveFacilities()` không filter theo period — đây là thiết kế có chủ đích

---

## 4. Out of Scope (chưa triển khai)
- Audit Log khi Admin xem báo cáo
- Validate lỗi rõ ràng cho `period` không hợp lệ (hiện fallback âm thầm về tháng hiện tại)
- Unit tests & integration tests
- Xuất Excel / PDF

---

## 5. Rủi ro đã xử lý
| Rủi ro | Biện pháp |
|---|---|
| Query lớn gây chậm | Single-query aggregation, không N+1 |
| Tháng không có dữ liệu | `getRevenueTrend` fill gap với row revenue = 0 |
| Phân trang sai | `countActiveFacilities` đếm đúng tập dữ liệu LEFT JOIN trả về |
