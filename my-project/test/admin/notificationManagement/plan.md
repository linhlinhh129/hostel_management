# Implementation Plan: Notification Management Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminNotificationServlet.java`, `NotificationDAO`
- **Constraint**: Đảm bảo 100% Unit Test không kết nối DB thật. Các class phụ thuộc phải được Mock hoàn toàn để cô lập môi trường kiểm thử.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminNotificationServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_Create_Success`: Submit form hợp lệ (title, content, recipientType). Xác minh DAO insert được gọi và HTTP 201 hoặc Redirect.
- `testDoGet_List_Success`: Gọi danh sách. Xác minh trả về đúng danh sách phân trang được Mock.
- `testDoGet_Detail_Success`: Xem chi tiết ID hợp lệ. Xác minh trả về đầy đủ thông tin.
- `testDoGet_Search_ValidKeyword`: Gửi keyword tìm kiếm, xác minh Service/DAO nhận đúng tham số.

### 3.2 Error Cases
- `testDoPost_Create_EmptyFields`: Để trống Title/Content, xác minh lỗi Validation.
- `testDoPost_Create_InvalidRecipient`: Gửi recipient khác `ALL`, xác minh lỗi Validation.
- `testDoGet_Detail_NotFound`: Mock DAO ném `NotFoundException`, xác minh trả về 404.
- `testAuth_Unauthorized_Forbidden`: Kiểm thử thiếu Session ném 401, sai Role ném 403.

### 3.3 Boundary Values
- `testDoPost_Create_TitleLengthBoundary`: Tiêu đề đúng 255 ký tự (Pass), 256 ký tự (Fail).
- `testDoPost_Create_ContentLengthBoundary`: Nội dung 1000 ký tự (Pass), 1001 ký tự (Fail).
- `testDoGet_List_PaginationLimits`: Tham số page âm tự fallback về 0.

### 3.4 Concurrent Scenarios
- `testConcurrency_ServletState`: Dùng 20 Threads gọi liên tục doPost/doGet để xác minh các biến request/response không bị leak data (Thread-safety).

## 4. Các bước thực hiện
1. Setup test class và `@Mock`.
2. Implement Happy path.
3. Implement Error Cases & Boundary.
4. Implement Thread-safety test.
