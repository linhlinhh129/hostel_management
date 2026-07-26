# Research: Quản lý Thanh toán (Payment Management Snapshot Binding)

## 1. Ràng buộc Định danh Cố định Người nộp tiền
- **Quyết định**: Sử dụng `i.contract_id` và `p.created_by` / `i.tenant_id` để JOIN dữ liệu người nộp tiền trong `PaymentDAO`.
- **Lý do**: Trước đây `PaymentDAO` dùng `COALESCE(r.tenant_id, (SELECT TOP 1 tenant_id FROM contracts WHERE room_id = r.room_id ...))`. Logic này truy vấn cư dân *hiện tại* của phòng, dẫn tới khi cư dân cũ thanh lý hợp đồng và trả phòng thì lịch sử giao dịch bị rỗng tên, hoặc khi cư dân mới dời vào thì toàn bộ giao dịch cũ bị gán nhầm cho cư dân mới.
- **Giải pháp**:
  ```sql
  LEFT JOIN invoices i ON p.invoice_id = i.invoice_id
  LEFT JOIN users u ON COALESCE(p.created_by, i.tenant_id, (SELECT tenant_id FROM contracts WHERE contract_id = i.contract_id)) = u.user_id
  ```

## 2. Quy tắc dọn dẹp mã nguồn thừa (Cleanup Unused Code)
- **Quyết định**: Xóa bỏ các hàm legacy/unused bao gồm `reportError()`, `reportIncorrectInvoice()`, `handleReportIncorrect()`.
- **Lý do**: Các hàm này thuộc luồng cũ và không còn bất kỳ giao diện hay controller nào gọi tới.
