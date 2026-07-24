# Implementation Plan: View Revenue Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminRevenueServlet.java`, `RevenueService`
- **Constraint**: Đảm bảo 100% Unit Test. Cô lập logic thống kê (Service layer) thông qua Mockito.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminRevenueServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_Index_Success`: Mở trang tổng quan thành công.
- `testDoGet_ByFacility_Success`: Danh sách doanh thu theo cơ sở (phân trang).
- `testDoGet_ByPeriod_Success`: Danh sách doanh thu theo kỳ.
- `testPeriodParsing_YYYYMM_to_MMyyyy`: Đảm bảo tham số thời gian từ form được parse chuẩn xác.

### 3.2 Error Cases
- `testDoGet_InvalidDateRange`: Bắt lỗi khi chọn khoảng thời gian ngược.
- `testAuth_Unauthorized_Forbidden`: Role `MANAGER` bị 403, chưa login bị redirect.
- `testPathNotFound`: Đường dẫn rác (vd `/by-unknown`) văng 404.

### 3.3 Boundary Values
- `testDoGet_EmptyData`: Mock Service trả về 0 revenue, UI không được crash NullPointer.
- `testDoGet_MissingPeriodParameter`: Fallback về tháng hiện tại tự động.
- `testDoGet_PaginationBoundary`: Test tham số page là số âm hoặc chữ văng.

### 3.4 Concurrent Scenarios
- `testConcurrency_ReadReports`: 50 threads cùng lúc load Report để đảm bảo Servlet stateless không bị chia sẻ chéo biến period.

## 4. Các bước thực hiện
1. Setup test class và `@Mock`.
2. Implement Happy path (Index, Facility, Period).
3. Implement Error Cases & Boundary.
4. Implement Thread-safety test.
