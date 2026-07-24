# Test Specification: Cập nhật trạng thái sửa chữa (Operator - Unit Test Only)

**Status:** Draft
**Target Feature:** Báo cáo Hoàn thành Yêu cầu (`my-project/sdd/specs/operator/UpdateSatatus`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** tập trung vào Servlet POST (`action=complete`). Mục tiêu cao nhất là xác minh cơ chế Validation Server-side (không cho để trống hình ảnh minh chứng, lý do) và cơ chế Mapping đặc biệt (đẩy `notes` vào cột `rejection_reason` và file hình ảnh vào `attachment_urls2`).

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Báo cáo Hoàn thành (1 Ảnh)**: KHI Operator gửi POST request chứa 1 file ảnh hợp lệ và `notes` đầy đủ, HỆ THỐNG PHẢI lưu file qua Mock FileIO, nối tên file gán vào trường `attachment_urls2` và gọi DAO cập nhật `status = COMPLETED`.
- **Báo cáo Hoàn thành (5 Ảnh)**: KHI Operator đính kèm đủ giới hạn tối đa 5 ảnh, HỆ THỐNG PHẢI lưu tên file phân cách bởi dấu phẩy (vd: `img1.jpg,img2.jpg...`) vào DB thông qua Mock DAO.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **Thiếu hình ảnh minh chứng**: KHI form được gửi lên mà thuộc tính file upload rỗng, HỆ THỐNG BẮT BUỘC chặn lại, trả về lỗi Validation và KHÔNG lưu database.
- **Thiếu ghi chú (Notes)**: KHI để trống `notes` (ghi chú kết quả), HỆ THỐNG PHẢI bắt Validation Error.
- **Sai trạng thái**: KHI cố tình gửi lệnh "Hoàn thành" (`action=complete`) cho một yêu cầu đang ở trạng thái `PENDING` (chưa nhận) hoặc đã `COMPLETED`, HỆ THỐNG PHẢI từ chối.
- **Định dạng ảnh sai**: KHI đính kèm file `.pdf` hoặc `.exe` thay vì ảnh, HỆ THỐNG PHẢI ném Validation Error.

### 2.3 Boundary Values (Các giá trị biên)
- **Tối đa độ dài Text**: KHI truyền chuỗi Ghi chú có độ dài tối đa cho phép (ví dụ 1000 ký tự), HỆ THỐNG PHẢI pass qua validation và lưu thành công.
- **Kích thước ảnh lớn (Mock)**: (Mô phỏng) KHI đính kèm file vượt 5MB, Servlet Config/Logic PHẢI bắt được ngoại lệ `SizeLimitExceededException` hoặc tự check dung lượng trả lỗi.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Race Condition - Báo cáo trùng**: KHI 2 Operator cùng lúc ấn nút submit "Hoàn thành" cho cùng 1 Yêu cầu. Mock DAO mô phỏng Optimistic Lock văng lỗi ở thread thứ 2. HỆ THỐNG PHẢI bắt lỗi Lock an toàn, cho phép 1 người thành công và 1 người nhận thông báo "Yêu cầu đã được xử lý bởi người khác".
