# Danh sách công việc (Tasks) - Chi tiết Yêu cầu (DetailRequest)

## Phase 1: Re-design Layout (Hoàn thành)
- [x] T001 [US1] Thiết kế lại file `detail.jsp` áp dụng hệ thống Grid để chia cột Nội dung (Giữa) và Cột Thông tin/Hành động (Phải).
- [x] T002 [US1] Di chuyển Tiêu đề, Nội dung mô tả lỗi, Ảnh đính kèm sang khu vực Center Prose.
- [x] T003 [US1] Di chuyển Trạng thái, Thông tin người gửi, Phòng, Ngày tháng sang khu vực Right Panel.
- [x] T004 [US2] Đặt các nút CTA (Nhận/Từ chối) lên Right Panel dưới dạng width 100% (block buttons).
- [x] T005 [P] Cập nhật CSS tùy chỉnh thêm (nếu cần) để phần Right Panel có đường kẻ dọc (hairline border-left) giống hệt thiết kế Right TOC của Mintlify.

## Phase 2: Giới hạn ký tự Ghi chú hoàn thành (Mới cập nhật)
- [x] T006 [US3] Frontend: Thêm thuộc tính `maxlength="1000"` vào trường `textarea` name="notes" trong modal báo cáo hoàn thành tại `src/main/webapp/WEB-INF/views/operator/requests/detail.jsp`. Thêm validation text.
- [x] T007 [US3] Backend: Thêm câu lệnh kiểm tra `notes.length() > 1000` trong `DetailRequestServlet.java` nhánh xử lý `action = "complete"` và trả về lỗi nếu vi phạm.
