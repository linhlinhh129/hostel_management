# Test Specification: Tiếp nhận và xử lý yêu cầu người thuê (Receive Tenant Request)

**File bị ảnh hưởng**: `ManagerTicketsServletTest.java`
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào luồng State Machine (PENDING -> RECEIVED -> IN_PROGRESS -> DONE / REJECTED) và các thao tác ghi nhận Timeline.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewTickets_Success`: KHI truy cập danh sách, THE SYSTEM SHALL trả về list yêu cầu và có lọc/tìm kiếm.
- `testDoGet_ViewTicketDetail_Success`: KHI xem chi tiết, THE SYSTEM SHALL hiển thị đầy đủ thông tin Ticket và Timeline xử lý.
- `testDoPost_ReceiveTicket_Success`: KHI tiếp nhận yêu cầu (`PENDING`), THE SYSTEM SHALL chuyển trạng thái sang `RECEIVED`.
- `testDoPost_ScheduleTicket_Success`: KHI hẹn ngày sửa, THE SYSTEM SHALL lưu schedule và chuyển trạng thái sang `IN_PROGRESS`.
- `testDoPost_CompleteTicket_Success`: KHI xác nhận hoàn tất kèm ghi chú, THE SYSTEM SHALL lưu notes và chuyển sang `DONE`.
- `testDoPost_RejectTicket_Success`: KHI từ chối yêu cầu kèm lý do, THE SYSTEM SHALL lưu reason và chuyển sang `REJECTED`.

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_ActionOnClosedTicket_Fails`: KHI tiếp nhận / hoàn thành yêu cầu đã ở trạng thái `DONE` hoặc `REJECTED`, THE SYSTEM SHALL báo lỗi và redirect lại.
- `testDoPost_CompleteWithoutNotes_Fails`: KHI hoàn thành nhưng bỏ trống `notes`, THE SYSTEM SHALL báo lỗi không được phép.
- `testDoPost_RejectWithoutReason_Fails`: KHI từ chối nhưng bỏ trống `reason`, THE SYSTEM SHALL báo lỗi không được phép.
- `testDoPost_ActionCrossFacility_Forbidden`: KHI thao tác trên Ticket của phòng thuộc cơ sở khác, THE SYSTEM SHALL trả về HTTP 403.
- `testDoPost_UploadInvalidImage_Fails`: KHI up ảnh nghiệm thu sai định dạng (ví dụ `.exe`) hoặc vượt 10MB, THE SYSTEM SHALL từ chối.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_CompleteUpload_MaxBoundary`: (Nếu test được FileSize) File upload chính xác 10MB -> Pass, 10.1MB -> Fail.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_DoubleAction_RaceCondition`: Giả lập 1 Manager nhấn Tiếp nhận (`RECEIVED`), 1 Manager khác nhấn Từ chối (`REJECTED`) cùng lúc cho cùng một Ticket. Cơ chế bảo vệ State Machine THE SYSTEM SHALL chỉ cho phép 1 thao tác thành công.
