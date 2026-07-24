# Kế hoạch Tạo trang "Lịch sử cập nhật điện nước" riêng biệt

Theo yêu cầu mới, chúng ta sẽ tách bạch 2 tính năng để Kỹ thuật viên (Operator) dễ làm việc:
1. **Danh sách điện nước**: Chỉ hiển thị của tháng hiện tại, có nút Cập nhật/Sửa để điền số liệu. (Giữ nguyên gốc)
2. **Lịch sử cập nhật điện nước**: Trang mới hoàn toàn, dùng để xem lại lịch sử các tháng cũ, CÓ bộ lọc Tháng/Năm, CHỈ ĐỂ XEM (không có nút Sửa/Cập nhật để tránh sửa nhầm dữ liệu tháng cũ).

## Các bước thực hiện:

### 1. Phục hồi "Danh sách điện nước" (Actionable List)
- Xóa bỏ bộ lọc Tháng/Năm ở trang `list.jsp` hiện tại để trả nó về đúng nghĩa là "Kỳ đo đạc hiện tại" (chỉ làm việc cho tháng này).
- Trả `ListElectricServlet.java` về logic ban đầu (chỉ lấy tháng/năm hiện tại).

### 2. Tạo Servlet mới cho "Lịch sử"
#### [NEW] `src/main/java/com/quanlyphongtro/controller/operator/MeterReadingHistoryServlet.java`
- Khởi tạo Servlet ánh xạ với đường dẫn `/operator/meter-readings/history`.
- Logic: Nhận tham số `month` và `year` từ thanh tìm kiếm. Lấy danh sách số điện nước qua `MeterReadingService`.
- Trả dữ liệu về giao diện mới.

### 3. Tạo Giao diện (JSP) mới cho "Lịch sử"
#### [NEW] `src/main/webapp/WEB-INF/views/operator/meter_readings/history.jsp`
- Tạo giao diện sao chép từ danh sách cũ nhưng có các thay đổi:
  - Có đầy đủ bộ lọc chọn Tháng và chọn Năm.
  - Trạng thái chỉ dùng để quan sát xem phòng đó tháng ấy đã chốt số hay chưa.
  - Có thêm cột **Thao tác** với nút **Chi tiết** cho các bản ghi đã cập nhật. Khi ấn vào, sẽ mở Modal (Popup) hiển thị ảnh công tơ điện/nước, số lượng tiêu thụ, và người đã cập nhật.

### 4. Cập nhật Sidebar Menu
#### [MODIFY] `src/main/webapp/WEB-INF/views/layout/sidebar.jsp`
- Giữ nguyên "Danh sách điện nước".
- Thêm 1 menu hoàn toàn mới: **Lịch sử điện nước** (có icon dạng lịch sử/đồng hồ).

## Xác nhận từ User
> [!IMPORTANT]
> Đây là một tính năng hoàn chỉnh và rõ ràng nhất, đáp ứng chính xác việc vừa có nơi chuyên "Nhập số" vừa có nơi chuyên "Xem lịch sử".
> Bạn xem qua Kế hoạch ở trên, nếu ưng ý 100% thì hãy báo "Duyệt" để mình code ngay nhé!
