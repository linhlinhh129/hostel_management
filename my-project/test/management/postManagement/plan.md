# Implementation Plan: Post Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `CommunityPostServlet.java`
- **Dependencies**: `PostService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào xử lý file upload (kích thước file, định dạng), kiểm duyệt bài viết và rate limiting.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/CommunityPostServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewPostList_Success`: Xem danh sách bài viết.
- `testDoGet_ViewPostDetail_Success`: Xem chi tiết bài viết.
- `testDoPost_CreatePost_Success`: Tạo bài viết mới thành công (PENDING).
- `testDoPost_ApprovePost_Success`: Duyệt bài viết qua AJAX.
- `testDoPost_DeletePost_Success`: Xóa bài viết (Soft Delete) qua AJAX.

### 3.2 Error Cases
- `testDoPost_CreatePost_EmptyTitleOrContent`: Trả lỗi validation khi tạo.
- `testDoGet_ViewPostDetail_NotFound`: Trả lỗi khi xem bài viết không tồn tại.
- `testDoPost_UnauthorizedAccess`: 403 khi tài khoản TENANT thao tác.

### 3.3 Boundary Values
- `testDoPost_CreatePost_TitleMaxLength`: Test tạo bài có tiêu đề chuẩn 250 ký tự (pass), 251 ký tự (fail).
- `testDoPost_CreatePost_ImageMaxSize`: Test upload ảnh 5MB (pass), 5.01MB (fail).

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleApprove_RaceCondition`: Giả lập 2 Manager duyệt cùng bài viết một lúc.
- `testConcurrency_RateLimit`: Test 101 requests / phút.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `CommunityPostServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
