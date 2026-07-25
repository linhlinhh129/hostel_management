# Quickstart & Integration Guide: Hiển thị Kỳ hợp đồng

## Runnable Test Scenario: Kiểm tra hiển thị Kỳ hợp đồng trong Chi tiết Hóa đơn

1. Đăng nhập với tài khoản **Manager**.
2. Truy cập Danh sách Hóa đơn (`GET /manager/invoices`).
3. Bấm nút **"Xem"** trên một hóa đơn bất kỳ để chuyển đến trang `GET /manager/invoices/{id}`.
4. Kiểm tra mục **Thông tin chung** phía bên phải.
5. **Kỳ vọng**:
   - Xuất hiện dòng **Kỳ hợp đồng** ngay bên dưới **Kỳ hóa đơn**.
   - Định dạng hiển thị: `dd/MM/yyyy - dd/MM/yyyy` (ví dụ `01/01/2026 - 31/12/2026`) hoặc *"Chưa có hợp đồng"*.
