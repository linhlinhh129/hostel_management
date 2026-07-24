# Implementation Plan: News Feed Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `NewsFeedApiServlet.java` (JSON API)
- **Dependencies**: `NewsFeedService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Trả về JSON đúng cấu trúc. Chặn các request quá giới hạn (Rate Limit).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/NewsFeedApiServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testApiGetNewsFeed_Success`: API trả về bài viết hợp lệ trong 24h.
- `testApiGetTopPosts_Success`: API trả về Top 5 bài viết tương tác.
- `testApiToggleLike_Success`: Bật / Tắt trạng thái Like.
- `testApiAddComment_Success`: Gửi bình luận thành công.
- `testApiDeleteComment_Success`: Xóa bình luận thành công.

### 3.2 Error Cases
- `testApiAddComment_EmptyContent`: Từ chối lưu bình luận trống.
- `testApi_Unauthenticated`: 401 Unauthorized nếu currentUser null.
- `testApi_InvalidPostId`: 404 nếu postId không tồn tại.

### 3.3 Boundary Values
- `testApiGetNewsFeed_24HourBoundary`: Test hiển thị bài viết được tạo đúng 23h59m59s trước.
- `testApiAddComment_MaxLength`: Test bình luận đúng 1000 ký tự (pass), 1001 (fail).

### 3.4 Concurrent Scenarios
- `testConcurrency_RateLimit`: Test hệ thống chặn ở 101 requests/phút.
- `testConcurrency_LikeCounter_RaceCondition`: Giả lập 50 lượt Like cùng lúc bằng ExecutorService.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `NewsFeedApiServlet`.
2. Kiểm chứng payload JSON thay vì attribute/view.
