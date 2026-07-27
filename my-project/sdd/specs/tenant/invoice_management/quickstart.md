# Quickstart & Test Verification Guide: Tenant Invoice Meter Photos

## Scenario 1: Tenant xem chi tiết Hóa đơn có đầy đủ ảnh điện nước
1. Đăng nhập tài khoản Tenant.
2. Truy cập Màn hình Chi tiết Hóa đơn: `http://localhost:8080/hostel-management/tenant/invoice-detail?id={id}`
3. Cuộn xuống khu vực thông tin tính tiền.
4. **Kết quả mong đợi**:
   - Hiển thị khối **"Ảnh chỉ số điện nước"** thiết kế chuẩn thẻ Card đồng bộ với giao diện Manager.
   - Thẻ "Ảnh công tơ điện" hiển thị hình ảnh điện thực tế có hiệu ứng di chuột phóng to nhẹ (`scale(1.02)`).
   - Thẻ "Ảnh công tơ nước" hiển thị hình ảnh nước thực tế.
   - Click vào ảnh mở ra tab ảnh gốc chất lượng cao.

## Scenario 2: Hóa đơn không có ảnh điện nước (Chốt thủ công)
1. Đăng nhập tài khoản Tenant.
2. Mở Hóa đơn chốt thủ công không tải ảnh.
3. **Kết quả mong đợi**:
   - Khối ảnh tự động ẩn đi hoặc hiển thị nhãn "Không có ảnh minh chứng" một cách tự nhiên, không bị vỡ giao diện.
