# Implementation Plan: Tương tác với Bản tin (News Feed)

## 1. Context and Goals
Tính năng "Tương tác với Bản tin" (Interact with News Feed) cung cấp giao diện cho Tenant theo dõi các bài viết đã được Ban quản lý phê duyệt trong ngày hiện tại. Đồng thời, hệ thống cho phép Tenant xem chi tiết, thích/bỏ thích (Like/Unlike) và bình luận (Comment) vào bài viết, nhằm xây dựng cộng đồng gắn kết và trao đổi thông tin minh bạch.

## 2. Database Design
Dựa trên kiến trúc hiện tại, chúng ta cần bổ sung thêm 2 bảng mới để lưu trữ dữ liệu lượt Thích và Bình luận, liên kết trực tiếp với bảng `posts` và `users`:

### `post_likes` table
- `like_id` (INT IDENTITY PRIMARY KEY)
- `post_id` (INT, FK to `posts`)
- `tenant_id` (INT, FK to `users`)
- `created_at` (DATETIME2 DEFAULT GETDATE())
- **Constraint**: `UNIQUE(post_id, tenant_id)` để đảm bảo mỗi Tenant chỉ được thích bài viết 1 lần. 

### `post_comments` table
- `comment_id` (INT IDENTITY PRIMARY KEY)
- `post_id` (INT, FK to `posts`)
- `tenant_id` (INT, FK to `users`)
- `content` (NVARCHAR(1000) NOT NULL)
- `created_at` (DATETIME2 DEFAULT GETDATE())

*Ghi chú*: Thay vì thêm cột `likeCount` và `commentCount` trực tiếp trong bảng `posts` và phải dùng Trigger/Event để cập nhật liên tục, ta có thể `COUNT()` trực tiếp từ 2 bảng trên qua lệnh `JOIN` khi query danh sách bài viết. Phương án này vừa an toàn dữ liệu, vừa phù hợp với lượng tải của nghiệp vụ.

## 3. Backend (Servlet Controllers & Action Endpoints)
- **`TenantNewsFeedServlet`** (`GET /tenant/news-feed`):
  - Truy vấn lấy danh sách bài viết có trạng thái `status = 'APPROVED'` được duyệt trong 24 giờ qua và Top 5 bài viết nổi bật.
  - Sắp xếp theo thời gian phê duyệt mới nhất (DESC).
  - Tính toán `likeCount` và `commentCount` cho từng bài viết.
  - Scope Attributes: `request.setAttribute("postList", List<CommunityPostDTO>)` & `request.setAttribute("topPostList", List<CommunityPostDTO>)`.
  - Forward View: `/WEB-INF/views/tenant/news-feed.jsp`.

- **`TenantPostReactionServlet`** (`POST /tenant/post-reaction`):
  - Nhận `postId` từ form parameters.
  - Kiểm tra `user_id` hiện tại từ session trong bảng `post_reactions`.
  - Toggle Like/Unlike: Xóa bản ghi nếu đã thích, hoặc thêm mới nếu chưa thích.
  - Action Redirect: `response.sendRedirect(request.getContextPath() + "/tenant/news-feed#post-" + postId)`.

- **`TenantPostCommentServlet`** (`POST /tenant/post-comment`):
  - Validate nội dung bình luận `content` (không rỗng, <= 1000 ký tự).
  - Lưu bản ghi mới vào `post_comments` với `user_id` hiện tại.
  - Action Redirect: `response.sendRedirect(request.getContextPath() + "/tenant/news-feed#post-" + postId)`.

- **`TenantCommentDeleteServlet`** (`POST /tenant/comment-delete`):
  - Nhận `commentId` và `postId`.
  - Kiểm tra điều kiện bảo mật chính chủ: `comment.user_id == session.user_id`.
  - Nếu không đúng chính chủ: Forward trang lỗi `403 Forbidden` (*"Bạn không có quyền xóa bình luận này"*).
  - Nếu đúng chính chủ: Xóa bản ghi trong `post_comments` và Redirect về `news-feed#post-{postId}`.

## 4. Frontend & Views (JSP & Servlet Flow)
- **View Template**: `/WEB-INF/views/tenant/news-feed.jsp`
- **UI Components**:
  - **News Feed Card List**: Hiển thị danh sách các bài viết `APPROVED` trong 24 giờ qua. Render thông tin tác giả, tiêu đề, hình ảnh (modal phóng to), lượt thích và danh sách bình luận.
  - **Top Highlight Posts Widget**: Widget hiển thị Top 5 bài viết nổi bật bên cột sidebar.
  - **Like Action Form**: Form submit nút Thích/Bỏ thích gọi `POST /tenant/post-reaction`.
  - **Comment Action Form**: Form gửi bình luận trực tiếp `POST /tenant/post-comment`.
  - **Delete Comment Button**: Chỉ hiển thị nút "Xóa" tại các bình luận do chính Tenant đăng nhập sở hữu (`comment.userId == currentUser.id`), gọi `POST /tenant/comment-delete`.
- **Error Handling & Protection**:
  - Giao diện "Empty State" khi không có bài viết nào được duyệt trong 24 giờ qua.
  - Chuyển hướng về `/login` nếu chưa có Session đăng nhập.
  - Forward tới trang 403 Forbidden nếu gửi request gỡ bình luận của người dùng khác.
