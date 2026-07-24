# Implementation Plan: Update Service Price (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ServicePricePageServlet.java`
- **Dependencies**: `FacilityService`, `ServicePriceHistoryService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào xử lý Routing qua `action` parameter và luồng Error Forward về JSP thay vì ném ra lỗi cứng.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ServicePricePageServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewServicePrices_Success`: Mặc định GET không action trả về danh sách giá dịch vụ.
- `testDoGet_ViewServicePrices_NoFacility_Empty`: Không có cơ sở quản lý trả về rỗng / thông báo.
- `testDoGet_ViewHistory_Success`: GET `?action=history` trả về danh sách lịch sử theo loại giá.
- `testDoPost_UpdatePrice_Success`: POST `?action=update` submit giá mới, gọi service và redirect thành công.

### 3.2 Error Cases
- `testDoGet_CrossFacilityAccess_Forbidden`: Cố xem lịch sử của cơ sở không quản lý (403).
- `testDoPost_UpdatePrice_InvalidNumber_ForwardError`: Update giá chứa chữ `ABC`. Bắt lỗi và forward về index.jsp (HTTP 200).
- `testDoPost_UpdatePrice_ZeroOrNegative_ForwardError`: Update giá âm. Bắt lỗi `INVALID_PRICE` và forward.
- `testDoPost_UpdatePrice_MissingRequiredField_ForwardError`: Thiếu `newPrice`. Forward báo lỗi.
- `testDoPost_UpdatePrice_InvalidType_ForwardError`: Sai loại phí (Vd: `INTERNET` - không thuộc enum cố định). Forward báo lỗi.
- `testDoPost_InvalidAction_BadRequest`: Gửi action lạ (`?action=delete`). Trả về 400.
- `testDoGet_UnauthorizedAccess`: User không có Role MANAGER truy cập. Trả về 403.

### 3.3 Boundary Values
- `testDoPost_UpdatePrice_MaxInt`: Giá mới lớn bằng `Integer.MAX_VALUE`.

### 3.4 Concurrent Scenarios
- `testConcurrency_UpdatePrice_RaceCondition`: Dùng `ExecutorService` đẩy 2 Thread update giá điện cùng lúc.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `ServicePricePageServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
