# Implementation Plan: Operator Update Meter Reading (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `UpdateMeterReadingServlet.java`
- **Dependencies**: `MeterReadingService`, `AuditLogDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Mock `HttpServletRequest.getPart()` để giả lập upload ảnh công tơ điện và nước. Kiểm tra chặt chẽ Validation logic (không cho chỉ số lùi, không cho thiếu ảnh).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/UpdateMeterReadingServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_InsertSuccess`: Gửi đầy đủ thông tin (roomCode, số điện/nước hợp lệ, 2 ảnh hợp lệ). Hệ thống gọi `insertMeterReading` do chưa có dữ liệu trong tháng. Redirect thành công.
- `testDoPost_UpdateSuccess`: Giống trên nhưng Mock Service trả về `existingMeterId != null`. Hệ thống gọi `updateMeterReading` để ghi đè.

### 3.2 Error Cases
- `testDoPost_MissingRoomCode`: Cố tình để `roomCode` rỗng. Hệ thống redirect kèm thông báo lỗi.
- `testDoPost_RoomNotFound`: Nhập mã phòng không tồn tại. `getPreviousReadingByRoomCode` trả về null. Hệ thống redirect kèm flashMessage báo phòng không tồn tại.
- `testDoPost_ElectricReadingInvalid`: Số điện mới (100) nhỏ hơn số điện cũ (120). Bắt lỗi Validation `ELECTRIC_READING_INVALID`.
- `testDoPost_WaterReadingInvalid`: Số nước mới nhỏ hơn số cũ. Bắt lỗi Validation `WATER_READING_INVALID`.
- `testDoPost_MissingElectricImage`: Thiếu ảnh điện (Part = null hoặc size = 0). Bắt lỗi `ELECTRIC_METER_IMAGE_REQUIRED`.
- `testDoPost_MissingWaterImage`: Thiếu ảnh nước (Part = null hoặc size = 0). Bắt lỗi `WATER_METER_IMAGE_REQUIRED`.
- `testDoPost_NumberFormatException`: Người dùng truyền chữ ("abc") vào tham số số điện mới. Servlet catch `NumberFormatException` và fallback an toàn.

### 3.3 Boundary Values
- `testDoPost_SameReadings`: Số điện mới bằng số cũ (120 = 120), số nước mới bằng số cũ. Cập nhật thành công (pass validation).
- `testDoPost_FirstTimeReading`: Đọc lần đầu tiên (phòng có tồn tại, nhưng chưa bao giờ ghi điện nước, `getReadingBeforeCurrentMonth` trả về null nhưng `getPreviousReadingByRoomCode` trả về DTO). Logic sẽ đặt số cũ là 0 và pass validation.
- `testDoPost_FileSizeExceeded`: Mô phỏng Tomcat văng `IllegalStateException` khi ảnh > 5MB. Tránh crash 500.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleSubmit`: 2 threads cùng gọi POST cập nhật chỉ số điện nước. Service văng exception ở luồng thứ 2. Servlet báo lỗi Graceful.

## 4. Các bước thực hiện
1. Thiết lập `UpdateMeterReadingServletTest` với Mockito (`@Mock MeterReadingService`, `@Mock Part`, v.v.).
2. Viết Test method tuân thủ format comment `# EARS [...]` theo `SPEC.md`.
