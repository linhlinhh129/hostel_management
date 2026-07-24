# Test Specification: Quản lý thông báo ban quản lý (Notification Facility Management)

**File bị ảnh hưởng**: `ManagerNotificationsServletTest.java`
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào các hành vi gửi thông báo và bảo vệ tính toàn vẹn dữ liệu.

## 1. Happy Path (Các kịch bản thành công)

- `testDoPost_SendGeneralNotification_Success`: KHI gửi thông báo chung hợp lệ cho cơ sở hoặc phòng, THE SYSTEM SHALL lưu thành công với trạng thái `SENT`.
- `testDoPost_SendDebtReminder_Success`: KHI gửi nhắc nợ hóa đơn, THE SYSTEM SHALL sinh ra mã thông báo chứa tiền tố `NTF-DEBT-` tới phòng tương ứng.
- `testDoPost_ReportUtilityIssue_Success`: KHI báo cáo sai chỉ số điện nước, THE SYSTEM SHALL cập nhật trạng thái hóa đơn/chỉ số sang `REPORTED` VÀ tạo task `PENDING` cho Operator. (Kiểm tra Transaction).

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_SendGlobalNotification_Forbidden`: KHI cố tình gửi thông báo với scope `ALL` (toàn hệ thống), THE SYSTEM SHALL trả về HTTP 403 Forbidden.
- `testDoPost_SendCrossFacility_Forbidden`: KHI gửi thông báo hoặc nhắc nợ sang phòng thuộc cơ sở khác, THE SYSTEM SHALL trả về HTTP 403 Forbidden.
- `testDoPost_MissingTitleOrContent_Fails`: KHI tiêu đề hoặc nội dung bị bỏ trống, THE SYSTEM SHALL không gọi Service lưu, và gán lỗi vào Session, redirect lại trang form.
- `testDoPost_ReportUtilityIssue_RollbackOnFailure`: KHI ghi nhận lỗi DB giữa chừng (vd: cập nhật status thành công nhưng lỗi chèn task PENDING), THE SYSTEM SHALL rollback giao dịch hoàn toàn.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_Notification_MaxLength`: KHI tiêu đề hoặc nội dung thông báo có độ dài đúng bằng biên tối đa cho phép (VD: 255 ký tự cho tiêu đề), THE SYSTEM SHALL lưu thành công. Vượt quá 1 ký tự sẽ báo lỗi.
- `testDoPost_DebtReminder_ExactlyOverdueByOneDay`: KHI hóa đơn vừa quá hạn đúng 1 ngày, THE SYSTEM SHALL cho phép gửi nhắc nợ (Biên thời gian).

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_DoubleStrike_ReportUtilityIssue`: Giả lập 2 Manager cùng nhấn nút báo lỗi sai số điện nước cho cùng 1 hóa đơn tại cùng một thời điểm. THE SYSTEM SHALL chỉ sinh ra đúng 1 Request cho Operator để tránh Spam, request còn lại báo "Đã được báo cáo trước đó".
