# Kế hoạch phát triển tính năng Danh sách chỉ số điện nước (ListElectric)

## 1. Mục tiêu
Xây dựng trang xem danh sách tình trạng chốt số điện nước của tất cả các phòng trong tháng hiện tại. Giúp Operator biết được phòng nào đã chốt số (có dữ liệu tháng này) và phòng nào chưa (CHUA_CAP_NHAT). Màn hình chỉ đọc (Read-only), không cho phép chỉnh sửa.

## 2. Thiết kế Giao diện (Frontend)
- **File:** `src/main/webapp/WEB-INF/views/operator/meter_readings/list.jsp`
- **Phong cách:** Mintlify `DESIGN.md`.
  - Hiển thị dưới dạng Bảng (Table) gọn gàng.
  - Các cột: Mã phòng, Số điện kỳ trước, Số nước kỳ trước, Thời gian cập nhật (tháng này), Trạng thái.
  - Cột Trạng thái sử dụng thẻ badge màu sắc: Xanh lá (Đã cập nhật), Vàng/Cam (Chưa cập nhật).
  - Không có nút thêm/sửa/xóa, không có input text.

## 3. Thiết kế Hệ thống (Backend)
- **DTO:** `com.quanlyphongtro.dto.MeterStatusDTO`
  - Chứa: `roomCode`, `previousElectricReading`, `previousWaterReading`, `updatedAt`, `status`.
- **DAO:** `com.quanlyphongtro.dao.MeterReadingDAO`
  - Hàm `getMeterStatusList(int currentMonth, int currentYear)`: Sử dụng câu truy vấn kết hợp (LEFT JOIN và OUTER APPLY hoặc subqueries) để lấy ra tất cả phòng ĐANG HOẠT ĐỘNG, và tìm bản ghi điện nước kỳ trước + kiểm tra bản ghi kỳ này.
- **Service:** `com.quanlyphongtro.service.MeterReadingService`
  - Gọi DAO và trả về List DTO.
- **Servlet:** `com.quanlyphongtro.controller.operator.ListElectricServlet`
  - Ánh xạ URL: `/operator/meter-readings`
  - Gọi Service để lấy List, chuyển attribute `meterList` sang `list.jsp`.

## 4. Xử lý logic kỳ trước / kỳ này
Sử dụng SQL Server Query để map:
- Nếu tồn tại bản ghi trong `meter_readings` có `MONTH(reading_date) == currentMonth` thì `status = DA_CAP_NHAT`, ngược lại là `CHUA_CAP_NHAT`.
- Số điện/nước kỳ trước được lấy bằng cách tìm bản ghi có `reading_date` lớn nhất nhưng nhỏ hơn tháng hiện tại.
