# Implementation Plan: MeterReading History Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `MeterReadingHistoryServlet.java`, `MeterReadingDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Tính năng History là Read-only. Xác minh bộ lọc thời gian và logic chặn ghi đè dữ liệu.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/MeterReadingHistoryServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadHistory_CurrentMonth`: Không truyền tham số, tự động lấy tháng/năm hiện tại. Truyền xuống Mock DAO và render kết quả.
- `testDoGet_LoadHistory_SpecificMonth`: Truyền `month=5&year=2026`. Parse đúng tham số.

### 3.2 Error Cases
- `testDoGet_InvalidMonthRange`: Truyền `month=13`. Bắt Validation Error hoặc tự Fallback về tháng 12/tháng hiện tại.
- `testDoGet_NumberFormatException`: Truyền `month=abc`. Fallback an toàn về cấu hình mặc định (tháng hiện tại).
- `testDoPost_MethodNotAllowed`: Gọi phương thức POST. Bị chặn (405 Method Not Allowed) do đây là trang Read-only.

### 3.3 Boundary Values
- `testDoGet_MissingImagePaths`: Trả về 1 bản ghi có `electricMeterImage = null`. Đảm bảo Servlet không ném lỗi trong quá trình xử lý đối tượng DTO.
- `testDoGet_NoHistoryForMonth`: Chọn 1 tháng trong tương lai chưa có lịch sử, render giao diện danh sách rỗng an toàn.

### 3.4 Concurrent Scenarios
- `testConcurrency_ReadHistoryWhileAdminDeletes`: Giả lập nhiều threads cùng get lịch sử. Kiểm tra Servlet stateless.

## 4. Các bước thực hiện
1. Thiết lập `MeterReadingHistoryServletTest` với Mockito (`@Mock MeterReadingDAO`).
2. Viết Test method tuân thủ format comment `# EARS [...]`.
