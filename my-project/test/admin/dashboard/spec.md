# Test Specification: Admin Dashboard (Unit Test Only)

**Status:** Draft
**Target Feature:** Admin Dashboard (`my-project/sdd/specs/admin/dashboard`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Chiến lược kiểm thử này CHỈ bao gồm **Unit Testing** (sử dụng JUnit 5, Mockito). Giao tiếp CSDL (DAO) và E2E sẽ bị loại bỏ, mọi phụ thuộc phải được Mock hoàn toàn.
Đảm bảo Servlet `AdminDashboardServlet` và các lớp hỗ trợ hoạt động đúng logic nghiệp vụ, tính toán chính xác các KPI, thống kê doanh thu, xử lý phân quyền và chịu tải/xử lý lỗi tốt mà không làm hỏng trải nghiệm người dùng, tuân thủ chặt chẽ 4 khía cạnh kiểm thử theo chuẩn.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Truy cập hợp lệ**: KHI một user có role `ADMIN` truy cập `/admin/dashboard`, HỆ THỐNG PHẢI trả về giao diện Dashboard với đẩy đủ số liệu KPI cards (Tổng doanh thu, Tổng cơ sở, Tổng thông báo, Tổng nhật ký hôm nay).
- **Tính toán doanh thu tháng đúng**: KHI hiển thị tổng doanh thu tháng, HỆ THỐNG PHẢI chỉ tính tổng các hóa đơn có trạng thái `PAID` phát sinh trong tháng hiện tại (từ ngày 1 đến ngày truy cập).
- **Thống kê nhân sự đúng**: KHI hiển thị widget nhân sự, HỆ THỐNG PHẢI đếm chính xác tổng nhân sự, số lượng `MANAGER`, và số lượng `OPERATOR`.
- **Hoạt động gần đây hợp lệ**: KHI hiển thị widget Audit Log, HỆ THỐNG PHẢI hiển thị tối đa 5 bản ghi mới nhất, bao gồm cả hành động và tên người thực hiện.
- **Bảng doanh thu theo cơ sở chuẩn xác**: KHI hiển thị bảng doanh thu, HỆ THỐNG PHẢI liệt kê các cơ sở đang `ACTIVE` với đầy đủ doanh thu (từ hóa đơn `PAID`), dư nợ (`UNPAID` + `OVERDUE`).

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Lỗi DAO/Nguồn dữ liệu (Exception handling)**: KHI một DAO (ví dụ: AuditLogDAO) ném ra Exception, HỆ THỐNG KHÔNG ĐƯỢC trắng màn hình hoặc crash. HỆ THỐNG PHẢI log `WARN`, trả về danh sách rỗng (hoặc `0`) cho widget bị lỗi và vẫn hiển thị bình thường các widget khác.
- **Chưa đăng nhập**: KHI người dùng chưa đăng nhập cố truy cập `/admin/dashboard`, HỆ THỐNG PHẢI redirect về `/login`.
- **Sai Role (Forbidden)**: KHI người dùng có role `MANAGER` hoặc `OPERATOR` truy cập `/admin/dashboard`, HỆ THỐNG PHẢI từ chối truy cập và trả về HTTP 403 Forbidden.

### 2.3 Boundary Values (Các giá trị biên)
- **Hệ thống chưa có dữ liệu (Rỗng)**: KHI database trống (không có cơ sở, không hóa đơn, không nhật ký), Dashboard PHẢI hiển thị các KPI bằng `0`, danh sách hoạt động báo "Chưa có hoạt động nào", bảng doanh thu cơ sở báo "Chưa có dữ liệu doanh thu", KHÔNG ĐƯỢC ném `NullPointerException`.
- **Vượt quá 5 hoạt động gần đây**: KHI bảng Audit Log có 100 bản ghi, widget hoạt động gần đây CHỈ ĐƯỢC hiển thị đúng 5 bản ghi mới nhất (sắp xếp theo thời gian giảm dần).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Data Contention/Cache**: KHI nhiều Admin cùng truy cập liên tục trong vòng 60 giây, HỆ THỐNG PHẢI sử dụng cache hợp lệ để không query quá tải DB. 
- **Cập nhật dữ liệu đồng thời**: KHI một Admin đang xem Dashboard và một hóa đơn chuyển sang `PAID` ở thread khác, Dashboard (nếu chưa hết hạn cache) có thể chưa thấy ngay, nhưng nếu đã hết 60 giây cache, lần load tiếp theo PHẢI thấy dữ liệu thay đổi chuẩn xác.
