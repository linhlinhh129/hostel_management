# Task List: Quản lý bài viết cộng đồng (Manager)

## 1. Cơ sở dữ liệu (Database)

- [x] Đã hoàn thành (Cấm chỉnh sửa hay thiết kế lại database).

## 2. Tầng Model & DTO

- [x] Tạo entity class `CommunityPost` trong `com.quanlyphongtro.model` tham chiếu với bảng `community_posts`.

- [x] Tạo `CommunityPostDTO` và `CommunityPostCreateDTO` trong `com.quanlyphongtro.dto` để giao tiếp dữ liệu.

## 3. Tầng DAO (Data Access Object)

- [x] Tạo `CommunityPostDAO` trong `com.quanlyphongtro.dao`.

- [x] Viết phương thức `insert(CommunityPost post)` - sử dụng PreparedStatement.

- [x] Viết phương thức `getPendingPosts(String cursor, int limit)` - áp dụng Cursor-based và `deleted_at IS NULL`.

- [x] Viết phương thức `approvePost(int postId, int reviewerId)`.

- [x] Viết phương thức `softDeletePost(int postId)` - cập nhật `deleted_at`.

## 4. Tầng Service (Business Logic)

- [x] Tạo `CommunityPostService` và `CommunityPostServiceImpl` trong `com.quanlyphongtro.service`.

- [x] Xử lý logic validate form tạo bài viết (Tiêu đề <= 250 ký tự, Nội dung không rỗng).

- [x] Xử lý logic upload ảnh (validate format JPG/JPEG/PNG, size <= 5MB), copy file upload vào folder quy định.

- [x] Triển khai cơ chế theo dõi Rate Limiting.

## 5. Tầng Servlet (Controller)

- [x] Tạo `CommunityPostServlet` (kế thừa `BaseServlet`) trong package `com.quanlyphongtro.controller.manager`.

- [x] Xử lý HTTP GET: Trả về trang `list-pending.jsp` hoặc dữ liệu JSON với Rate Limit headers.

- [x] Xử lý HTTP POST (Multipart): Xử lý tạo mới bài viết và nhận file upload.

- [x] Xử lý HTTP POST (Action approve): Xử lý duyệt bài viết.

- [x] Xử lý HTTP POST (Action delete): Xử lý xóa mềm bài viết.

## 6. Tầng Frontend (JSP & UI)

- [x] Task 6.1: Xây dựng `create.jsp` tại `/WEB-INF/views/manager/postManagement/`. Hiển thị form tạo bài viết gồm: tiêu đề, nội dung, chụp ảnh trực tiếp hoặc tải ảnh lên, nút Lưu và Hủy.

- [x] Task 6.2: Xây dựng `list-pending.jsp` tại `/WEB-INF/views/manager/postManagement/`. Hiển thị danh sách bài viết trạng thái `PENDING`, bao gồm tiêu đề, người tạo, ngày tạo và các nút **Duyệt**, **Xóa**, **Xem chi tiết**.

- [x] Task 5.3: Mở file `sidebar.jsp`, chèn nhóm menu **Cộng đồng** (Đã có sẵn trong file sidebar.jsp từ trước).

- [x] Task 6.4: Áp dụng giao diện thống nhất theo Bootstrap 5 và `DESIGN.md` cho toàn bộ màn hình của module.

## 7. Kiểm thử (Testing) & Hoàn thiện

- [ ] Test luồng truy cập qua Role Filter: Chỉ `MANAGER` được dùng.

- [ ] Test lỗi upload: Nhập sai tiêu đề, file ảnh > 5MB, sai định dạng.

- [ ] Test tính năng Soft Delete và Cursor-based Pagination.