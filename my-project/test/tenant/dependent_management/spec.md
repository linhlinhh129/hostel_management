# Test Specification: Quản lý Người phụ thuộc của Người thuê (Tenant Dependent Management)

**File bị ảnh hưởng**: `TenantDependentsServletTest.java` (Giả định Servlet phục vụ API người phụ thuộc cho Tenant)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`TenantService` / `DependentService`). Chú trọng bảo mật PII (Mask CCCD) và Soft Delete.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewMyDependents_Success`: KHI Tenant truy cập danh sách người phụ thuộc, THE SYSTEM SHALL chỉ trả về danh sách thuộc `tenant_id` của họ VÀ `deleted_at IS NULL`.
- `testDoGet_ViewMyDependents_Empty`: KHI Tenant không có người phụ thuộc, THE SYSTEM SHALL hiển thị thông báo "Hiện chưa có người phụ thuộc nào được đăng ký".
- `testDoGet_ViewDependentDetail_Success`: KHI Tenant xem chi tiết người phụ thuộc hợp lệ, THE SYSTEM SHALL trả về dữ liệu.
- `testDoGet_ViewDependentDetail_MaskCCCD`: KHI trả về chi tiết người phụ thuộc, THE SYSTEM SHALL che (mask) CCCD theo chuẩn `0790******123`.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_ViewDependent_CrossTenant_Forbidden`: KHI Tenant xem chi tiết người phụ thuộc của người khác (IDOR), THE SYSTEM SHALL trả về HTTP 403 Forbidden.
- `testDoGet_ViewDependent_NotFound_OrSoftDeleted`: KHI Tenant xem chi tiết người phụ thuộc không tồn tại HOẶC đã bị Soft Delete, THE SYSTEM SHALL trả về HTTP 404 Not Found.
- `testDoPost_ModifyDependent_Forbidden`: KHI Tenant cố tình gửi request POST (thêm, sửa, xóa), THE SYSTEM SHALL chặn quyền truy cập (Role của Tenant ở chức năng này là Read-only).
- `testDoGet_UnauthorizedAccess`: KHI chưa đăng nhập, THE SYSTEM SHALL trả về HTTP 401 hoặc redirect.

## 3. Boundary Values (Giá trị biên)

- Không yêu cầu test nhập liệu do chỉ có quyền xem (Read-only). 

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- Không có thao tác ghi dữ liệu (Read-only) nên không test Race condition. Có thể test Rate Limit.
