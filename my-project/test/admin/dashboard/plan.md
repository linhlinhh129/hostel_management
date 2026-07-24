# Implementation Plan: Admin Dashboard Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminDashboardServlet.java`
- **Constraint**: Đảm bảo 100% Unit Test không gọi xuống Database thật (Mock toàn bộ các dependency như DAO, Service). Test thuần túy Hành vi (Behavior).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_Success_WithData`: Giả lập các DAO (Revenue, Facility, Notification, AuditLog) trả về dữ liệu thực tế. Kiểm tra `request.setAttribute` nhận đúng các key (`monthlyRevenue`, `totalFacilities`, `recentActivities`, v.v.) và forward đúng về `dashboard.jsp`.
- `testDoGet_Success_EmptyData`: Giả lập các DAO trả về `0` hoặc danh sách rỗng. Đảm bảo Servlet không ném lỗi `NullPointerException` và gán đúng các giá trị mặc định.

### 3.2 Error Cases
- `testDoGet_WithoutSession_RedirectsToLogin`: Request không có `currentUser`. Xác minh `response.sendRedirect("/login")`.
- `testDoGet_RoleManager_Returns403`: Request có `currentUser` nhưng role là `MANAGER`. Xác minh `response.sendError(403)`.
- `testDoGet_DaoThrowsException`: Giả lập một DAO (ví dụ `AuditLogDAO`) ném Exception. Đảm bảo hệ thống không sập, mà catch lỗi và gán các attribute còn lại bình thường (hoặc gán rỗng cho phần bị lỗi).

### 3.3 Boundary Values
- `testRecentActivities_Limit5`: Kiểm tra tham số truyền vào DAO khi query hoạt động gần nhất phải đúng giới hạn là `5`.
- `testMonthlyRevenue_OnlyPaid`: Kiểm tra tham số trạng thái truyền vào `RevenueDAO` để tính doanh thu phải là `PAID`.

### 3.4 Concurrent Scenarios
- `testDoGet_Concurrency_NoSharedState`: Chạy song song nhiều threads gọi `doGet` cùng lúc để đảm bảo Servlet an toàn luồng (Thread-safe), không khai báo biến instance gây tranh chấp dữ liệu.

## 4. Các bước thực hiện
1. Khởi tạo class test `AdminDashboardServletTest`.
2. Setup các annotation `@Mock` và `@InjectMocks` (hoặc khởi tạo thủ công).
3. Triển khai code cho từng phương thức test.
4. Chạy `mvn test` để xác minh và pass toàn bộ.
