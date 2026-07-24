# Implementation Plan: Tenant Notification Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `TenantNotificationServlet.java`
- **Dependencies**: `NotificationService` (mocked)
- **Constraint**: Bảo vệ Read-only.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantNotificationServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewNotifications_Success`: Trả về danh sách thông báo của Tenant.
- `testDoGet_ViewNotifications_Empty`: Trả về danh sách rỗng (Empty state).
- `testDoGet_ViewNotificationDetail_Success`: Trả về chi tiết của 1 thông báo hợp lệ.

### 3.2 Error Cases
- `testDoGet_ViewNotification_CrossTenant_NotFound`: Xem chi tiết thông báo của người khác -> 404.
- `testDoPost_CreateNotification_Forbidden`: Cố tình POST tạo thông báo -> 405 Method Not Allowed.
- `testDoGet_UnauthorizedAccess`: Truy cập chưa đăng nhập -> Redirect.

### 3.3 Boundary Values
- `testDoGet_ViewNotifications_PaginationBounds`: Tham số `page` quá lớn hoặc âm.

### 3.4 Concurrent Scenarios
- Không áp dụng.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `TenantNotificationServlet`.
2. Map đầy đủ các kịch bản.
