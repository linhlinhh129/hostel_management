# Tasks - Danh sách yêu cầu sửa chữa (ListRequest)

- [x] Cập nhật `RequestDAO.java`: Thêm hàm `getRequests()` (có phân trang và bộ lọc).
- [x] Cập nhật `RequestDAO.java`: Thêm hàm `countRequests()` để lấy tổng số bản ghi.
- [x] Tạo `ListRequestServlet.java` tại `/operator/requests` xử lý logic lấy dữ liệu và phân trang.
- [x] Tạo file giao diện `list.jsp` tại `WEB-INF/views/operator/requests/list.jsp`.
- [x] Xây dựng form lọc (Filter) theo Trạng thái (status) và Thể loại (category).
- [x] Xây dựng giao diện Bảng (Table) và Phân trang (Pagination) chuẩn Mintlify.
