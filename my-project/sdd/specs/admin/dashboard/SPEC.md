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
* Tổng số nhân sự đang hoạt động (ACTIVE)
* Tổng số thông báo đã tạo
* Tổng số Audit Log trong ngày hôm nay

WHEN Admin truy cập Dashboard
THE SYSTEM SHALL tính doanh thu tháng hiện tại bằng tổng giá trị hóa đơn có trạng thái PAID trong tháng hiện tại.

WHEN không có dữ liệu cho một KPI Card
THE SYSTEM SHALL hiển thị giá trị `0` thay vì lỗi hoặc trống.

### 3.2 Widget Thống kê Cơ sở

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị widget thống kê cơ sở bao gồm:

* Tổng số cơ sở
* Số cơ sở đang hoạt động (ACTIVE)
* Số cơ sở nháp (DRAFT)
* Số cơ sở đã vô hiệu hóa (INACTIVE)

### 3.3 Widget Thống kê Nhân sự

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị widget thống kê nhân sự bao gồm:

* Tổng số nhân sự
* Số nhân sự là Ban Quản Lý (MANAGER)
* Số nhân sự là Nhân viên vận hành (OPERATOR)

### 3.4 Widget Hoạt động Gần đây

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị tối đa 10 hoạt động mới nhất từ Audit Log, bao gồm:

* Thời gian thực hiện
* Người thực hiện
* Hành động

WHEN không có hoạt động nào
THE SYSTEM SHALL hiển thị thông báo "Chưa có hoạt động nào".

### 3.5 Widget Doanh thu theo Cơ sở

WHEN Admin xem Dashboard
THE SYSTEM SHALL hiển thị bảng doanh thu tháng hiện tại theo từng cơ sở, bao gồm:

* Tên cơ sở
* Doanh thu tháng (tổng hóa đơn PAID)

WHEN không có dữ liệu doanh thu
THE SYSTEM SHALL hiển thị bảng rỗng với thông báo "Chưa có dữ liệu doanh thu".

### 3.6 Điều hướng từ Dashboard

WHEN Admin click vào một KPI Card hoặc widget
THE SYSTEM SHALL điều hướng Admin tới module tương ứng.

### 3.7 Phân quyền

WHILE người dùng chưa đăng nhập
THE SYSTEM SHALL chuyển hướng về trang đăng nhập.

WHILE người dùng đã đăng nhập nhưng không có vai trò ADMIN
THE SYSTEM SHALL từ chối truy cập và trả về lỗi FORBIDDEN.

---

## 4. API Contract

### 4.1 Lấy dữ liệu Dashboard

```http
GET /api/v1/dashboard
```

#### Response 200

```json
{
  "success": true,
  "data": {
    "kpi": {
      "totalRevenueThisMonth": 50000000,
      "totalFacilities": 5,
      "totalActiveEmployees": 12,
      "totalNotifications": 30,
      "totalAuditLogsToday": 8
    },
    "facilityStats": {
      "total": 5,
      "active": 3,
      "draft": 1,
      "inactive": 1
    },
    "employeeStats": {
      "total": 12,
      "managers": 3,
      "operators": 9
    },
    "recentActivities": [
      {
        "auditLogId": 101,
        "action": "UPDATE",
        "entityType": "Facility",
        "createdBy": "admin@example.com",
        "createdAt": "2026-06-19T09:30:00"
      }
    ],
    "revenueByFacility": [
      {
        "facilityId": 1,
        "facilityName": "Nhà trọ Hòa Lạc",
        "revenueThisMonth": 20000000
      }
    ]
  }
}
```

#### Response 401

```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Vui lòng đăng nhập để tiếp tục"
  }
}
```

#### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Bạn không có quyền truy cập chức năng này"
  }
}
```

---

## 5. Technical Constraints

* Thời gian phản hồi tối đa: 1 giây (P95) — do Dashboard tổng hợp nhiều nguồn dữ liệu
* Dữ liệu Dashboard được cache tối đa 60 giây để tránh query nặng khi nhiều Admin truy cập
* Nếu một nguồn dữ liệu (vd: Audit Log) bị lỗi, hệ thống vẫn trả về các phần còn lại với giá trị `null` hoặc `0` cho phần lỗi
* Chỉ người dùng có vai trò ADMIN được truy cập Dashboard
* `recentActivities` giới hạn tối đa 10 bản ghi mới nhất
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
