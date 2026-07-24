# Implementation Plan: Dashboard Operator Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorDashboardServlet.java`, `DashboardService` / `IncidentDAO` / `AppointmentDAO`.
- **Constraint**: Đảm bảo 100% Unit Test. Mock các method đếm (Count) và danh sách (List) cho việc render Dashboard. Đảm bảo logic xử lý ngoại lệ DB an toàn.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorDashboardServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadDashboard_Success`: Render đầy đủ Card Thống kê (Pending, InProgress, Meter Read) và Lịch hẹn trong ngày hôm nay.

### 3.2 Error Cases
- `testDoGet_RoleMismatch`: Giả lập Session User mang Role `TENANT` truy cập. Xác nhận hệ thống trả về HTTP 403 hoặc chuyển hướng.
- `testDoGet_DatabaseException_GracefulHandling`: Khi gọi `mockDashboardService.getSummary()` văng `SQLException`. Xác nhận View vẫn render ra kèm Data mặc định rỗng thay vì ném Exception dội ngược về Tomcat.

### 3.3 Boundary Values
- `testDoGet_ZeroData`: Toàn bộ các thông số là 0, không có lịch hẹn. Xác nhận list lịch hẹn là Empty và View xử lý tốt.
- `testDoGet_AppointmentsLimit`: Mock trả về 15 Lịch hẹn. Xác minh rằng Controller/View chỉ gán 5 Lịch hẹn vào request attributes để show trên màn hình chính.

### 3.4 Concurrent Scenarios
- `testConcurrency_SnapshotRead`: Giả lập tình huống luồng khác đang Insert dữ liệu, hệ thống lấy ra giá trị tĩnh đúng tại thời điểm load Dashboard mà không làm sai lệch số liệu.

## 4. Các bước thực hiện
1. Thiết lập `OperatorDashboardServletTest` với Mockito (`@Mock DashboardService`).
2. Viết Test method tuân thủ format comment `# EARS [...]`.
