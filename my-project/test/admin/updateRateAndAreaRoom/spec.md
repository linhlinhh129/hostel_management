# Test Specification: Cập nhật thông tin phòng (Update Room Rate & Area - Unit Test Only)

**Status:** Draft
**Target Feature:** Admin Update Room (`my-project/sdd/specs/admin/updateRateAndAreaRoom`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Tuyệt đối không kết nối DB thật, mọi đối tượng phụ thuộc (RoomDAO, FacilityDAO, v.v.) phải được cô lập bằng Mock.
Mục tiêu là đảm bảo Servlet/API kiểm soát tốt dữ liệu đầu vào (Decimal/Numeric), phân quyền hợp lý, và bảo vệ tính vẹn toàn dữ liệu khi cơ sở (Facility) đã ngừng hoạt động (INACTIVE).

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem chi tiết phòng**: KHI Admin GET chi tiết phòng hợp lệ (`/admin/rooms/{roomId}`), HỆ THỐNG PHẢI gọi Mock DAO trả về thông tin phòng và forward tới JSP.
- **Cập nhật số dương**: KHI Admin POST dữ liệu diện tích và giá phòng là số dương hợp lệ, HỆ THỐNG PHẢI map thành `BigDecimal` và cập nhật thành công (Redirect + Flash message).
- **Xóa thông tin (Để trống)**: KHI Admin POST form với ô diện tích hoặc giá phòng bỏ trống (`""`), HỆ THỐNG PHẢI chuyển đổi giá trị thành `null` và lưu thành công xuống DAO.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Cơ sở bị vô hiệu hóa (INACTIVE)**: KHI Admin cố POST form cập nhật cho một phòng thuộc cơ sở đang ở trạng thái `INACTIVE`, HỆ THỐNG PHẢI ném `ValidationException("Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng.")` và không gọi thao tác lưu.
- **Dữ liệu âm hoặc sai định dạng**: KHI nhập số âm (vd: `-50.5`) hoặc ký tự chữ cái (vd: `abc`), HỆ THỐNG PHẢI bắt Validation và báo lỗi `Diện tích/Giá phòng không hợp lệ/không được âm`.
- **Phòng không tồn tại**: KHI truy cập hoặc sửa phòng không tồn tại, HỆ THỐNG PHẢI ném `NotFoundException` (HTTP 404).
- **Phân quyền**: KHI user không phải ADMIN hoặc chưa đăng nhập -> `FORBIDDEN` (403) / `UNAUTHORIZED` (401).

### 2.3 Boundary Values (Các giá trị biên)
- **Biên giá trị 0**: KHI nhập diện tích hoặc giá phòng chính xác bằng `0`. (Kiểm tra xem hệ thống xử lý thế nào, thường giá bằng 0 có thể hợp lệ đối với nhà trọ miễn phí, diện tích = 0 có thể ném Validation).
- **Biên số cực lớn**: KHI nhập mức giá hàng tỷ VNĐ (`999999999999`), HỆ THỐNG PHẢI phân tích (parse) an toàn vào `BigDecimal` mà không văng `NumberFormatException` (bị giới hạn bởi kích thước kiểu dữ liệu DB nhưng ở Java phải parse được).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Race Condition cập nhật phòng**: KHI 2 Admin cùng lúc (cùng mili-giây) nhấn Lưu thông tin của cùng 1 phòng, HỆ THỐNG PHẢI chứng minh an toàn luồng trong nội bộ `AdminRoomServlet` (các biến lấy từ request phải cục bộ trong hàm `doPost`, không bị rò rỉ (leak) sang thread khác).
