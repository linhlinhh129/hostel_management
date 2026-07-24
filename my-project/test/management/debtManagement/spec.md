# Test Specification: Quản lý công nợ (Unit Test Only)

**Status:** Draft
**Target Feature:** Quản lý công nợ (`my-project/sdd/specs/manager/debtManagement`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng nghiệp vụ của `DebtPageServlet`. Trọng tâm là kiểm tra thuật toán tính toán ngày nợ, tiền nợ, và phí chậm nộp tạm tính trực tiếp trong quá trình truy xuất (on-the-fly) và đảm bảo an toàn truy cập (IDOR).

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **AC01 - Xem danh sách công nợ**: KHI Manager gọi `GET /manager/debts`, HỆ THỐNG PHẢI trả về danh sách các hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE` thuộc cơ sở đang quản lý.
- **AC02 - Lọc công nợ**: KHI Manager gọi `GET /manager/debts?status=OVERDUE`, HỆ THỐNG PHẢI lọc và chỉ trả về hóa đơn quá hạn.
- **AC03 - Xem chi tiết công nợ**: KHI Manager gọi `GET /manager/debts?action=detail&id=10`, HỆ THỐNG PHẢI trả về đối tượng `DebtDetailDTO` bao gồm thông tin chi tiết tiền phòng, điện nước và thông tin khách thuê.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **AC04 - Sai trạng thái lọc**: KHI Manager truyền tham số `status=INVALID_STATUS`, HỆ THỐNG PHẢI từ chối và ném ra lỗi HTTP 400 (Bad Request).
- **AC05 - IDOR Truy cập chéo cơ sở**: KHI Manager A truyền `id` của một hóa đơn nợ thuộc cơ sở của Manager B, HỆ THỐNG PHẢI chặn và trả về HTTP 404 (Not Found) - giả vờ như không tồn tại để giấu dữ liệu.
- **AC06 - Phân quyền**: KHI User có Role là `TENANT` truy cập, HỆ THỐNG PHẢI ném HTTP 403 Forbidden.

### 2.3 Boundary Values (Các giá trị biên)
- **AC07 - Biên tính ngày quá hạn**: 
  - KHI Ngày hiện tại = Hạn thanh toán, HỆ THỐNG PHẢI trả về Số ngày nợ = `0`.
  - KHI Ngày hiện tại < Hạn thanh toán (chưa tới hạn), HỆ THỐNG PHẢI trả về Số ngày nợ = `0`.
- **AC08 - Biên tính phí chậm nộp (3 ngày ân hạn)**:
  - KHI Số ngày nợ = 3, Phí chậm nộp = `0`.
  - KHI Số ngày nợ = 4, Phí chậm nộp = `1% * Tiền phòng`.
- **AC09 - Biên tính tiền còn nợ**: KHI người thuê đóng lố tiền (Tổng tiền đã thanh toán > Tổng tiền hóa đơn), Số tiền CÒN NỢ PHẢI bị khóa ở mức `0` thay vì trả ra số âm.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **AC10 - Thread-Safety của Servlet**: KHI 10 Thread của Manager cùng truy cập xem danh sách công nợ, Servlet PHẢI không lưu bất kỳ biến trạng thái (state) nào ở cấp độ class, đảm bảo các kết quả phân trang và từ khóa tìm kiếm (`keyword`, `currentPage`) của Manager này không dính vào Manager kia.
