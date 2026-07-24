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

## 3. Backend (API Services)
- **GET /api/v1/news-feed**:
  - Truy vấn lấy danh sách các bài viết có trạng thái `status = 'APPROVED'`.
  - **Lọc dữ liệu**: Chỉ lấy bài viết được phê duyệt/đăng trong **ngày hiện tại** (như yêu cầu của AC-01).
  - Sort theo thời gian phê duyệt mới nhất (DESC).
  - Tính toán và trả về thêm `likeCount` và `commentCount`.
- **GET /api/v1/news-feed/{id}**:
  - Trả về chi tiết bài viết kèm mảng hình ảnh. Nếu bài viết không tồn tại hoặc chưa duyệt, trả về `HTTP 404`.
- **POST /api/v1/posts/{id}/like**:
  - API xử lý toggle Like.
  - Kiểm tra xem người dùng (`tenant_id`) đã có trong bảng `post_likes` của bài viết này chưa.
  - Nếu đã like: thực hiện xóa bản ghi (bỏ thích).
  - Nếu chưa like: thêm mới bản ghi. Trả về tổng số like mới (`likeCount`).
- **POST /api/v1/posts/{id}/comments**:
  - Validate nội dung bình luận (không rỗng, <= 1000 ký tự). Trả về `HTTP 400` kèm mã lỗi `INVALID_COMMENT` nếu sai.
  - Thêm record vào bảng `post_comments`. Trả về thông tin chi tiết bình luận vừa tạo (`HTTP 201`).
- **GET /api/v1/posts/{id}/comments**:
  - Truy xuất toàn bộ bình luận của bài viết.
  - Sắp xếp thời gian tạo tăng dần (cũ nhất ở trên, mới nhất ở dưới).

## 4. Frontend (Tenant Web/App)
- **UI Components**:
  - **News Feed Screen**: Giao diện chính liệt kê các thẻ (Cards) bài viết. Hiển thị thumbnail, title, summary, thời gian và số liệu tương tác (Like, Comment).
  - **Post Detail Screen**: Hiển thị đầy đủ text, hình ảnh bài viết và khối thông tin tác giả. 
  - **Like Button**: Nút trạng thái toggle, cập nhật UI tức thì khi nhấn (Optimistic UI) trước khi/trong lúc đợi API trả về.
  - **Comment Section**: Khung hiển thị danh sách các bình luận + Form nhập nội dung bình luận phía dưới (kèm logic đếm ký tự).
- **Error Handling / Integration**:
  - Giao diện "Empty State" khi không có bài viết nào trong ngày hôm nay.
  - Xử lý mã lỗi `HTTP 404` hiển thị màn hình báo lỗi bài viết không tồn tại.
  - Client-side validation cho comment text box (hiệu ứng khóa nút Submit nếu độ dài sai).
