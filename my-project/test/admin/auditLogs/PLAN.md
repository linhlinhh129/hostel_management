# Implementation Plan: Audit Logs Test (Unit Test Only)

**Date**: 2026-07-23 | **Spec**: [spec.md](spec.md)

## Summary
Kế hoạch triển khai mã nguồn kiểm thử (Unit Testing) cho module Audit Logs. Kiểm thử chỉ tập trung vào tầng Controller (`AdminAuditLogServlet`) và Service (nếu có) thông qua Mockito để test logic xử lý, validate tham số, phân quyền, các giá trị biên và trường hợp tranh chấp đồng thời. 

## Technical Context
- **Language/Version**: Java 17
- **Primary Dependencies**: JUnit 5.11.4, Mockito 5.14.2, AssertJ
- **Testing**: Unit Test (Tuyệt đối không đụng DB, không E2E UI)
- **Project Type**: Web Application

## Project Structure

```text
src/test/java/com/quanlyphongtro/
└── controller/admin/
    └── AdminAuditLogServletTest.java   # Bao phủ toàn bộ 4 khía cạnh kiểm thử Unit Test
```

## Tasks Cần Thực Hiện

1. **Setup & Happy Path**
   - Khởi tạo `AdminAuditLogServletTest`.
   - Setup Mockito (mock `HttpServletRequest`, `HttpServletResponse`, `AuditLogService`).
   - Viết test case `doGet` thành công (Lọc, Phân trang, Xem chi tiết).

2. **Error Cases (Unwanted)**
   - Viết test cases bắt lỗi `INVALID_FILTER`, hoặc khi `fromDate > toDate` (xử lý an toàn).
   - Viết test cases kiểm tra logic phân quyền (UNAUTHORIZED khi chưa đăng nhập, FORBIDDEN khi không phải admin).
   - Viết test case ném 404 (ID không tồn tại) hoặc DAO ném Exception.

3. **Boundary Values**
   - Viết test cases xử lý tham số `page` < 1 (fallback về 1) và `page` quá lớn (fallback rỗng).
   - Viết test case xử lý chuỗi filter rỗng hoặc chỉ toàn có khoảng trắng.
   - Viết test case ID biên (âm hoặc 0).

4. **Concurrent Scenarios**
   - Sử dụng `ExecutorService` tạo 50 threads mô phỏng Request `doGet` đồng thời vào cùng một Servlet instance.
   - Dùng CountDownLatch và AssertJ để verify rằng không có state leakage (rò rỉ trạng thái) giữa các request, chứng minh kiến trúc Thread Safety.
