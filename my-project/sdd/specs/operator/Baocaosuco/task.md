# Checklist Công việc - Báo cáo sự cố (Operator)

- `[ ]` 1. Tạo giao diện JSP Form
  - `[ ]` Tạo file `create.jsp` tại `WEB-INF/views/operator/incidents/`
  - `[ ]` Viết mã HTML/Bootstrap 5 cho các input: Cơ sở, Vị trí (Khu vực chung / Phòng), Phân loại sự cố, Mức độ ưu tiên, Mô tả chi tiết.
  - `[ ]` Tích hợp input Upload ảnh có Preview Thumbnail bằng JavaScript.
  - `[ ]` Thêm script kiểm tra dữ liệu bắt buộc (Validation frontend).
  - `[ ]` Áp dụng ngôn ngữ thiết kế từ `DESIGN.md` (nút bo tròn, CSS style phù hợp).

- `[ ]` 2. Tạo Controller (Servlet)
  - `[ ]` Khởi tạo file `IncidentReportServlet.java` trong package `controller.operator`.
  - `[ ]` Xử lý `doGet()`: Chuẩn bị dữ liệu nếu cần (danh sách Cơ sở / Phòng) và forward sang file `create.jsp`.
  - `[ ]` Xử lý `doPost()`: Nhận dữ liệu submit. Lấy `staff_id` người báo cáo từ Session.
  - `[ ]` Xử lý Upload multipart để nhận và lưu file ảnh, lấy URL lưu vào `attachment_urls1`.
  - `[ ]` Ghép nối chuỗi `priority`, `facility`, `room` vào trường `title` và `content` của `Request`.
  - `[ ]` Validate chống bỏ trống các tham số bắt buộc.

- `[ ]` 3. Xử lý Logic Database
  - `[ ]` Sử dụng `RequestDAO.java` và phương thức `insertIncidentReport` để lưu dữ liệu.
  - `[ ]` Cập nhật `FacilityDAO` / `RoomDAO` (nếu cần) để load dữ liệu thả xuống (dropdown) tại Form.

- `[ ]` 4. Tích hợp và Kiểm thử
  - `[ ]` Cấu hình lại (Build WAR) và chạy trên Apache Tomcat 10.1.
  - `[ ]` Đăng nhập vào hệ thống dưới quyền `OPERATOR`.
  - `[ ]` Chạy luồng báo cáo sự cố hoàn chỉnh, đảm bảo dữ liệu ghi vào DB không bị lỗi và chuẩn định dạng.
