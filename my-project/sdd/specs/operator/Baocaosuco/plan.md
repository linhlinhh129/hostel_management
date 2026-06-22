# Kế hoạch triển khai: Báo cáo sự cố (Operator)

Phát triển tính năng Báo cáo sự cố cho Nhân viên vận hành (Operator) tại hiện trường, tuân thủ các ràng buộc của dự án và tài liệu Spec.

## 1. Xung đột tài liệu và Giải pháp (Conflict)
- **Vấn đề:** Trong file `Spec.md` yêu cầu *"dùng Basic SQL Statements (ghép chuỗi thông thường) thay vì PreparedStatements"*. Tuy nhiên, `constitution.md` (tài liệu tối cao) lại quy định **bắt buộc dùng PreparedStatement** để chống SQL Injection.
- **Giải pháp đề xuất:** Tuân thủ tuyệt đối `constitution.md` và `AGENTS.md`. Sử dụng PreparedStatement bằng việc gọi hàm `insertIncidentReport` đã có sẵn trong `RequestDAO.java`.

## 2. Xử lý Ràng buộc Database
- **Yêu cầu:** Không được sửa database, chỉ dùng trường có sẵn.
- **Giải pháp:** Bảng `requests` hiện không có các trường tách biệt cho Tòa nhà, Phòng và Mức độ ưu tiên. Mình sẽ ghép thông tin này vào trường `title` (Ví dụ: `[KHẨN CẤP] [Cơ sở A - Phòng 101] Báo cáo rò rỉ nước`) và chi tiết vào `content` để lưu trữ dữ liệu chính xác mà không cần sửa DB schema.

## 3. Cấu trúc thay đổi (Proposed Changes)

### Tầng Controller
**[NEW]** `src/main/java/com/quanlyphongtro/controller/operator/IncidentReportServlet.java`
- Xử lý endpoint `/operator/incidents/create`.
- Xử lý GET để hiển thị Form.
- Xử lý POST để nhận dữ liệu sự cố, nén/lưu ảnh upload, gán trạng thái PENDING và insert vào DB.

### Tầng View
**[NEW]** `src/main/webapp/WEB-INF/views/operator\incidents\create.jsp`
- Tạo giao diện HTML/Bootstrap 5 tuân thủ ngôn ngữ thiết kế từ `DESIGN.md`.
- Form nhập liệu gồm: Dropdown chọn tòa nhà/vị trí, Phân loại sự cố, Mức độ, Nội dung mô tả và tính năng Upload ảnh kèm Preview thu nhỏ.

### Tầng DAO
**[REUSE]** `src/main/java/com/quanlyphongtro/dao/RequestDAO.java`
- Tái sử dụng phương thức `insertIncidentReport(Request)` hiện có để chèn vào CSDL.
