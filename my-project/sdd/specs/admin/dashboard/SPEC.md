# Feature: Admin Dashboard

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** [YYYY-MM-DD]
**Priority:** High

---

## 1. Business Context

Admin Dashboard là màn hình trung tâm sau khi Admin đăng nhập vào hệ thống quản lý nhà trọ. Đây là điểm xuất phát để Admin nắm bắt nhanh tình trạng tổng thể của toàn hệ thống và điều hướng tới các module nghiệp vụ.

Dashboard tổng hợp dữ liệu từ các module: Quản lý Cơ sở, Quản lý Nhân sự, Báo cáo Doanh thu, Quản lý Thông báo và Audit Log — giúp Admin không cần phải vào từng module mới biết được trạng thái hệ thống.

---

## 2. User Stories

### Story 1 (Happy Path)

Là Admin, tôi muốn xem tổng quan hệ thống ngay khi đăng nhập để nắm bắt nhanh tình trạng vận hành mà không cần vào từng module.

### Story 2 (Happy Path)

Là Admin, tôi muốn xem doanh thu tháng hiện tại của từng cơ sở để đánh giá hiệu quả hoạt động.

### Story 3 (Happy Path)

Là Admin, tôi muốn xem các hoạt động gần đây trong hệ thống để theo dõi các thao tác quan trọng vừa xảy ra.

### Story 4 (Edge Case)

Là Admin, khi hệ thống chưa có dữ liệu (mới khởi tạo), tôi muốn Dashboard hiển thị trạng thái rỗng hợp lệ thay vì lỗi.

### Story 5 (Edge Case)

Là Admin, khi một module nguồn bị lỗi, tôi muốn Dashboard vẫn hiển thị được các phần dữ liệu còn lại, không bị trắng toàn bộ màn hình.

---

## 3. Acceptance Criteria (EARS)

### 3.1 KPI Cards

WHEN Admin truy cập Dashboard
THE SYSTEM SHALL hiển thị các KPI Cards sau:

* Tổng doanh thu tháng hiện tại (tổng tất cả cơ sở)
* Tổng số cơ sở (tất cả trạng thái)
* Tổng số thông báo đã tạo
* Tổng số nhật ký trong ngày hôm nay

WHEN Admin truy cập Dashboard
THE SYSTEM SHALL tính doanh thu tháng hiện tại bằng tổng giá trị hóa đơn có trạng thái PAID trong tháng hiện tại.

WHEN không có dữ liệu cho một KPI Card
THE SYSTEM SHALL hiển thị giá trị `0` thay vì lỗi hoặc trống.

### 3.2 Widget Thống kê Nhân sự

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị widget thống kê nhân sự bao gồm:

* Tổng số nhân sự
* Số nhân sự là Ban Quản Lý (MANAGER)
* Số nhân sự là Nhân viên vận hành (OPERATOR)

### 3.3 Widget Hoạt động Gần đây

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị tối đa 5 hoạt động mới nhất từ Audit Log, bao gồm:

* Thời gian thực hiện
* Người thực hiện
* Hành động

WHEN không có hoạt động nào
THE SYSTEM SHALL hiển thị thông báo "Chưa có hoạt động nào".

### 3.4 Widget Doanh thu theo Cơ sở

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị bảng doanh thu tháng hiện tại theo từng cơ sở, bao gồm:

* Tên cơ sở
* Doanh thu tháng (tổng hóa đơn PAID)
* Số hóa đơn chưa thanh toán (UNPAID)
* Số hóa đơn quá hạn (OVERDUE)

WHEN không có dữ liệu doanh thu
THE SYSTEM SHALL hiển thị bảng rỗng với thông báo "Chưa có dữ liệu doanh thu".

### 3.5 Phân quyền

WHILE người dùng chưa đăng nhập
THE SYSTEM SHALL chuyển hướng về trang đăng nhập.

WHILE người dùng đã đăng nhập nhưng không có vai trò ADMIN
THE SYSTEM SHALL từ chối truy cập và trả về lỗi FORBIDDEN.

---

## 4. Servlet Contract

### 4.1 Servlet Entry Point

|    Thuộc tính   | Giá trị                                     |
|-----------------|---------------------------------------------|
| **Servlet**     | `AdminDashboardServlet`                     |
| **URL Pattern** | `GET /admin/dashboard`                      |
| **Forward đến** | `/WEB-INF/views/admin/dashboard.jsp`        |
| **Phân quyền**  | Role = `ADMIN` (kiểm tra qua `RoleFilter`) |

---

### 4.2 Request Attributes

Servlet set các attribute sau trước khi forward sang JSP:

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
|-----------|-----------|---------------|-------|
| `currentPeriodLabel` | `String` | `LocalDate.now()` | Kỳ hiện tại, định dạng `"MM/yyyy"` |
| `monthlyRevenue` | `BigDecimal` | `RevenueDAO.getMonthlyRevenueTotal(period)` | Tổng doanh thu tháng (hóa đơn PAID) |
| `totalFacilities` | `int` | `FacilityDAO.count("", "")` | Tổng số cơ sở (tất cả trạng thái) |
| `activeFacilities` | `int` | `FacilityDAO.count("", "ACTIVE")` | Số cơ sở đang ACTIVE |
| `totalPersonnel` | `int` | `PersonnelDAO.countAll()` | Tổng nhân sự (MANAGER + OPERATOR) |
| `managerCount` | `int` | `PersonnelDAO.countByRole("MANAGER")` | Số nhân sự vai trò MANAGER |
| `operatorCount` | `int` | `PersonnelDAO.countByRole("OPERATOR")` | Số nhân sự vai trò OPERATOR |
| `totalNotifications` | `int` | `NotificationDAO.count("")` | Tổng thông báo `target_type = 'ALL'` |
| `todayAuditLogs` | `int` | `AuditLogDAO.countToday()` | Số nhật ký trong ngày hôm nay |
| `facilityRevenueStats` | `List<FacilityRevenueStatDTO>` | `RevenueDAO.getFacilityRevenues(period)` | Doanh thu theo từng cơ sở ACTIVE |
| `recentActivities` | `List<RevenueActivityDTO>` | `AuditLogDAO.findRecent(5)` | Tối đa 5 hoạt động mới nhất |

---

### 4.3 FacilityRevenueStatDTO

| Field | Type | Mô tả |
|---|---|---|
| `facilityId` | `int` | ID cơ sở |
| `facilityCode` | `String` | Mã cơ sở (VD: `"CG"`) |
| `facilityName` | `String` | Tên cơ sở |
| `totalRevenue` | `BigDecimal` | Doanh thu đã thu (PAID) |
| `totalOutstanding` | `BigDecimal` | Dư nợ (UNPAID + OVERDUE) |
| `totalBilledAmount` | `BigDecimal` | Tổng phát sinh = `totalRevenue + totalOutstanding` |
| `paidCount` | `int` | Số hóa đơn PAID |
| `unpaidCount` | `int` | Số hóa đơn UNPAID |
| `overdueCount` | `int` | Số hóa đơn OVERDUE |
| `collectionRate` | `int` | Tỷ lệ thu (%) |

---

### 4.4 RevenueActivityDTO

| Field | Type | Mô tả |
|---|---|---|
| `actorName` | `String` | Tên người thực hiện (fallback: `"Hệ thống"`) |
| `actionDescription` | `String` | Mô tả hành động (VD: `"Tạo mới cơ sở"`) |
| `timeLabel` | `String` | Thời gian định dạng `"dd/MM/yyyy HH:mm:ss"` |

---

### 4.5 Xử lý lỗi

| Tình huống | Hành vi |
|---|---|
| Chưa đăng nhập | Redirect về `/login` (xử lý bởi `BaseServlet`) |
| Role không phải ADMIN | HTTP 403 Forbidden |
| Một DAO lỗi (exception) | Log `WARN`, giữ giá trị `0` / list rỗng — các phần còn lại vẫn hiển thị bình thường |
| Không có dữ liệu | Hiển thị `0` hoặc list rỗng, không throw exception |

---

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 1 giây (P95) — do Dashboard tổng hợp nhiều nguồn dữ liệu
* Dữ liệu Dashboard được cache tối đa 60 giây để tránh query nặng khi nhiều Admin truy cập
* Nếu một nguồn dữ liệu (vd: Audit Log) bị lỗi, hệ thống vẫn trả về các phần còn lại với giá trị `null` hoặc `0` cho phần lỗi
* Chỉ người dùng có vai trò ADMIN được truy cập Dashboard
* `recentActivities` giới hạn tối đa 5 bản ghi mới nhất
* `revenueByFacility` chỉ tính trong tháng hiện tại (từ ngày 1 đến ngày hiện tại)
* Doanh thu chỉ tính hóa đơn có trạng thái PAID

---

## 6. Out of Scope

* Dashboard cho role MANAGER hoặc OPERATOR
* Biểu đồ thống kê nâng cao (bar chart, line chart)
* So sánh doanh thu tháng này với tháng trước
* Dự báo doanh thu
* Dashboard thời gian thực (real-time WebSocket)
* Export Dashboard ra PDF/Excel
* Cấu hình widget theo ý Admin
