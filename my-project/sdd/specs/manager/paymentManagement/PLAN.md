# PLAN.md

## Mục tiêu
- Xây dựng chức năng quản lý giao dịch thanh toán để Ban quản lý kiểm tra, đối soát và duyệt giao dịch.
- Hỗ trợ xem danh sách giao dịch, xem ảnh xác nhận thanh toán, duyệt giao dịch và đồng bộ trạng thái công nợ.

## Phạm vi
- Danh sách giao dịch thanh toán với các trường: transactionId, transactionCode, tenantName, roomCode, amount, paymentDate, paymentMethod, status.
- Chi tiết giao dịch hiển thị ảnh xác nhận thanh toán.
- Duyệt giao dịch thành công và cập nhật trạng thái công nợ liên quan.
- Xử lý giao dịch không tồn tại, proof không tồn tại, giao dịch đã duyệt.
- Phân quyền cho role Management Board.

## Giải pháp kỹ thuật
### Dữ liệu
- `PaymentTransaction`: id, transactionCode, tenantId, roomId, debtId, amount, paymentDate, paymentMethod, paymentProofUrl, status, approvedAt, approvedBy.
- `PaymentStatus`: PENDING, PAID.

### Luồng xử lý
1. Lấy danh sách giao dịch với tìm kiếm keyword, lọc status, phân trang.
2. Lấy chi tiết giao dịch hiển thị đầy đủ, bao gồm `paymentProofUrl`.
3. Duyệt giao dịch: chỉ chấp nhận `PENDING`, chuyển trạng thái sang `PAID`, ghi `approvedAt` và `approvedBy`.
4. Đồng bộ `Debt.status` của khoản nợ liên quan sang `PAID` khi giao dịch được duyệt.

### API đề xuất
- `GET /api/v1/payments`
- `GET /api/v1/payments/{transactionId}`
- `POST /api/v1/payments/{transactionId}/approve`

### Quy tắc nghiệp vụ
- `paymentProofUrl` phải tồn tại; nếu không, trả về 404 `PAYMENT_PROOF_NOT_FOUND`.
- Giao dịch đã duyệt không được duyệt lại; trả về 400 `PAYMENT_ALREADY_APPROVED`.
- Giao dịch không tồn tại trả về 404 `TRANSACTION_NOT_FOUND`.
- Khi duyệt thành công, cập nhật `Debt` tương ứng.

### Bảo mật
- Chỉ người dùng role `Management Board` được phép xem và duyệt giao dịch.
- Trả về 401 khi chưa xác thực và 403 khi sai role.

### Hiệu năng
- API danh sách và duyệt giao dịch phải phản hồi < 1 giây.
- API chi tiết giao dịch phải phản hồi < 500ms.

## Rủi ro
- Đồng bộ trạng thái công nợ thất bại nếu không kiểm soát transaction/rollback.
- Proof bị mất hoặc đường dẫn ảnh không hợp lệ.
- Duyệt nhầm giao dịch do thiếu xác thực role.

## Giả định
- Ảnh proof đã được tải lên và chỉ cần hiển thị URL.
- Số tiền giao dịch khớp với giá trị công nợ liên quan.
- Giao dịch thanh toán không hỗ trợ tách nhỏ nhiều phần.

## Tài liệu tham khảo
- `SPEC.md`
- `CONTEXT.md`
