# Test Specification: Quản lý thanh toán (Payment Management)

**File bị ảnh hưởng**: `PaymentServletTest.java`, `PaymentDetailServletTest.java`
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào luồng chuyển đổi trạng thái giao dịch (State Machine) và Transaction toàn vẹn.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewPaymentList_Success`: KHI truy cập danh sách thanh toán, THE SYSTEM SHALL hiển thị đầy đủ thông tin, tích hợp phân trang.
- `testDoGet_ViewPaymentDetail_Success`: KHI truy cập chi tiết thanh toán, THE SYSTEM SHALL hiển thị thông tin kèm theo hóa đơn liên quan.
- `testDoPost_ApprovePayment_Success`: KHI duyệt giao dịch `PENDING`, THE SYSTEM SHALL cập nhật giao dịch thành `SUCCESS` VÀ hóa đơn thành `PAID` (trong cùng Transaction).
- `testDoPost_RejectPayment_Success`: KHI từ chối giao dịch `PENDING`, THE SYSTEM SHALL cập nhật trạng thái thành `REJECTED`.
- `testDoPost_ReApproveRejectedPayment_Success`: (Edge Case) KHI duyệt lại một giao dịch đã bị `REJECTED` trước đó, THE SYSTEM SHALL cập nhật thành công sang `SUCCESS`.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_ApproveNonExistentPayment_Fails`: KHI duyệt giao dịch không tồn tại, THE SYSTEM SHALL trả về HTTP 404 `TRANSACTION_NOT_FOUND`.
- `testDoPost_ApproveAlreadyApprovedPayment_Fails`: KHI duyệt lại giao dịch đã `SUCCESS`, THE SYSTEM SHALL trả về HTTP 400 `PAYMENT_ALREADY_APPROVED`.
- `testDoPost_RejectAlreadyApprovedPayment_Fails`: KHI từ chối giao dịch đã `SUCCESS`, THE SYSTEM SHALL báo lỗi không được phép.
- `testUnauthorizedAccess`: KHI người dùng có role `TENANT` truy cập URL duyệt thanh toán, THE SYSTEM SHALL trả về HTTP 403. KHI chưa đăng nhập, trả về 401.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_ApprovePayment_AmountExactlyMatchesInvoice`: KHI số tiền thanh toán (Payment Amount) bằng chính xác Tổng tiền hóa đơn, THE SYSTEM SHALL duyệt thành công. 

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_DoubleApprove_RaceCondition`: Giả lập 2 Manager cùng nhấn nút "Duyệt" cho một giao dịch `PENDING` cùng một lúc. THE SYSTEM SHALL chỉ cho phép 1 Manager duyệt thành công, người thứ hai nhận lỗi `PAYMENT_ALREADY_APPROVED` để ngăn hóa đơn bị trừ/cập nhật hai lần.
- `testConcurrency_ApproveAndReject_RaceCondition`: Giả lập 1 Manager nhấn Duyệt, 1 Manager khác nhấn Từ chối. Cơ chế Transaction phải đảm bảo State Machine cuối cùng giữ vững tính nhất quán (không bị dead-lock).
