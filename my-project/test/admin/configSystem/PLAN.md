# Implementation Plan: System Config Test (Unit Test Only)

**Date**: 2026-07-23 | **Spec**: [spec.md](spec.md)

## Summary
Kế hoạch triển khai mã nguồn kiểm thử (Unit Testing) cho module System Config. Kiểm thử tập trung vào `AdminSystemConfigServlet` và các DTO để xác minh logic validation form, xử lý lỗi, che giấu dữ liệu nhạy cảm (password, secretKey) và xử lý luồng đồng thời bằng Mockito (Thread Safety).

## Technical Context
- **Language/Version**: Java 17
- **Primary Dependencies**: JUnit 5.11.4, Mockito 5.14.2, AssertJ
- **Testing**: Unit Test (Tuyệt đối không đụng DB, không E2E UI)
- **Project Type**: Web Application

## Project Structure

```text
src/test/java/com/quanlyphongtro/
├── dto/
│   ├── EmailConfigDTOTest.java         # Test logic mask password (nếu có)
│   └── VNPayConfigDTOTest.java         # Test logic mask secretKey (nếu có)
└── controller/admin/
    └── AdminSystemConfigServletTest.java # Nơi test Validation, Error Handling, Concurrency
```

## Tasks Cần Thực Hiện

1. **Happy Path & DTOs**
   - Viết `EmailConfigDTOTest` và `VNPayConfigDTOTest`.
   - Cấu hình Mock Request/Response/Service trong `AdminSystemConfigServletTest`.
   - Viết test case cho phép update Email/VNPay thành công (mock service nhận tham số chuẩn).
   - Viết test case hiển thị form cấu hình thành công (GET).

2. **Error Cases (Unwanted)**
   - Viết test cases bỏ trống trường bắt buộc, điền port không hợp lệ (không phải số nguyên).
   - Viết test case giả lập lỗi khi Service ném Exception (mất kết nối DB).
   - Viết test case từ chối quyền truy cập (FORBIDDEN).

3. **Boundary Values**
   - Viết test cases kiểm tra biên của `port` (1, 65535) hợp lệ và (0, 65536) không hợp lệ.
   - Viết test case xử lý input chuỗi toàn khoảng trắng.
   - Viết test case chuỗi vượt độ dài (nếu có limit).

4. **Concurrent Scenarios**
   - Sử dụng `ExecutorService` với 20 threads giả lập Race Condition đẩy POST request cập nhật cùng lúc (cả Email và VNPay).
   - Giả lập Thread A gọi GET, Thread B gọi POST.
   - Verify bằng Mockito xem các tham số từ các request độc lập có bị lẫn lộn vào nhau không khi truy cập chung vào instance của Servlet.
