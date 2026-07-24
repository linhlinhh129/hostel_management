# Test Specification: Quản lý khoản phí và giá dịch vụ (Update Service Price)

**File bị ảnh hưởng**: `ServicePricePageServletTest.java` (Thực chất theo API thì là `ServicePricePageServlet`)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`FacilityService` hoặc `ServicePriceService`).

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewServicePrices_Success`: KHI truy cập danh sách giá (`GET /manager/service-prices`), THE SYSTEM SHALL trả về list giá dịch vụ hiện tại (Điện, Nước, Phí dịch vụ) của cơ sở được phân công.
- `testDoGet_ViewServicePrices_NoFacility_Empty`: KHI Manager chưa được phân công cơ sở, THE SYSTEM SHALL hiển thị thông báo "Bạn chưa được phân quyền quản lý cơ sở nào".
- `testDoGet_ViewHistory_Success`: KHI chọn action `history` (`GET /manager/service-prices?action=history&priceType=ELECTRICITY`), THE SYSTEM SHALL trả về danh sách lịch sử cập nhật giá.
- `testDoPost_UpdatePrice_Success`: KHI submit form cập nhật giá hợp lệ, THE SYSTEM SHALL lưu DB, ghi log lịch sử và redirect về trang danh sách.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_CrossFacilityAccess_Forbidden`: KHI truy cập hoặc xem lịch sử giá của cơ sở khác, THE SYSTEM SHALL trả về 403 `FACILITY_ACCESS_DENIED`.
- `testDoPost_UpdatePrice_InvalidNumber_ForwardError`: KHI nhập giá mới chứa chữ hoặc sai định dạng số, THE SYSTEM SHALL bắt Exception, gán `errorMessage="Dữ liệu không hợp lệ."` và forward lại trang `index.jsp` (HTTP 200 thay vì 400 theo SDD).
- `testDoPost_UpdatePrice_ZeroOrNegative_ForwardError`: KHI nhập giá <= 0, THE SYSTEM SHALL báo lỗi `INVALID_PRICE` và forward.
- `testDoPost_UpdatePrice_MissingRequiredField_ForwardError`: KHI bỏ trống giá hoặc loại giá, THE SYSTEM SHALL báo lỗi `REQUIRED_FIELD_MISSING`.
- `testDoPost_UpdatePrice_InvalidType_ForwardError`: KHI truyền loại giá sai (không phải ELECTRICITY, WATER, SERVICE_FEE), THE SYSTEM SHALL báo lỗi `INVALID_PRICE_TYPE`.
- `testDoPost_InvalidAction_BadRequest`: KHI gửi `POST` với `action` không hợp lệ, THE SYSTEM SHALL trả về 400 Bad Request.
- `testDoGet_UnauthorizedAccess`: KHI người dùng không có role MANAGER truy cập, THE SYSTEM SHALL trả về 403.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_UpdatePrice_MaxInt`: KHI nhập giá mới cực lớn (vượt quá kiểu `int` hoặc nằm sát biên), THE SYSTEM SHALL xử lý lỗi hoặc ép kiểu an toàn.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_UpdatePrice_RaceCondition`: Giả lập 2 Manager thao tác cập nhật giá cùng một lúc trên cùng 1 loại phí. THE SYSTEM SHALL xử lý nguyên vẹn (Ghi đủ 2 bản ghi lịch sử theo đúng trình tự và giá chót được cập nhật chính xác vào cơ sở).
