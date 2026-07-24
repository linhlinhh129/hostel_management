# **Feature: Dashboard cho ban quản lý**

Status: Completed

Author: Antigravity

Reviewer: [Tên]

Date: 2026-07-14

Priority: High

---

## **1. Business Context**

Tính năng Dashboard ra đời nhằm cung cấp một bảng điều khiển trung tâm tập hợp toàn bộ các con số thống kê hoạt động vận hành và công nợ tài chính của cơ sở được phân công. Việc này giúp giải quyết khó khăn của Manager khi phải truy cập rời rạc vào từng phân hệ để cộng dồn báo cáo, giúp nâng cao tốc độ ra quyết định, quản lý công nợ hiệu quả và theo sát tiến trình xử lý sự cố trong ngày.

---

## **2. User Stories**

### **Story 1 (Happy Path)**

As a Manager, I want to xem tổng quan tỷ lệ lấp đầy phòng, số phòng trống, số lượng cư dân và người phụ thuộc cư trú thực tế so that tôi đánh giá được hiệu suất cho thuê phòng của cơ sở.

### **Story 2 (Happy Path)**

As a Manager, I want to xem tổng tiền đã thu trong tháng hiện tại, số lượng hóa đơn trễ hạn và tổng nợ tồn đọng chưa thu so that tôi kịp thời đôn đốc cư dân thanh toán tiền phòng.

### **Story 3 (Happy Path)**

As a Manager, I want to thấy được danh sách 5 sự cố mới gửi gần nhất và thống kê số lượng sự cố theo trạng thái so that tôi chủ động sắp xếp thời gian đi tới các phòng tương ứng để xử lý sửa chữa.

---

## **3. Acceptance Criteria (EARS)**

### **Thống kê hoạt động**

WHEN Manager views Dashboard THE SYSTEM SHALL load and calculate room counts, occupied counts, resident counts, and occupancy rate.

### **Thống kê tài chính**

WHEN Manager views Dashboard THE SYSTEM SHALL sum paid invoices for the current month AND sum outstanding unpaid/overdue invoices.

### **Thống kê và Danh sách sự cố**

WHEN Manager views Dashboard THE SYSTEM SHALL count tickets in each status group (New, In Progress, Done, Rejected) AND load the 5 most recent tickets.

---

## **4. Servlet Contract**

### **4.1 Servlet Entry Point**

| Thuộc tính | Giá trị |
| --- | --- |
| **Servlet** | `ManagerDashboardServlet` |
| **URL Pattern** | `GET /manager/dashboard` — trang bảng điều khiển tổng hợp |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua `UserSessionDTO` / `currentUser` trong session) |

---

### **4.2 Request Attributes — Danh sách (list.jsp)**

*(Dashboard hiển thị các chỉ số tổng hợp phẳng và bảng danh sách sự cố gần nhất trên trang chính `dashboard.jsp`)*

| Attribute | Java Type | Nguồn dữ liệu | Mô tả |
| --- | --- | --- | --- |
| `facilityName`, `facilityCode`, `facilityStatus` | `String` | `stats` từ `DashboardService` | Thông tin cơ sở trọ hiển thị |
| `totalRooms`, `occupiedRooms`, `vacantRooms` | `int` | `stats` từ `DashboardService` | Thống kê số lượng phòng |
| `totalTenants`, `totalDependents` | `int` | `stats` từ `DashboardService` | Thống kê số cư dân |
| `pendingTickets`, `sentNotifications` | `int` | `stats` từ `DashboardService` | Đếm sự cố chờ xử lý và thông báo đã gửi |
| `occupancyRate` | `int` | `stats` từ `DashboardService` | Tỷ lệ lấp đầy (%) |
| `activeContracts`, `unpaidInvoices`, `overdueInvoices`, `pendingPayments` | `int` | `stats` từ `DashboardService` | Thống kê hợp đồng và hóa đơn |
| `monthlyRevenue`, `totalOutstanding` | `BigDecimal` | `stats` từ `DashboardService` | Thống kê doanh thu và nợ tồn đọng |
| `ticketCountNew`, `ticketCountInProgress`, `ticketCountDone`, `ticketCountRejected` | `int` | `stats` từ `DashboardService` | Phân loại số lượng sự cố theo trạng thái |
| `recentTickets` | `List<Map<String, Object>>`| `stats` từ `DashboardService` | Danh sách 5 sự cố mới gửi gần nhất |

---

### **4.4 Xử lý lỗi (Servlet Behavior)**

| Tình huống | Hành vi |
| --- | --- |
| Chưa đăng nhập | Redirect về `/login` |
| Manager chưa được phân công cơ sở nào | Gán các giá trị mặc định trống (`—` hoặc `0`) và hiển thị màn hình bình thường |
| Thất bại kết nối Database | Trả về các thuộc tính rỗng và log lỗi bằng logger |

---

## **5. Technical Constraints**

- **Phân quyền và Bảo mật:**
  - Dashboard chỉ hiển thị số liệu của cơ sở mà Manager đó được giao làm đại diện (`manager_id` khớp với ID session đăng nhập). Bất kỳ truy cập trái phép nào từ các tài khoản không phải MANAGER đều bị chặn.
- **Tránh lỗi chia cho 0:**
  - Khi tính toán tỷ lệ lấp đầy (`occupancyRate = occupiedRooms / totalRooms * 100`), hệ thống bắt buộc phải kiểm tra nếu `totalRooms == 0` thì trả về kết quả bằng `0`.
- **Hiệu năng (Performance):**
  - Thời gian phản hồi khi tải toàn bộ số liệu thống kê Dashboard tổng hợp từ nhiều bảng không vượt quá **400 ms (p95)**.

---

## **6. Out of Scope**

- Vẽ biểu đồ trực quan (Charts) hiển thị biến động doanh thu hay tỷ lệ lấp đầy.
- Tính năng tự động làm mới trang (Auto-refresh) theo thời gian thực (Manager phải tải lại trang thủ công).
- Xuất số liệu báo cáo ra các tệp tài liệu bên ngoài (Excel, PDF).
