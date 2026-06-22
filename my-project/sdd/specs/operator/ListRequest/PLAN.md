# Kế hoạch Thực hiện - Danh sách Yêu cầu (ListRequest)

## 1. Mục tiêu
Thiết kế trang Danh sách yêu cầu sửa chữa cho nhân viên Vận hành, cho phép xem, lọc và phân trang (Server-side) theo đúng chuẩn Mintlify và cấu trúc layout `app-shell`. Lấy dữ liệu thực từ bảng `requests` trong DB mà KHÔNG chỉnh sửa bất kỳ cấu trúc DB nào.

## 2. Kiến trúc Backend (Java Servlet & DAO)
- **DAO (`RequestDAO.java`):** Bổ sung 2 hàm mới:
  - `List<Request> getRequests(String status, String category, int offset, int limit)`: Truy vấn danh sách có lọc và phân trang, JOIN với bảng `users`, `rooms`, `facilities` để lấy tên phòng và người gửi. Sắp xếp theo `created_at DESC` (do DB không có cột `appointment_date` như trong Spec giả định).
  - `int countRequests(String status, String category)`: Đếm tổng số bản ghi thỏa mãn bộ lọc để tính số trang.
- **Servlet (`ListRequestServlet.java`):**
  - Mapped URL: `/operator/requests`
  - Đọc các tham số `status`, `category`, `page` từ URL.
  - Gọi DAO, tính toán phân trang (`limit = 20`).
  - Set attributes và forward tới `list.jsp`.

## 3. Kiến trúc Frontend (JSP)
- **File:** `list.jsp` trong thư mục `/WEB-INF/views/operator/requests/`
- **Layout:** Sử dụng `.app-shell` và `.main-wrapper`.
- **Thành phần giao diện:**
  - **Header & Filter Bar:** Khối tiêu đề có thanh công cụ lọc dữ liệu. Form lọc GET trực tiếp lên `/operator/requests`.
  - **Table:** Bảng danh sách hiển thị các cột: Mã YC, Tiêu đề, Phòng, Thể loại, Ngày tạo, Trạng thái, Hành động. Sử dụng class `custom-table` và `table-hover` đồng bộ với trang Điên nước.
  - **Pagination:** Khối phân trang bên dưới bảng.
  - **Empty State:** Hiển thị thông báo "Không có yêu cầu nào phù hợp" nếu danh sách rỗng.
