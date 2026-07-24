# Tasks: Quản lý khoản phí và giá dịch vụ

> **Lưu ý:** Module này đã được phát triển hoàn thiện trong mã nguồn (`ServicePricePageServlet.java`, `index.jsp`, `history.jsp`). Danh sách các task dưới đây phản ánh chính xác các công việc đã được thực hiện để hoàn thành chức năng này theo đúng `SPEC.md` và `PLAN.md`.

## Phase 1: Setup
- [x] T001 Tạo thư mục giao diện `src/main/webapp/WEB-INF/views/manager/service-prices/`
- [x] T002 Tạo controller `ServicePricePageServlet` tại `src/main/java/com/quanlyphongtro/controller/manager/ServicePricePageServlet.java`

## Phase 2: Foundational
- [x] T003 Thiết lập các hàm đọc/ghi giá từ bảng `facilities` trong `ServicePriceDAO.java`
- [x] T004 Thiết lập hàm lưu và lấy lịch sử cập nhật từ DB

## Phase 3: Story 1 & 8 - Xem danh sách giá và Kiểm tra quyền
- [x] T005 [US1] Khởi tạo giao diện danh sách giá dịch vụ trong `index.jsp`
- [x] T006 [US1] Tích hợp logic `GET /manager/service-prices` trong `ServicePricePageServlet.java` để fetch dữ liệu giá điện, nước, phí dịch vụ.
- [x] T007 [US8] Triển khai logic kiểm tra Session `currentUser` và role `MANAGER` trong `ServicePricePageServlet.java` chặn truy cập trái phép.

## Phase 4: Story 2 - Mở pop-up thay đổi giá
- [x] T008 [US2] Xây dựng UI Modal / Pop-up trên `index.jsp` cho phép nhập giá mới và ghi chú.
- [x] T009 [US2] Cấu hình Form Submit phương thức POST về `/manager/service-prices?action=update`.

## Phase 5: Story 3, 4, 5, 6 - Cập nhật giá (Điện, Nước, Phí dịch vụ) & Validation
- [x] T010 [US3] [US4] [US5] Bổ sung logic xử lý `POST` với action `update` trong `ServicePricePageServlet.java`.
- [x] T011 [US6] Triển khai logic catch `NumberFormatException` và kiểm tra giá `newPrice >= 0` tại Controller.
- [x] T012 [US6] Xử lý lỗi trả về UI thông qua biến `errorMessage` và render lại `index.jsp` khi có lỗi nhập liệu.

## Phase 6: Story 7 - Lịch sử thay đổi
- [x] T013 [US7] Khi cập nhật thành công, thực hiện ghi log vào bảng lịch sử tại hàm `updatePrice` trong Service.
- [x] T014 [US7] Khởi tạo giao diện `history.jsp` để hiển thị lịch sử thay đổi giá theo `priceType`.
- [x] T015 [US7] Tích hợp logic `GET` với `action=history` phân trang danh sách lịch sử tại Controller.

## Phase 7: Polish & Cross-Cutting
- [x] T016 Định dạng số tiền có dấu phẩy (vd: 10,000) trên `index.jsp` và `history.jsp`.
- [x] T017 Đảm bảo tất cả thông báo thành công và thất bại đều được hiển thị rõ ràng trên UI.
