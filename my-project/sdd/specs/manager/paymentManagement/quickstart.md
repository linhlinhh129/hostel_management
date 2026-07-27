# Quickstart & Verification Guide: Quản lý Thanh toán

## Kịch bản Kiểm thử Nghiệm thu (Verification Scenarios)

### Kịch bản 1: Kiểm thử Định danh Cố định Người nộp tiền khi Cư dân Trả phòng
1. Chọn 1 Hóa đơn đang có Cư dân A thuê phòng.
2. Thực hiện Tạo giao dịch thanh toán (`payments`) cho hóa đơn này.
3. Chuyển trạng thái hợp đồng của Cư dân A sang `INACTIVE` (hoặc làm Cư dân A trả phòng).
4. Truy cập màn hình Danh sách Thanh toán (`/manager/payments`) và Chi tiết Thanh toán (`/manager/payments/{id}`).
5. **Kỳ vọng**: Tên cư dân A, SĐT, Email vẫn hiển thị ĐẦY ĐỦ 100% trong lịch sử giao dịch thanh toán, không bị rỗng và không bị mất thông tin.

### Kịch bản 2: Kiểm thử Định danh Cố định khi có Cư dân B Mới dời vào
1. Tiếp tục từ Kịch bản 1, tạo Hợp đồng mới cho Cư dân B dời vào đúng phòng đó.
2. Truy cập lại Giao dịch thanh toán cũ của Cư dân A.
3. **Kỳ vọng**: Giao dịch thanh toán cũ vẫn hiển thị tên Cư dân A (không bị nhảy sang Cư dân B).
