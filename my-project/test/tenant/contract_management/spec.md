# Test Specification: Quản lý Hợp đồng của Người thuê (Tenant Contract Management)

**File bị ảnh hưởng**: `TenantContractsServletTest.java` (Giả định Servlet phục vụ API hợp đồng cho Tenant)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`ContractService`). Đảm bảo nguyên tắc bảo mật thông tin cá nhân.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewMyContracts_Success`: KHI Tenant truy cập danh sách hợp đồng, THE SYSTEM SHALL trả về đúng các hợp đồng mà `tenant_id` khớp với `currentUser.id`.
- `testDoGet_ViewMyContracts_Empty`: KHI Tenant chưa có hợp đồng nào, THE SYSTEM SHALL trả về list rỗng hoặc hiển thị thông báo "Bạn chưa có hợp đồng thuê nào".
- `testDoGet_ViewContractDetail_Success`: KHI Tenant truy cập chi tiết một hợp đồng hợp lệ của mình, THE SYSTEM SHALL trả về đầy đủ các thông tin (Phòng, Tiền cọc, Tiền thuê, Giá dịch vụ...).

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_ViewContract_CrossTenant_Forbidden`: KHI Tenant truy cập chi tiết hợp đồng của người khác (IDOR), THE SYSTEM SHALL trả về HTTP 403 `CONTRACT_ACCESS_DENIED`.
- `testDoGet_ViewContract_NotFound`: KHI Tenant truy cập hợp đồng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 `CONTRACT_NOT_FOUND`.
- `testDoPost_UpdateContract_Forbidden`: KHI Tenant cố tình gửi request POST (hoặc PUT/DELETE) để chỉnh sửa hợp đồng, THE SYSTEM SHALL trả về HTTP 403/405 (Vì Tenant chỉ có quyền Read-only).
- `testDoGet_UnauthorizedAccess`: KHI người dùng chưa đăng nhập truy cập, THE SYSTEM SHALL trả về HTTP 401 hoặc redirect.

## 3. Boundary Values (Giá trị biên)

- `testDoGet_ViewContracts_Pagination`: KHI danh sách hợp đồng quá dài, THE SYSTEM SHALL xử lý phân trang đúng đắn (nếu có áp dụng phân trang).

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- Không có xung đột dữ liệu (Do Tenant chỉ có quyền Read-only). Có thể test tải (Load testing) request truy xuất liên tục.
