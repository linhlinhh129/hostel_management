# TASKS: Quản lý bài viết cá nhân (Tenant Post Management)

Dựa trên yêu cầu trong `SPEC.md` và `PLAN.md`, dưới đây là danh sách các công việc (tasks) cần thực hiện để hoàn thành tính năng **Quản lý bài viết cá nhân dành cho Khách thuê (Tenant)**:

## 1. Database & DAO Layer
- [ ] Bổ sung thiết kế bảng `posts` và `post_images` trong CSDL nếu chưa có.
- [ ] Viết các phương thức trong `PostDAO`:
  - [ ] `getPostsByTenantId(int tenantId)`: Lấy danh sách bài viết do Tenant tạo, sắp xếp theo thời gian mới nhất (DESC).
  - [ ] `getPostByIdAndTenantId(int postId, int tenantId)`: Lấy thông tin chi tiết bài viết kèm mảng ảnh đính kèm, kiểm tra quyền sở hữu theo `tenant_id`.
  - [ ] `createPost(Post post, List<PostImage> images)`: Tạo bài viết mới ở trạng thái `Pending` và lưu danh sách ảnh đính kèm.
  - [ ] `deletePost(int postId, int tenantId)`: Xóa bài viết và các ảnh liên quan với điều kiện sở hữu `post_id = ? AND tenant_id = ?`.

## 2. Controller (Java Web Servlets)
- [ ] Tạo `TenantMyPostsServlet` mapped với `@WebServlet("/tenant/my-posts")` (GET):
  - [ ] Kiểm tra Session đăng nhập & role `TENANT`.
  - [ ] Gọi `PostDAO.getPostsByTenantId()`, gán `request.setAttribute("postList", ...)` và forward sang `/WEB-INF/views/tenant/my-posts.jsp`.
- [ ] Tạo `TenantPostDetailServlet` mapped với `@WebServlet("/tenant/post-detail")` (GET):
  - [ ] Nhận tham số `id`, gọi `PostDAO.getPostByIdAndTenantId()`.
  - [ ] Nếu không phải chính chủ: Forward trang lỗi 403 Forbidden.
  - [ ] Nếu hợp lệ: Gán `request.setAttribute("post", ...)` và forward sang `/WEB-INF/views/tenant/post-detail.jsp`.
- [ ] Tạo `TenantPostCreateFormServlet` mapped với `@WebServlet("/tenant/post-create")` (GET):
  - [ ] Forward sang giao diện tạo bài viết `/WEB-INF/views/tenant/post-create.jsp`.
- [ ] Tạo `TenantPostCreateServlet` mapped với `@WebServlet("/tenant/post-create")` (POST):
  - [ ] Cấu hình `@MultipartConfig` xử lý upload multi-file (tối đa 10 ảnh, max 10MB/ảnh, định dạng JPG/JPEG/PNG/WEBP).
  - [ ] Read `title`, `content` và danh sách ảnh tải lên. Validate không để rỗng.
  - [ ] Lưu bài viết và Redirect về `/tenant/my-posts?msg=created_success`.
- [ ] Tạo `TenantPostDeleteServlet` mapped với `@WebServlet("/tenant/post-delete")` (POST):
  - [ ] Nhận `id` bài viết từ form.
  - [ ] Kiểm tra quyền sở hữu chính chủ (`post.tenant_id == session.tenant_id`).
  - [ ] Nếu không chính chủ: Forward trang lỗi 403 (Forbidden).
  - [ ] Nếu hợp lệ: Thực thi xóa bài viết và Redirect về `/tenant/my-posts?msg=deleted_success`.

## 3. View (JSP & UI)
- [ ] Tạo file `/WEB-INF/views/tenant/my-posts.jsp`:
  - [ ] Hiển thị danh sách các bài viết cá nhân do Tenant hiện tại tạo.
  - [ ] Thẻ badge thể hiện trạng thái bài viết (`Pending`, `Approved`, `Rejected`).
  - [ ] Nút "Xem chi tiết" dẫn tới `/tenant/post-detail?id=${post.id}`.
  - [ ] Nút "Xóa bài viết" kèm xác nhận Javascript `confirm()` gửi tới `POST /tenant/post-delete`.
- [ ] Tạo file `/WEB-INF/views/tenant/post-detail.jsp`:
  - [ ] Hiển thị đầy đủ thông tin tiêu đề, nội dung bài viết và bộ sưu tập (gallery) hình ảnh đính kèm.
- [ ] Tạo file `/WEB-INF/views/tenant/post-create.jsp`:
  - [ ] Form nhập tiêu đề, nội dung bài viết.
  - [ ] Component đính kèm file ảnh (tải từ thiết bị hoặc chụp ảnh).

## 4. Kiểm thử & Bảo mật (Testing & Validation)
- [ ] Test trường hợp Tenant xem danh sách bài viết cá nhân của chính mình.
- [ ] Test tạo bài viết thành công (kiểm tra upload 1 ảnh và upload nhiều ảnh <= 10 ảnh).
- [ ] Test validate bài viết (tiêu đề/nội dung rỗng, file ảnh quá 10MB hoặc sai định dạng).
- [ ] Test xóa bài viết thành công.
- [ ] Test bảo mật (IDOR): Đăng nhập với tư cách Tenant A, gửi `POST /tenant/post-delete` để xóa `postId` của Tenant B xem có bị hệ thống chặn lỗi 403 hay không.
