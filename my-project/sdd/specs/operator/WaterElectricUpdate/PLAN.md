# Kế hoạch Thực hiện - Cập nhật Điện Nước (WaterElectricUpdate)

## 1. Mục tiêu
Thiết kế trang Cập nhật chỉ số điện nước cho nhân viên Vận hành, cho phép nhập mã phòng, điền số mới và tải ảnh minh chứng lên. Dữ liệu sẽ được tự động lookup số điện/nước kỳ trước từ CSDL (thông qua mã phòng) để đối chiếu trực tiếp nhằm ngăn chặn lỗi nhập liệu.

## 2. Kiến trúc Backend (Java Servlet & DAO)
- **DAO (`MeterReadingDAO.java`):** Bổ sung hàm:
  - `MeterStatusDTO getPreviousReadingByRoomCode(String roomCode)`: Lấy thông tin phòng, ID phòng và các số điện nước của kỳ khai báo gần nhất.
- **Service (`MeterReadingService.java`):** Tích hợp việc gọi DAO để xử lý nghiệp vụ lookup.
- **Servlet (`UpdateMeterReadingServlet.java`):**
  - Mapped URL: `/operator/meter-readings/update`
  - Xử lý **GET**: Render giao diện form `update.jsp`.
  - Xử lý **POST**: Đón nhận `multipart/form-data`, lookup `roomCode` trong Database.
  - Xử lý Validate chặt chẽ:
    - **AC06**: Mã phòng không hợp lệ -> Báo lỗi không tồn tại phòng.
    - **AC02, AC03**: Số điện/nước mới nhỏ hơn số cũ -> Báo lỗi.
    - **AC04, AC05**: Xử lý lưu ảnh công tơ thực tế vào thư mục dự án và lưu đường dẫn.

## 3. Kiến trúc Frontend (JSP)
- **File:** `update.jsp` trong thư mục `/WEB-INF/views/operator/meter_readings/`
- **Layout:** Tuân thủ `.app-shell` và `.main-wrapper`.
- **Thành phần giao diện:**
  - Ô nhập `Mã phòng` (Có ghi chú giải thích hệ thống sẽ tự lookup).
  - Khối khai báo Chỉ số Điện (Ô nhập số điện mới, Nút tải ảnh minh chứng công tơ điện).
  - Khối khai báo Chỉ số Nước (Ô nhập số nước mới, Nút tải ảnh minh chứng công tơ nước).
  - Thanh Alert thông báo lỗi (Validation error từ Backend trả về).
