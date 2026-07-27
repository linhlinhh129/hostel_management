# Feature: Bảng tin cộng đồng (Tenant News Feed)

**Status:** Draft  
**Author:** AI Agent  
**Reviewer:** [Tên]  
**Date:** 2026-07-25  
**Priority:** High  

---

# 1. Business Context

Bảng tin cộng đồng cho Khách thuê (Tenant) là nơi hiển thị các bài viết đã được Ban quản lý phê duyệt nhằm truyền tải thông báo, tin tức và các hoạt động đến cư dân theo dạng mạng xã hội. 

Tính năng này cho phép Tenant cập nhật tin tức tòa nhà, tương tác với các bài viết (thích, xem danh sách bình luận, bình luận) và quản lý (xóa) các bình luận của chính mình.

---

# 2. User Stories

### Story 1 - Xem bảng tin
**Là** Người thuê (Tenant), **tôi muốn** xem các bài viết đã được duyệt trong 24 giờ qua **để** cập nhật các thông báo và tin tức mới nhất từ tòa nhà.

### Story 2 - Xem top bài viết nổi bật
**Là** Người thuê (Tenant), **tôi muốn** xem danh sách các bài viết có tương tác cao nhất **để** nắm bắt những thông tin đang được cư dân quan tâm.

### Story 3 - Phóng to ảnh đính kèm
**Là** Người thuê (Tenant), **tôi muốn** phóng to ảnh đính kèm của bài viết **để** xem rõ nội dung hình ảnh.

### Story 4 - Thích hoặc bỏ thích bài viết
**Là** Người thuê (Tenant), **tôi muốn** thích hoặc bỏ thích một bài viết **để** thể hiện sự quan tâm đối với bài viết đó.

### Story 5 - Bình luận bài viết
**Là** Người thuê (Tenant), **tôi muốn** bình luận vào bài viết trực tiếp trên bảng tin **để** trao đổi ý kiến với cư dân và Ban quản lý.

### Story 6 - Xem danh sách bình luận
**Là** Người thuê (Tenant), **tôi muốn** mở rộng danh sách bình luận của bài viết **để** theo dõi các thảo luận xung quanh bài viết.

### Story 7 - Xóa bình luận cá nhân
**Là** Người thuê (Tenant), **tôi muốn** xóa bình luận do chính mình tạo ra **để** gỡ bỏ những phản hồi không còn phù hợp (chỉ được xóa bình luận của chính mình, không được xóa bình luận của người khác).

---

# 3. Acceptance Criteria (EARS)

### UC01 - Xem bảng tin
- **WHEN** Tenant truy cập trang Bảng tin
- **THE SYSTEM SHALL** hiển thị tất cả bài viết có trạng thái **APPROVED**.
- **AND** chỉ hiển thị các bài viết được duyệt trong 24 giờ qua.
- **AND** sắp xếp bài viết theo thời gian tạo mới nhất.
- **AND** hỗ trợ tải thêm bài viết khi nhấn "Tải thêm bài viết" (Pagination).

### UC02 - Hiển thị thông tin bài viết
- **WHEN** bài viết được hiển thị trên bảng tin
- **THE SYSTEM SHALL** hiển thị đầy đủ:
  - Hình ảnh (nếu có)
  - Tiêu đề
  - Nội dung (hiển thị toàn bộ, không thu gọn)
  - Người đăng (tên và avatar)
  - Thời gian đăng
  - Số lượt thích
  - Số lượng bình luận

### UC03 - Xem top bài viết nổi bật
- **WHEN** giao diện bảng tin tải xong
- **THE SYSTEM SHALL** hiển thị widget "Top Bài Nổi Bật" ở cột bên phải.
- **AND** gọi API lấy top 5 bài viết có tương tác cao nhất.
- **AND** khi click vào một top post, chuyển hướng sang trang chi tiết bài viết.

### UC04 - Phóng to ảnh
- **WHEN** Tenant click vào hình ảnh của một bài viết trên bảng tin
- **THE SYSTEM SHALL** hiển thị popup (modal) phóng to hình ảnh đó.

### UC05 - Thích bài viết
- **WHEN** Tenant chọn nút "Thích"
- **THE SYSTEM SHALL** lưu một bản ghi vào bảng `post_reactions` và cập nhật tổng số lượt thích của bài viết.

### UC06 - Bỏ thích bài viết
- **WHEN** Tenant chọn "Bỏ thích" (bấm nút Thích lần nữa)
- **THE SYSTEM SHALL** xóa bản ghi tương ứng trong bảng `post_reactions` và cập nhật lại tổng số lượt thích.

### UC07 - Xem danh sách bình luận
- **WHEN** Tenant nhấn nút "Bình luận" trên một bài viết
- **THE SYSTEM SHALL** xổ xuống (expand) danh sách bình luận ngay bên dưới bài viết.
- **AND** gọi API lấy tất cả bình luận của bài viết đó và cuộn xuống cuối danh sách.

### UC08 - Bình luận bài viết
- **WHEN** Tenant nhập nội dung bình luận hợp lệ và nhấn Gửi
- **THE SYSTEM SHALL** tạo bản ghi mới trong bảng `post_comments` thuộc về `user_id` hiện tại.
- **AND** nối bình luận mới nhất vào cuối danh sách hiện tại.

### UC09 - Dữ liệu bình luận không hợp lệ
- **WHEN** nội dung bình luận để trống
- **THE SYSTEM SHALL** từ chối gửi request và không lưu.

### UC10 - Xóa bình luận cá nhân
- **WHEN** Tenant nhấn nút "Xóa" tại một bình luận do chính mình tạo ra
- **THE SYSTEM SHALL** hiển thị hộp thoại xác nhận.
- **AND** khi Tenant xác nhận, hệ thống kiểm tra `user_id` của bình luận khớp với `user_id` của Tenant đang đăng nhập, thực hiện xóa bình luận và tải lại danh sách bình luận.

### UC11 - Chặn xóa bình luận của người khác
- **WHEN** bình luận không phải do Tenant đang đăng nhập tạo ra
- **THE SYSTEM SHALL** không hiển thị nút "Xóa" trên giao diện.
- **AND IF** có yêu cầu xóa trực tiếp gửi tới API, hệ thống từ chối và trả về HTTP 403 Forbidden ("Bạn không có quyền xóa bình luận của người khác").

### UC12 - Phân quyền truy cập
- **WHILE** người dùng chưa đăng nhập hoặc không có vai trò Tenant
- **THE SYSTEM SHALL** từ chối truy cập và chuyển hướng về trang Login.

---

# 4. Servlet Routes & Page Controller Contract

## 4.1 Màn hình Bảng tin cộng đồng

### Servlet Mapping
```http
GET /tenant/news-feed
```
- **Servlet:** `TenantNewsFeedServlet`
- **Mô tả:** Tiếp nhận request, lấy danh sách bài viết đã được duyệt (`APPROVED`) trong 24 giờ qua và top 5 bài viết nổi bật.
- **Scope & Attribute Name:** 
  - `request.setAttribute("postList", List<CommunityPostDTO>)`
  - `request.setAttribute("topPostList", List<CommunityPostDTO>)`
- **Forward View:** `/WEB-INF/views/tenant/news-feed.jsp`

---

## 4.2 Thích / Bỏ thích bài viết (Action Servlet)

### Servlet Mapping
```http
POST /tenant/post-reaction
```
- **Servlet:** `TenantPostReactionServlet`
- **Form Parameters:** `postId`
- **Xử lý:** Lấy `user_id` từ session. Đảo ngược trạng thái (Toggle Like/Unlike) trong CSDL (`post_reactions`). Thực hiện `response.sendRedirect(request.getContextPath() + "/tenant/news-feed#post-" + postId)`.

---

## 4.3 Thêm bình luận vào bài viết (Action Servlet)

### Servlet Mapping
```http
POST /tenant/post-comment
```
- **Servlet:** `TenantPostCommentServlet`
- **Form Parameters:** `postId`, `content`
- **Xử lý:** Validate `content` không rỗng và tối đa 1000 ký tự. Lưu bình luận vào CSDL thuộc `user_id` hiện tại. Thực hiện `response.sendRedirect(request.getContextPath() + "/tenant/news-feed#post-" + postId)`.

---

## 4.4 Xóa bình luận cá nhân (Action Servlet)

### Servlet Mapping
```http
POST /tenant/comment-delete
```
- **Servlet:** `TenantCommentDeleteServlet`
- **Form Parameters:** `commentId`, `postId`
- **Xử lý:** Lấy `user_id` hiện tại từ session. Kiểm tra xem bình luận `commentId` có thuộc sở hữu của `user_id` này hay không.
  - Nếu không phải chủ sở hữu: Forward trang lỗi 403 (Forbidden - *"Bạn không có quyền xóa bình luận này"*).
  - Nếu hợp lệ: Xóa bản ghi trong `post_comments` và `response.sendRedirect(request.getContextPath() + "/tenant/news-feed#post-" + postId)`.

---

# 4.5 Error Handling & Redirection

| Error Code | Status / Action | Description |
| --- | --- | --- |
| UNAUTHORIZED | Redirect `/login` | Chưa đăng nhập (Session không tồn tại) |
| FORBIDDEN | Forward 403 Page | Cố tình gửi request xóa bình luận của người dùng khác |
| NOT_FOUND | Forward 404 Page | Bài viết hoặc bình luận không tồn tại |
| INVALID_INPUT | Redirect `/tenant/news-feed` | Nội dung bình luận để trống hoặc quá 1000 ký tự |

---

# 5. Technical Constraints

- Chỉ hiển thị bài viết có trạng thái **APPROVED**.
- Chỉ hiển thị bài viết được duyệt trong 24 giờ qua.
- Mỗi người dùng chỉ được thích một lần trên mỗi bài viết.
- Một người dùng có thể bình luận nhiều lần trên cùng một bài viết.
- **Ràng buộc kiểm duyệt bình luận:** Khách thuê (Tenant) CHỈ có quyền xóa các bình luận do chính mình tạo ra (`comment.user_id == current_user.id`).
- Nội dung bình luận tối đa **1000 ký tự**.
- Hình ảnh bài viết được lấy từ trường `image_url`.
- Thời gian phản hồi API không vượt quá **500 ms (P95)**.
- Rate limit: **100 requests/phút/người dùng**.

---

# 6. Out of Scope

- Xóa bình luận của người khác (đó là quyền của Ban quản lý/Manager).
- Chỉnh sửa bình luận.
- Chia sẻ bài viết.
- Gắn thẻ người dùng.
- Đăng bài trực tiếp từ Bảng tin (Tenant sử dụng tính năng Đăng bài riêng trong Quản lý bài viết cá nhân).
- Thông báo thời gian thực khi có lượt thích hoặc bình luận mới.
- Tìm kiếm và lọc bài viết.
