# Quickstart & Integration Validation Guide

## Runnable Scenario 1: Mở chi tiết Hóa đơn từ nút "Xem" duy nhất

### Kịch bản 1: Mở trang Chi tiết Hóa đơn
1. Đăng nhập với tài khoản **Manager**.
2. Tại `GET /manager/invoices`, xác nhận cột thao tác của bảng danh sách **chỉ có duy nhất nút "Xem"**.
3. Bấm nút **"Xem"** để vào trang Chi tiết Hóa đơn `GET /manager/invoices/{id}`.
4. **Kỳ vọng**: Vào trang chi tiết thành công, xác nhận nút **"Báo cáo sai số"** xuất hiện và không còn nút "Xóa Hóa Đơn".

### Kịch bản 2: Báo cáo sai số và gửi Operator
1. Trong trang Chi tiết Hóa đơn, bấm **"Báo cáo sai số"**.
2. Điền/kiểm tra Tiêu đề và Nội dung mô tả sự cố nạp sẵn, chọn Operator và bấm **"Gửi Operator"**.
3. **Kỳ vọng**: Chuyển trạng thái chỉ số sang `REPORTED`, chèn yêu cầu `UTILITY` `PENDING` đẩy sang Module Danh sách Yêu cầu của Operator, redirect về `/manager/invoices` kèm thông báo thành công.
