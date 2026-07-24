# Test Specification: Cập nhật chỉ số điện nước (Unit Test Only)

**Status:** Draft
**Target Feature:** Báo cáo / Cập nhật chỉ số điện nước (`my-project/sdd/specs/operator/WaterElectricUpdate`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic Servlet `UpdateMeterReadingServlet`. Mục tiêu chính là đảm bảo Server-side Validation hoạt động đúng (chặn số liệu âm, số liệu nhỏ hơn tháng trước, chặn thiếu ảnh) và giả lập chính xác multipart/form-data upload ảnh công tơ. Tuyệt đối không kết nối DB thật.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **AC01 - Cập nhật thành công**: KHI Operator nhập `roomCode` hợp lệ, chỉ số điện/nước mới CÓ GIÁ TRỊ LỚN HƠN tháng trước, và đính kèm 2 file ảnh (`image/jpeg`), HỆ THỐNG PHẢI thực hiện ghi nhận thông qua Mock Service, trả về HTTP 200 (hoặc chuyển hướng trang báo thành công) và không có lỗi Validation nào.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **AC02 - Chỉ số điện tụt lùi**: KHI chỉ số điện mới NHỎ HƠN chỉ số tháng trước, HỆ THỐNG PHẢI từ chối lưu và ném ra thông báo `ELECTRIC_READING_INVALID`.
- **AC03 - Chỉ số nước tụt lùi**: KHI chỉ số nước mới NHỎ HƠN chỉ số tháng trước, HỆ THỐNG PHẢI ném lỗi `WATER_READING_INVALID`.
- **AC04 & AC05 - Thiếu ảnh minh chứng**: KHI request gửi lên nhưng List `Parts` bị thiếu mất một trong hai biến `electricMeterImage` hoặc `waterMeterImage` (hoặc file size = 0), HỆ THỐNG BẮT BUỘC chặn đứng và báo lỗi `_METER_IMAGE_REQUIRED`.
- **AC06 - Phòng không tồn tại**: KHI truy vấn Mock Service báo về `Room Not Found`, Servlet PHẢI catch và phản hồi 404 (hoặc giao diện lỗi tương ứng).
- **File Upload giả mạo**: KHI đính kèm file có tên `image.jpg` nhưng `getContentType()` lại trả về `application/pdf`, HỆ THỐNG PHẢI báo lỗi định dạng.

### 2.3 Boundary Values (Các giá trị biên)
- **Chỉ số bằng tháng trước**: KHI số điện/nước mới BẰNG Y HỆT số tháng trước (ví dụ: phòng không có người ở), HỆ THỐNG PHẢI chấp nhận vượt qua vòng Validation.
- **Biên giới hạn 5MB**: (Tuỳ chọn) KHI Servlet nhận `IllegalStateException` từ Tomcat do file > 5MB, HỆ THỐNG PHẢI bắt lỗi một cách Graceful (catch exception) thay vì văng màn hình trắng 500.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Race Condition - Báo cáo trùng lắp**: KHI 2 Operator cùng lúc ấn nút "Cập nhật" cho cùng 1 phòng. Mock Service giả lập Thread 1 pass, Thread 2 văng lỗi `OptimisticLockException` hoặc `DataIntegrityViolation`. HỆ THỐNG PHẢI xử lý an toàn Thread 2: không sập, báo lỗi "Chỉ số tháng này đã được cập nhật bởi người khác".
