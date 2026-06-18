# TASKS.md

## 1. Khảo sát và chuẩn bị
1.1. Đọc kỹ `SPEC.md` và `CONTEXT.md`.
1.2. Thiết kế mô hình `PaymentTransaction` và trạng thái `PENDING`/`PAID`.
1.3. Xác định các quan hệ với `Debt`.

## 2. Xây dựng API danh sách giao dịch
2.1. Implement endpoint `GET /api/v1/payments`.
2.2. Hỗ trợ query `keyword`, `status`, `page`, `size`.
2.3. Trả về danh sách rỗng nếu không có giao dịch.

## 3. Xây dựng API chi tiết giao dịch
3.1. Implement endpoint `GET /api/v1/payments/{transactionId}`.
3.2. Trả về `paymentProofUrl`.
3.3. Xử lý 404 `PAYMENT_PROOF_NOT_FOUND` nếu proof không tồn tại.

## 4. Xây dựng API duyệt giao dịch
4.1. Implement endpoint `POST /api/v1/payments/{transactionId}/approve`.
4.2. Validate transaction tồn tại.
4.3. Validate status hiện tại là `PENDING`.
4.4. Update status sang `PAID`, set `approvedAt`, `approvedBy`.
4.5. Đồng bộ update `Debt.status` sang `PAID`.

## 5. Bảo mật và phân quyền
5.1. Thêm xác thực role `Management Board` cho mọi endpoint.
5.2. Kiểm thử các trường hợp 401 và 403.

## 6. UI/UX
6.1. Cập nhật màn hình danh sách giao dịch.
6.2. Thêm màn hình hiển thị chi tiết, ảnh xác nhận thanh toán.
6.3. Thêm nút duyệt giao dịch với xác nhận.

## 7. Kiểm thử
7.1. Viết unit test cho validation và business logic.
7.2. Viết integration test cho endpoint GET/POST.
7.3. Kiểm thử các lỗi `TRANSACTION_NOT_FOUND`, `PAYMENT_ALREADY_APPROVED`, `UNAUTHORIZED`, `FORBIDDEN`.

## 8. Tài liệu và bàn giao
8.1. Cập nhật tài liệu API nếu cần.
8.2. Nêu rõ các giả định và câu hỏi mở.
