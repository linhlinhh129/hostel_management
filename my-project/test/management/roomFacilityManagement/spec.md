# Test Specification: Quản lý phòng và cơ sở (Room & Facility Management)

**File bị ảnh hưởng**: Các Servlet xử lý API quản lý cơ sở và phòng (Ví dụ: `ManagerFacilitiesServletTest.java`, `ManagerRoomsServletTest.java` hoặc gộp chung tùy kiến trúc).
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập DB. Tập trung vào kiểm tra tính bảo mật (Chỉ cho phép Manager xem dữ liệu thuộc cơ sở được phân công).

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewAssignedFacilities_Success`: KHI truy cập API danh sách cơ sở, THE SYSTEM SHALL trả về list cơ sở mà Manager đó được phân công quản lý.
- `testDoGet_ViewAssignedFacilities_Empty`: KHI Manager chưa được phân công cơ sở nào, THE SYSTEM SHALL hiển thị thông báo "Bạn chưa được phân công cơ sở quản lý" (hoặc trả list rỗng/thông báo tương ứng).
- `testDoGet_ViewRoomsInFacility_Success`: KHI truy cập danh sách phòng của cơ sở ĐƯỢC phân công, THE SYSTEM SHALL trả về danh sách phòng kèm phân trang.
- `testDoGet_ViewRoomDetail_Success`: KHI xem chi tiết phòng thuộc cơ sở ĐƯỢC phân công, THE SYSTEM SHALL trả về đầy đủ thông tin phòng (ID, Mã phòng, Tầng, Trạng thái...).

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoGet_ViewRooms_CrossFacility_Forbidden`: KHI truy cập danh sách phòng của cơ sở KHÔNG được phân công, THE SYSTEM SHALL trả về 403 `FACILITY_ACCESS_DENIED`.
- `testDoGet_ViewRoomDetail_CrossFacility_Forbidden`: KHI xem chi tiết phòng thuộc cơ sở KHÔNG được phân công, THE SYSTEM SHALL trả về 403 `FACILITY_ACCESS_DENIED`.
- `testDoGet_FacilityNotFound_Fails`: KHI truy cập cơ sở không tồn tại, THE SYSTEM SHALL trả về 404 `FACILITY_NOT_FOUND`.
- `testDoGet_RoomNotFound_Fails`: KHI xem phòng không tồn tại, THE SYSTEM SHALL trả về 404 `ROOM_NOT_FOUND`.
- `testDoGet_UnauthorizedAccess`: KHI người dùng không có vai trò MANAGER truy cập, THE SYSTEM SHALL chặn 403.
- `testDoGet_UnassignedManager_Forbidden`: KHI Manager chưa được phân công cố tình truy cập vào 1 ID cơ sở bất kỳ, THE SYSTEM SHALL trả về 403.

## 3. Boundary Values (Giá trị biên)

- `testDoGet_PaginationBounds`: Test phân trang với trang âm, trang vượt quá tổng số trang. THE SYSTEM SHALL xử lý thành trang 1 hoặc trả rỗng hợp lệ.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- `testConcurrency_RateLimit`: Bắn 101 requests/phút vào API. THE SYSTEM SHALL bắt đầu chặn và trả HTTP 429 từ request 101.
