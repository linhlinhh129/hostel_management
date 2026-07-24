# Test Specification: Quản lý Thông báo (Tenant Notification Management)

**File bị ảnh hưởng**: `TenantNotificationServletTest.java`
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`NotificationService`). Bảo đảm phân quyền chỉ xem được thông báo Public hoặc đích danh Tenant.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewNotifications_Success`: KHI Tenant truy cập trang danh sách thông báo, THE SYSTEM SHALL trả về danh sách được sắp xếp mới nhất lên đầu, giới hạn phân trang (mặc định 10).
- `testDoGet_ViewNotifications_Empty`: KHI không có thông báo nào, THE SYSTEM SHALL hiển thị giao diện Empty State ("Hiện chưa có thông báo nào.").
- `testDoGet_ViewNotificationDetail_Success`: KHI Tenant xem chi tiết thông báo hợp lệ, THE SYSTEM SHALL trả về đầy đủ nội dung thông báo.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_ViewNotification_CrossTenant_NotFound`: KHI Tenant xem chi tiết thông báo của người khác hoặc thông báo bị xóa, THE SYSTEM SHALL trả về HTTP 404 (Không tìm thấy hoặc không có quyền truy cập).
- `testDoPost_CreateNotification_Forbidden`: KHI Tenant cố tình gửi request POST để tạo thông báo, THE SYSTEM SHALL trả về HTTP 403 hoặc 405 (Vì Tenant chỉ có quyền đọc).
- `testDoGet_UnauthorizedAccess`: KHI người dùng chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401 hoặc chuyển hướng trang Login.

## 3. Boundary Values (Giá trị biên)

- `testDoGet_ViewNotifications_Pagination`: KHI truyền tham số `page` quá lớn hoặc âm (ví dụ `page=-1` hoặc `page=999999`), THE SYSTEM SHALL xử lý mặc định về `page=1` hoặc trả về danh sách rỗng một cách an toàn mà không bị crash.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- (Chỉ áp dụng các luồng Read-only, không yêu cầu test Race condition).
