# Implementation Plan: DetailRequest Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorDetailRequestServlet.java` (hoặc tên tương đương xử lý chi tiết Yêu cầu sửa chữa), `IncidentDAO` / `RequestDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Sử dụng Mockito để giả lập luồng thay đổi trạng thái và logic Optimistic Locking (nếu có).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorDetailRequestServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewDetail_Success`: Truy cập đúng ID, Mock DAO trả về thông tin chi tiết, hệ thống đẩy vào `request.setAttribute`.
- `testDoPost_AcceptRequest_Success`: Gửi POST `action=accept`. Trạng thái ban đầu `PENDING`. Mock DAO gọi hàm cập nhật người tiếp nhận và chuyển trạng thái `IN_PROGRESS`.
- `testDoPost_RejectRequest_Success`: Gửi POST `action=reject` kèm `reason="Lý do hợp lệ"`. Mock DAO gọi hàm cập nhật lưu lý do và từ chối xử lý.

### 3.2 Error Cases
- `testDoPost_RejectWithoutReason`: Gửi POST `action=reject` nhưng `reason` rỗng. Báo lỗi Validation.
- `testDoPost_AcceptAlreadyProcessingRequest`: Gửi POST `action=accept` cho Yêu cầu đã mang trạng thái `IN_PROGRESS`. Báo lỗi nghiệp vụ.
- `testDoGet_RequestNotFound`: GET ID = 9999 (không tồn tại). Mock DAO trả về `null`. Hệ thống trả về 404.

### 3.3 Boundary Values
- `testDoPost_RejectReasonBoundary`: Lý do từ chối dài đúng 1000 ký tự -> Thành công. 1001 ký tự -> Lỗi.
- `testDoGet_UIStateBoundary`: Nếu trạng thái là `COMPLETED`, assert rằng Servlet thiết lập một cờ ẩn UI hai nút Nhận/Từ chối.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleAcceptRaceCondition`: 2 Threads cùng bắn request "Nhận yêu cầu". Mock DAO được thiết lập ném `OptimisticLockException` cho Thread số 2. Đảm bảo Thread 2 nhận thông báo báo lỗi thân thiện thay vì 500.

## 4. Các bước thực hiện
1. Setup class test cho `OperatorDetailRequestServlet`.
2. Tạo Mock đối tượng request/response/session và các DAO.
3. Viết các test method phủ 4 khía cạnh.
