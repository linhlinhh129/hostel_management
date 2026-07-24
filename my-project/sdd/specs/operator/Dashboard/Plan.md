# Kế hoạch triển khai: Tính năng Dashboard Nhân viên vận hành

Dựa trên các tài liệu đặc tả `Context.md` và `Spec.md` đã xây dựng, dưới đây là kế hoạch kỹ thuật chi tiết để triển khai màn hình Dashboard.

## Các hạng mục triển khai

### 1. Tầng Database & Truy xuất dữ liệu (DAO)
#### [TẠO MỚI] `DashboardDAO.java`
Tạo một class DAO mới để xử lý việc tổng hợp dữ liệu hiệu quả. Vì dự án yêu cầu sử dụng Basic SQL, chúng ta sẽ viết các truy vấn để lấy:
- `getPendingRequestsCount(int operatorId)`: Đếm số yêu cầu đang chờ xử lý.
- `getInProgressRequestsCount(int operatorId)`: Đếm số yêu cầu đang thực hiện.
- `getMeterReadingProgress(int operatorId, int month, int year)`: Lấy tiến độ ghi điện nước (số phòng đã ghi / tổng số phòng).
- `getIncidentsReportedCount(int operatorId, int month, int year)`: Đếm số sự cố đã báo cáo trong tháng.
- `getTodaysAppointments(int operatorId, String todayDate)`: Lấy danh sách các lịch hẹn trong ngày hôm nay.

### 2. Tầng Business Logic (Service)
#### [TẠO MỚI] `DashboardService.java`
- Chịu trách nhiệm gọi các hàm từ `DashboardDAO`.
- Tổng hợp các kết quả trả về vào một đối tượng chung `DashboardSummaryDTO`.

#### [TẠO MỚI] `DashboardSummaryDTO.java`
- Đối tượng DTO chứa toàn bộ dữ liệu hiển thị (Số lượng yêu cầu, Tiến độ điện nước, Số sự cố, Danh sách lịch hẹn).

### 3. Tầng Controller (Servlet)
#### [TẠO MỚI] `OperatorDashboardServlet.java`
- Điều hướng request tới đường dẫn `/operator/dashboard`.
- Trong phương thức `doGet`, sẽ gọi `DashboardService` để lấy `DashboardSummaryDTO`, sau đó set thuộc tính này vào request và forward tới giao diện JSP (`dashboard.jsp`). Việc này nhằm đảm bảo tính đồng nhất với các màn hình hiện tại của hệ thống.

### 4. Tầng Giao diện (JSP UI)
#### [TẠO MỚI] `dashboard.jsp`
Đường dẫn dự kiến: `/WEB-INF/views/operator/dashboard/dashboard.jsp`
- **Thẻ thống kê (Metric Cards):** Hiển thị các con số yêu cầu và tiến độ điện nước.
- **Thao tác nhanh (Quick Actions):** Các nút bấm chuyển hướng trực tiếp tới `/operator/incidents/create` và `/operator/meter-readings/update`.
- **Lịch hẹn hôm nay:** Một bảng/danh sách ngắn hiển thị các lịch hẹn sửa chữa trong ngày, khi bấm vào sẽ chuyển đến trang chi tiết yêu cầu. Bảng này sẽ dùng `appointSchedule` (cột `appoint_schedule` trong DB) để lấy đúng thời gian lịch hẹn, hiển thị dưới dạng HH:mm, thay vì lấy từ `rejection_reason` như trước đây để đảm bảo tính chính xác và hiển thị tốt cả cho các yêu cầu PENDING và IN_PROGRESS. Tích hợp thêm hàm `getDashboardAppointmentTime()` vào model `Request` để định dạng thời gian.

#### [CẬP NHẬT] `sidebar.jsp`
- Thêm một menu "Dashboard" ở vị trí đầu tiên trong thanh điều hướng (sidebar) của nhân viên vận hành.

## Kế hoạch kiểm thử (Verification Plan)
### Kiểm thử thủ công
1. Đăng nhập với tài khoản Nhân viên vận hành.
2. Xác nhận màn hình Dashboard là trang hiển thị đầu tiên (hoặc có thể bấm từ sidebar).
3. Kiểm tra các con số thống kê xem có khớp với dữ liệu thực tế đang có trong hệ thống không.
4. Bấm vào các nút Thao tác nhanh xem có chuyển hướng đúng trang hay không.
5. Kiểm tra danh sách Lịch hẹn xem có đúng các lịch của ngày hôm nay và click vào có ra chi tiết không.

---
**Câu hỏi mở cần xác nhận:** 
- Bạn có đồng ý với việc dùng Servlet gửi thẳng dữ liệu qua `dashboard.jsp` như các màn hình cũ không, hay bắt buộc phải viết API trả về JSON như trong file Spec mô tả (nếu dùng API JSON thì sẽ tốn công setup thêm cơ chế AJAX ở Frontend)? Mặc định mình sẽ dùng cách forward JSP cho nhanh và đồng nhất code base nhé!
