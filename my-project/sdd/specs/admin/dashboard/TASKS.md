# TASKS: Phân chia Chi tiết Đầu Việc - Admin Dashboard

**Total Story Points:** ~18 points  
**Sprint Duration:** 1 sprint = 1 tuần  
**Status:** Completed

---

## Epic 1: Backend (8 points)

### Task 1.1: Implement AdminDashboardServlet (5 points)
**Assignee:** Backend Developer  
**Duration:** 2 ngày  
**Mô tả:**
- Implement `doGet()` thu thập dữ liệu từ 5 DAO
- Mỗi DAO call nằm trong try/catch riêng — partial failure không ảnh hưởng các phần khác
- Set đầy đủ attributes: `monthlyRevenue`, `totalFacilities`, `activeFacilities`, `totalPersonnel`, `managerCount`, `operatorCount`, `totalNotifications`, `todayAuditLogs`, `facilityRevenueStats`, `recentActivities`, `currentPeriodLabel`
- Format thời gian hoạt động gần đây: `dd/MM/yyyy HH:mm:ss`
- Forward sang `/WEB-INF/views/admin/dashboard.jsp`

**Acceptance Criteria:**
- ✓ Servlet thu thập đủ data từ 5 nguồn
- ✓ Partial failure: nếu 1 DAO lỗi, các DAO khác vẫn chạy bình thường
- ✓ Không có SQL trong Servlet — tất cả đi qua DAO
- ✓ Không có `System.out.println()` — dùng SLF4J logger

**Dependencies:** Các DAO methods phải tồn tại

---

### Task 1.2: Implement/Verify DAO Methods (3 points)
**Assignee:** Backend Developer  
**Duration:** 1 ngày  
**Mô tả:**
- `FacilityDAO.count(keyword, status)` — đếm cơ sở theo filter
- `PersonnelDAO.countAll()` — đếm tổng nhân sự
- `PersonnelDAO.countByRole(role)` — đếm theo MANAGER/OPERATOR
- `NotificationDAO.count(keyword)` — đếm tổng thông báo
- `AuditLogDAO.countToday()` — đếm log hôm nay
- `AuditLogDAO.findRecent(limit)` — lấy N bản ghi mới nhất
- `RevenueDAO.getMonthlyRevenueTotal(period)` — tổng doanh thu tháng
- `RevenueDAO.getFacilityRevenues(period)` — doanh thu từng cơ sở kèm unpaidCount, overdueCount

**Acceptance Criteria:**
- ✓ Tất cả methods tồn tại và hoạt động đúng
- ✓ Dùng PreparedStatement + try-with-resources
- ✓ Return default value (0 hoặc empty list) khi không có dữ liệu

**Dependencies:** Database schema phải có đủ tables

---

## Epic 2: Frontend (7 points)

### Task 2.1: KPI Cards (1 point)
**Assignee:** Frontend Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- 4 KPI Cards: Doanh thu tháng, Tổng cơ sở, Thông báo, Nhật ký hôm nay
- Doanh thu format: `#,##0đ`
- Card rỗng hiển thị `0` không để trống

**Acceptance Criteria:**
- ✓ 4 cards hiển thị đúng giá trị
- ✓ Format tiền đúng
- ✓ Không có giá trị `null` hay trống

---

### Task 2.2: Hero Section + Quick Actions (1 point)
**Assignee:** Frontend Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- Hero: lời chào + label tháng hiện tại + button "Báo cáo doanh thu"
- Quick Actions: Thêm cơ sở (`/admin/facilities/create`), Thêm nhân sự (`/admin/personnel/create`), Tạo thông báo (`/admin/notifications/create`)

**Acceptance Criteria:**
- ✓ Tên Admin hiển thị đúng từ session
- ✓ Label tháng đúng tháng hiện tại
- ✓ Quick Action links điều hướng đúng trang

---

### Task 2.3: Widget Doanh thu theo Cơ sở (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 ngày  
**Mô tả:**
- Bảng 4 cột: Cơ sở (code + name), Doanh thu đã thu, Chưa thanh toán, Quá hạn
- Doanh thu format: `#,##0 đ`
- Badge warning cho unpaidCount
- Badge danger cho overdueCount > 0, badge neutral nếu = 0
- Link cơ sở điều hướng tới `/admin/facilities/{id}`
- Empty state: "Chưa có dữ liệu doanh thu kỳ này"
- Link "Xem báo cáo đầy đủ →" tới `/admin/revenue`

**Acceptance Criteria:**
- ✓ Bảng hiển thị đúng dữ liệu
- ✓ Badge màu đúng theo logic
- ✓ Empty state hiển thị khi không có dữ liệu
- ✓ Link điều hướng hoạt động

---

### Task 2.4: Widget Thống kê Nhân sự (1 point)
**Assignee:** Frontend Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- 3 cột: Tổng nhân sự, Ban Quản lý, Vận hành
- Link "Quản lý →" tới `/admin/personnel`

**Acceptance Criteria:**
- ✓ 3 số liệu hiển thị đúng
- ✓ Link điều hướng hoạt động

---

### Task 2.5: Widget Hoạt động Gần đây (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 ngày  
**Mô tả:**
- List tối đa 5 bản ghi
- Mỗi item: avatar (chữ cái đầu tên), tên người thực hiện, mô tả hành động, thời gian
- Empty state: "Chưa có hoạt động nào"
- Link "Xem chi tiết →" tới `/admin/audit-logs`

**Acceptance Criteria:**
- ✓ Hiển thị tối đa 5 bản ghi
- ✓ Format thời gian: `dd/MM/yyyy HH:mm:ss`
- ✓ Empty state hiển thị đúng
- ✓ Link điều hướng hoạt động

---

## Epic 3: Testing & Verify (3 points)

### Task 3.1: Test Happy Path (1 point)
**Assignee:** QA / Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- Đăng nhập với tài khoản ADMIN
- Kiểm tra tất cả KPI Cards hiển thị đúng số liệu
- Kiểm tra tất cả widgets có dữ liệu
- Kiểm tra Quick Actions điều hướng đúng

**Acceptance Criteria:**
- ✓ Tất cả số liệu đúng với database
- ✓ Không có lỗi console/log

---

### Task 3.2: Test Empty State (1 point)
**Assignee:** QA / Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- Test với hệ thống chưa có dữ liệu doanh thu
- Test với chưa có audit log
- Kiểm tra KPI Cards hiển thị `0`

**Acceptance Criteria:**
- ✓ KPI Cards hiển thị `0` (không null, không lỗi)
- ✓ Empty state message hiển thị đúng cho từng widget

---

### Task 3.3: Test Partial Failure & Phân quyền (1 point)
**Assignee:** QA / Developer  
**Duration:** 0.5 ngày  
**Mô tả:**
- Test truy cập Dashboard với tài khoản MANAGER → phải bị FORBIDDEN
- Test truy cập Dashboard chưa đăng nhập → phải redirect login
- Kiểm tra log khi một DAO throw exception

**Acceptance Criteria:**
- ✓ MANAGER/OPERATOR không truy cập được Dashboard
- ✓ Chưa đăng nhập redirect về /login
- ✓ Khi một DAO lỗi, Dashboard vẫn hiển thị các phần còn lại

---

## Summary

| Epic | Points | Status |
|---|---|---|
| Epic 1: Backend | 8 | ✅ Done |
| Epic 2: Frontend | 7 | ✅ Done |
| Epic 3: Testing | 3 | ✅ Done |
| **Total** | **18** | ✅ **Completed** |

## Task Dependency Graph

```
Task 1.2 (DAO Methods)
  ↓
Task 1.1 (Servlet)
  ↓
Task 2.1 (KPI Cards)
Task 2.2 (Hero + Quick Actions)
Task 2.3 (Widget Doanh thu)
Task 2.4 (Widget Nhân sự)
Task 2.5 (Widget Hoạt động)
  ↓
Task 3.1 (Test Happy Path)
Task 3.2 (Test Empty State)
Task 3.3 (Test Partial Failure)
```
