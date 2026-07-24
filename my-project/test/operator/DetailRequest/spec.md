# Test Specification: Chi tiết yêu cầu sửa chữa (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Chi tiết yêu cầu sửa chữa (`my-project/sdd/specs/operator/DetailRequest`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic hiển thị chi tiết sự cố và các hành động xử lý luồng trạng thái của nhân viên vận hành (Tiếp nhận / Từ chối). Đảm bảo tính toàn vẹn của trạng thái sự cố và phân quyền.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem chi tiết yêu cầu**: KHI truy cập trang chi tiết với `incident_id` hợp lệ, HỆ THỐNG PHẢI trả về đầy đủ thông tin (Tiêu đề, Hình ảnh, Trạng thái) từ Mock DAO và hiển thị nút [Nhận] / [Từ chối] nếu trạng thái đang là `PENDING`.
- **Nhận yêu cầu (Accept)**: KHI nhấn nút [Nhận yêu cầu] với `incident_id` đang `PENDING`, HỆ THỐNG PHẢI cập nhật trạng thái thành `IN_PROGRESS` (hoặc `ACCEPTED`), gán người xử lý là nhân viên hiện tại VÀ điều hướng/hiển thị nút [Cập nhật trạng thái].
- **Từ chối yêu cầu (Reject)**: KHI nhấn [Từ chối] KÈM THEO một lý do hợp lệ, HỆ THỐNG PHẢI lưu lý do vào Mock DAO, cập nhật trạng thái (hoặc hủy phân công) VÀ điều hướng về trang Danh sách.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Từ chối không có lý do**: KHI nhấn [Từ chối] NHƯNG để trống lý do, HỆ THỐNG PHẢI chặn lại bằng Validation, báo lỗi bắt buộc nhập lý do VÀ không gọi hàm cập nhật của Mock DAO.
- **Tiếp nhận sai trạng thái**: KHI gửi lệnh [Nhận yêu cầu] cho một sự cố đã ở trạng thái `IN_PROGRESS` hoặc `COMPLETED`, HỆ THỐNG PHẢI từ chối hành động và báo lỗi "Yêu cầu này đã được xử lý".
- **Không tìm thấy yêu cầu**: KHI gửi `incident_id` không tồn tại, HỆ THỐNG PHẢI bắt lỗi và hiển thị trang 404 (Not Found).

### 2.3 Boundary Values (Các giá trị biên)
- **Lý do từ chối quá dài**: KHI nhập lý do từ chối chứa chính xác độ dài tối đa cho phép (vd: 1000 ký tự), HỆ THỐNG PHẢI lưu thành công. Nhập 1001 ký tự PHẢI văng lỗi Validation.
- **Biên trạng thái UI**: KHI truy cập sự cố đã `IN_PROGRESS`, HỆ THỐNG BẮT BUỘC KHÔNG ĐƯỢC render hai nút [Nhận] và [Từ chối].

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Cướp yêu cầu (Race Condition)**: KHI Nhân viên A và Nhân viên B cùng lúc nhấn nút [Nhận yêu cầu] cho cùng một sự cố đang `PENDING`. Mock DAO giả lập cơ chế Optimistic Locking, HỆ THỐNG PHẢI cho phép 1 người nhận thành công, người còn lại nhận thông báo "Sự cố đã được nhân viên khác tiếp nhận".
