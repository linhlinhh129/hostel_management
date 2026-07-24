# Implementation Plan: Tenant Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerTenantsServlet.java`
- **Dependencies**: `TenantService`, `RoomService`, `ContractService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào xử lý nghiệp vụ kết thúc hợp đồng (`End Rental`), Khóa/Mở Khóa (`Lock/Unlock`) và kiểm soát quyền truy cập chéo cơ sở (Cross-facility).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerTenantsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewTenantsList_Success`: Xem danh sách cư dân (có lọc, phân trang).
- `testDoGet_ViewTenantDetail_Success`: Xem chi tiết cư dân (phòng, hợp đồng, phụ thuộc).
- `testDoPost_UpdateProfile_Success`: Submit form cập nhật profile thành công.
- `testDoPost_LockTenant_Success`: Khóa tài khoản cư dân.
- `testDoPost_UnlockTenant_Success`: Mở khóa tài khoản cư dân.
- `testDoPost_EndRental_Success`: Kết thúc hợp đồng (INACTIVE, giải phóng phòng).

### 3.2 Error Cases
- `testDoGet_CreateTenantDirectly_Redirect`: Cố tình truy cập trang tạo mới cư dân.
- `testDoPost_ActionCrossFacility_Forbidden`: Cố thao tác với cư dân thuộc cơ sở khác (403).
- `testDoPost_UpdateDuplicateEmailOrCCCD_Fails`: Cập nhật trùng Email/CCCD.
- `testDoPost_UpdateInvalidFormat_Fails`: Cập nhật sai định dạng SĐT/CCCD.
- `testDoPost_EndRentalBeforeContractDate_Fails`: Kết thúc hợp đồng trước ngày ký hợp đồng.

### 3.3 Boundary Values
- `testDoPost_UpdateProfile_LengthBoundaries`: Test giới hạn độ dài Tên, Email, CCCD.

### 3.4 Concurrent Scenarios
- `testConcurrency_EndRentalAndLock_RaceCondition`: Hai Thread cùng thao tác trên 1 Tenant (1 End Rental, 1 Lock).

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `ManagerTenantsServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
