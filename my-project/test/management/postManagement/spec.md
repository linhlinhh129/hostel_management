# Test Specification: Quản lý bài viết cộng đồng (Post Management)

**File bị ảnh hưởng**: `CommunityPostServletTest.java` (Quản lý view và tạo), API endpoint (nếu tách rời)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào luồng kiểm duyệt bài viết và validate dữ liệu.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewPostList_Success`: KHI truy cập danh sách, THE SYSTEM SHALL hiển thị tất cả bài viết kèm theo phân trang.
- `testDoGet_ViewPostDetail_Success`: KHI truy cập chi tiết, THE SYSTEM SHALL hiển thị đầy đủ thông tin bài viết.
- `testDoPost_CreatePost_Success`: KHI submit tạo bài viết hợp lệ, THE SYSTEM SHALL tạo bài viết với trạng thái `PENDING` và redirect về danh sách.
- `testDoPost_ApprovePost_Success`: KHI gọi AJAX duyệt bài viết `PENDING`, THE SYSTEM SHALL chuyển trạng thái thành `APPROVED` và trả về JSON success.
- `testDoPost_DeletePost_Success`: KHI gọi AJAX xóa bài viết, THE SYSTEM SHALL Soft Delete (`deleted_at`) bài viết và trả về JSON success.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_CreatePost_EmptyTitleOrContent`: KHI tạo bài viết với tiêu đề hoặc nội dung để trống, THE SYSTEM SHALL báo lỗi validation và forward lại form (hoặc trả 400).
- `testDoGet_ViewPostDetail_NotFound`: KHI xem chi tiết bài viết không tồn tại, THE SYSTEM SHALL redirect về trang danh sách kèm query `?error=notfound`.
- `testDoPost_UnauthorizedAccess`: KHI người dùng không phải Ban quản lý thao tác, THE SYSTEM SHALL trả về HTTP 403.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_CreatePost_TitleMaxLength`: KHI tiêu đề bài viết dài đúng 250 ký tự, THE SYSTEM SHALL lưu thành công. Tiêu đề 251 ký tự THE SYSTEM SHALL từ chối.
- `testDoPost_CreatePost_ImageMaxSize`: KHI upload ảnh có kích thước đúng 5MB, THE SYSTEM SHALL chấp nhận. KHI ảnh 5.01MB, THE SYSTEM SHALL từ chối.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_DoubleApprove_RaceCondition`: Giả lập 2 Manager duyệt cùng một bài viết PENDING cùng lúc. THE SYSTEM SHALL đảm bảo chỉ một lượt duyệt thành công, lượt kia bị chặn.
- `testConcurrency_RateLimit`: Đẩy 101 requests / phút vào endpoint tạo/duyệt. THE SYSTEM SHALL bắt đầu chặn từ request 101.
