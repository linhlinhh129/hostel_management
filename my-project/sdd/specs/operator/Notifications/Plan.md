# Kế hoạch triển khai: Danh sách thông báo hệ thống (Operator Notifications)

Dựa trên tài liệu `Context.md` và `Spec.md` đã chốt, dưới đây là kế hoạch chi tiết để xây dựng tính năng Xem thông báo dành cho Nhân viên vận hành (Operator).

## 1. Tầng Data (DAO)

**File:** `src/main/java/com/quanlyphongtro/dao/NotificationDAO.java`
- Bổ sung hàm `findNotificationsForOperator(int facilityId, int page, int limit)`:
  - Lấy thông báo với điều kiện:
    - `status = 'SENT'`
    - `deleted_at IS NULL`
    - `target_type = 'ALL' OR (target_type = 'FACILITY' AND facility_id = ?)`
  - Sắp xếp: `sent_at DESC` hoặc `created_at DESC`.
- Bổ sung hàm `countNotificationsForOperator(int facilityId)` để đếm tổng số bản ghi phục vụ phân trang.

## 2. Tầng Controller (Servlet)

**File:** `src/main/java/com/quanlyphongtro/controller/operator/NotificationListServlet.java` (Tạo mới)
- Ánh xạ đường dẫn: `/operator/notifications`
- Method: `GET`
- Xử lý logic:
  - Kiểm tra Session để lấy `facilityId` của Operator đang đăng nhập.
  - Lấy tham số `page` từ URL (mặc định là `1`), `limit` (mặc định là `10`).
  - Gọi `NotificationDAO` để lấy danh sách.
  - Tính toán các giá trị phân trang (`totalPages`, `currentPage`).
  - Chuyển dữ liệu sang request attribute và forward request sang file `notifications.jsp`.

## 3. Tầng View (UI)

**File:** `src/main/webapp/WEB-INF/views/operator/notifications.jsp` (Tạo mới)
- Thiết kế:
  - Sử dụng Layout chuẩn của hệ thống (Header, Sidebar, Footer).
  - Tạo giao diện Danh sách thông báo dạng thẻ (Card) hoặc bảng (Table).
  - Các thông tin hiển thị: Tiêu đề, Nội dung tóm tắt, Thời gian gửi, Badge phân loại (Toàn hệ thống / Khu trọ).
  - Cung cấp Accordion hoặc Modal (Popup) để "Xem chi tiết" toàn văn nội dung thông báo.
  - Thanh phân trang (Pagination) ở cuối danh sách.

**File:** `src/main/webapp/WEB-INF/views/layout/sidebar-operator.jsp` (Cập nhật)
- Thêm menu **"Thông báo hệ thống"** vào thanh Sidebar, dẫn tới `/operator/notifications`.
- Đánh dấu trạng thái `active` khi truy cập đúng đường dẫn.

## 4. Kế hoạch Kiểm thử (Verification Plan)
- Đăng nhập bằng tài khoản Operator (VD: `operator01`).
- Kiểm tra thanh Sidebar hiển thị item "Thông báo hệ thống".
- Truy cập vào trang danh sách thông báo và kiểm tra tính chính xác của dữ liệu (chỉ hiện thông báo `ALL` hoặc `FACILITY` thuộc quản lý của `operator01`).
- Đăng nhập bằng Role khác (Tenant) để test thử tính năng phân quyền (chặn truy cập `/operator/notifications`).
