# Test Specification: Danh sách chỉ số điện nước (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Danh sách chỉ số điện nước các phòng (`my-project/sdd/specs/operator/ListElectric`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic hiển thị Danh sách chỉ số điện nước của tất cả các phòng. Tập trung vào việc mapping trạng thái (ĐÃ CẬP NHẬT vs CHƯA CẬP NHẬT) dựa trên sự hiện diện của dữ liệu kỳ hiện tại trong Mock DAO.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Render List thành công**: KHI gửi GET request, HỆ THỐNG PHẢI gọi Mock DAO lấy danh sách. Đối với phòng đã có dữ liệu kỳ này (updatedAt != null), gán biến trạng thái `DA_CAP_NHAT`. Đối với phòng chưa có dữ liệu kỳ này (updatedAt == null), gán `CHUA_CAP_NHAT`.
- **Danh sách rỗng**: KHI Mock DAO trả về danh sách rỗng (vd: toà nhà mới chưa có phòng), HỆ THỐNG PHẢI render an toàn màn hình báo "Không có dữ liệu" mà không bị NullPointerException.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Lỗi kết nối Mock DB**: KHI DAO văng `SQLException` hoặc `RuntimeException` trong quá trình load danh sách, HỆ THỐNG PHẢI bắt lỗi, log ra console và hiển thị giao diện báo lỗi thân thiện (Flash message) thay vì sập 500 trang trắng.
- **Unauthorized Role**: KHI User Tenant cố tình vào xem danh sách điện nước tổng, HỆ THỐNG PHẢI ném lỗi 403 Forbidden.

### 2.3 Boundary Values (Các giá trị biên)
- **Chỉ số bằng 0**: KHI Mock trả về số điện kỳ trước = 0, số nước = 0 (phòng mới tinh), HỆ THỐNG PHẢI render chính xác số 0, không được hiểu lầm là Null/Blank.
- **Dữ liệu lớn (Khối lượng lớn)**: (Mô phỏng) KHI Mock trả về danh sách 10,000 phòng, HỆ THỐNG PHẢI xử lý gán attribute thành công (nếu không có rule phân trang thì test giới hạn của ArrayList trên JVM Memory).

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Data Snapshot**: KHI Nhân viên A đang load danh sách, đồng thời Nhân viên B đang update chỉ số 1 phòng. HỆ THỐNG PHẢI thể hiện tính chất Snapshot Read của luồng GET, không bị Crash mảng `ConcurrentModificationException` trong nội bộ Servlet.
