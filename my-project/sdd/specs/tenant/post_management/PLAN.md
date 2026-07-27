# Implementation Plan: Manage Personal Posts (Tenant)

## 1. Context and Goals
Tính năng Quản lý bài viết cá nhân (Manage Personal Posts) cho phép Tenant tạo, xem chi tiết, xem danh sách và xóa các bài viết cá nhân (kèm hình ảnh). Các bài viết này ban đầu sẽ ở trạng thái **Pending** và chờ Ban quản lý phê duyệt trước khi hiển thị trên Bản tin (News Feed).

## 2. Open Questions & Spec Contradictions
> [!WARNING]
> **Mâu thuẫn trong SPEC.md:**
> - **Story 5** ghi: "khi bài viết đã được Ban quản lý duyệt, tôi sẽ **không được phép** xóa bài viết đó."
> - **AC-06** ghi: "Bài viết sẽ không còn xuất hiện trong danh sách bài viết cá nhân và trên bản tin (**nếu đã được duyệt**)." => Hàm ý là có thể xóa bài viết đã duyệt.
> - **Context Assumption 3** cũng đề cập "Tenant có thể xóa bài viết ở mọi trạng thái (Pending hoặc Approved)."
> 
> **Quyết định đề xuất (Action needed):** Cần chốt lại với PO/BA để thống nhất việc Tenant có được phép xóa bài viết đã duyệt hay không để cài đặt validation ở API `DELETE /api/v1/posts/{id}` cho phù hợp.

## 3. Database Design
Bổ sung thêm bảng mới vào thiết kế cơ sở dữ liệu (`schema.sql`) để lưu trữ bài viết và hình ảnh:

### `posts` table
- `post_id` (INT IDENTITY PRIMARY KEY)
- `tenant_id` (INT, FK to `users`)
- `title` (NVARCHAR(200) NOT NULL)
- `content` (NVARCHAR(MAX) NOT NULL)
- `status` (NVARCHAR(20) DEFAULT 'PENDING') -- PENDING, APPROVED, REJECTED
- `created_at` (DATETIME2 DEFAULT GETDATE())
- `updated_at` (DATETIME2 DEFAULT GETDATE())
- `deleted_at` (DATETIME2 NULL)

### `post_images` table
Do yêu cầu tối đa 10 ảnh mỗi bài, ta nên lưu thành bảng riêng để dễ quản lý hoặc mở rộng sau này:
- `image_id` (INT IDENTITY PRIMARY KEY)
- `post_id` (INT, FK to `posts`)
- `image_url` (NVARCHAR(500) NOT NULL)
- `created_at` (DATETIME2 DEFAULT GETDATE())

## 4. Backend (Servlet Controllers & Action Endpoints)
- **`TenantMyPostsServlet`** (`GET /tenant/my-posts`):
  - Lấy danh sách bài viết cá nhân do `tenant_id` từ Session đã tạo, sắp xếp theo thời gian mới nhất (DESC).
  - Scope Attribute: `request.setAttribute("postList", List<PostDTO>)`.
  - Forward View: `/WEB-INF/views/tenant/my-posts.jsp`.

- **`TenantPostDetailServlet`** (`GET /tenant/post-detail?id={postId}`):
  - Đọc `id` bài viết, kiểm tra quyền sở hữu của Tenant đang đăng nhập.
  - Scope Attribute: `request.setAttribute("post", PostDetailDTO)`.
  - Forward View: `/WEB-INF/views/tenant/post-detail.jsp`.
  - Trả về 403 Page nếu không phải chủ sở hữu.

- **`TenantPostCreateFormServlet`** (`GET /tenant/post-create`):
  - Forward View: `/WEB-INF/views/tenant/post-create.jsp`.

- **`TenantPostCreateServlet`** (`POST /tenant/post-create`):
  - Nhận form `multipart/form-data` gồm `title`, `content`, `images` (multi-file upload, max 10 ảnh, <= 10MB/ảnh).
  - Validate dữ liệu, lưu bài viết ở trạng thái `Pending` và lưu ảnh vào CSDL/Storage.
  - Success Redirect: `response.sendRedirect(request.getContextPath() + "/tenant/my-posts?msg=created_success")`.
  - Error: Forward lại `post-create.jsp` kèm thông báo lỗi.

- **`TenantPostDeleteServlet`** (`POST /tenant/post-delete`):
  - Nhận `id` bài viết cần xóa.
  - Kiểm tra điều kiện chính chủ: `post.tenant_id == session.tenant_id`.
  - Nếu không chính chủ: Forward trang lỗi 403 (Forbidden).
  - Nếu hợp lệ: Xóa bài viết cùng các ảnh/dữ liệu liên quan và Redirect về `/tenant/my-posts?msg=deleted_success`.

## 5. Frontend & Views (JSP & Servlet Flow)
- **View Templates**:
  - `/WEB-INF/views/tenant/my-posts.jsp`: Hiển thị danh sách bài viết cá nhân do Tenant đăng nhập tạo ra, badge trạng thái (Pending/Approved/Rejected), nút Xem chi tiết và nút Xóa bài viết.
  - `/WEB-INF/views/tenant/post-detail.jsp`: Hiển thị nội dung chi tiết bài viết cá nhân và bộ sưu tập (gallery) hình ảnh đính kèm.
  - `/WEB-INF/views/tenant/post-create.jsp`: Form nhập tiêu đề, nội dung, đính kèm tối đa 10 ảnh (upload từ máy hoặc chụp ảnh).
- **Form Actions & Redirects**:
  - Submit Form Đăng bài (`POST /tenant/post-create`) với `enctype="multipart/form-data"`.
  - Submit Form Xóa bài (`POST /tenant/post-delete`) kèm hộp thoại xác nhận JavaScript `confirm()`.
  - Nhận thông báo qua query param `?msg=created_success` hoặc `?msg=deleted_success` hiển thị Alert thông báo trên JSP.
