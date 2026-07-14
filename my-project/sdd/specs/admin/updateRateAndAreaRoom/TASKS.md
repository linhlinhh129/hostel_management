# TASKS: Cập nhật thông tin phòng

**Status:** In Progress
**Priority:** Medium

## 1. Thiết kế & Cơ sở dữ liệu (Database)

- [ ] Cập nhật bảng `rooms` đảm bảo cột `area` (decimal) và `room_fee` (decimal) hỗ trợ NULL.
- [ ] Chỉnh sửa các Model/DTO liên quan đến `Room` để map đúng 2 trường `area` và `room_fee` bằng `BigDecimal`.

## 2. Data Access Object (DAO)

- [ ] Cập nhật hàm `RoomDAO.getRoomDetail(int roomId)` để select ra `area`, `room_fee`, và `facility_status` của cơ sở.
- [ ] Viết hàm `RoomDAO.updateAreaAndFee(int roomId, BigDecimal area, BigDecimal roomFee)` để thực hiện câu lệnh `UPDATE rooms SET area=?, room_fee=? WHERE id=?`.

## 3. Tầng Service (Business Logic)

- [ ] Cập nhật `RoomService.updateRoomAreaAndFee(int roomId, BigDecimal area, BigDecimal roomFee)`:
    - [ ] Kiểm tra phòng có tồn tại không.
    - [ ] Kiểm tra trạng thái cơ sở (facility status). Nếu `INACTIVE`, throw `ValidationException("Cơ sở đã bị vô hiệu hóa...")`.
    - [ ] Kiểm tra validation logic (area >= 0, roomFee >= 0). Nếu vi phạm, throw `ValidationException`.
    - [ ] Gọi `RoomDAO.updateAreaAndFee`.
    - [ ] Thêm ghi log hành động (Audit Log) (VD: "Cập nhật diện tích/giá cho phòng X").

## 4. Tầng Controller (Servlet)

- [ ] Cập nhật `AdminRoomServlet` để ánh xạ đường dẫn `POST /admin/rooms/{roomId}/update`.
- [ ] Xử lý request `POST`:
    - [ ] Trích xuất `roomId` từ URL path info.
    - [ ] Parse `area` và `roomFee` từ string sang `BigDecimal`. Xử lý empty string chuyển thành `null`. Bắt exception parse lỗi (throw `ValidationException`).
    - [ ] Gọi `RoomService.updateRoomAreaAndFee`.
    - [ ] Try/catch các `ValidationException` để redirect lại trang detail kèm Flash message `error`.
    - [ ] Nếu thành công, set Flash message `success` và redirect về `GET /admin/rooms/{roomId}`.

## 5. Tầng View (Frontend / JSP)

- [ ] Cập nhật file `/WEB-INF/views/admin/rooms/detail.jsp`:
    - [ ] Hiển thị thông tin `area` và `roomFee` hiện tại.
    - [ ] Thêm Form với method `POST` gửi đến action `/admin/rooms/{roomId}/update`.
    - [ ] Có các input cho Diện tích và Giá phòng.
    - [ ] Thêm logic JSTL (`<c:if test="${room.facilityStatus == 'INACTIVE'}">`) để ẩn hoặc disable form cập nhật, đồng thời hiển thị cảnh báo "Cơ sở đã bị vô hiệu hoá...".
    - [ ] Hiển thị vùng Flash message cho các thông báo `success` và `error`.

## 6. Testing & Validation

- [ ] Manual test cập nhật thành công với giá trị hợp lệ.
- [ ] Manual test xóa trắng thông tin (để rỗng input).
- [ ] Manual test nhập số âm, nhập chữ.
- [ ] Manual test vô hiệu hóa cơ sở, sau đó thử chỉnh sửa phòng (cả từ UI lẫn postman).
- [ ] Viết Unit Test cho Service về các logic kiểm tra trạng thái cơ sở và validation số âm.
