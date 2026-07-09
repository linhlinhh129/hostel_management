# TASKS: Interact with News Feed

## 1. Database (SQL)
- [ ] Bổ sung thiết kế bảng `post_likes` (kèm khóa ngoại `post_id`, `tenant_id` và UNIQUE constraint) vào `schema.sql`.
- [ ] Bổ sung thiết kế bảng `post_comments` (lưu nội dung bình luận) vào `schema.sql`.

## 2. Backend (APIs)
- [ ] **Infrastructure & Models**:
  - [ ] Khởi tạo entity `PostLike` và `PostComment`.
  - [ ] Khởi tạo các class DTO Response/Request (ví dụ: `CommentRequest`, `NewsFeedResponse`).
- [ ] **Services & Controllers**:
  - [ ] Implement logic API `GET /api/v1/news-feed`: Query các bài viết `APPROVED` trong ngày hiện tại, đếm số Like, số Comment.
  - [ ] Implement logic API `GET /api/v1/news-feed/{id}`: Trả về chi tiết nội dung, xử lý trường hợp 404 NotFound.
  - [ ] Implement logic API `POST /api/v1/posts/{id}/like`: Xử lý thêm/xóa Like (Toggle) để tăng giảm tương tác.
  - [ ] Implement logic API `POST /api/v1/posts/{id}/comments`: Xử lý thêm Comment mới vào Database.
  - [ ] Implement logic API `GET /api/v1/posts/{id}/comments`: Truy vấn danh sách Comment, Sort theo ngày tăng dần.
- [ ] **Validation & Exceptions**:
  - [ ] Cài đặt validation độ dài Comment tối đa 1000 ký tự, quăng lỗi `INVALID_COMMENT`.
  - [ ] Bắt lỗi HTTP 404 cho thao tác Like/Comment nếu bài viết bị vô hiệu hóa hoặc bị xóa.

## 3. Frontend (UI)
- [ ] **Components**:
  - [ ] Code giao diện danh sách News Feed (Cards) ở trang chủ Tenant.
  - [ ] Code màn hình Chi tiết bài viết (Post Detail) hiển thị toàn bộ nội dung.
  - [ ] Code component Like (nút bấm toggle, đổi icon filled/outlined, cập nhật số đếm).
  - [ ] Code giao diện danh sách Comment (Avatar, Tên người bình luận, thời gian, text).
  - [ ] Code Form nhập Comment (kèm số đếm ký tự 0/1000).
- [ ] **Integration & State**:
  - [ ] Tích hợp API `GET /api/v1/news-feed` vào lúc khởi chạy trang.
  - [ ] Tích hợp API `GET /api/v1/news-feed/{id}` và `GET /api/v1/posts/{id}/comments` ở màn chi tiết.
  - [ ] Gắn Action vào nút Like gọi `POST /api/v1/posts/{id}/like`.
  - [ ] Gắn Action vào nút Send Comment gọi `POST /api/v1/posts/{id}/comments`.
- [ ] **Error Handling / Empty States**:
  - [ ] Code giao diện "Empty" khi không có bài đăng trong ngày.
  - [ ] Handle 404: Hiển thị thông báo "Bài viết không tồn tại hoặc đã bị xóa."
  - [ ] Disable nút Gửi (Send) nếu độ dài bình luận = 0 hoặc > 1000 ký tự.
