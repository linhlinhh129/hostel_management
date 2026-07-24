# Implementation Plan: Dependent Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerTenantsServlet.java` (Các endpoint phụ thuộc: `dependents/add`, `dependents/{id}/edit`, `dependents/{id}/remove`)
- **Dependencies**: `TenantService`, `AuditLogDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào nghiệp vụ chặn tạo cho Hợp đồng Inactive, Xóa mềm (Soft Delete) và Data Masking (Che mờ CCCD).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerTenantsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_AddDependent_Success`: Gửi POST hợp lệ. Mock Service add thành công. Audit log lưu. Redirect.
- `testDoGet_ViewDependent_MaskIdentity`: Gọi GET xem detail. Logic Masking ẩn phần giữa của CCCD trả về dạng `***`.
- `testDoPost_SoftDelete_Success`: Gửi POST remove. Đánh dấu `deleted_at`.

### 3.2 Error Cases
- `testDoPost_AddDependent_TenantInactive`: Mock Service trả về cờ Tenant Inactive -> Trả về lỗi không cho phép.
- `testDoPost_AddDependent_MissingFullName`: Validation Fail khi thiếu tên. Flash error.
- `testDoPost_EditDependent_IDOR_CrossFacility`: Manager A cố Edit dependent của Tenant không thuộc cơ sở -> 403 Forbidden.

### 3.3 Boundary Values
- `testDoPost_Validation_IdentityLength`: Test nhập CCCD 9 ký tự (pass), 12 ký tự (pass), 11 ký tự (fail).
- `testDoPost_Validation_PhoneLength`: Test nhập SĐT 10 ký tự (pass), 9 ký tự (fail).

### 3.4 Concurrent Scenarios
- `testConcurrency_SoftDelete_DoubleStrike`: 2 Manager cùng bấm xóa mềm 1 dependent cùng lúc. Mock Service ném exception ở lượt thứ 2, Servlet bắt mượt mà không văng HTTP 500.

## 4. Các bước thực hiện
1. Thiết lập `ManagerTenantsServletTest.java`.
2. Tạo Tests theo mô hình EARS.
