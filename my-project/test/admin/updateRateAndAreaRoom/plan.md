# Implementation Plan: Update Room Rate & Area Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminRoomServlet.java`, `RoomDAO`
- **Constraint**: Đảm bảo 100% Unit Test không kết nối DB thật. Các class phụ thuộc phải được Mock hoàn toàn để cô lập môi trường kiểm thử.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminRoomServletTest.java` (Sẽ tập trung vào phần Update Rate & Area theo SPEC)

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_RoomDetail_Success`: Xem chi tiết phòng thành công.
- `testDoPost_Update_PositiveValues_Success`: Cập nhật diện tích và giá là số dương hợp lệ.
- `testDoPost_Update_EmptyValues_Success`: Cập nhật với dữ liệu bị bỏ trống (nhận null).

### 3.2 Error Cases
- `testDoPost_Update_FacilityInactive`: Sửa phòng của cơ sở INACTIVE -> Lỗi Validation.
- `testDoPost_Update_NegativeOrInvalidValues`: Truyền số âm hoặc chữ vào -> Lỗi Validation.
- `testDoGetPost_RoomNotFound`: RoomID không tồn tại -> Lỗi 404.
- `testAuth_Unauthorized_Forbidden`: Kiểm thử phân quyền (Role Manager hoặc chưa Login).

### 3.3 Boundary Values
- `testDoPost_Update_ZeroValues`: Giá trị chính xác bằng 0.
- `testDoPost_Update_ExtremeLargeValues`: Mức giá tiền tỷ, test khả năng parse `BigDecimal` an toàn.

### 3.4 Concurrent Scenarios
- `testConcurrency_UpdateRoom`: Gọi đồng thời nhiều threads sửa 1 phòng, verify Servlet không bị leak biến instance.

## 4. Các bước thực hiện
1. Setup test class và `@Mock`.
2. Implement Happy path.
3. Implement Error Cases & Boundary.
4. Implement Thread-safety test.
