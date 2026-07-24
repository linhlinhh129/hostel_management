# Implementation Tasks: Admin Dashboard Tests

## Phase 1: Setup
- [X] T001 Khởi tạo file `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java` và khai báo các `@Mock` (HttpServletRequest, Response, Session, RevenueDAO, FacilityDAO, NotificationDAO, AuditLogDAO).
- [X] T002 Cấu hình `@InjectMocks` cho `AdminDashboardServlet` và setup `BeforeEach` cơ bản.

## Phase 2: Happy Path Tests [US1]
- [X] T003 [P] [US1] Viết `testDoGet_Success_WithData` kiểm tra luồng trả về đầy đủ số liệu KPI tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
- [X] T004 [P] [US1] Viết `testDoGet_Success_EmptyData` kiểm tra luồng trả về giá trị 0 hoặc rỗng tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.

## Phase 3: Error Cases Tests [US2]
- [X] T005 [P] [US2] Viết `testDoGet_WithoutSession_RedirectsToLogin` kiểm tra chặn chưa đăng nhập tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
- [X] T006 [P] [US2] Viết `testDoGet_RoleManager_Returns403` kiểm tra chặn quyền Manager tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
- [X] T007 [P] [US2] Viết `testDoGet_DaoThrowsException` kiểm tra khả năng bắt exception của DAO không làm crash trang tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.

## Phase 4: Boundary & Concurrent Tests [US3]
- [X] T008 [P] [US3] Viết `testRecentActivities_Limit5` kiểm tra giới hạn 5 bản ghi tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
- [X] T009 [P] [US3] Viết `testMonthlyRevenue_OnlyPaid` kiểm tra điều kiện trạng thái PAID tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
- [X] T010 [P] [US3] Viết `testDoGet_Concurrency_NoSharedState` giả lập multi-threading gọi doGet tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.

## Phase 5: Polish & Execute
- [X] T011 Chạy `mvn test -Dtest=AdminDashboardServletTest` để xác minh tất cả test case đều PASS tại `src/test/java/com/quanlyphongtro/controller/admin/AdminDashboardServletTest.java`.
