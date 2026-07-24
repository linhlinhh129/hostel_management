# Implementation Plan: Operator Notifications Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorNotificationServlet.java`, `NotificationDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Mock DAO mô phỏng luồng kiểm tra quyền `facility_id` của Operator. Đảm bảo UI nhận đúng List thông báo.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorNotificationServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadNotifications_SystemWide`: Mock DAO trả về thông báo có `target_type = ALL`. Servlet phải gắn vào thuộc tính an toàn.
- `testDoGet_LoadNotifications_FacilitySpecific`: Mock DAO trả về thông báo có `target_type = FACILITY` và `facility_id` khớp với User đang đăng nhập.

### 3.2 Error Cases
- `testDoGet_UnauthorizedAccess`: User có Role `TENANT` truy cập. Bị chặn bởi HTTP 403.
- `testDoPost_MethodNotAllowed`: Gửi POST request để chặn việc ghi đè (vì màn hình này chỉ hiển thị danh sách). Trả về HTTP 405.
- `testDoGet_MissingFacilityInSession`: Session bị mất `facility_id`. Bắt exception và render lỗi 500 mềm (Friendly error page).

### 3.3 Boundary Values
- `testDoGet_NoNotifications`: Không có thông báo nào. Mảng rỗng. Giao diện hiển thị "Chưa có thông báo nào".
- `testDoGet_PaginationOutOfBounds`: Truyền tham số `page=99999`. Fallback về trang 1 hoặc mảng rỗng tùy logic, không được văng Exception.
- `testDoGet_ExtremelyLongContent`: Nội dung dài (10,000 ký tự). Test đảm bảo Servlet vẫn mapping DTO bình thường, không bị tràn bộ đệm.

### 3.4 Concurrent Scenarios
- `testConcurrency_SnapshotRead`: Giả lập nhiều Threads cùng GET danh sách khi Admin đang tiến hành Xóa mềm 1 thông báo. Kiểm tra Servlet stateless.

## 4. Các bước thực hiện
1. Thiết lập `OperatorNotificationServletTest` với Mockito (`@Mock NotificationDAO`).
2. Viết Test method tuân thủ format comment `# EARS [...]`.
