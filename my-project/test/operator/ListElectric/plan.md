# Implementation Plan: ListElectric Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorListElectricServlet.java`, `MeterReadingDAO` / `RoomDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Chú trọng vào logic mapping trạng thái cập nhật (CHUA_CAP_NHAT vs DA_CAP_NHAT).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorListElectricServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ListRooms_MixedStatus`: Mock DAO trả về 2 phòng. Phòng 1 có `updatedAt != null` (ĐÃ CẬP NHẬT). Phòng 2 có `updatedAt == null` (CHƯA CẬP NHẬT). Xác nhận Servlet xử lý hiển thị chuẩn xác.
- `testDoGet_EmptyList`: Trả về mảng rỗng, hệ thống không báo lỗi NullPointer.

### 3.2 Error Cases
- `testDoGet_DatabaseConnectionError`: Khi Mock DAO văng `SQLException`, Servlet bắt lỗi an toàn và hiển thị thông báo flash.
- `testDoGet_UnauthorizedAccess`: User có Role `TENANT` bị chặn bởi 403 Forbidden.

### 3.3 Boundary Values
- `testDoGet_ZeroReadingsBoundary`: Mock trả về số điện kỳ trước = 0, nước kỳ trước = 0. Test đảm bảo UI vẫn render số 0 bình thường.
- `testDoGet_LargeDataset`: Test xử lý mapping một List chứa 10,000 phần tử xem có bị nghẽn (logic vòng lặp) ở Controller không.

### 3.4 Concurrent Scenarios
- `testConcurrency_SnapshotRead`: Nhiều Threads đọc list cùng lúc. Đảm bảo Servlet hoàn toàn Stateless và không gây `ConcurrentModificationException`.

## 4. Các bước thực hiện
1. Setup class test cho `OperatorListElectricServlet`.
2. Tạo Mock đối tượng `MeterReadingDAO` trả về các List DTO ảo.
3. Viết các test method phủ 4 khía cạnh.
