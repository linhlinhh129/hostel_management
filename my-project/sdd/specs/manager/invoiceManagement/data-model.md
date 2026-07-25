# Data Model: Quản lý hóa đơn

## 1. Entities & Data Structures

### `Invoice` (Model/Entity)
Đại diện cho hóa đơn lưu trong bảng `invoices`.
- **Fields**:
  - `id` (int): Mã hóa đơn (PK).
  - `roomId` (int): Liên kết phòng.
  - `managerId` (int): ID người tạo hóa đơn.
  - `invoiceMonth`, `invoiceYear` (int): Kỳ hóa đơn.
  - `dueDate` (Date): Hạn thanh toán.
  - `roomRate` (BigDecimal): Tiền phòng.
  - `electricOld`, `electricNew` (int): Chỉ số điện.
  - `electricRate` (BigDecimal): Đơn giá điện.
  - `waterOld`, `waterNew` (int): Chỉ số nước.
  - `waterRate` (BigDecimal): Đơn giá nước.
  - `serviceFee`, `internetFee`, `otherFee` (BigDecimal): Các loại phí khác.
  - `totalAmount` (BigDecimal): Tổng tiền.
  - `status` (String): `UNPAID`, `PAID`, `OVERDUE`.
  - `note` (String).

### `InvoiceDetailDTO` (Data Transfer Object)
Dùng để hiển thị dữ liệu chi tiết hóa đơn.
- **Fields bổ sung**:
  - `contractPeriod` (String): Định dạng `dd/MM/yyyy - dd/MM/yyyy` (lấy từ hợp đồng).
  - Tên phòng, người thuê.

## 2. Thay đổi so với thiết kế cũ
- Đã loại bỏ trường `taxRate` và `taxAmount` khỏi tất cả các Object/DTO.
- Tổng tiền (`totalAmount`) được tính trực tiếp từ các khoản phí mà không cộng thêm bất kỳ khoản Thuế/VAT nào.
- Logic tính phí quá hạn sử dụng `pending_payment_date` được nạp trong DAO để đóng băng mốc thời gian quá hạn nếu có một payment đang chờ duyệt (PENDING) liên kết tới Invoice.
