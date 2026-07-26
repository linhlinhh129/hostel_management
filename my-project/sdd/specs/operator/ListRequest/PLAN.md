# Kế hoạch Thực hiện - Danh sách Yêu cầu (ListRequest)

## 1. Mục tiêu
Thiết kế trang Danh sách yêu cầu sửa chữa cho nhân viên Vận hành, cho phép xem, lọc và phân trang (Server-side) theo đúng chuẩn Mintlify và cấu trúc layout `app-shell`. Lấy dữ liệu thực từ bảng `requests` trong DB mà KHÔNG chỉnh sửa bất kỳ cấu trúc DB nào.

## 2. Kiến trúc Backend (Java Servlet & DAO)
- **DAO (`RequestDAO.java`):** Bổ sung 2 hàm mới:
  - `List<Request> getRequests(String status, String category, int offset, int limit)`: Truy vấn danh sách có lọc và phân trang, JOIN với bảng `users`, `rooms`, `facilities` để lấy tên phòng và người gửi. Sắp xếp theo `created_at DESC` (do DB không có cột `appointment_date` như trong Spec giả định).
  - `int countRequests(String status, String category)`: Đếm tổng số bản ghi thỏa mãn bộ lọc để tính số trang.
- **Servlet (`ListRequestServlet.java`):**
  - Mapped URL: `/operator/requests`
  - Đọc các tham số `status`, `category`, `page` từ URL.
  - Gọi DAO, tính toán phân trang (`limit = 20`).
  - Set attributes và forward tới `list.jsp`.

## 3. Kiến trúc Frontend (JSP)
- **File:** `list.jsp` trong thư mục `/WEB-INF/views/operator/requests/`
- **Layout:** Sử dụng `.app-shell` và `.main-wrapper`.
- **Thành phần giao diện:**
  - **Header & Filter Bar:** Khối tiêu đề có thanh công cụ lọc dữ liệu. Form lọc GET trực tiếp lên `/operator/requests`. Dropdown lọc thể loại và trạng thái phải hiển thị bằng tiếng Việt.
  - **Table:** Bảng danh sách hiển thị các cột: Mã YC, Tiêu đề, Thể loại (Dịch sang tiếng Việt: Sự cố điện, Sự cố nước...), Số phòng, Ngày tạo, Trạng thái (Dịch sang tiếng Việt), Hành động. Sử dụng class `table-mintlify` và `table-hover` đồng bộ với hệ thống. Việc dịch sang tiếng Việt có thể thực hiện bằng thẻ `<c:choose>` trong JSP.
  - **Pagination:** Khối phân trang bên dưới bảng.
  - **Empty State:** Hiển thị thông báo "Không có yêu cầu nào phù hợp" nếu danh sách rỗng.

## 4. Kế hoạch khắc phục lỗi hiển thị ảnh đính kèm Yêu cầu (Ticket) trên màn hình Manager
Theo rà soát, Database lưu trữ đường dẫn ảnh ở dạng `/uploads/tickets/tên_file.jpg` hoàn toàn hợp lệ (không có lỗi Database). Tuy nhiên, có lỗi ở màn hình Xem chi tiết của Manager (`manager/tickets/detail.jsp`):

### Nguyên nhân lỗi (Root Cause):
Lập trình viên trước đó đã hardcode thêm chuỗi `/uploads/` vào thẻ `<img>` của Manager:
```jsp
<img src="${ctx}/uploads/${trimmedUrl}" ...
```
Do `trimmedUrl` vốn dĩ đã có `/uploads/...`, nên URL sinh ra bị lặp thành `/hostel-management/uploads//uploads/...` dẫn tới ảnh bị vỡ (Broken Image). Trong khi bên Tenant và Operator dùng hàm kiểm tra an toàn hơn nên không bị lỗi này.

### Phương án sửa lỗi (Implementation Plan):
**[MODIFY] `manager/tickets/detail.jsp`**
Sửa lại thẻ hiển thị ảnh ở màn hình Manager tương tự như Operator/Tenant bằng đoạn `<c:choose>` để loại bỏ lỗi lặp đường dẫn `/uploads/`:

```jsp
<c:choose>
  <c:when test="${fn:startsWith(trimmedUrl, ctx)}">${trimmedUrl}</c:when>
  <c:otherwise>${ctx}${trimmedUrl}</c:otherwise>
</c:choose>
```
*Việc này sẽ xử lý dứt điểm lỗi hiển thị ảnh mà KHÔNG tác động tới cấu trúc hay dữ liệu trong Database như lệnh cấm đã đưa ra.*

## 5. Kế hoạch Validate Lịch Hẹn (AC04)
Theo yêu cầu mới (AC04), việc xác nhận lịch hẹn phải nằm trong giờ hành chính (08:00 sáng đến 18:00 chiều) và không được chọn ngày trong quá khứ. Các thay đổi bao gồm:

### Frontend (JSP)
Cập nhật 2 file:
- **`operator/requests/detail.jsp`**
- **`manager/tickets/detail.jsp`**

**Thay đổi:**
1. Thêm thuộc tính `min` cho thẻ `<input type="datetime-local">` (sử dụng ngày giờ hiện tại) để block trình duyệt không cho chọn ngày trong quá khứ.
2. Viết thêm 1 hàm JavaScript nhỏ gắn vào sự kiện `change` hoặc `submit` để kiểm tra `getHours()`: nếu `hour < 8 || hour >= 18` thì gọi `alert` và ngăn chặn submit (`preventDefault()`).

### Backend (Servlet)
Cập nhật 2 Controller:
- **`DetailRequestServlet.java`** (Operator)
- **`ManagerTicketsServlet.java`** (Manager)

**Thay đổi:**
- Trước khi gọi DAO lưu lịch hẹn (`updateAppointmentSchedule` / `scheduleTicket` / `rescheduleTicket`), parse `appointmentDate` sang `LocalDateTime`.
- Kiểm tra:
  - Nếu `date.toLocalDate().isBefore(LocalDate.now())`: Gửi lỗi "Không được chọn ngày trong quá khứ".
  - Nếu `date.getHour() < 8 || date.getHour() >= 18`: Gửi lỗi "Giờ làm việc chỉ từ 08:00 đến 18:00".
- Nếu có lỗi, `req.getSession().setAttribute("errorMsg", ...)` và `redirect` lại trang chi tiết.
