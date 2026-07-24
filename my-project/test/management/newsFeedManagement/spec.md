# Test Specification: Bảng tin cộng đồng (News Feed Management)

**File bị ảnh hưởng**: `NewsFeedApiServletTest.java` (AJAX APIs)
**Nguyên tắc**: Test API JSON Responses. Mô phỏng (Mock) các Service xử lý Like, Comment, Load Bài Viết.

## 1. Happy Path (Các kịch bản thành công)

- `testApiGetNewsFeed_Success`: KHI gọi GET danh sách, THE SYSTEM SHALL trả về danh sách bài viết `APPROVED`, đã tạo trong 24h, sắp xếp mới nhất.
- `testApiGetTopPosts_Success`: KHI gọi GET top, THE SYSTEM SHALL trả về 5 bài viết có lượng tương tác cao nhất.
- `testApiToggleLike_Success`: KHI POST like lần 1, THE SYSTEM SHALL trả về `isLiked: true`. KHI POST like lần 2, THE SYSTEM SHALL trả về `isLiked: false`.
- `testApiAddComment_Success`: KHI POST gửi bình luận, THE SYSTEM SHALL trả về DTO bình luận mới được tạo (kèm Avatar, Tên).
- `testApiDeleteComment_Success`: KHI DELETE bình luận, THE SYSTEM SHALL trả về JSON `success: true`.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testApiAddComment_EmptyContent`: KHI nội dung bình luận để trống, THE SYSTEM SHALL trả về HTTP 400 và JSON báo lỗi validation.
- `testApi_Unauthenticated`: KHI gọi bất kỳ API bảng tin nào mà Session `currentUser == null`, THE SYSTEM SHALL trả về HTTP 401 kèm JSON thông báo "Chưa đăng nhập".
- `testApi_InvalidPostId`: KHI gửi bình luận vào Post không tồn tại, THE SYSTEM SHALL trả về HTTP 404 hoặc lỗi báo Post không tồn tại.

## 3. Boundary Values (Giá trị biên)

- `testApiGetNewsFeed_24HourBoundary`: Giả lập bài viết tạo lúc `Now - 23h59m59s` (ĐƯỢC hiển thị) và bài viết tạo lúc `Now - 24h00m01s` (BỊ LỌC BỎ).
- `testApiAddComment_MaxLength`: KHI nội dung bình luận đúng 1000 ký tự (Biên an toàn), THE SYSTEM SHALL lưu thành công. KHI nội dung bình luận 1001 ký tự, THE SYSTEM SHALL từ chối.
- `testApiGetTopPosts_LimitBoundary`: Đảm bảo API Top chỉ trả về đúng 5 phần tử kể cả khi Database có 100 bài viết tương tác cao.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_RateLimit`: Ép tải 101 requests/phút từ 1 user. THE SYSTEM SHALL bắt đầu chặn từ Request 101 và trả về lỗi Rate Limit (HTTP 429 nếu có, hoặc lỗi tương tự).
- `testConcurrency_LikeCounter_RaceCondition`: Giả lập 50 users click LIKE cùng lúc vào 1 bài viết. THE SYSTEM SHALL cộng dồn chính xác số Like = 50 mà không bị mất dữ liệu (Lost Update).
