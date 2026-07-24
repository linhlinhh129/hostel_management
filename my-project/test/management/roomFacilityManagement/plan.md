# Implementation Plan: Room & Facility Management (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ManagerFacilitiesServlet.java` (Hoặc các Servlet tương ứng quản lý API/View cơ sở)
- **Dependencies**: `FacilityService`, `RoomService` (mocked)
- **Constraint**: Đảm bảo 100% Unit Test. Focus vào kiểm thử luồng phân quyền dữ liệu (Cross-facility IDOR) và chặn truy cập trái phép.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/manager/ManagerFacilitiesServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewAssignedFacilities_Success`: Xem danh sách cơ sở được phân công.
- `testDoGet_ViewAssignedFacilities_Empty`: Xử lý đúng khi chưa được phân công.
- `testDoGet_ViewRoomsInFacility_Success`: Xem danh sách phòng (phân trang).
- `testDoGet_ViewRoomDetail_Success`: Xem chi tiết phòng hợp lệ.

### 3.2 Error Cases
- `testDoGet_ViewRooms_CrossFacility_Forbidden`: Xem phòng của cơ sở không được phân công (403).
- `testDoGet_ViewRoomDetail_CrossFacility_Forbidden`: Xem chi tiết phòng cơ sở không được phân công (403).
- `testDoGet_FacilityNotFound_Fails`: Truy cập cơ sở không tồn tại (404).
- `testDoGet_RoomNotFound_Fails`: Truy cập phòng không tồn tại (404).
- `testDoGet_UnauthorizedAccess`: Tenant/Admin truy cập (403).
- `testDoGet_UnassignedManager_Forbidden`: Manager chưa có cơ sở cố ý truy cập theo ID (403).

### 3.3 Boundary Values
- `testDoGet_PaginationBounds`: Test tham số phân trang âm, cực lớn.

### 3.4 Concurrent Scenarios
- `testConcurrency_RateLimit`: Test cơ chế Rate limit (100 req/min).

## 4. Các bước thực hiện
1. Setup Unit Test bằng Mockito cho Servlet.
2. Map đầy đủ các thẻ `# EARS` theo Spec vào test case.
