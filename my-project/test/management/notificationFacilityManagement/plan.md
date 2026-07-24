# Implementation Plan: Notification Facility Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerNotificationsServlet.java`
- **Dependencies**: `NotificationService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào bảo vệ scope (không cho spam toàn hệ thống), rollback giao dịch và kiểm thử đồng thời (race condition).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_SendGeneralNotification_Success`: Gửi thông báo hợp lệ, trả về SUCCESS.
- `testDoPost_SendDebtReminder_Success`: Gửi nhắc nợ, tự động generate prefix `NTF-DEBT-`.
- `testDoPost_ReportUtilityIssue_Success`: Báo cáo sai số điện nước, tạo task `PENDING` cho Operator.

### 3.2 Error Cases
- `testDoPost_SendGlobalNotification_Forbidden`: Cố tình gửi scope `ALL` -> 403.
- `testDoPost_SendCrossFacility_Forbidden`: Gửi sang phòng của cơ sở khác -> 403.
- `testDoPost_MissingTitleOrContent_Fails`: Thiếu param -> báo lỗi validation, không gọi Service.
- `testDoPost_ReportUtilityIssue_RollbackOnFailure`: Giả lập DB Exception giữa chừng -> Đảm bảo rollback.

### 3.3 Boundary Values
- `testDoPost_Notification_MaxLength`: Gửi thông báo có tiêu đề/nội dung chạm mốc tối đa cho phép.
- `testDoPost_DebtReminder_ExactlyOverdueByOneDay`: Hóa đơn vừa quá hạn 1 ngày.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleStrike_ReportUtilityIssue`: Giả lập bấm nút báo sai số điện nước 2 lần cực nhanh. Đảm bảo chỉ sinh ra 1 task PENDING.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `ManagerNotificationsServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
