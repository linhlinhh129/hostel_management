# Test Specification: Báo cáo sự cố (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Báo cáo sự cố tại hiện trường (`my-project/sdd/specs/operator/Baocaosuco`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng nghiệp vụ Báo cáo sự cố (`IncidentReportServlet`). Đặc biệt tập trung vào logic phân loại vị trí (Phòng vs Khu vực chung), xử lý tệp tin đa phương tiện (Ảnh đính kèm), và đảm bảo PreparedStatement DAO được gọi với đúng tham số.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Báo cáo sự cố Phòng**: KHI nhân viên chọn Vị trí = `Phòng` và gửi form hợp lệ, HỆ THỐNG PHẢI lấy `staff_id` từ Session, gán trạng thái `PENDING`, và Mock DAO nhận đúng `room_id` kèm theo danh sách ảnh.
- **Báo cáo sự cố Khu vực chung**: KHI nhân viên chọn Vị trí = `Khu vực chung` (vd: Hành lang), HỆ THỐNG PHẢI truyền `room_id = null` (hoặc rỗng) xuống Mock DAO và gán đúng giá trị `public_area`.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Thiếu thông tin bắt buộc**: KHI form Submit bị thiếu Cơ sở, Phân loại, hoặc Mô tả, HỆ THỐNG PHẢI chặn lại ở Controller/Validator VÀ KHÔNG gọi Mock DAO.
- **Gửi ảnh sai định dạng**: KHI người dùng upload file PDF hoặc `.exe` vào ô đính kèm ảnh, HỆ THỐNG PHẢI từ chối và báo lỗi định dạng ảnh.
- **Session không hợp lệ**: KHI Session không tồn tại hoặc không phải vai trò `OPERATOR`, HỆ THỐNG PHẢI chặn request bằng Forbidden/Redirect Login.

### 2.3 Boundary Values (Các giá trị biên)
- **Không có ảnh đính kèm**: KHI form được gửi lên không có bất kỳ file ảnh nào, HỆ THỐNG PHẢI vẫn lưu thành công báo cáo (ảnh là tuỳ chọn hoặc xử lý array ảnh rỗng một cách an toàn).
- **Độ dài mô tả tối đa**: KHI "Mô tả chi tiết" chứa chính xác 2000 ký tự (giới hạn biên), HỆ THỐNG PHẢI lưu thành công. Vượt quá giới hạn sẽ ném lỗi Validation.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Double Submit Form**: KHI nhân viên bấm nút `[Gửi báo cáo]` 5 lần liên tục do mạng lag, HỆ THỐNG PHẢI áp dụng cơ chế Idempotent (như Rate Limit hoặc Token ẩn) để đảm bảo Mock DAO chỉ thực thi `INSERT` đúng 1 lần duy nhất cho một payload giống hệt nhau trong khoảng thời gian ngắn.
