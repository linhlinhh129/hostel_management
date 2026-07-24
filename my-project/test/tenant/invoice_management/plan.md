# Implementation Plan: Tenant Invoice Management & Payment (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Targets**: 
  - `TenantInvoiceServlet.java` (Quản lý hiển thị)
  - `TenantPaymentServlet.java` (Khởi tạo giao dịch VNPAY)
  - `TenantPaymentReturnServlet.java` (Xử lý kết quả VNPAY)
- **Dependencies**: `InvoiceService`, `PaymentService` (mocked)
- **Constraint**: Bảo mật giao dịch, toàn vẹn dữ liệu và an toàn luồng (Thread-safe) trong thanh toán IPN.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantInvoiceServletTest.java`
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantPaymentServletTest.java`
- `src/test/java/com/quanlyphongtro/controller/tenant/TenantPaymentReturnServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewInvoices_Success`: GET `/invoices` trả về danh sách, sắp xếp giảm dần theo kỳ.
- `testDoGet_ViewInvoiceDetail_Success`: GET `/invoices?id=` trả về chi tiết chính xác.
- `testDoPost_CreatePaymentUrl_Success`: POST tạo giao dịch VNPay, trả về URL và Invoice chuyển sang PROCESSING.
- `testDoGet_PaymentReturn_Success`: GET Return URL xử lý IPN thành công (`vnp_ResponseCode=00`), Hash đúng, tiền đúng -> Cập nhật PAID và lưu `payments`.

### 3.2 Error Cases
- `testDoGet_ViewInvoice_CrossTenant_Forbidden`: Truy cập ID hóa đơn của người khác -> 403.
- `testDoPost_CreatePayment_Conflict`: Thanh toán hóa đơn đã PAID hoặc đang PROCESSING -> 409 Conflict.
- `testDoGet_PaymentReturn_InvalidSignature`: VNPAY trả kết quả nhưng sai `SecureHash` -> Reject (400) hoặc xử lý thất bại (Ghi log).
- `testDoGet_PaymentReturn_AmountMismatch`: Số tiền VNPAY trả về lệch so với Database -> Reject.
- `testDoGet_PaymentReturn_FailedTransaction`: Mã `vnp_ResponseCode` != 00 -> Cập nhật hóa đơn sang FAILED.

### 3.3 Boundary Values
- `testDoGet_PaymentReturn_Idempotency`: Gửi liên tiếp 2 request Return giống hệt nhau (Trùng Transaction No) -> Request 2 bỏ qua, không nhân đôi data.

### 3.4 Concurrent Scenarios
- `testConcurrency_PaymentIpn_RaceCondition`: Dùng `ExecutorService` đẩy 2 Threads gọi Return/IPN đồng thời. Hệ thống chỉ được xử lý cập nhật 1 lần (để tránh nạp tiền 2 lần).

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho cả 3 Servlets.
2. Thiết lập kỹ thuật Mock `vnp_SecureHash` giả lập cho các kịch bản bảo mật.
