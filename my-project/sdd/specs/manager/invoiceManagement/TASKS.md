# TASKS.md

## 1. Khảo sát và thiết kế
1.1. Xác nhận yêu cầu với `SPEC.md` và `CONTEXT.md`.
1.2. Thiết kế mô hình `Invoice` và trạng thái thanh toán.
1.3. Xác định luật tính toán `subtotal`, `taxAmount`, `totalAmount`.

## 2. Xây dựng API danh sách hóa đơn
2.1. Implement endpoint `GET /api/v1/invoices`.
2.2. Hỗ trợ tìm kiếm theo `keyword`, `roomCode` và lọc `status`.
2.3. Thêm phân trang `page`/`size`.
2.4. Xử lý trường hợp không có hóa đơn.

## 3. Xây dựng API chi tiết hóa đơn
3.1. Implement endpoint `GET /api/v1/invoices/{invoiceId}`.
3.2. Trả về các trường chi tiết theo yêu cầu.
3.3. Trả về 404 `INVOICE_NOT_FOUND` khi không tồn tại.

## 4. Xây dựng API điều chỉnh hóa đơn
4.1. Implement endpoint `PUT /api/v1/invoices/{invoiceId}`.
4.2. Validate các ràng buộc: `newElectricReading >= oldElectricReading`, `newWaterReading >= oldWaterReading`, `dueDate >= currentDate`.
4.3. Tính lại `electricAmount`, `waterAmount`, `taxAmount`, `totalAmount`.
4.4. Chặn cập nhật nếu `status == PAID`, trả về lỗi `PAID_INVOICE_CANNOT_BE_UPDATED`.
4.5. Trả về 200 với `updatedAt` và `updatedBy`.

## 5. Xây dựng API xuất hóa đơn
5.1. Implement endpoint `GET /api/v1/invoices/{invoiceId}/export`.
5.2. Tạo file PDF hợp lệ.
5.3. Trả về `fileName` và `downloadUrl`.
5.4. Xử lý lỗi `INVOICE_EXPORT_FAILED` khi xuất thất bại.

## 6. Bảo mật
6.1. Thêm kiểm tra xác thực và role `Management Board` cho tất cả endpoint.
6.2. Kiểm thử trường hợp 401/403.

## 7. UI/UX
7.1. Cập nhật màn hình danh sách hóa đơn.
7.2. Cập nhật màn hình chi tiết hóa đơn.
7.3. Cập nhật chức năng điều chỉnh hóa đơn với form validation.
7.4. Thêm nút xuất PDF và xử lý tải file.

## 8. Kiểm thử
8.1. Viết unit test cho validation và logic tính toán.
8.2. Viết integration test cho tất cả endpoint.
8.3. Kiểm thử các trường hợp lỗi 400, 401, 403, 404.
8.4. Kiểm tra hiệu năng xuất PDF.

## 9. Tài liệu và bàn giao
9.1. Cập nhật tài liệu API nếu cần.
9.2. Ghi chú các giả định và câu hỏi mở.
9.3. Chuẩn bị review cho stakeholder.
