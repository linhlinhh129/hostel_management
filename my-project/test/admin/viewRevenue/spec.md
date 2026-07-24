# Test Specification: Xem Báo Cáo Doanh Thu (View Revenue - Unit Test Only)

**Status:** Draft
**Target Feature:** Admin View Revenue (`my-project/sdd/specs/admin/viewRevenue`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Tuyệt đối không kết nối DB thật, mọi logic thống kê, tính toán doanh thu (RevenueService) phải được cô lập bằng Mock.
Đảm bảo luồng xử lý ngày tháng (period), xử lý lỗi khi không có dữ liệu, và phân trang hoạt động hoàn hảo dưới góc độ Servlet Controller.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem tổng quan doanh thu (Index)**: KHI GET `/admin/revenue`, HỆ THỐNG PHẢI trả về `systemRevenue`, `facilityRevenues` (không phân trang), `periodRevenues` (6 tháng) bằng cách gọi Mock Service.
- **Xem doanh thu theo cơ sở (By Facility)**: KHI GET `/admin/revenue/by-facility`, HỆ THỐNG PHẢI trả về danh sách phân trang (kích thước 10) thông qua đối tượng `PageDTO`.
- **Xem doanh thu theo kỳ (By Period)**: KHI GET `/admin/revenue/by-period`, HỆ THỐNG PHẢI trả về dữ liệu của 12 tháng gần nhất (mặc định) giảm dần theo thời gian.
- **Xử lý định dạng thời gian**: KHI nhận tham số `period` dạng `YYYY-MM` (từ form HTML5), HỆ THỐNG PHẢI tự động chuyển sang `MM/yyyy` để truyền xuống Service hợp lệ.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Lỗi khoảng thời gian nghịch đảo**: KHI thời gian truyền vào gây mâu thuẫn (Ví dụ khoảng thời gian không hợp lệ), Mock Service ném lỗi `INVALID_DATE_RANGE`, HỆ THỐNG PHẢI xử lý và thông báo lỗi.
- **Phân quyền**: KHI người dùng có role là `MANAGER` (hoặc khác ADMIN) cố gắng truy cập `/admin/revenue/*`, HỆ THỐNG PHẢI chặn lại và trả mã 403 FORBIDDEN.
- **Không đăng nhập**: Trả mã 401 hoặc Redirect tới `/login`.
- **Path không tồn tại**: Truy cập `/admin/revenue/unknown` PHẢI trả về HTTP 404.

### 2.3 Boundary Values (Các giá trị biên)
- **Không có dữ liệu (Empty State)**: KHI Mock Service trả về `SystemRevenueDTO` với doanh thu `0` hoặc mảng rỗng, Servlet KHÔNG được văng `NullPointerException` mà phải set rỗng bình thường để UI hiển thị "Không có dữ liệu".
- **Tham số Period bị rỗng hoặc Null**: KHI param `period` = rỗng hoặc không truyền, HỆ THỐNG PHẢI tự động fallback về tháng hiện tại (`MM/yyyy` của thời điểm gọi API).
- **Phân trang biên**: KHI truyền `page < 0` hoặc truyền chữ `page=abc` vào `/by-facility`, hệ thống PHẢI fallback về `page = 1`.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Luồng dữ liệu đọc đồng thời**: KHI 100 Admin cùng xem báo cáo doanh thu một lúc, HỆ THỐNG PHẢI xử lý Request một cách Thread-safe. Các biến như `period`, `page` không được khai báo toàn cục (Instance variable) trong Servlet, tránh gây nhiễu dữ liệu báo cáo giữa các Admin.
