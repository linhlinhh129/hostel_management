# PLAN: Kế hoạch Thực thi Cập nhật thông tin phòng

**Status:** Planning  
**Date:** 2026-07-13  
**Priority:** Medium  
**Estimated Duration:** 2 weeks

---

## 1. Tổng quan Giải pháp

Feature Cập nhật thông tin phòng cho phép Admin điều chỉnh Diện tích (m²) và Giá phòng (VNĐ) của từng phòng.

**Kiến trúc:**
- Backend Servlet: `AdminRoomServlet` tiếp nhận request cập nhật.
- Service & DAO: `RoomService`, `RoomDAO` thực thi cập nhật xuống CSDL.
- Frontend UI: Trang chi tiết phòng (`detail.jsp`) bao gồm form cập nhật.
- Database: Cập nhật các trường `area`, `room_fee` trong bảng `rooms`.

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)
**Mục tiêu:** Cập nhật DB schema, chuẩn bị API/Servlet contract.
**Công việc:**
- Đảm bảo bảng `rooms` hỗ trợ trường `area` (decimal) và `room_fee` (decimal), cho phép NULL.
- Định nghĩa Servlet mapping và validation rules.
- Cập nhật DTO `Room` để chứa thêm thông tin diện tích và giá.

### Giai đoạn 2: Backend Development (Tuần 1-2)
**Mục tiêu:** Xây dựng logic nghiệp vụ và DAO.
**Công việc:**
- Sửa đổi `RoomDAO.findById` để lấy đầy đủ chi tiết.
- Thêm `RoomDAO.updateAreaAndFee`.
- Thêm logic validation (số âm, facility INACTIVE) trong `RoomService`.
- Tích hợp vào `AdminRoomServlet` (`doPost`).

### Giai đoạn 3: Frontend Development (Tuần 2)
**Mục tiêu:** Cập nhật giao diện chi tiết phòng.
**Công việc:**
- Chỉnh sửa file `/WEB-INF/views/admin/rooms/detail.jsp`.
- Thêm form cập nhật diện tích và giá.
- Xử lý hiển thị thông báo lỗi (Flash messages).
- Disable form nếu cơ sở đang INACTIVE.

### Giai đoạn 4: Testing & Deployment (Tuần 2)
**Mục tiêu:** Đảm bảo tính năng hoạt động ổn định.
**Công việc:**
- Viết Unit test cho service (đặc biệt là validation logic).
- Test manual các edge cases (nhập số âm, chữ, cơ sở inactive).

---

## 3. Key Technical Challenges

### Ràng buộc Cơ sở INACTIVE
- Phải kiểm tra trạng thái của Facility trước khi cho phép update Room. 
- Cần join bảng `facilities` hoặc dùng `FacilityDAO` để check status ngay trước khi lưu để tránh race condition.

### Xử lý giá trị NULL
- Giao diện gửi lên chuỗi rỗng (`""`), Backend phải parse và chuyển thành `NULL` trong Database thay vì lỗi hoặc lưu số `0`.

---

## 4. Dependencies

### Internal Dependencies
- Yêu cầu chức năng Quản lý cơ sở (Facility Management) và Tạo phòng (Room Management) đã hoạt động.

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Cập nhật nhầm phòng thuộc cơ sở INACTIVE | High | Bắt buộc check status từ Backend, không chỉ ẩn form ở Frontend |
| Parse lỗi khi nhập text vào ô số | Medium | Dùng try-catch khi parse `BigDecimal`, ném ValidationException |
| Đổi giá phòng ảnh hưởng hợp đồng cũ | High | Cần confirm logic kinh doanh: Giá phòng ở bảng `rooms` chỉ là giá niêm yết, hợp đồng sẽ snapshot giá này tại thời điểm tạo. |

---

## 6. Success Criteria

- ✓ Admin có thể xem diện tích và giá phòng.
- ✓ Admin có thể cập nhật diện tích và giá phòng thành công.
- ✓ Có thể xóa trắng (set NULL) diện tích và giá.
- ✓ Bắt lỗi số âm, chữ cái thành công.
- ✓ Từ chối cập nhật nếu cơ sở INACTIVE.
- ✓ Giao diện phản hồi thông báo thành công/lỗi rõ ràng.

---

## 7. Timeline

- **Week 1:** Thiết kế & Backend
- **Week 2:** Frontend, Testing & Hoàn thiện

**Total:** 2 weeks

---

## 8. Constraints

- Chỉ Admin mới có quyền thực hiện tính năng này (Role = ADMIN).
- Dữ liệu diện tích và giá phòng phải được định dạng chính xác trước khi lưu vào DB.
