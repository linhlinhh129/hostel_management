# Test Specification: Quản lý người thuê (Tenant Management)

**File bị ảnh hưởng**: `ManagerTenantsServletTest.java`
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào luồng bảo vệ dữ liệu xuyên cơ sở (Cross-facility) và xử lý State (Lock/Unlock/End Rental).

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewTenantsList_Success`: KHI truy cập danh sách, THE SYSTEM SHALL trả về danh sách cư dân thuộc cơ sở quản lý (có phân trang và lọc).
- `testDoGet_ViewTenantDetail_Success`: KHI xem chi tiết, THE SYSTEM SHALL trả về thông tin cá nhân, phòng, hợp đồng và người phụ thuộc.
- `testDoPost_UpdateProfile_Success`: KHI submit thông tin hợp lệ, THE SYSTEM SHALL cập nhật bản ghi trong `dbo.users` thành công.
- `testDoPost_LockTenant_Success`: KHI khóa tài khoản `ACTIVE`, THE SYSTEM SHALL chuyển status sang `LOCKED`.
- `testDoPost_UnlockTenant_Success`: KHI mở khóa tài khoản `LOCKED`, THE SYSTEM SHALL chuyển status về `ACTIVE` và reset login attempts.
- `testDoPost_EndRental_Success`: KHI kết thúc thuê, THE SYSTEM SHALL chuyển trạng thái thành `INACTIVE` VÀ gán `tenant_id = NULL` ở bảng phòng.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_CreateTenantDirectly_Redirect`: KHI cố tình truy cập trang tạo mới cư dân, THE SYSTEM SHALL redirect về `/manager/contracts`.
- `testDoPost_ActionCrossFacility_Forbidden`: KHI cập nhật/khóa/mở khóa/kết thúc thuê đối với cư dân KHÔNG thuộc cơ sở quản lý, THE SYSTEM SHALL trả về HTTP 403.
- `testDoPost_UpdateDuplicateEmailOrCCCD_Fails`: KHI cập nhật trùng Email hoặc CCCD đã tồn tại, THE SYSTEM SHALL báo lỗi trùng lặp.
- `testDoPost_UpdateInvalidFormat_Fails`: KHI cập nhật SĐT hoặc CCCD sai định dạng, THE SYSTEM SHALL báo lỗi validation.
- `testDoPost_EndRentalBeforeContractDate_Fails`: KHI kết thúc hợp đồng với ngày kết thúc nhỏ hơn ngày ký hợp đồng, THE SYSTEM SHALL báo lỗi.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_UpdateProfile_LengthBoundaries`: KHI tên, email, CCCD đạt đúng độ dài cho phép tối đa trong DB. THE SYSTEM SHALL pass.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_EndRentalAndLock_RaceCondition`: Giả lập 2 Manager thao tác cùng lúc trên 1 cư dân. 1 Manager khóa tài khoản, 1 Manager kết thúc thuê. THE SYSTEM SHALL xử lý an toàn (Lock xong thì vẫn end rental được, hoặc tùy theo quy định logic) và ghi log Transaction đầy đủ.
