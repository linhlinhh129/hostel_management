# Implementation Tasks: Quản lý Cơ sở Tests

## Phase 1: Setup
- [X] T001 Khởi tạo file `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T002 Khai báo các `@Mock` cho `FacilityDAO`, `RoomDAO`, Request, Response, Session và `@InjectMocks` tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.

## Phase 2: Danh sách và Chi tiết [US1]
- [X] T003 [P] [US1] Viết test kiểm tra `doGet` lấy danh sách cơ sở có hỗ trợ phân trang và filter tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T004 [P] [US1] Viết `testList_EmptyDatabase` (danh sách rỗng) tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.

## Phase 3: Happy Path & Chỉnh sửa (DRAFT/ACTIVE) [US2]
- [X] T005 [P] [US2] Viết `testDoPost_Create_Success` tạo mới cơ sở DRAFT, chuyển code thành in hoa tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T006 [P] [US2] Viết `testDoPost_UpdateDraft_Success` kiểm tra cho phép sửa đủ thông tin tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.

## Phase 4: Error Cases & Validation [US3]
- [X] T007 [P] [US3] Viết `testDoPost_Create_MissingFields` gửi form rỗng tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T008 [P] [US3] Viết `testDoPost_Create_CodeExists` mock trùng lặp ID (Lỗi 409) tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T009 [P] [US3] Viết `testDoPost_UpdateActive_ChangeImmutableFields` cố tình đổi Code khi đã ACTIVE (Lỗi 400) tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T010 [P] [US3] Viết test kiểm tra Validate Boundary (Tầng/Phòng min 1 max 99, chuỗi ký tự độ dài chuẩn) tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.

## Phase 5: Kích hoạt, Sinh phòng & Vô hiệu hóa (Core Logic) [US4]
- [X] T011 [P] [US4] Viết `testDoPost_Activate_Success` đảm bảo DAO insert phòng đủ số lượng (tầng x phòng) với code chuẩn tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T012 [P] [US4] Viết `testDoPost_Activate_InvalidStatus` kiểm tra kích hoạt cơ sở đã ACTIVE tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T013 [P] [US4] Viết `testDoPost_Activate_RoomGenerationFails_Rollback` mock sinh phòng lỗi và kiểm tra Rollback về DRAFT tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.
- [X] T014 [P] [US4] Viết `testDoPost_Deactivate_Success` cập nhật trạng thái INACTIVE tại `src/test/java/com/quanlyphongtro/controller/admin/AdminFacilityServletTest.java`.

## Phase 6: Polish & Execute
- [X] T015 Chạy `mvn test -Dtest=AdminFacilityServletTest` để đảm bảo suite tests hoạt động hoàn hảo.
