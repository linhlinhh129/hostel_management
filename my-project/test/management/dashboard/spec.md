# Test Specification: Dashboard Ban Quản Lý (Unit Test Only)

**Status:** Draft
**Target Feature:** Dashboard (`my-project/sdd/specs/manager/dashboard`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic Servlet `ManagerDashboardServlet`. Mục tiêu chính là đảm bảo quá trình tổng hợp số liệu hiển thị đúng logic phân quyền và xử lý an toàn các phép toán khi dữ liệu trống, tránh sập Server.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **AC01 - Hiển thị Dashboard thành công**: KHI Manager đã đăng nhập và ĐƯỢC PHÂN CÔNG cơ sở (Facility ID != null), HỆ THỐNG PHẢI gọi `DashboardService.getDashboardStats()` truyền đúng ID cơ sở, sau đó map toàn bộ các biến số (totalRooms, activeContracts, ticketCount...) vào `request.setAttribute` và forward tới `dashboard.jsp`.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **AC02 - Chưa đăng nhập hoặc sai Role**: KHI User chưa đăng nhập (Session null) HOẶC Role không phải `MANAGER`, HỆ THỐNG PHẢI chặn lại ngay từ đầu (redirect `/login` hoặc trả về 403 Forbidden).
- **AC03 - Manager chưa được phân công cơ sở**: KHI Manager đăng nhập thành công nhưng thuộc tính `facilityId` bị rỗng (chưa được Admin phân công), HỆ THỐNG PHẢI không gọi query nặng, tự động trả về các thuộc tính giao diện bằng `0` hoặc chuỗi rỗng `"-"` để trang vẫn render bình thường mà không bị văng lỗi.
- **AC04 - Lỗi kết nối Database**: KHI Mock Service ném ra `SQLException` (Database down), Servlet PHẢI catch lỗi, log error và gán các thuộc tính rỗng để trả về trang Dashboard trắng chứ không hiện trang lỗi 500 ném Stacktrace ra ngoài.

### 2.3 Boundary Values (Các giá trị biên)
- **Cơ sở mới hoàn toàn (0 phòng)**: KHI Mock Service trả về `totalRooms = 0`, HỆ THỐNG BẮT BUỘC phải bắt được lỗi **Chia cho 0 (Divide by Zero)** ở công thức tính tỷ lệ lấp đầy (`occupancyRate`). Giá trị trả về cho View phải là `0%` thay vì nổ exception `ArithmeticException`.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Thread-Safety của Servlet**: KHI 10 request từ các Manager khác nhau cùng truy cập hàm `doGet` của `ManagerDashboardServlet` cùng lúc. HỆ THỐNG PHẢI xử lý an toàn, không có biến instance (cấp class) nào bị ghi đè chéo khiến Manager A nhìn thấy dữ liệu của Manager B.
