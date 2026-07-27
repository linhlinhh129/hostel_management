# Quickstart & Integration Guide: Quản lý Hóa Đơn

## Runnable Test Scenario 1: Kiểm tra hiển thị Kỳ hợp đồng trong Chi tiết Hóa đơn

1. Đăng nhập với tài khoản **Manager**.
2. Truy cập Danh sách Hóa đơn (`GET /manager/invoices`).
3. Bấm nút **"Xem"** trên một hóa đơn bất kỳ để chuyển đến trang `GET /manager/invoices/{id}`.
4. Kiểm tra mục **Thông tin chung** phía bên phải.
5. **Kỳ vọng**:
   - Xuất hiện dòng **Kỳ hợp đồng** ngay bên dưới **Kỳ hóa đơn**.
   - Định dạng hiển thị: `dd/MM/yyyy - dd/MM/yyyy` (ví dụ `01/01/2026 - 31/12/2026`) hoặc *"Chưa có hợp đồng"*.

## Runnable Test Scenario 2: Kiểm tra Đóng băng phí phạt (Late Fee Freeze)

1. Đợi hoặc sửa CSDL để một hóa đơn chuyển sang trạng thái `OVERDUE`. Ghi nhận số tiền phí phạt muộn (ví dụ X).
2. Đăng nhập với tài khoản Tenant, tạo một giao dịch thanh toán cho hóa đơn đó (Payment status = `PENDING`).
3. Đợi sang ngày hôm sau hoặc sửa thời gian hiện tại tiến lên 1 ngày.
4. Đăng nhập lại với tài khoản Manager, truy cập chi tiết hóa đơn.
5. **Kỳ vọng**: Phí phạt muộn vẫn bằng X (không bị tăng thêm 1 ngày) do đã bị đóng băng tại thời điểm PENDING payment được tạo ra.
6. Nếu Manager `APPROVE` payment, hóa đơn chuyển sang `PAID` và phí phạt X được lưu cố định.
7. Nếu Manager `REJECT` payment, phí phạt lập tức tính bù lại khoảng thời gian bị đóng băng.
