# Test Specification: Quản lý hóa đơn (Invoice Management)

**File bị ảnh hưởng**: `InvoiceServletTest.java`, `InvoiceDetailServletTest.java`
**Nguyên tắc**: Test Behavior, không Test Implementation. Sử dụng Mockito để cô lập Database (DAO/Service).

## 1. Happy Path (Các kịch bản thành công)

- `testCreateInvoice_Success`: KHI gửi dữ liệu hợp lệ, THE SYSTEM SHALL truy xuất tự động chỉ số, đơn giá, tính toán chính xác số tiền (Điện, Nước, Phí dịch vụ, Tạm tính, Thuế, Tổng tiền) và gán trạng thái `UNPAID`.
- `testViewInvoiceList_Success`: KHI truy cập trang danh sách, THE SYSTEM SHALL hiển thị danh sách hóa đơn theo trạng thái và hỗ trợ phân trang.
- `testViewInvoiceDetail_Success`: KHI truy cập chi tiết, THE SYSTEM SHALL trả về đầy đủ các trường thông tin và hiển thị form chỉnh sửa nếu hóa đơn chưa `PAID`.
- `testUpdateInvoice_Success`: KHI thay đổi `otherFee` hoặc `taxRate`, THE SYSTEM SHALL tính toán lại tổng tiền phải nộp và lưu bản cập nhật.
- `testExportPDF_Trigger`: KHI thao tác in/xuất PDF, THE SYSTEM SHALL gọi ra cấu trúc HTML thuần được CSS print.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testCreateInvoice_RoomNotFound`: KHI mã phòng không tồn tại, THE SYSTEM SHALL trả về HTTP 404 `ROOM_NOT_FOUND`.
- `testCreateInvoice_DuplicatePeriod`: KHI phòng đã có hóa đơn trong kỳ (ví dụ: `202606`), THE SYSTEM SHALL trả về HTTP 400 `INVOICE_ALREADY_EXISTS`.
- `testCreateInvoice_FacilityPriceMissing`: KHI không tìm thấy đơn giá (Điện/Nước/Dịch vụ) của cơ sở, THE SYSTEM SHALL trả về HTTP 400 `FACILITY_PRICE_NOT_FOUND`.
- `testCreateInvoice_MeterReadingMissing`: KHI không có chỉ số điện nước trong kỳ, THE SYSTEM SHALL trả về HTTP 400 `METER_READING_NOT_FOUND`.
- `testCreateInvoice_InvalidMeterReading`: KHI chỉ số mới nhỏ hơn chỉ số cũ, THE SYSTEM SHALL trả về HTTP 400 `INVALID_ELECTRIC_READING` hoặc `INVALID_WATER_READING`.
- `testUpdateInvoice_AlreadyPaid`: KHI cố gắng sửa đổi hóa đơn đã ở trạng thái `PAID`, THE SYSTEM SHALL trả về HTTP 400 `PAID_INVOICE_CANNOT_BE_UPDATED`.
- `testUnauthorizedAccess`: KHI người dùng có role `TENANT` truy cập URL quản lý hóa đơn, THE SYSTEM SHALL trả về HTTP 403.

## 3. Boundary Values (Giá trị biên)

- `testCreateInvoice_DueDate_Today`: KHI `dueDate` bằng đúng ngày hiện tại (biên thấp nhất cho phép), THE SYSTEM SHALL chấp nhận (Không bị `INVALID_DUE_DATE`).
- `testCreateInvoice_UsageZero`: KHI chỉ số điện mới = chỉ số cũ (tiêu thụ = 0), THE SYSTEM SHALL tính thành tiền điện = 0 và không ném lỗi.
- `testCreateInvoice_TaxAndFeeZero`: KHI `taxRate = 0` và `otherFee = 0`, THE SYSTEM SHALL tính tổng tiền chính xác bằng tạm tính (không có phí cộng thêm).

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_CreateDuplicateInvoice_RaceCondition`: Giả lập 2 Request tạo hóa đơn cho cùng phòng HN0101 kỳ `202606` tại cùng 1 mili-giây. THE SYSTEM SHALL chỉ thành công 1 request, request còn lại bị văng lỗi `INVOICE_ALREADY_EXISTS` (Đảm bảo Thread-safe / Transaction Isolation).
- `testConcurrency_UpdateAndPay_RaceCondition`: Giả lập 1 Thread đang lưu chỉnh sửa hóa đơn, 1 Thread cùng lúc xác nhận thanh toán `PAID`. THE SYSTEM SHALL ngăn chặn bản chỉnh sửa bị ghi đè lên dữ liệu đã đóng băng.
