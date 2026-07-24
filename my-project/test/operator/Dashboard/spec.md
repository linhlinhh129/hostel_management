# Test Specification: Dashboard Nhân viên vận hành (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Dashboard Nhân viên vận hành (`my-project/sdd/specs/operator/Dashboard`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic gom nhóm và tổng hợp dữ liệu trên `OperatorDashboardServlet`. Đảm bảo hệ thống bóc tách chính xác dữ liệu theo `staff_id` và giới hạn số lượng hiển thị hợp lý.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Hiển thị Thống kê tổng quan**: KHI nhân viên truy cập Dashboard, HỆ THỐNG PHẢI gọi các Mock Services tương ứng để lấy tổng số Yêu cầu Pending, In-Progress, và Tiến độ ghi điện nước, sau đó gán vào Request Attributes.
- **Danh sách Lịch hẹn Hôm nay**: KHI có lịch hẹn trùng với `LocalDate.now()`, HỆ THỐNG PHẢI hiển thị các lịch hẹn này trên View.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Lỗi truy xuất dữ liệu**: KHI Mock Database ném `SQLException` do đứt kết nối trong quá trình tổng hợp số liệu, HỆ THỐNG PHẢI xử lý ngoại lệ an toàn, hiển thị giao diện báo lỗi hoặc thẻ dữ liệu trống thay vì sập toàn bộ trang 500.
- **Cố ý truy cập chéo Role**: KHI user có vai trò `TENANT` truy cập URL `/operator/dashboard`, HỆ THỐNG PHẢI chặn đứng và ném lỗi 403 Forbidden.

### 2.3 Boundary Values (Các giá trị biên)
- **Lịch hẹn quá tải (Limit Boundary)**: KHI trong ngày có 15 lịch hẹn, HỆ THỐNG PHẢI chỉ lấy và hiển thị TỐI ĐA 5 dòng đầu tiên trên Dashboard (các dòng sau xem ở trang chi tiết).
- **Zero Data**: KHI không có bất kỳ yêu cầu hay lịch hẹn nào (Số liệu = 0), HỆ THỐNG PHẢI hiển thị giao diện thân thiện (Ví dụ: "Hôm nay không có lịch hẹn").

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Tính toàn vẹn Dữ liệu Thống kê**: Do Dashboard gọi nhiều lệnh COUNT, KHI có luồng khác đang liên tục INSERT sự cố, Mock Service (hoặc Cache) của Dashboard PHẢI trả về Snapshot dữ liệu tĩnh tại thời điểm gọi, không để xảy ra hiệu ứng Dirty Read trên View.
