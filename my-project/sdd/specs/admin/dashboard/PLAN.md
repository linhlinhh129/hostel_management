# PLAN: Kế hoạch Thực thi Admin Dashboard

**Status:** Completed  
**Date:** 2026-07-03  
**Priority:** High  
**Estimated Duration:** 1-2 weeks

---

## 1. Tổng quan Giải pháp

Admin Dashboard là màn hình tổng quan read-only, tổng hợp dữ liệu từ 5 module: Cơ sở, Nhân sự, Doanh thu, Thông báo và Audit Log. Hiển thị dưới dạng KPI Cards + Widgets theo mô hình JSP/Servlet thuần, không dùng AJAX hay real-time.

**Kiến trúc:**
- `AdminDashboardServlet` — gọi `AdminDashboardService` để lấy dữ liệu, forward sang JSP
- `AdminDashboardService` / `AdminDashboardServiceImpl` — thu thập dữ liệu từ các DAO và xử lý logic
- `dashboard.jsp` — render giao diện từ attributes Servlet truyền sang
- Xử lý partial failure (try/catch riêng từng block) được thực hiện ở tầng Service để đảm bảo an toàn nếu một DAO lỗi

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Backend — Servlet & DAO (Ngày 1-2)

**Mục tiêu:** Implement `AdminDashboardServlet` thu thập đủ dữ liệu

**Công việc:**
- Implement các DAO method cần thiết nếu chưa có
- Implement `AdminDashboardService` và `AdminDashboardServiceImpl`
- Xử lý partial failure (try/catch riêng từng block) bên trong `AdminDashboardServiceImpl`
- Xử lý cache 60 giây trong `AdminDashboardServiceImpl`
- Implement `AdminDashboardServlet.doGet()` (thêm try-catch tổng quát để catch exception ngoài dự kiến)
- Set attributes cho JSP

**Deliverables:**
- `AdminDashboardServlet.java` hoàn chỉnh
- `AdminDashboardService.java` và `AdminDashboardServiceImpl.java` hoàn chỉnh
- Các DAO methods: `getMonthlyRevenueTotal`, `getFacilityRevenues`, `findRecent`, `countToday`, `countAll`, `countByRole`

---

### Giai đoạn 2: Frontend — JSP (Ngày 3-4)

**Mục tiêu:** Implement `dashboard.jsp` hiển thị đầy đủ các widget

**Công việc:**
- Implement Hero section (lời chào + label tháng hiện tại)
- Implement 4 KPI Cards
- Implement Quick Actions (Thêm cơ sở, Thêm nhân sự, Tạo thông báo)
- Implement widget Doanh thu theo cơ sở (bảng 4 cột)
- Implement widget Thống kê nhân sự
- Implement widget Hoạt động gần đây
- Implement empty states cho từng widget

**Deliverables:**
- `dashboard.jsp` hoàn chỉnh
- Empty state fragments tái sử dụng

---

### Giai đoạn 3: Testing & Hoàn thiện (Ngày 5)

**Mục tiêu:** Kiểm tra toàn bộ luồng và đảm bảo chất lượng

**Công việc:**
- Test với dữ liệu đầy đủ (happy path)
- Test với dữ liệu rỗng (empty state)
- Test partial failure (giả lập DAO lỗi)
- Test phân quyền (MANAGER, OPERATOR không truy cập được)
- Kiểm tra responsive design

---

## 3. Dependencies & Constraints

### Phụ thuộc bên trong
- `FacilityDAO.count()` — đã có
- `PersonnelDAO.countAll()`, `countByRole()` — đã có
- `NotificationDAO.count()` — đã có
- `AuditLogDAO.countToday()`, `findRecent()` — đã có
- `RevenueDAO.getMonthlyRevenueTotal()`, `getFacilityRevenues()` — đã có

### Phụ thuộc bên ngoài
- `AuthenticationFilter` — kiểm tra đăng nhập
- `RoleFilter` — kiểm tra quyền ADMIN

### Technical Constraints
- Không dùng AJAX, không dùng WebSocket
- Không dùng React/TypeScript
- SQL phải đi qua DAO — không viết SQL trong Servlet
- Session-based auth — không dùng JWT

---

## 4. Success Criteria

### Functional
- ✓ 4 KPI Cards hiển thị đúng dữ liệu
- ✓ Widget doanh thu hiển thị đúng theo tháng hiện tại
- ✓ Widget nhân sự hiển thị tổng/manager/operator
- ✓ Widget hoạt động gần đây hiển thị 5 bản ghi mới nhất
- ✓ Empty state hiển thị đúng khi không có dữ liệu
- ✓ Quick Actions điều hướng đúng trang
- ✓ Partial failure không làm hỏng toàn bộ trang

### Non-Functional
- ✓ Response time < 1 giây (P95)
- ✓ Không có SQL trong Servlet
- ✓ Không có lỗi compile/runtime
- ✓ Responsive design hoạt động

---

## 5. Risk Management

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| DAO method chưa có | Thấp | Trung bình | Kiểm tra trước khi bắt đầu, bổ sung nếu thiếu |
| Query chậm | Thấp | Cao | Dùng try/catch riêng, partial failure fallback |
| Data không đồng bộ giữa DAO | Thấp | Thấp | Mỗi widget độc lập, không phụ thuộc nhau |
| Partial failure không xử lý đúng | Thấp | Cao | Test riêng từng scenario |

---

## 6. Trạng thái Hiện tại

| Hạng mục | Trạng thái |
|---|---|
| `AdminDashboardServlet` | ✅ Hoàn thành |
| `dashboard.jsp` — KPI Cards | ✅ Hoàn thành |
| `dashboard.jsp` — Widget doanh thu | ✅ Hoàn thành |
| `dashboard.jsp` — Widget nhân sự | ✅ Hoàn thành |
| `dashboard.jsp` — Widget hoạt động | ✅ Hoàn thành |
| `dashboard.jsp` — Quick Actions | ✅ Hoàn thành |
| Partial failure handling | ✅ Hoàn thành |
| Empty states | ✅ Hoàn thành |
| SQL trong DAO (không trong Servlet) | ✅ Hoàn thành |
| Phân quyền qua RoleFilter | ✅ Hoàn thành |
