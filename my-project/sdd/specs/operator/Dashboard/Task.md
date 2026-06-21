# Danh sách công việc (Tasks) - Chức năng Dashboard

## 1. Tầng Database & DAO
- [x] Khởi tạo file `DashboardDAO.java` (Sử dụng lại `OperatorDashboardDAO.java` hiện có).
- [x] Viết truy vấn SQL đếm số yêu cầu (pending, in-progress).
- [x] Viết truy vấn SQL tính tiến độ chốt số điện nước.
- [x] Viết truy vấn SQL lấy danh sách lịch hẹn sửa chữa trong ngày.

## 2. Tầng Service & DTO
- [x] Khởi tạo `DashboardSummaryDTO.java` (Tái sử dụng luồng truyền trực tiếp thuộc tính request).
- [x] Khởi tạo `DashboardService.java` để tổng hợp dữ liệu từ DAO.

## 3. Tầng Controller
- [x] Khởi tạo `OperatorDashboardServlet.java` map với `/operator/dashboard` (Đã cập nhật logic hiện có).
- [x] Cấu hình lấy dữ liệu từ Service và forward sang file view JSP.

## 4. Tầng Giao diện (UI)
- [x] Tạo file `dashboard.jsp` trong thư mục `WEB-INF/views/operator/dashboard/`.
- [x] Xây dựng giao diện Thẻ thống kê (Metric Cards).
- [x] Xây dựng giao diện Nút thao tác nhanh (Quick Actions).
- [x] Xây dựng giao diện Bảng lịch hẹn hôm nay.
- [x] Bổ sung menu Dashboard vào file `sidebar.jsp`.

## 5. Kiểm thử & Căn chỉnh
- [x] Test thủ công để đảm bảo độ chính xác của các con số.
- [x] Kiểm tra tính responsive trên thiết bị di động (các thẻ phải xếp thành cột dọc).
