# Implementation Plan: Receive Tenant Request (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerTicketsServlet.java`
- **Dependencies**: `RequestService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào bảo vệ luồng trạng thái (State Machine) của Ticket và xác nhận upload file (nghiệm thu).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerTicketsServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewTickets_Success`: Xem danh sách Tickets.
- `testDoGet_ViewTicketDetail_Success`: Xem chi tiết và Timeline của Ticket.
- `testDoPost_ReceiveTicket_Success`: Tiếp nhận yêu cầu.
- `testDoPost_ScheduleTicket_Success`: Hẹn lịch sửa chữa.
- `testDoPost_CompleteTicket_Success`: Hoàn tất yêu cầu kèm upload ảnh.
- `testDoPost_RejectTicket_Success`: Từ chối yêu cầu kèm lý do.

### 3.2 Error Cases
- `testDoPost_ActionOnClosedTicket_Fails`: Cố thao tác trên Ticket đã DONE hoặc REJECTED.
- `testDoPost_CompleteWithoutNotes_Fails`: Cố xác nhận hoàn thành nhưng bỏ trống notes.
- `testDoPost_RejectWithoutReason_Fails`: Cố từ chối nhưng bỏ trống lý do.
- `testDoPost_ActionCrossFacility_Forbidden`: Cố xử lý Ticket ngoài phạm vi cơ sở được phân công (403).
- `testDoPost_UploadInvalidImage_Fails`: Upload file sai định dạng hoặc quá dung lượng khi nghiệm thu.

### 3.3 Boundary Values
- `testDoPost_CompleteUpload_MaxBoundary`: Upload file ảnh dung lượng đúng bằng biên 10MB.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleAction_RaceCondition`: 1 Manager nhấn Tiếp nhận (`RECEIVED`), 1 Manager khác nhấn Từ chối (`REJECTED`) cùng lúc.

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho `ManagerTicketsServlet`.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
