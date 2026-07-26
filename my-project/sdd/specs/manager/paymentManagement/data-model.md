# Data Model: Quản lý Thanh toán (Payment Management)

## 1. Entities & Data Structures

### `PaymentTransaction` (Model/Entity)
Ánh xạ bảng `dbo.payments`:
- `paymentId` (int): Mã giao dịch thanh toán (PK).
- `code` (String): Mã định danh giao dịch (e.g. `PAY-1721990000`).
- `invoiceId` (Integer): Mã hóa đơn liên kết (`invoices.invoice_id`).
- `roomId` (int): Mã phòng liên kết.
- `paymentAmount` (BigDecimal): Số tiền thanh toán.
- `paymentDate` (Date): Ngày thanh toán.
- `paymentMethod` (String): Phương thức (`VNPAY`, `BANK_TRANSFER`, `CASH`).
- `status` (String): Trạng thái (`PENDING`, `SUCCESS`, `REJECTED`).
- `createdBy` (Integer): Người khởi tạo giao dịch (`users.user_id`).

### `PaymentListItemDTO` & `PaymentDetailDTO`
- `transactionCode` (String).
- `amount` (BigDecimal).
- `paymentDate` (String).
- `paymentMethod` (String).
- `status` (String).
- `roomCode` (String).
- `tenantName` (String): Tên người thuê nộp tiền (Snapshot cố định từ `i.contract_id` / `p.created_by`).
- `tenantPhone`, `tenantEmail` (String).

## 2. Dynamic vs Snapshot Join Comparison
- **Cũ (Bị lỗi nhảy tên)**: `JOIN users ON COALESCE(r.tenant_id, (SELECT TOP 1 ... WHERE room_id = r.room_id))`
- **Mới (Chuẩn Cố định)**: `LEFT JOIN invoices i ON p.invoice_id = i.invoice_id LEFT JOIN users u ON COALESCE(p.created_by, i.tenant_id, (SELECT tenant_id FROM contracts WHERE contract_id = i.contract_id)) = u.user_id`
