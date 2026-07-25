# Feature: Dashboard Nhân viên vận hành

**Status:** Draft  
**Author:** [Antigravity]  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-20  
**Priority:** High

---

## 1. Business Context

Để tối ưu hóa trải nghiệm làm việc cho nhân viên vận hành, tính năng Dashboard ra đời nhằm cung cấp một giao diện tập trung, bao quát toàn bộ các hoạt động nghiệp vụ thiết yếu. Thay vì phải điều hướng qua nhiều module rời rạc, nhân viên có thể xem trực tiếp các con số thống kê quan trọng, danh sách các công việc khẩn cấp trong ngày, và sử dụng các nút truy cập nhanh để thực hiện báo cáo sự cố hoặc ghi điện nước một cách linh hoạt. 

---

## 2. User Stories

### Story 1 (Xem tổng quan công việc)
**As a** nhân viên vận hành,  
**I want to** xem các con số tổng quan (Số yêu cầu chờ xử lý, Số yêu cầu đang làm, Tiến độ chốt sổ điện nước),  
**so that** tôi đánh giá nhanh được khối lượng công việc hiện tại.

### Story 2 (Thao tác nhanh)
**As a** nhân viên vận hành,  
**when** đang đi tuần tra và phát hiện vấn đề,  
**I want to** bấm nút "Báo cáo sự cố mới" ngay trên Dashboard,  
**so that** tôi không phải mất thời gian tìm kiếm chức năng trong thanh Menu.

### Story 3 (Theo dõi công việc sát nút)
**As a** nhân viên vận hành,  
**I want to** thấy được danh sách các yêu cầu sửa chữa có lịch hẹn trong ngày hôm nay ở ngay màn hình chính,  
**so that** tôi chủ động sắp xếp thời gian đi tới các phòng tương ứng mà không bị lỡ hẹn.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị Thẻ thống kê (Metric Cards)
**WHEN** user truy cập vào Dashboard  
**THE SYSTEM SHALL** hiển thị các Thẻ số lượng bao gồm:
- Tổng số Yêu cầu đang thực hiện (In-progress Requests).
- Tổng số Sự cố đã báo cáo (Pending Incidents).
- Tiến độ Ghi điện nước tháng này (Đã cập nhật / Tổng số phòng).
- (Optional) Số sự cố cá nhân đã báo cáo trong tháng.

### AC02 – Hiển thị Lối tắt thao tác (Quick Actions)
**WHEN** màn hình Dashboard được render  
**THE SYSTEM SHALL** hiển thị nổi bật các nút bấm (Buttons):
- "Báo cáo sự cố mới" (Dẫn đến màn hình Create Incident).
- "Cập nhật điện nước" (Dẫn đến màn hình Update Meter Reading).

### AC03 – Hiển thị Lịch hẹn hôm nay (Today's Appointments)
**WHEN** user truy cập vào Dashboard  
**THE SYSTEM SHALL** hiển thị một danh sách ngắn gọn (Tối đa 5 dòng) chứa các yêu cầu sửa chữa có `appointment_date` trùng với ngày hiện tại.  
**AND** mỗi dòng có thể click vào để điều hướng đến màn hình "Chi tiết yêu cầu".

### AC04 – Xử lý luồng Click vào Thẻ thống kê
**WHEN** user click vào thẻ "Yêu cầu đang thực hiện"  
**THE SYSTEM SHALL** điều hướng user đến màn hình "Danh sách yêu cầu sửa chữa"  
**AND** tự động áp dụng bộ lọc trạng thái `status = in_progress`.

---

## 4. Giao tiếp Hệ thống (System Flow)

### Đường dẫn (Endpoint)
* **Endpoint:** `GET /operator/dashboard`
* **Loại dữ liệu (Content-Type):** Trả về HTML (JSP)

### Phản hồi Hệ thống (System Response)
* Server xử lý lấy thông tin `operatorId` từ Session.
* Lấy dữ liệu thống kê qua `OperatorDashboardDAO` và `MeterReadingService`.
* Gắn dữ liệu vào các attribute (`totalRooms`, `pendingTickets`, `upcomingAppointments`,...).
* **Thành công:** Forward đến giao diện `/WEB-INF/views/operator/dashboard.jsp`.

---

## 5. Technical Constraints

- **Tối ưu truy vấn:** API lấy dữ liệu Dashboard phải gọi nhiều hàm COUNT. Để tránh nghẽn Database, khuyến nghị sử dụng cơ chế Caching (Redis/Memcached) hoặc Materialized Views cho các dữ liệu ít thay đổi (như `total_rooms`).
- **Thời gian phản hồi:** Endpoint `/summary` phải trả về kết quả dưới `< 800ms`.
- **Responsive UI:** Khung hiển thị các thẻ thống kê trên giao diện Web (PC) sẽ dàn hàng ngang (Grid), nhưng trên Mobile bắt buộc phải tự động chuyển thành hàng dọc (Stack) để dễ dàng cuộn và theo dõi.

---

## 6. Out of Scope

- Báo cáo biểu đồ nâng cao (Charts) không thuộc phạm vi của MVP này. Giai đoạn này chỉ tập trung vào Số liệu tổng hợp (Numbers) và Danh sách (Lists).
- Tính năng tự động làm mới trang (Auto-refresh) chưa cần triển khai ngay, user có thể load lại trang thủ công khi cần.
