# Danh sách công việc (Tasks) - ListElectric

**Last Updated:** 2026-07-27 — Đồng bộ với code thực tế

---

## ✅ Đã hoàn thành (Phase 1 — MVP ban đầu)

- [x] Tạo DTO `MeterStatusDTO` để chứa dữ liệu bảng.
- [x] Tạo DAO `MeterReadingDAO` với hàm `getMeterStatusList()` sử dụng SQL Server thuần (LEFT JOIN + OUTER APPLY).
- [x] Tạo `MeterReadingService` kết nối DAO.
- [x] Tạo `ListElectricServlet` đón đường dẫn `/operator/meter-readings` và forward sang JSP.
- [x] Xây dựng UI `list.jsp` hiển thị dạng Bảng theo giao diện Mintlify.
- [x] Tích hợp CSS Mintlify cho Table và badge trạng thái.
- [x] Test luồng với dữ liệu tháng hiện tại.

---

## ✅ Đã hoàn thành (Phase 2 — Tính năng mở rộng)

- [x] Thêm **Filter bar** theo mã phòng (`roomCode`) và cơ sở (`facility`).
- [x] Thêm **phân trang client-side** (`clientPaginate`) ở cuối bảng.
- [x] Thêm **cột Thao tác** với nút "Cập nhật" / "Sửa" dẫn đến `UpdateMeterReadingServlet`.
- [x] Tạo `UpdateMeterReadingServlet` xử lý nhập mới + sửa chỉ số, bao gồm upload ảnh công tơ.
- [x] Mở rộng DTO `MeterStatusDTO` thêm các field: `roomId`, `meterId`, `currentElectricReading`, `currentWaterReading`, `electricImg`, `waterImg`, `updatedByName`, `invoicePaid`.
- [x] Cập nhật SQL trong `getMeterStatusList()`: JOIN `invoices` để lấy cờ `invoicePaid`.
- [x] Thêm method `isInvoicePaidForMonth(roomId, month, year)` vào DAO + Service.
- [x] Thêm **guard khóa cập nhật** trong `UpdateMeterReadingServlet`: nếu hóa đơn tháng hiện tại đã PAID → flash error + redirect.
- [x] Cập nhật UI `list.jsp` cột Thao tác: hiển thị badge **"🔒 Đã thanh toán"** khi `invoicePaid = true`.
- [x] Thêm `operatorId` filter vào DAO để phân quyền nhân viên vận hành chỉ thấy phòng của cơ sở mình quản lý.

---

## 🔲 Chưa thực hiện / Ngoài scope hiện tại

- [ ] Lọc theo tháng/năm để xem lịch sử kỳ trước.
- [ ] Xuất danh sách ra file Excel/PDF.
- [ ] Chức năng chốt sổ hàng loạt (bulk close).
