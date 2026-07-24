# Implementation Plan: Tenant Dependent Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `TenantDependentsServlet.java`
- **Dependencies**: `DependentService` / `TenantService` (mocked)
- **Constraint**: Bảo đảm Read-only, PII Masking (CCCD) và xử lý Soft Delete.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantDependentsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewMyDependents_Success`: Xem danh sách người phụ thuộc hợp lệ.
- `testDoGet_ViewMyDependents_Empty`: Danh sách rỗng.
- `testDoGet_ViewDependentDetail_Success`: Xem chi tiết 1 người phụ thuộc.
- `testDoGet_ViewDependentDetail_MaskCCCD`: Đảm bảo CCCD được mask theo format quy định.

### 3.2 Error Cases
- `testDoGet_ViewDependent_CrossTenant_Forbidden`: Thử xem chi tiết của Tenant khác (IDOR) -> 403.
- `testDoGet_ViewDependent_NotFound_OrSoftDeleted`: Xem ID không tồn tại hoặc đã bị Soft Delete (`deleted_at IS NOT NULL`) -> 404.
- `testDoPost_ModifyDependent_Forbidden`: Thử thao tác POST/Thêm/Sửa -> 403/405.
- `testDoGet_UnauthorizedAccess`: Chưa đăng nhập -> 401 hoặc Redirect.

### 3.3 Boundary Values
- (Không có dữ liệu đầu vào phức tạp)

### 3.4 Concurrent Scenarios
- Không áp dụng cho Read-only.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `TenantDependentsServlet`.
2. Tạo các bài test để che CCCD, filter Soft Delete và chặn Write-operations.
