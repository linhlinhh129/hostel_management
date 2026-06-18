# PLAN.md

## Mục tiêu
- Xây dựng chức năng quản lý hóa đơn cho Ban quản lý.
- Hỗ trợ xem danh sách hóa đơn, xem chi tiết, điều chỉnh hóa đơn, xuất hóa đơn PDF và phân quyền người dùng.
- Áp dụng các quy tắc tính toán tự động và bảo đảm tính toàn vẹn cho hóa đơn đã thanh toán.

## Phạm vi
- Danh sách hóa đơn: invoiceId, invoiceCode, roomCode, billingPeriod, totalAmount, dueDate, status.
- Chi tiết hóa đơn: roomFee, điện, nước, serviceFee, taxRate, taxAmount, totalAmount, dueDate, status.
- Điều chỉnh dữ liệu hợp lệ trước khi hóa đơn được thanh toán.
- Xuất hóa đơn PDF.
- Phân quyền Management Board.

## Giải pháp kỹ thuật
### Dữ liệu
- `Invoice`: id, code, roomId, billingPeriod, roomFee, oldElectricReading, newElectricReading, electricAmount, oldWaterReading, newWaterReading, waterAmount, serviceFee, taxRate, taxAmount, totalAmount, dueDate, status, updatedAt, updatedBy.
- `InvoiceStatus`: UNPAID, PAID, OVERDUE.

### Luồng xử lý
1. Lấy danh sách hóa đơn với query `keyword`, `roomCode`, `status`, `page`, `size`.
2. Trả về chi tiết hóa đơn theo `invoiceId`.
3. Cho phép điều chỉnh hóa đơn khi `status != PAID`.
4. Tính toán lại `electricAmount`, `waterAmount`, `taxAmount`, `totalAmount` sau khi cập nhật.
5. Xuất file PDF hóa đơn và trả về đường dẫn tải về.

### API đề xuất
- `GET /api/v1/invoices`
- `GET /api/v1/invoices/{invoiceId}`
- `PUT /api/v1/invoices/{invoiceId}`
- `GET /api/v1/invoices/{invoiceId}/export`

### Quy tắc nghiệp vụ
- Nếu hóa đơn đã thanh toán (`PAID`), không được phép điều chỉnh; trả về 400 `PAID_INVOICE_CANNOT_BE_UPDATED`.
- `newElectricReading >= oldElectricReading`.
- `newWaterReading >= oldWaterReading`.
- `dueDate >= currentDate` khi điều chỉnh.
- Thuế được tính tự động, không nhập trực tiếp `taxAmount`.

### Bảo mật
- Chỉ người dùng role `Management Board` được phép truy cập.
- Trả về 401 nếu chưa đăng nhập; 403 nếu sai role.

### Hiệu năng
- Danh sách và điều chỉnh: < 1000ms.
- Chi tiết hóa đơn: < 500ms.
- Xuất PDF: < 2000ms.

## Rủi ro
- Cập nhật dữ liệu sai dẫn đến tính toán tổng tiền sai.
- Việc xuất PDF có thể thất bại do lỗi file/permission.
- Trạng thái OVERDUE phải được đồng bộ nếu có cronjob bên ngoài.

## Giả định
- Module lập hóa đơn khác đã cung cấp đơn giá điện, nước, phí dịch vụ nếu cần.
- Trạng thái thanh toán được cập nhật từ module thanh toán ngoài hệ thống.
- PDF export lưu file ra đường dẫn ổn định.

## Tài liệu tham khảo
- `SPEC.md`
- `CONTEXT.md`
