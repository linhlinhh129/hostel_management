# Implementation Plan: Manager Dashboard (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerDashboardServlet.java`
- **Dependencies**: `DashboardService`
- **Constraint**: Đảm bảo 100% Unit Test. Mock `DashboardService.getDashboardStats(facilityId)` để giả lập trả về `Map<String, Object>` tổng hợp. Tập trung kiểm tra luồng phân quyền và xử lý dữ liệu trống.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerDashboardServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadDashboard_Success`: Đăng nhập Manager, được phân công `facilityId=10`. Mock Service trả về Map có chứa `totalRooms=50`, `occupiedRooms=30`... Hệ thống setAttributes đầy đủ và forward tới `dashboard.jsp`.

### 3.2 Error Cases
- `testDoGet_UnauthorizedAccess`: User chưa đăng nhập hoặc có Role = `TENANT`. Chặn 403.
- `testDoGet_ManagerNotAssignedFacility`: Đăng nhập Manager nhưng `facilityId=0` (chưa phân công cơ sở). Hệ thống bỏ qua gọi DB, trả về các thuộc tính rỗng hoặc 0 để không bị văng lỗi màn hình.
- `testDoGet_DatabaseConnectionError`: Mock Service ném `SQLException`. Servlet catch lỗi và trả về thông báo an toàn, không văng 500.

### 3.3 Boundary Values
- `testDoGet_ZeroRooms_OccupancyRate`: Mock Service trả về `totalRooms=0`, `occupiedRooms=0`. Servlet tính toán tỷ lệ `occupancyRate` phải xử lý ngoại lệ **Divide by Zero** an toàn, trả về `0%`.

### 3.4 Concurrent Scenarios
- `testConcurrency_DashboardLoad`: Dùng `ExecutorService` 10 threads, đảm bảo hàm GET không lưu trạng thái (Stateless).

## 4. Các bước thực hiện
1. Setup Unit Test với Mockito Extension, Mock `DashboardService`.
2. Map test cases với EARS.
