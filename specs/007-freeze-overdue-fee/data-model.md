# Data Model: Freeze Overdue Fee

Không tạo bảng mới. Tính năng này sửa đổi cách xử lý dữ liệu dựa trên schema hiện tại.

## Entities

- **Invoice**: Bảng `invoices`. Thể hiện một hóa đơn của phòng.
- **Payment**: Bảng `payments`. Thể hiện một giao dịch thanh toán cho hóa đơn. Các trạng thái quan trọng: `PENDING` (chờ duyệt), `SUCCESS` (được duyệt), `REJECTED` (bị từ chối).

## Logic Data Flow

1. Khi tính phí nợ trong DAO (DebtDAO, InvoiceDAO), hệ thống kiểm tra bảng `payments` để tìm ngày của giao dịch PENDING mới nhất thuộc hóa đơn.
2. Nếu có (tức là tenant đã tạo giao dịch chờ duyệt), hệ thống sử dụng ngày tạo payment đó làm mốc `endDate`.
3. Số ngày quá hạn = `endDate - dueDate`.
4. Nếu quản lý chấp nhận payment, trạng thái đổi thành SUCCESS, phí phạt đang đóng băng sẽ được lưu lại theo code cập nhật `late_fee` vào database.
5. Nếu quản lý từ chối, trạng thái đổi thành REJECTED. Query lấy PENDING payment không còn thỏa mãn, hệ thống trở về tính `endDate` = `LocalDate.now()`.
