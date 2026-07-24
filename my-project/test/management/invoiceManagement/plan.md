# Implementation Plan: Invoice Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `InvoiceServlet.java`, `InvoiceDetailServlet.java`
- **Dependencies**: `InvoiceService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Tập trung vào tính toán hóa đơn, xử lý ngoại lệ và bắt concurrent requests.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/InvoiceServletTest.java`
- `src/test/java/com/quanlyphongtro/controller/manager/InvoiceDetailServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_CreateInvoice_Success`: Kiểm tra form tạo hóa đơn hợp lệ, trả về trạng thái UNPAID.
- `testDoGet_ViewInvoiceList_Success`: Kiểm tra hiển thị danh sách hóa đơn theo trạng thái thanh toán.
- `testDoGet_ViewInvoiceDetail_Success`: Trả về chi tiết hóa đơn (mô phỏng DTO).
- `testDoPost_UpdateInvoice_Success`: Sửa đổi thông tin `taxRate`, `otherFee` thành công.

### 3.2 Error Cases
- `testDoPost_Create_RoomNotFound`: Trả về HTTP 404 khi không tìm thấy phòng.
- `testDoPost_Create_DuplicatePeriod`: Bắt lỗi HTTP 400 `INVOICE_ALREADY_EXISTS`.
- `testDoPost_Update_AlreadyPaid`: Trả về HTTP 400 `PAID_INVOICE_CANNOT_BE_UPDATED`.
- `testUnauthorizedAccess`: Trả về HTTP 403 nếu tài khoản không phải là Manager/Admin.

### 3.3 Boundary Values
- `testDoPost_Create_DueDateToday`: Ngày thanh toán bằng ngày hiện tại (biên).
- `testDoPost_Create_UsageZero`: Mức sử dụng điện/nước = 0.
- `testDoPost_Create_TaxAndFeeZero`: Thuế và phí phát sinh = 0.

### 3.4 Concurrent Scenarios
- `testConcurrency_CreateDuplicateInvoice_RaceCondition`: Giả lập nhiều request tạo cùng lúc.
- `testConcurrency_UpdateAndPay_RaceCondition`: Giả lập cập nhật và thanh toán cùng một thời điểm.

## 4. Các bước thực hiện
1. Cài đặt các class Test bằng `@ExtendWith(MockitoExtension.class)`.
2. Tạo Tests theo mô hình EARS, sử dụng `ExecutorService` cho các bài test đồng thời.
