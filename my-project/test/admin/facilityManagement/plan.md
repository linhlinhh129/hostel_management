# Implementation Plan: Quản lý Cơ sở Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `AdminFacilityServlet.java`, `FacilityDAO`, `RoomDAO`
- **Constraint**: Đảm bảo sử dụng Mockito để cô lập Logic. Các luồng xử lý Transaction (như kích hoạt cơ sở và sinh phòng tự động) sẽ test behavior của việc gọi DAO method, thay vì thao tác SQL thật.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_Create_Success`: Submit form hợp lệ. Kiểm tra DAO `insert` được gọi với Code in hoa. Redirect thành công.
- `testDoPost_UpdateDraft_Success`: Cập nhật cơ sở `DRAFT` hợp lệ. Đảm bảo cập nhật mọi trường (Code, Tên, Số tầng, Số phòng).
- `testDoPost_Activate_Success`: Kích hoạt cơ sở `DRAFT`. Đảm bảo gọi logic sinh phòng (`generateRooms`) đúng chuẩn vòng lặp tầng x phòng, sau đó đổi trạng thái sang `ACTIVE`.
- `testDoPost_Deactivate_Success`: Vô hiệu hóa cơ sở `ACTIVE`. Đảm bảo gọi update trạng thái sang `INACTIVE`.

### 3.2 Error Cases
- `testDoPost_Create_MissingFields`: Submit form trống Code hoặc Name. Xác minh quăng lỗi Validation HTTP 400.
- `testDoPost_Create_CodeExists`: Mock DAO ném lỗi Duplicate Code. Xác minh trả về HTTP 409 `FACILITY_CODE_ALREADY_EXISTS`.
- `testDoPost_Activate_InvalidStatus`: Kích hoạt cơ sở đang `ACTIVE`. Xác minh ném lỗi 400 `FACILITY_INVALID_STATUS_FOR_ACTIVATION`.
- `testDoPost_UpdateActive_ChangeImmutableFields`: Cố đổi số tầng của cơ sở `ACTIVE`. Xác minh ném lỗi 400.
- `testDoPost_Activate_RoomGenerationFails_Rollback`: Mock logic sinh phòng ném lỗi. Xác minh Transaction được rollback, cơ sở vẫn giữ `DRAFT`.

### 3.3 Boundary Values
- `testCreate_MaxFloorsAndRooms`: Đặt tầng 99, phòng 99. Xác minh dữ liệu được chấp nhận hợp lệ. Đặt tầng 100, phòng 100 -> Lỗi.
- `testCreate_CodeLengthLimits`: Đặt code 2 ký tự (pass), 10 ký tự (pass), 1 ký tự hoặc 11 ký tự (fail 400).
- `testList_EmptyDatabase`: Gọi doGet lấy danh sách khi DB trống. Xác minh không lỗi `NullPointerException` mà trả về list rỗng.

### 3.4 Concurrent Scenarios
- `testCreate_Concurrency_CodeDuplicate`: Nếu hai luồng cùng chạy tạo cơ sở với cùng Code, luồng nào gọi `insert` trước sẽ pass, luồng sau bị DAO từ chối (Unique Constraint). Trong Unit test, mock DAO throw `DuplicateKeyException` cho luồng thứ 2.
- `testActivate_Concurrency`: Đảm bảo chỉ 1 thread được kích hoạt và sinh phòng tại 1 thời điểm (kiểm tra behavior mock nếu Servlet thiết kế khóa luồng hoặc dựa vào DB lock).

## 4. Các bước thực hiện
1. Khởi tạo class test `AdminFacilityServletTest`.
2. Setup các mock objects (`FacilityDAO`, `RoomDAO`).
3. Viết nhóm test xử lý danh sách (`doGet`).
4. Viết nhóm test xử lý Tạo/Sửa cơ sở.
5. Viết nhóm test Kích hoạt và Sinh phòng (logic cốt lõi).
6. Viết nhóm test phân quyền, ném lỗi 403, 404.
7. Chạy `mvn test` để xác minh.
