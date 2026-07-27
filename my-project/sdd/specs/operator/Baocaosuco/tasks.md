# Tasks: Báo cáo sự cố (Operator)

**Input**: Design documents from `my-project/sdd/specs/operator/Baocaosuco/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), data-model.md

## Format: `[ID] [P?] [Story] Description`

## Phase 1: Setup (Shared Infrastructure)
- [x] T001 Khởi tạo thư mục và xác nhận file DAO đã có phương thức `insertIncidentReport(Request)`.

## Phase 2: Foundational (Blocking Prerequisites)
- [x] T002 Tạo Controller mới cho việc xử lý Báo cáo sự cố.

## Phase 3: User Story 1 & 2 - Giao diện Form và Upload Ảnh (P1)
- [x] T003 [P] [US1] Tạo trang JSP mới `src/main/webapp/WEB-INF/views/operator\incidents\create.jsp` chứa HTML Form.
- [x] T004 [P] [US1] Bổ sung JS để hiển thị Dropdown `Hành lang, cầu thang...` hoặc `Phòng` dựa vào Radio button "Khu vực chung" hay "Phòng".
- [x] T005 [P] [US2] Thêm JS/UI tính năng chọn ảnh và Preview ảnh thu nhỏ ở Frontend.

## Phase 4: Validation (Tiêu đề, Chi tiết vị trí, Mô tả)
- [x] T006 [P] [US1] Thêm Validation (HTML5 `maxlength`, `required`) cho Tiêu đề (50 ký tự), Chi tiết vị trí (50 ký tự), Mô tả (1000 ký tự) ở `create.jsp`.
- [x] T007 [P] [US1] Bổ sung JS báo lỗi viền đỏ nếu người dùng cố tình lách validation.

## Phase 5: Gửi Báo Cáo (Backend)
- [x] T008 [US3] Tạo Servlet `src/main/java/com/quanlyphongtro/controller/operator/IncidentReportServlet.java`.
- [x] T009 [US3] Xử lý `doGet` chuyển hướng sang `create.jsp`.
- [x] T010 [US3] Xử lý `doPost` tiếp nhận form data, thêm Backend Validation: kiểm tra Tiêu đề, Chi tiết vị trí, Mô tả theo các limit. Trả về thông báo lỗi nếu có.
- [x] T011 [US3] Gọi `RequestDAO.insertIncidentReport` để chèn vào DB, set người gửi là `staff_id` từ Session. Trả về Toast success.

## Dependencies & Execution Order
- T003, T004, T005, T006, T007 có thể thực hiện trước hoặc song song với các task Backend (T008-T011).
- T011 phụ thuộc vào dữ liệu từ form và các hàm DAO đã có sẵn.
