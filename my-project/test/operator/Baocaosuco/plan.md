# Implementation Plan: Báo cáo sự cố Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `OperatorIncidentReportServlet.java`, `IncidentDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Sử dụng Mock `HttpServletRequest.getParts()` để giả lập file đa phương tiện đính kèm. Mock DAO để xác nhận việc gọi PreparedStatement chứa tham số `room_id = null` cho trường hợp khu vực chung.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorIncidentReportServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_ReportForRoom_Success`: Gửi Báo cáo cho Phòng cụ thể (`room_id = 101`). Xác minh DAO nhận đủ dữ liệu và gán trạng thái `PENDING`.
- `testDoPost_ReportForPublicArea_Success`: Gửi Báo cáo Khu vực chung. Xác minh tham số `room_id` được truyền xuống MockDAO là `null` hoặc `0`.

### 3.2 Error Cases
- `testDoPost_MissingRequiredFields`: Cố tình để trống `description` hoặc `category`. Xác minh Servlet báo lỗi validation và từ chối gọi DAO.
- `testDoPost_UnauthorizedAccess`: Session null hoặc Role là `TENANT`. Xác minh hệ thống ném ngoại lệ Forbidden / Chuyển hướng ra trang login.
- `testDoPost_InvalidImageFormat`: Gửi `Part` mô phỏng tệp `.pdf`. Xác minh hệ thống từ chối tải tệp.

### 3.3 Boundary Values
- `testDoPost_NoImagesUploaded`: Form gửi lên không đính kèm file ảnh nào. Xác minh hệ thống vẫn cho phép tạo Báo cáo.
- `testDoPost_DescriptionBoundary`: Cố tình truyền tham số `description` chứa 2000 ký tự. Hệ thống vẫn lưu thành công (tùy thuộc giới hạn cấu hình, nếu qua 2000 thì từ chối).

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleSubmit`: Bắn 5 request `doPost` đồng thời từ cùng 1 session, cùng 1 nội dung. Xác nhận rằng cơ chế Idempotent (vd: Token chống lặp) sẽ chỉ cho phép Mock DAO thực thi đúng 1 lần.

## 4. Các bước thực hiện
1. Thiết lập `OperatorIncidentReportServletTest` với Mockito (`@Mock IncidentDAO`).
2. Implement các helper method giả lập đối tượng `Part`.
3. Viết Test method tuân thủ format comment `# EARS [...]`.
