# Test Specification: Quản lý Bài viết Cá nhân (Tenant Post Management)

**File bị ảnh hưởng**: `TenantMyPostsServletTest.java`, `TenantCreatePostServletTest.java`, `TenantDeletePostServletTest.java` (Tùy theo cấu trúc routing)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`PostService`). Tập trung vào quyền sở hữu bài viết (IDOR) và ràng buộc trạng thái.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewMyPosts_Success`: KHI Tenant xem danh sách bài viết cá nhân, THE SYSTEM SHALL chỉ trả về các bài viết do chính Tenant tạo.
- `testDoGet_ViewPostDetail_Success`: KHI xem chi tiết bài viết, THE SYSTEM SHALL trả về đầy đủ nội dung, hình ảnh và trạng thái bài.
- `testDoPost_CreatePost_Success`: KHI Tenant tạo bài viết với nội dung hợp lệ (Tiêu đề, nội dung không rỗng), THE SYSTEM SHALL lưu bài viết với trạng thái `PENDING` và trả về mã thành công (HTTP 201 hoặc Redirect).
- `testDoPost_DeletePost_Success`: KHI Tenant xóa bài viết (POST với action delete) của chính mình đang ở trạng thái PENDING, THE SYSTEM SHALL xóa thành công (HTTP 200 hoặc Redirect).

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_CreatePost_InvalidData`: KHI Tenant tạo bài viết mà bỏ trống tiêu đề hoặc nội dung, THE SYSTEM SHALL trả về lỗi HTTP 400 hoặc Forward báo lỗi "Dữ liệu không hợp lệ".
- `testDoPost_DeletePost_CrossTenant_Forbidden`: KHI Tenant cố xóa bài viết của người khác (IDOR), THE SYSTEM SHALL trả về HTTP 403 "Bạn không có quyền xóa bài viết này".
- `testDoPost_DeletePost_ApprovedPost_Forbidden`: KHI Tenant xóa bài viết của mình nhưng bài đó đã được duyệt (APPROVED), THE SYSTEM SHALL từ chối xóa và trả về HTTP 403.
- `testDoGet_UnauthorizedAccess`: KHI truy cập khi chưa đăng nhập, THE SYSTEM SHALL redirect về trang Login (HTTP 401).

## 3. Boundary Values (Giá trị biên)

- `testDoPost_CreatePost_MaxImages`: KHI Tenant gửi dữ liệu bài viết kèm theo hơn 10 hình ảnh, THE SYSTEM SHALL từ chối hoặc báo lỗi. (Nếu hệ thống có validate giới hạn 10 ảnh).

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_Delete_And_Approve`: KHI Tenant A bấm xóa bài viết CÙNG LÚC với việc Manager bấm duyệt (Approve) bài viết đó. THE SYSTEM SHALL chỉ cho phép 1 luồng thành công (Nếu Approve trước, Delete sẽ thất bại với mã 403; Nếu Delete trước, Approve sẽ trả 404).
