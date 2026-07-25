# Tasks: Quản lý hóa đơn (Loại bỏ Thuế, Thêm Kỳ hợp đồng, Freeze Phí Phạt)

**Input**: Design documents from `/my-project/sdd/specs/manager/invoiceManagement/`

## Phase 1: Models & DTOs (Data Layer Updates)

**Purpose**: Cập nhật cấu trúc dữ liệu để phù hợp với nghiệp vụ mới (Bỏ Thuế, Thêm Kỳ Hợp Đồng)
- [x] T001 [P] [US4] Bổ sung trường `contractPeriod` (String) vào `src/main/java/com/quanlynhatro/dto/InvoiceDetailDTO.java`.
- [x] T002 [P] [US1] Loại bỏ hoàn toàn trường `taxRate` khỏi Entity `src/main/java/com/quanlynhatro/model/Invoice.java`.
- [x] T003 [P] [US4] Loại bỏ trường `taxRate` và `taxAmount` (nếu có) khỏi `InvoiceDetailDTO` và các DTO Create/Update liên quan.

## Phase 2: DAO & Service (Business Logic Updates)

**Purpose**: Cập nhật câu truy vấn DB và logic tính toán
- [x] T004 [US4] Cập nhật phương thức `findById` trong `src/main/java/com/quanlynhatro/dao/InvoiceDAO.java` để `JOIN` bảng `contracts`, lấy `contract_start_date` và `contract_end_date`, sau đó format và gán vào `contractPeriod` của `InvoiceDetailDTO`.
- [x] T005 [P] [US1] Loại bỏ `tax_rate` khỏi các câu lệnh `INSERT`, `UPDATE` trong `InvoiceDAO.java`.
- [x] T006 [US1] Xóa bỏ các công thức tính thuế (taxAmount) và tính lại `totalAmount = subtotal + lateFee` trong `src/main/java/com/quanlynhatro/service/impl/InvoiceServiceImpl.java`.
- [x] T007 [US4] Cập nhật logic "Báo cáo sai số" trong `InvoiceServiceImpl.java` để gửi thông báo (Insert vào bảng `notifications` hoặc ghi log) mà KHÔNG thay đổi trạng thái hóa đơn.
- [x] T008 [P] [US5] Xóa bỏ hoàn toàn endpoint và logic "Xóa hóa đơn" (Delete) khỏi `InvoiceServlet` và `InvoiceService`.

## Phase 3: Views (JSP)

**Purpose**: Cập nhật giao diện người dùng
- [x] T009 [P] [US4] Hiển thị "Kỳ hợp đồng" trong `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp`.
- [x] T010 [P] [US1] Xóa các trường nhập liệu và hiển thị liên quan đến Thuế (Thuế %, Tiền thuế) trong `create.jsp`, `edit.jsp` và `detail.jsp`.
- [x] T011 [P] [US5] Xóa nút "Xóa hóa đơn" khỏi giao diện của `detail.jsp` và `list.jsp` (nếu có).
- [x] T012 [P] [US4] Đảm bảo nút "Báo cáo sai số" gọi đúng endpoint mới (chỉ gửi thông báo) và hiển thị thông báo thành công cho người dùng mà không đổi màu trạng thái.

## Phase 4: Đóng băng phí phạt (Late Fee Freeze)

**Purpose**: Implement tính năng ngưng đếm ngày quá hạn khi có giao dịch thanh toán chờ duyệt
- [x] T015 [US6] Cập nhật truy vấn SQL trong `InvoiceDAO.java` bổ sung cột `pending_payment_date` thông qua subquery tìm payment `PENDING` mới nhất.
- [x] T016 [US6] Trong hàm tính toán phí phạt của `InvoiceDAO.java`, kiểm tra `pending_payment_date`: nếu có thì dùng ngày đó thay vì `LocalDate.now()` làm mốc kết thúc tính số ngày nợ.

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Xác minh tính toàn vẹn của ứng dụng
- [x] T013 Biên dịch lại dự án (`mvn clean package`) để đảm bảo không có lỗi compile sau khi xóa trường `taxRate`.
- [x] T014 Chạy ứng dụng và kiểm tra tạo hóa đơn mới không có thuế, và xem chi tiết hiển thị đúng kỳ hợp đồng.
- [x] T017 Kiểm thử Scenario Đóng băng phí phạt (như mô tả trong `quickstart.md`).

---

## Dependencies & Execution Order
- Phase 1 (Models) cần làm trước tiên để tránh lỗi biên dịch.
- Phase 2 (DAO/Service) phụ thuộc vào Phase 1.
- Phase 3 (Views) có thể làm độc lập về mặt HTML/CSS, nhưng để chạy được phải chờ Phase 1 & 2 hoàn tất.
- Phase 4 (Late Fee Freeze) hoàn toàn thuộc phạm vi truy vấn DB và Logic DAO, có thể độc lập triển khai.
