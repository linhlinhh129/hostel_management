# Feature: Bảng tin cộng đồng

**Status:** Draft\
**Author:** Bùi Đỉnh\
**Reviewer:** \[Tên\]\
**Date:** 2026-07-08\
**Priority:** High

---

# 1. Business Context

Bảng tin cộng đồng là nơi hiển thị các bài viết đã được Ban quản lý phê duyệt nhằm truyền tải thông báo, tin tức và các hoạt động đến người dùng theo dạng mạng xã hội. Tính năng này giúp tăng khả năng tương tác giữa Ban quản lý và cư dân thông qua việc thích và bình luận bài viết.

Tính năng góp phần nâng cao hiệu quả truyền thông nội bộ và tăng mức độ tương tác trong hệ thống.

---

# 2. User Stories

### Story 1 - Xem bảng tin
**Là** Ban quản lý, **tôi muốn** xem các bài viết đã được duyệt trong 24 giờ qua **để** theo dõi nội dung đã được công bố.

### Story 2 - Xem top bài viết nổi bật
**Là** Ban quản lý, **tôi muốn** xem danh sách các bài viết có tương tác cao nhất **để** nắm bắt các thông tin đang được quan tâm.

### Story 3 - Phóng to ảnh đính kèm
**Là** Ban quản lý, **tôi muốn** phóng to ảnh đính kèm của bài viết **để** xem chi tiết nội dung hình ảnh.

### Story 4 - Thích hoặc bỏ thích bài viết
**Là** Ban quản lý, **tôi muốn** thích hoặc bỏ thích một bài viết **để** thể hiện sự quan tâm đối với bài viết đó.

### Story 5 - Bình luận bài viết
**Là** Ban quản lý, **tôi muốn** bình luận vào bài viết trực tiếp trên bảng tin **để** trao đổi thông tin.

### Story 6 - Xem danh sách bình luận
**Là** Ban quản lý, **tôi muốn** mở rộng danh sách bình luận của bài viết trực tiếp trên bảng tin **để** theo dõi thảo luận.

### Story 7 - Xóa bình luận
**Là** Ban quản lý, **tôi muốn** xóa bình luận **để** kiểm duyệt nội dung.
---

# 3. Acceptance Criteria (EARS)
### UC01 - Xem bảng tin
- **WHEN** Ban quản lý truy cập trang Bảng tin
- **THE SYSTEM SHALL** hiển thị tất cả bài viết có trạng thái **APPROVED**.
- **AND** chỉ hiển thị các bài viết được duyệt trong 24 giờ qua.
- **AND** sắp xếp bài viết theo thời gian tạo mới nhất.
- **AND** tải thêm bài viết khi nhấn "Tải thêm bài viết" (Pagination).

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
- **WHEN** Ban quản lý click vào hình ảnh của một bài viết trên bảng tin
- **THE SYSTEM SHALL** hiển thị popup (modal) phóng to hình ảnh đó.

### UC05 - Thích bài viết
- **WHEN** Ban quản lý chọn nút "Thích"
- **THE SYSTEM SHALL** lưu một bản ghi vào bảng `post_reactions` và cập nhật tổng số lượt thích của bài viết.

### UC06 - Bỏ thích bài viết
- **WHEN** Ban quản lý chọn "Bỏ thích" (bấm nút Thích lần nữa)
- **THE SYSTEM SHALL** xóa bản ghi tương ứng trong bảng `post_reactions` và cập nhật lại tổng số lượt thích.

### UC07 - Xem danh sách bình luận
- **WHEN** Ban quản lý nhấn nút "Bình luận" trên một bài viết
- **THE SYSTEM SHALL** xổ xuống (expand) danh sách bình luận ngay bên dưới bài viết.
- **AND** gọi API lấy tất cả bình luận của bài viết đó và cuộn xuống cuối danh sách.

### UC08 - Bình luận bài viết
- **WHEN** Ban quản lý nhập nội dung bình luận hợp lệ và nhấn Gửi
- **THE SYSTEM SHALL** tạo bản ghi mới trong bảng `post_comments`.
- **AND** nối bình luận mới nhất vào cuối danh sách hiện tại.

### UC09 - Dữ liệu bình luận không hợp lệ
- **WHEN** nội dung bình luận để trống
- **THE SYSTEM SHALL** từ chối gọi API và không lưu.

### UC10 - Xóa bình luận
- **WHEN** Ban quản lý nhấn nút "Xóa" tại một bình luận
- **THE SYSTEM SHALL** yêu cầu xác nhận.
- **AND** nếu xác nhận, xóa bình luận đó và tải lại danh sách bình luận.

### UC11 - Phân quyền
- **WHILE** người dùng chưa đăng nhập
- **THE SYSTEM SHALL** không cho phép tương tác (thích, bình luận, xóa).

# 4. Servlet & API Contract
Do đặc thù hiển thị động, tính năng Bảng tin (News Feed) sử dụng kết hợp giữa Servlet render giao diện (JSP) và Servlet cung cấp API (JSON) cho các tác vụ AJAX.

## 4.1 Servlet Entry Point (Giao diện)

| Thuộc tính | Giá trị |
|---|---|
| **Servlet** | `ManagerNewsFeedServlet` |
| **URL Pattern** | `GET /manager/news-feed` |
| **JSP View** | `/WEB-INF/views/manager/newsFeedManagement/news-feed.jsp` |
| **Phân quyền** | Dành cho Manager (Kiểm tra qua Filter bảo vệ thư mục `/manager`) |

**Lưu ý:** `ManagerNewsFeedServlet` chỉ làm nhiệm vụ forward tới giao diện JSP. Dữ liệu bài viết, tương tác (Like, Comment) được gọi thông qua AJAX tới `NewsFeedApiServlet`.

---

## 4.2 AJAX API Endpoints (`NewsFeedApiServlet`)

| Endpoint | Method | Chức năng | Tham số / Payload | Trả về (JSON `data`) |
|---|---|---|---|---|
| `/api/v1/news-feed` | `GET` | Lấy danh sách bảng tin | `offset`, `limit` | `List<CommunityPostDTO>` |
| `/api/v1/news-feed/top` | `GET` | Lấy top bài viết tương tác | `limit` (mặc định 5) | `List<CommunityPostDTO>` |
| `/api/v1/posts/{postId}/comments` | `GET` | Danh sách bình luận của bài | - | `List<CommentDTO>` |
| `/api/v1/posts/{postId}/reactions` | `POST` | Toggle Thích / Bỏ thích | - | `{"isLiked": boolean}` |
| `/api/v1/posts/{postId}/comments` | `POST` | Gửi bình luận mới | JSON: `{"content": "..."}` | Chi tiết `CommentDTO` vừa tạo |
| `/api/v1/comments/{commentId}` | `DELETE` | Xóa bình luận | - | `{"success": true}` |

---

## 4.3 Xử lý lỗi (API Behavior)

Tất cả các response từ `NewsFeedApiServlet` đều tuân thủ cấu trúc JSON chuẩn:
```json
{
  "success": false,
  "error": "Thông báo lỗi"
}
```

| Tình huống | Mã HTTP | Hành vi |
|---|---|---|
| Chưa đăng nhập (`currentUser` == null) | 401 | Trả về thông báo `"Chưa đăng nhập"` |
| Tham số không hợp lệ / Thiếu dữ liệu | 400 | Trả về message từ `ValidationException` |
| Không tìm thấy Endpoint | 404 | Trả về thông báo `"Not found"` |
| Lỗi xử lý hệ thống (Exception) | 500 | Trả về thông báo `"Lỗi server"` |

---

# 5. Technical Constraints

- Chỉ hiển thị bài viết có trạng thái **APPROVED**.
- Chỉ hiển thị bài viết được duyệt trong 24 giờ qua.
- Mỗi người dùng chỉ được thích một lần trên mỗi bài viết.
- Một người dùng có thể bình luận nhiều lần trên cùng một bài viết.
- Nội dung bình luận tối đa **1000 ký tự**.
- Hình ảnh bài viết được lấy từ trường `image_url`.
- Thời gian phản hồi API không vượt quá **500 ms (P95)**.
- Rate limit: **100 requests/phút/người dùng**.

---

# 6. Out of Scope

- Chỉnh sửa bình luận.
- Chia sẻ bài viết.
- Gắn thẻ người dùng.
- Đăng bài trực tiếp từ Bảng tin.
- Thông báo thời gian thực khi có lượt thích hoặc bình luận mới.
- Tìm kiếm và lọc bài viết.