# Implementation Plan: Tenant Contract Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `TenantContractsServlet.java` (hoặc Controller đảm nhiệm API Contract cho Tenant)
- **Dependencies**: `ContractService` (mocked)
- **Constraint**: Bảo đảm Read-only và bảo mật IDOR.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantContractsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewMyContracts_Success`: Xem danh sách hợp đồng của chính mình.
- `testDoGet_ViewMyContracts_Empty`: Danh sách hợp đồng rỗng.
- `testDoGet_ViewContractDetail_Success`: Xem chi tiết 1 hợp đồng hợp lệ.

### 3.2 Error Cases
- `testDoGet_ViewContract_CrossTenant_Forbidden`: Thử xem hợp đồng của Tenant khác -> 403.
- `testDoGet_ViewContract_NotFound`: Xem hợp đồng không tồn tại -> 404.
- `testDoPost_UpdateContract_Forbidden`: Thử POST/sửa hợp đồng -> 403 hoặc 405.
- `testDoGet_UnauthorizedAccess`: Chưa đăng nhập -> 401 hoặc Redirect.

### 3.3 Boundary Values
- (Không có dữ liệu đầu vào phức tạp)

### 3.4 Concurrent Scenarios
- Không áp dụng cho Read-only operation.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `TenantContractsServlet`.
2. Tạo các HTTP methods và verify các quyền giới hạn đúng như Spec.
