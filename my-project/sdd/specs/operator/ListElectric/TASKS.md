# Danh sách công việc (Tasks) - ListElectric

- [ ] Tạo DTO `MeterStatusDTO` để chứa dữ liệu bảng.
- [ ] Tạo DAO `MeterReadingDAO` với hàm `getMeterStatusList()` sử dụng SQL Server thuần.
- [ ] Tạo `MeterReadingService` kết nối DAO.
- [ ] Tạo `ListElectricServlet` đón đường dẫn `/operator/meter-readings` và forward sang JSP.
- [ ] Xây dựng UI `list.jsp` hiển thị dạng Bảng theo giao diện Mintlify.
- [ ] Kiểm tra giao diện xem có đúng tiêu chuẩn Read-only (không có form nhập liệu) theo Context.md hay không.
- [ ] Tích hợp CSS Mintlify (nếu chưa có đủ class cho Table).
- [ ] Test luồng với dữ liệu tháng hiện tại (nếu Database chưa có thì tự mock dữ liệu trong SQL Server để test).
