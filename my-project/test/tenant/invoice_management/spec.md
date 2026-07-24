# Test Specification: Quản lý Hóa đơn và Thanh toán VNPAY (Tenant Invoice Management)

**File bị ảnh hưởng**: `TenantInvoiceServletTest.java`, `VnpayPaymentServletTest.java` (Giả định Servlet phục vụ hóa đơn và IPN)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`InvoiceService`, `PaymentService`). Tập trung cao vào bảo mật thanh toán (VNPAY IPN).

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewInvoices_Success`: KHI Tenant truy cập danh sách hóa đơn, THE SYSTEM SHALL trả về list hóa đơn xếp theo kỳ giảm dần, giới hạn bằng `tenant_id`.
- `testDoGet_ViewInvoiceDetail_Success`: KHI Tenant chọn 1 hóa đơn hợp lệ, THE SYSTEM SHALL hiển thị đầy đủ chi tiết (Tiền phòng, điện, nước, internet...).
- `testDoPost_CreatePaymentUrl_Success`: KHI Tenant ấn "Thanh toán VNPAY" cho hóa đơn UNPAID, THE SYSTEM SHALL gọi Service tạo URL VNPAY, đổi trạng thái thành PROCESSING, và trả về/redirect URL đó.
- `testDoPost_VnpayIpn_Success`: KHI VNPAY gọi IPN thành công (`vnp_ResponseCode=00`), THE SYSTEM SHALL xác thực `SecureHash`, đổi trạng thái hóa đơn thành `PAID` và ghi vào bảng `payments`.
- `testDoGet_PaymentHistory_Success`: KHI Tenant xem lịch sử giao dịch, THE SYSTEM SHALL trả về các giao dịch SUCCESS của Tenant.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_ViewInvoice_CrossTenant_Forbidden`: KHI xem hóa đơn của người khác, THE SYSTEM SHALL trả về HTTP 403.
- `testDoGet_ViewInvoice_NotFound`: KHI hóa đơn không tồn tại, THE SYSTEM SHALL trả về HTTP 404.
- `testDoPost_Payment_Conflict_AlreadyPaid`: KHI cố thanh toán một hóa đơn đã PAID hoặc PROCESSING, THE SYSTEM SHALL trả về HTTP 409 Conflict.
- `testDoPost_VnpayIpn_InvalidSignature`: KHI VNPAY IPN gửi sai `SecureHash`, THE SYSTEM SHALL Reject (Không cập nhật CSDL) và có thể trả về mã lỗi bảo mật.
- `testDoPost_VnpayIpn_AmountMismatch`: KHI số tiền từ VNPAY khác với hóa đơn, THE SYSTEM SHALL Reject.
- `testDoPost_VnpayIpn_FailedTransaction`: KHI VNPAY trả kết quả thất bại (khác `00`), THE SYSTEM SHALL cập nhật hóa đơn thành `FAILED`.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_VnpayIpn_Idempotency`: KHI IPN gửi liên tiếp 2 lần cùng 1 giao dịch thành công, THE SYSTEM SHALL bỏ qua lần 2 (trả về HTTP 200 cho VNPAY) mà không insert double vào bảng `payments`.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_Payment_RaceCondition`: Giả lập 2 Request nhấn nút "Thanh toán VNPAY" trên cùng 1 hóa đơn đồng thời. Chỉ có 1 request thành công đổi sang PROCESSING, request còn lại phải văng HTTP 409 Conflict.
- `testConcurrency_Ipn_RaceCondition`: Giả lập 2 Request IPN đẩy về cùng lúc. Đảm bảo Transaction (Rollback/Commit) giữ toàn vẹn DB.
