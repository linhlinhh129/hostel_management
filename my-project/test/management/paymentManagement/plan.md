# Implementation Plan: Payment Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `PaymentServlet.java`, `PaymentDetailServlet.java`
- **Dependencies**: `PaymentService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào State Machine (PENDING -> SUCCESS / REJECTED) và các Race Conditions.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/PaymentServletTest.java`
- `src/test/java/com/quanlyphongtro/controller/manager/PaymentDetailServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewPaymentList_Success`: Xem danh sách thành công.
- `testDoGet_ViewPaymentDetail_Success`: Xem chi tiết thành công.
- `testDoPost_ApprovePayment_Success`: Duyệt thanh toán -> SUCCESS & Hóa đơn PAID.
- `testDoPost_RejectPayment_Success`: Từ chối thanh toán -> REJECTED.
- `testDoPost_ReApproveRejectedPayment_Success`: Duyệt lại giao dịch REJECTED -> SUCCESS.

### 3.2 Error Cases
- `testDoPost_ApproveNonExistentPayment_Fails`: Duyệt ID ảo -> 404.
- `testDoPost_ApproveAlreadyApprovedPayment_Fails`: Duyệt lại cái đã duyệt -> 400.
- `testDoPost_RejectAlreadyApprovedPayment_Fails`: Từ chối cái đã duyệt -> 400.
- `testUnauthorizedAccess`: Role TENANT truy cập -> 403.

### 3.3 Boundary Values
- `testDoPost_ApprovePayment_AmountExactlyMatchesInvoice`: Số tiền thanh toán khớp chính xác 100% với tổng hóa đơn.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleApprove_RaceCondition`: 2 Manager cùng bấm Duyệt 1 giao dịch.
- `testConcurrency_ApproveAndReject_RaceCondition`: 1 Manager bấm Duyệt, 1 Manager bấm Từ chối cùng lúc.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `PaymentServlet` và `PaymentDetailServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
