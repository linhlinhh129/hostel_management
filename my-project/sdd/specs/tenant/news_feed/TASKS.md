# TASKS: Bảng tin cộng đồng (Tenant News Feed)

Dựa trên yêu cầu trong `SPEC.md` và `PLAN.md`, dưới đây là danh sách các công việc (tasks) cần thực hiện để hoàn thành tính năng **Bảng tin dành cho Khách thuê (Tenant)**:

## 1. Database & DAO Layer
- [ ] Bổ sung bảng `post_reactions` và `post_comments` trong CSDL nếu chưa có.
- [ ] Viết các phương thức truy vấn trong `CommunityPostDAO` (hoặc `PostDAO`):
  - [ ] `getApprovedPostsIn24h()`: Lấy danh sách bài viết trạng thái `APPROVED` được duyệt trong 24 giờ qua.
  - [ ] `getTopHighInteractionPosts(int limit)`: Lấy top 5 bài viết có lượt tương tác (Like + Comment) cao nhất.
  - [ ] `getPostReactionsCount(int postId)` & `hasUserLikedPost(int postId, int userId)`: Kiểm tra lượt thích và trạng thái thả tim của user.
- [ ] Viết các phương thức xử lý bình luận trong `PostCommentDAO`:
  - [ ] `getCommentsByPostId(int postId)`: Lấy toàn bộ bình luận của bài viết.
  - [ ] `addComment(PostComment comment)`: Thêm bình luận mới vào CSDL.
  - [ ] `deleteComment(int commentId, int userId)`: Xóa bình luận với điều kiện chính chủ `comment_id = ? AND user_id = ?`.

## 2. Controller (Java Web Servlets)
- [ ] Tạo `TenantNewsFeedServlet` mapped với `@WebServlet("/tenant/news-feed")` (GET):
  - [ ] Kiểm tra Session đăng nhập & role `TENANT`.
  - [ ] Lấy danh sách bài viết `APPROVED` trong 24h qua và Top 5 bài viết nổi bật.
  - [ ] Gán `request.setAttribute("postList", ...)` và `request.setAttribute("topPostList", ...)`.
  - [ ] Forward sang giao diện `/WEB-INF/views/tenant/news-feed.jsp`.
- [ ] Tạo `TenantPostReactionServlet` mapped với `@WebServlet("/tenant/post-reaction")` (POST):
  - [ ] Nhận `postId` từ form parameters.
  - [ ] Thực hiện Toggle Like/Unlike cho `user_id` hiện tại.
  - [ ] Redirect về `/tenant/news-feed#post-{postId}`.
- [ ] Tạo `TenantPostCommentServlet` mapped với `@WebServlet("/tenant/post-comment")` (POST):
  - [ ] Nhận `postId` và `content` từ form parameters.
  - [ ] Validate `content` không rỗng và tối đa 1000 ký tự.
  - [ ] Thêm bình luận mới thuộc về `user_id` hiện tại và Redirect về `/tenant/news-feed#post-{postId}`.
- [ ] Tạo `TenantCommentDeleteServlet` mapped với `@WebServlet("/tenant/comment-delete")` (POST):
  - [ ] Nhận `commentId` và `postId` từ form parameters.
  - [ ] Kiểm tra điều kiện bảo mật chính chủ (`comment.user_id == session.user_id`).
  - [ ] Nếu không phải chính chủ: Forward tới trang lỗi 403 Forbidden (*"Bạn không có quyền xóa bình luận này"*).
  - [ ] Nếu đúng chính chủ: Xóa bản ghi trong `post_comments` và Redirect về `/tenant/news-feed#post-{postId}`.

## 3. View (JSP & UI)
- [ ] Tạo file `/WEB-INF/views/tenant/news-feed.jsp`:
  - [ ] Hiển thị danh sách các bài viết `APPROVED` dạng danh thiếp / card bài viết.
  - [ ] Hiển thị widget Top 5 bài viết nổi bật ở cột bên phải.
  - [ ] Tích hợp Modal phóng to hình ảnh khi click vào ảnh đính kèm bài viết.
  - [ ] Form submit nút Thích/Bỏ thích gọi `POST /tenant/post-reaction`.
  - [ ] Form gửi bình luận trực tiếp gọi `POST /tenant/post-comment`.
  - [ ] **Phân quyền nút xóa:** Chỉ hiển thị nút "Xóa" tại bình luận do chính Tenant đang đăng nhập tạo ra (`comment.userId == currentUser.id`).
  - [ ] Hiển thị Empty State khi không có bài viết nào được duyệt trong 24h qua.

## 4. Kiểm thử & Bảo mật (Testing & Validation)
- [ ] Test hiển thị bài viết `APPROVED` trong 24h qua và Top 5 bài nổi bật.
- [ ] Test Toggle Like/Unlike bài viết.
- [ ] Test gửi bình luận mới (validate nội dung rỗng và quá 1000 ký tự).
- [ ] Test xóa bình luận của chính mình thành công.
- [ ] Test bảo mật (Security): Cố tình gửi `POST /tenant/comment-delete` với `commentId` của Tenant khác xem có bị hệ thống chặn lỗi 403 Forbidden hay không.
