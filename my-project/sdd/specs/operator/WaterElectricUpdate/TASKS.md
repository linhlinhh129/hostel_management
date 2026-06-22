# Danh sách Công việc - Cập nhật Điện Nước (WaterElectricUpdate)

## Backend
- [x] Tạo phương thức `getPreviousReadingByRoomCode` trong `MeterReadingDAO.java` để tra cứu số điện/nước cũ bằng `roomCode`.
- [x] Cập nhật phương thức `updateMeterReading` để có thể nhận thêm `roomId` truyền ngầm từ kết quả tra cứu mã phòng.
- [x] Sửa đổi `UpdateMeterReadingServlet.java` để thay vì dùng `roomId` ẩn, nay chuyển sang nhận `roomCode` trực tiếp từ giao diện và tự lookup trong Database.
- [x] Cài đặt validation kiểm tra AC02 (Số điện mới < số cũ), AC03 (Số nước mới < số cũ) và AC06 (Sai mã phòng).
- [x] Xử lý lưu file ảnh công tơ điện, công tơ nước vào thư mục `uploads/meters` và lưu đường dẫn vào CSDL.
- [x] Fix triệt để lỗi `ClassCastException` khi xác thực quyền Operator bằng `UserSessionDTO`.

## Frontend
- [x] Bổ sung link "Cập nhật điện nước" độc lập trên `sidebar.jsp` (Nằm ngay dưới mục Chỉ số điện nước).
- [x] Cải tổ trang danh sách (`list.jsp`): Xóa modal popup cập nhật cũ và thay thế nút thành `<a>` link chuyển hướng sang trang `update` kèm tham số `roomCode`.
- [x] Xây dựng trang giao diện mới `update.jsp` hoàn chỉnh với các trường:
  - Input mã phòng
  - Input số điện mới & Input upload file ảnh công tơ điện
  - Input số nước mới & Input upload file ảnh công tơ nước
- [x] Trang trí UI chuẩn Mintlify, đảm bảo hiển thị đúng cấu trúc thẻ `.app-shell`, `.main-wrapper` và các component thông báo lỗi (Alerts).
