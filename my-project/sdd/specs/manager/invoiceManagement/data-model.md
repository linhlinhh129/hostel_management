# Data Model: Quản lý hóa đơn & Giao dịch thanh toán

## 1. Entities & Data Structures

### `Invoice` (Model/Entity)
Đại diện cho hóa đơn lưu trong bảng `invoices`.
- **Fields**:
  - `id` (int): Mã hóa đơn (PK).
  - `code` (String): Mã định danh hóa đơn (e.g. `INV-CG0101-202607`).
  - `roomId` (int): Liên kết phòng.
  - `contractId` (Integer): Snapshot liên kết Hợp đồng (`contracts.contract_id`). Gắn cố định khi tạo hóa đơn.
  - `tenantId` (Integer): Snapshot liên kết Người thuê (`users.user_id`). Gắn cố định khi tạo hóa đơn.
  - `meterId` (Integer): Liên kết chỉ số điện nước.
  - `dueDate` (Date): Hạn thanh toán.
  - `roomFee` (BigDecimal): Tiền phòng.
  - `electricityPrice`, `waterPrice`, `serviceFee`, `internetFee`, `otherFee`, `lateFee` (BigDecimal): Các loại phí.
  - `totalAmount` (BigDecimal): Tổng tiền.
  - `status` (String): `UNPAID`, `PAID`, `OVERDUE`.
  - `note` (String).

### `InvoiceDetailDTO` (Data Transfer Object)
Dùng để hiển thị dữ liệu chi tiết hóa đơn.
- **Fields bổ sung**:
  - `contractId` (Integer), `contractCode` (String).
  - `tenantId` (Integer), `tenantName` (String), `tenantPhone` (String), `tenantEmail` (String).
  - `contractPeriod` (String): Định dạng `dd/MM/yyyy - dd/MM/yyyy` (lấy từ `contracts`).

## 2. Quy tắc Snapshot Định danh Người thuê (Snapshot Binding Rules)
- `contract_id` và `tenant_id` được ghi nhận vĩnh viễn vào `invoices` tại thời điểm tạo hóa đơn.
- Khi truy vấn `findById` và `findInvoices`, hệ thống `LEFT JOIN contracts c ON i.contract_id = c.contract_id` và `LEFT JOIN users u ON i.tenant_id = u.user_id`.
- Đảm bảo thông tin người thuê ban đầu KHÔNG BỊ TRỐNG RỖNG và KHÔNG BỊ NHẢY TÊN khi cư dân trả phòng (`INACTIVE` / soft-deleted) hoặc có cư dân mới vào ở.
