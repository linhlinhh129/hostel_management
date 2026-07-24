# TASKS: Cập nhật thông tin phòng

**Status:** Completed
**Priority:** Medium

## 1. Thiết kế & Cơ sở dữ liệu (Database)

- [x] Cập nhật bảng `rooms` đảm bảo cột `area` (decimal) và `room_fee` (decimal) hỗ trợ NULL.
- [x] Chỉnh sửa các Model/DTO liên quan đến `Room` để map đúng 2 trường `area` và `room_fee` bằng `BigDecimal`.

## 2. Data Access Object (DAO)

- [x] Cập nhật hàm `RoomDAO.findDetailForAdmin(int roomId)` để select ra `area`, `room_fee`, và `facility_status` của cơ sở.
- [x] Viết hàm `RoomDAO.updateAreaAndFee(int roomId, BigDecimal area, BigDecimal roomFee)` để thực hiện câu lệnh `UPDATE rooms SET area=?, room_fee=? WHERE room_id=? AND deleted_at IS NULL`.

## 3. Tầng Service (Business Logic)

- [x] Cập nhật `RoomService.updateAreaAndFee(int roomId, String areaStr, String feeStr)`:
    - [x] Kiểm tra phòng có tồn tại không.
    - [x] Kiểm tra trạng thái cơ sở (facility status). Nếu `INACTIVE`, throw `ValidationException("Cơ sở đã bị vô hiệu hóa...")`.
    - [x] Kiểm tra validation logic (định dạng, không được âm, rỗng đổi thành null) thông qua hàm helper `parsePositiveDecimal`. Nếu vi phạm, throw `ValidationException`.
    - [x] Gọi `RoomDAO.updateAreaAndFee`.
    - [ ] Thêm ghi log hành động (Audit Log) (nếu cần thiết kế sau).

## 4. Tầng Controller (Servlet)

- [x] Cập nhật `AdminRoomServlet` để ánh xạ đường dẫn `POST /admin/rooms/{roomId}/update`.
- [x] Xử lý request `POST`:
    - [x] Trích xuất `roomId` từ URL path info.
    - [x] Gọi `RoomService.updateAreaAndFee` với tham số chuỗi thô từ `req.getParameter`.
    - [x] Try/catch các `ValidationException` để redirect lại trang detail kèm Flash message `error`.
    - [x] Nếu thành công, set Flash message `success` và redirect về `GET /admin/rooms/{roomId}` (Post-Redirect-Get).

## 5. Tầng View (Frontend / JSP)

- [x] Cập nhật file `/WEB-INF/views/admin/rooms/detail.jsp`:
    - [x] Hiển thị thông tin `area` và `roomFee` hiện tại.
    - [x] Thêm Form với method `POST` gửi đến action `${ctx}/admin/rooms/${room.id}/update`.
    - [x] Có các input cho Diện tích và Giá phòng.
    - [x] Thêm logic JSTL (`<c:if test="${room.facilityStatus == 'INACTIVE'}">`) để ẩn hoặc disable form cập nhật, đồng thời hiển thị cảnh báo "Cơ sở đã bị vô hiệu hoá...".
    - [x] Hiển thị vùng Flash message cho các thông báo `success` và `error`.

## 6. Testing & Validation

- [x] Manual test cập nhật thành công với giá trị hợp lệ.
- [x] Manual test xóa trắng thông tin (để rỗng input).
- [x] Manual test nhập số âm, nhập chữ.
- [x] Manual test vô hiệu hóa cơ sở, sau đó thử chỉnh sửa phòng.
- [ ] Viết Unit Test cho Service về các logic kiểm tra trạng thái cơ sở và validation số âm (nếu cần mở rộng).

