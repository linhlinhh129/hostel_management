# Implementation Plan: ListRequest Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorListRequestServlet.java`, `IncidentDAO` / `RequestDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Mock các method Query danh sách Request theo Filter và Pagination. Đảm bảo parse các query string `page`, `category_id`, `status` một cách an toàn.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorListRequestServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ListRequests_DefaultParams`: Truy cập không truyền params. Xác nhận Mock DAO nhận đúng `page=1`, `limit=20`, filter rỗng.
- `testDoGet_ListRequests_WithFilters`: Truy cập truyền `status=pending`, `category_id=2`. Xác nhận Mock DAO được gọi với đúng cấu trúc filter.

### 3.2 Error Cases
- `testDoGet_InvalidFilterFormat`: Cố tình truyền `category_id=abc`. Xác nhận Servlet bắt `NumberFormatException`, bỏ qua điều kiện filter (hoặc gán = null) và vẫn lấy ra được list an toàn.
- `testDoGet_UnauthorizedAccess`: User mang Role `TENANT`. Xác minh văng 403 Forbidden.

### 3.3 Boundary Values
- `testDoGet_PaginationOutOfBounds`: Truyền `page=9999`. Mock DAO trả về List rỗng. Servlet gắn List rỗng vào View và trả về thông báo "Không có dữ liệu".
- `testDoGet_NoDataFound`: Filter quá khắt khe, không có records. Trả về mảng rỗng mà không bị Null Pointer.

### 3.4 Concurrent Scenarios
- `testConcurrency_SnapshotPaging`: Giả lập nhiều Threads cùng đọc List (Get). Đảm bảo Servlet hoàn toàn Stateless và xử lý đa luồng tốt.

## 4. Các bước thực hiện
1. Thiết lập `OperatorListRequestServletTest` với Mockito (`@Mock IncidentDAO`).
2. Viết Test method tuân thủ format comment `# EARS [...]`.
