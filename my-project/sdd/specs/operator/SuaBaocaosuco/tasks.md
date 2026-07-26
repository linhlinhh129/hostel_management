# Tasks: Sửa Báo cáo sự cố (Edit Incident Report)

## Phase 1: Setup (Shared Infrastructure)
- [ ] T001 Xác nhận method `getRequestById` và `updateIncidentReport` hoạt động bình thường trong file `src/main/java/com/quanlyphongtro/dao/RequestDAO.java`.

## Phase 2: Foundational (Blocking Prerequisites)
- [ ] T002 Tạo Controller mới cho việc xử lý Sửa báo cáo sự cố.

## Phase 3: User Story 1 - Giao diện Form và Đổ dữ liệu cũ (P1)
- [x] T003 [P] [US1] Tạo trang JSP mới `src/main/webapp/WEB-INF/views/operator\incidents\edit.jsp` chứa HTML Form (copy cấu trúc từ `create.jsp`).
- [x] T004 [P] [US1] Tích hợp logic binding dữ liệu cũ (`${incident.title}`, `${incident.content}`) vào HTML form ở `edit.jsp`.
- [x] T005 [P] [US1] Thêm Validation (HTML5 `maxlength`, `required`) cho Tiêu đề (50 ký tự), Chi tiết vị trí (50 ký tự), Mô tả (1000 ký tự) ở `edit.jsp`.
- [x] T006 [P] [US1] Bổ sung JS báo lỗi viền đỏ nếu người dùng cố tình lách validation.

## Phase 4: Gửi Yêu Cầu Cập Nhật (Backend)
- [x] T007 [US1] Tạo Servlet `src/main/java/com/quanlyphongtro/controller/operator/EditIncidentReportServlet.java`.
- [x] T008 [US1] Xử lý `doGet` tiếp nhận `request_id`, kiểm tra quyền sở hữu (`sender_id` trùng khớp) và gọi `getRequestById` truyền data sang `edit.jsp`.
- [x] T009 [US1] Xử lý `doPost` tiếp nhận form data, thêm Backend Validation: kiểm tra Tiêu đề, Chi tiết vị trí, Mô tả theo các limit. Trả về thông báo lỗi nếu có.
- [x] T010 [US1] Trích xuất hình ảnh đính kèm (upload) nếu có sửa đổi trong Form.
- [x] T011 [US1] Gọi `RequestDAO.updateIncidentReport` để chèn vào DB với các điều kiện: `sender_id` và `status='PENDING'`. Trả về Toast success.

## Phase 5: Polish & Cross-Cutting Concerns
- [x] T012 Bổ sung route hiển thị nút "Sửa" trên màn hình Lịch sử (nếu chưa có).
- [x] T013 Thực hiện các kịch bản kiểm thử trong `quickstart.md` để đảm bảo hệ thống phản hồi chính xác.

## Implementation Strategy
- **Ưu tiên:** Triển khai khung Servlet (`doGet`) và binding dữ liệu JSP (`edit.jsp`) trước, sau đó bổ sung Validation và logic `doPost`.
- **MVP (Minimal Viable Product):** Các field text cơ bản được validate đúng theo spec (Tiêu đề, Vị trí <= 50, Mô tả <= 1000).

## Dependencies & Execution Order
- T003, T004, T005, T006 có thể thực hiện song song với việc xây dựng bộ khung của T007 và T008.
- T009-T011 phải đợi T008 hoàn tất để đảm bảo luồng truy cập được bảo vệ (kiểm tra `status`).
