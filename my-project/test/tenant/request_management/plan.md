# Implementation Plan: Tenant Request Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `TenantRequestServlet.java` (Quản lý luồng List, Detail, Create)
- **Dependencies**: `RequestService` (mocked)
- **Constraint**: Bảo vệ phân quyền truy cập chéo (IDOR) và kiểm tra chặt chẽ File Upload (Extension, Max Size).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantRequestServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewRequestsList_Success`: Xem danh sách yêu cầu cá nhân (Sắp xếp thời gian giảm dần).
- `testDoGet_ViewRequestDetail_Success`: Xem chi tiết một yêu cầu hợp lệ do chính Tenant tạo.
- `testDoPost_CreateRequest_Success`: Gửi biểu mẫu tạo yêu cầu hợp lệ -> Trạng thái PENDING.

### 3.2 Error Cases
- `testDoPost_CreateRequest_MissingTitleOrContent`: Thiếu Title/Content -> 400 Bad Request.
- `testDoPost_CreateRequest_InvalidCategory`: Thể loại yêu cầu không tồn tại hoặc sai -> 400 Bad Request.
- `testDoPost_CreateRequest_InvalidAttachmentType`: Đính kèm file sai định dạng (VD: .exe) -> 400 Bad Request.
- `testDoGet_ViewRequestDetail_CrossTenant_NotFound`: Xem IDOR request của người khác -> 404 Not Found.
- `testDoGet_UnauthorizedAccess`: Chưa đăng nhập -> Redirect tới `/login`.

### 3.3 Boundary Values
- `testDoPost_CreateRequest_MaxAttachmentSize`: Đính kèm file vượt quá 5MB -> Reject / Error.

### 3.4 Concurrent Scenarios
- Không có (Chỉ có thao tác cá nhân tạo Request).

## 4. Các bước thực hiện
1. Tái tạo cấu trúc Unit Test cơ bản, Mock `HttpServletRequest` bao gồm cả tính năng `getParts()` cho Upload.
2. Viết từng Test case như định nghĩa.
