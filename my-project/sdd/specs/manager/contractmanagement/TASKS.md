# TASKS.md: Quản lý hợp đồng (Contract Management)

> **Lưu ý:** Module này đã được phát triển hoàn thiện trong mã nguồn (`ContractServlet.java` và các views `.jsp` tương ứng). Danh sách các task dưới đây phản ánh chính xác các công việc đã được thực hiện để xây dựng chức năng này theo chuẩn `SPEC.md` và `PLAN.md`.

## Phase 1: Setup
- [x] T001 Khởi tạo thư mục `src/main/webapp/WEB-INF/views/manager/contracts/`
- [x] T002 Khởi tạo Controller `ContractServlet.java` tại `src/main/java/com/quanlyphongtro/controller/manager/ContractServlet.java`

## Phase 2: Foundational
- [x] T003 Thiết lập DAO đọc ghi dữ liệu từ bảng `contracts` (`ContractDAO.java`)
- [x] T004 Viết các câu truy vấn cơ sở dữ liệu (`JOIN` bảng `contracts`, `rooms`, `facilities` và `users`) phục vụ hiển thị chi tiết

## Phase 3: Story 1 & 6 - Danh sách hợp đồng & Phân quyền
- [x] T005 [US1] Xây dựng giao diện `list.jsp` hiển thị danh sách các hợp đồng (Mã HĐ, Khách thuê, Trạng thái...)
- [x] T006 [US1] Tích hợp logic xử lý `GET /manager/contracts` lấy danh sách hợp đồng qua `managerId`
- [x] T007 [US6] Cấu hình bảo mật qua biến session `currentUser`, từ chối (403) nếu không phải Role `MANAGER` hoặc `ADMIN`

## Phase 4: Story 2 & 5 - Xem chi tiết & In hợp đồng
- [x] T008 [US2] Xây dựng giao diện `detail.jsp` hiển thị thông tin snapshot phòng, tiền cọc và khách thuê
- [x] T009 [US2] Tích hợp logic xử lý `GET /manager/contracts/detail?id=...` để truyền data `contract` xuống JSP
- [x] T010 [US5] Thiết lập layout hiển thị bản in trên trình duyệt (sử dụng CSS media queries `@media print`)

## Phase 5: Story 3 & 4 - Tạo hợp đồng mới & Snapshot thông tin phòng
- [x] T011 [US3] Thiết kế form tạo hợp đồng `create.jsp` với các trường điền khách thuê, chọn phòng, ngày tháng
- [x] T012 [US4] Xử lý `GET /manager/contracts/create` để đổ ra danh sách các phòng còn trống (`availableRooms`) của cơ sở
- [x] T013 [US3] Xử lý `POST /manager/contracts/create`: Validate dữ liệu Date, sinh tự động `contract_id` và mã `code`
- [x] T014 [US4] [US3] Tự động snapshot (lưu cứng) `rent_price` và `deposit_amount` vào bản ghi `contracts` tại thời điểm tạo

## Phase 6: Story 7 - Tạo tài khoản người thuê từ hợp đồng
- [x] T015 [US7] Xây dựng form `add_tenant.jsp` pre-fill dữ liệu lấy từ hợp đồng cũ
- [x] T016 [US7] Xử lý `GET /manager/contracts/add-tenant` để load form
- [x] T017 [US7] Xử lý `POST /manager/contracts/add-tenant`: Validate trùng lặp Email/CCCD, tạo Account Tenant mới, gán `tenant_id` vào hợp đồng, chuyển status phòng thành OCCUPIED

## Phase 7: Story 8 - Xóa (Soft delete)
- [x] T018 [US8] Hiển thị nút "Xóa" trên giao diện chi tiết hoặc danh sách, chỉ enable khi `status` = `INACTIVE`
- [x] T019 [US8] Triển khai `POST /manager/contracts/delete`, bắt Exception và reject nếu cố tình xóa hợp đồng đang `ACTIVE`
