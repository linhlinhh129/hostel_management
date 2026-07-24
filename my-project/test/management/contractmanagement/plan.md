# Implementation Plan: Contract Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ContractServlet.java`
- **Dependencies**: `ContractService`, `ContractDAO`, `AuditLogDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Chặn đứng kịch bản IDOR (thao tác khác Facility), bắt chặt điều kiện nghiệp vụ như CCCD hợp lệ, cấm thuê phòng đã có khách.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ContractServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_LoadContractsList_Success`: Gọi `GET /manager/contracts`, Mock trả về danh sách hợp đồng đúng theo `facilityId`.
- `testDoPost_CreateContract_Success`: Gửi POST request với đủ param (Tên, CCCD 12 số, roomId hợp lệ). Trả về HTTP 302 redirect tới trang danh sách thành công.
- `testDoPost_AddTenant_Success`: Gọi form thêm người thuê `POST /manager/contracts/add-tenant`. Sinh pass, đổi trạng thái phòng.
- `testDoPost_SoftDelete_Success`: Gọi POST /delete với hợp đồng trạng thái `INACTIVE`. Xóa thành công.

### 3.2 Error Cases
- `testDoPost_CreateContract_ValidationFailed`: Bỏ trống tên khách thuê, hoặc CCCD bị rỗng. Trả lại trang `create.jsp` kèm `errorMessage`.
- `testDoPost_CreateContract_RoomAlreadyRented`: Phòng đã có khách thuê (Mock trả về Exception `ROOM_ALREADY_HAS_ACTIVE_CONTRACT`).
- `testDoPost_CreateContract_IDOR_AccessDenied`: Manager A (Facility 1) truyền `roomId` thuộc (Facility 2). Trả về 403.
- `testDoPost_SoftDelete_ActiveContract_Denied`: Xóa hợp đồng đang `ACTIVE`. Bị chặn và báo lỗi qua Flash Message.

### 3.3 Boundary Values
- `testDoPost_CreateContract_CCCD_BoundaryValues`: Gửi CCCD độ dài 9 số (pass), 12 số (pass), 11 số (fail validation).
- `testDoPost_CreateContract_SameDates`: Ngày bắt đầu = Ngày ký hợp đồng. Trả về thành công.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleBooking`: Dùng 2 Thread cùng `POST /create` cho cùng 1 `roomId`. Service văng ngoại lệ chặn Race Condition, luồng 2 phải catch an toàn trả báo lỗi.

## 4. Các bước thực hiện
1. Setup Unit Test với Mockito Extension, Mock `ContractService`.
2. Map test cases với EARS.
