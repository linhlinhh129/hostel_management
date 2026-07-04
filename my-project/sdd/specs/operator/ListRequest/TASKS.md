# Tasks - Danh sách yêu cầu sửa chữa (ListRequest)

- [x] Cập nhật `RequestDAO.java`: Thêm hàm `getRequests()` (có phân trang và bộ lọc).
# Tasks - Danh sách yêu cầu sửa chữa (ListRequest)

- [x] Cập nhật `RequestDAO.java`: Thêm hàm `getRequests()` (có phân trang và bộ lọc).
- [x] Cập nhật `RequestDAO.java`: Thêm hàm `countRequests()` để lấy tổng số bản ghi.
- [x] Tạo `ListRequestServlet.java` tại `/operator/requests` xử lý logic lấy dữ liệu và phân trang.
- [x] Tạo file giao diện `list.jsp` tại `WEB-INF/views/operator/requests/list.jsp`.
- [x] Xây dựng form lọc (Filter) theo Trạng thái (status) và Thể loại (category).
- [x] Xây dựng giao diện Bảng (Table) và Phân trang (Pagination) chuẩn Mintlify.

## 2. Giai đoạn 2: Sửa lỗi hiển thị ảnh đính kèm (Manager)
- [x] T001 [US_FIX] Xóa đường dẫn hardcode `/uploads/` và dùng `<c:choose>` để nối đường dẫn ảnh trước khi sửa trong `f:\SU26\New folder\hostel_management\src\main\webapp\WEB-INF\views\manager\tickets\detail.jsp`
- [x] T002 [US_FIX] Xóa đường dẫn hardcode `/uploads/` và dùng `<c:choose>` để nối đường dẫn ảnh sau khi sửa trong `f:\SU26\New folder\hostel_management\src\main\webapp\WEB-INF\views\manager\tickets\detail.jsp`
