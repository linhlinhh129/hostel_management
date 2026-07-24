# Implementation Plan: Tenant Post Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Targets**: `TenantMyPostsServlet.java`, `TenantCreatePostServlet.java`, `TenantDeletePostServlet.java`, `TenantPostDetailServlet.java` (Tùy theo cấu trúc source code hiện tại)
- **Dependencies**: `PostService` (mocked)
- **Constraint**: Chỉ xóa được bài PENDING của chính mình (IDOR protection).

## 2. Các file cần tạo/chỉnh sửa
- Cần kiểm tra tên Servlet chính xác và tạo Test Class tương ứng (VD: `TenantCreatePostServletTest.java`, `TenantDeletePostServletTest.java`).

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewMyPosts_Success`: Xem danh sách bài viết cá nhân.
- `testDoGet_ViewPostDetail_Success`: Xem chi tiết bài viết.
- `testDoPost_CreatePost_Success`: Đăng bài mới (Trạng thái PENDING).
- `testDoPost_DeletePost_Success`: Xóa bài viết của mình (PENDING).

### 3.2 Error Cases
- `testDoPost_CreatePost_InvalidData`: Đăng bài nhưng bỏ trống nội dung -> Error (400 hoặc Forward).
- `testDoPost_DeletePost_CrossTenant_Forbidden`: Xóa bài của người khác -> 403.
- `testDoPost_DeletePost_Approved_Forbidden`: Xóa bài của mình đã APPROVED -> 403.
- `testDoGet_UnauthorizedAccess`: Truy cập chưa đăng nhập -> Redirect.

### 3.3 Boundary Values
- `testDoPost_CreatePost_MaxImages`: Gửi quá nhiều hình ảnh -> Reject.

### 3.4 Concurrent Scenarios
- `testConcurrency_Delete_And_Approve`: Test xung đột Delete và Approve.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho các Servlets liên quan đến Post.
2. Implement các Test case.
