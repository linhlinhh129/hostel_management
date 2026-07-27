# Kế hoạch phát triển tính năng Danh sách chỉ số điện nước (ListElectric)

**Last Updated:** 2026-07-27 — Đồng bộ với code thực tế

---

## 1. Mục tiêu

Xây dựng trang xem danh sách tình trạng chốt số điện nước của tất cả các phòng trong tháng hiện tại. Giúp Operator biết được phòng nào đã chốt số (`DA_CAP_NHAT`) và phòng nào chưa (`CHUA_CAP_NHAT`). Màn hình cho phép nhân viên thực hiện cập nhật chỉ số trực tiếp từ danh sách, **ngoại trừ** phòng đã có hóa đơn PAID trong tháng.

---

## 2. Thiết kế Giao diện (Frontend)

- **File:** `src/main/webapp/WEB-INF/views/operator/meter_readings/list.jsp`
- **Phong cách:** Mintlify `DESIGN.md`.
  - Hiển thị dưới dạng Bảng (Table) gọn gàng.
  - Các cột: Mã phòng, Số điện kỳ này, Số nước kỳ này, Thời gian cập nhật, Trạng thái, **Thao tác**.
  - Cột Trạng thái sử dụng badge màu sắc: Xanh lá (Đã cập nhật), Vàng/Cam (Chưa cập nhật).
  - **Cột Thao tác:**
    - Hóa đơn chưa thanh toán + chưa cập nhật → nút **"Cập nhật"**
    - Hóa đơn chưa thanh toán + đã cập nhật → nút **"Sửa"**
    - Hóa đơn đã thanh toán (PAID) → badge **"🔒 Đã thanh toán"** (không có nút)
  - **Filter bar** phía trên bảng: lọc theo Mã phòng và Cơ sở.
  - **Phân trang client-side** (`clientPaginate`) ở cuối bảng.

---

## 3. Thiết kế Hệ thống (Backend)

### DTO: `com.quanlyphongtro.dto.MeterStatusDTO`

| Field | Kiểu | Mô tả |
|---|---|---|
| `roomId` | `int` | ID phòng |
| `roomCode` | `String` | Mã phòng |
| `previousElectricReading` | `Integer` | Chỉ số điện kỳ trước |
| `previousWaterReading` | `Integer` | Chỉ số nước kỳ trước |
| `currentElectricReading` | `Integer` | Chỉ số điện kỳ này |
| `currentWaterReading` | `Integer` | Chỉ số nước kỳ này |
| `updatedAt` | `Timestamp` | Thời gian cập nhật |
| `status` | `String` | `DA_CAP_NHAT` / `CHUA_CAP_NHAT` |
| `meterId` | `Integer` | ID bản ghi meter |
| `electricImg` | `String` | URL ảnh công tơ điện |
| `waterImg` | `String` | URL ảnh công tơ nước |
| `updatedByName` | `String` | Tên người cập nhật |
| `invoicePaid` | `boolean` | `true` nếu hóa đơn tháng này đã PAID |

### DAO: `com.quanlyphongtro.dao.MeterReadingDAO`

- `getMeterStatusList(int currentMonth, int currentYear, String facility, String roomCode, Integer operatorId)`:
  - LEFT JOIN `meter_readings` kỳ này
  - LEFT JOIN `invoices` để lấy cờ `invoicePaid`
  - OUTER APPLY để lấy chỉ số kỳ trước
  - WHERE `r.deleted_at IS NULL` + filter động theo `operatorId`, `roomCode`, `facility`
- `isInvoicePaidForMonth(int roomId, int month, int year)`:
  - Kiểm tra hóa đơn của phòng trong tháng/năm đã PAID chưa. Dùng trong `UpdateMeterReadingServlet` để chặn cập nhật.

### Service: `com.quanlyphongtro.service.MeterReadingService`

- `getMeterStatusForCurrentMonth(String facility, String roomCode, Integer operatorId)` — lấy tháng hiện tại tự động.
- `getMeterStatusList(int month, int year, String facility, String roomCode, Integer operatorId)` — lấy theo tháng/năm chỉ định.
- `isInvoicePaidForMonth(int roomId, int month, int year)` — delegate check paid.
- `insertMeterReading(...)`, `updateMeterReading(...)`, `checkCurrentMonthReadingExists(...)` — các phương thức ghi/sửa dữ liệu.

### Servlet: `com.quanlyphongtro.controller.operator.ListElectricServlet`

- Ánh xạ URL: `GET /operator/meter-readings`
- Đọc params `roomCode`, `facility` từ request.
- Gọi `MeterReadingService` → set attribute `meterList`, `facilities`, `searchRoomCode` → forward sang `list.jsp`.

### Servlet: `com.quanlyphongtro.controller.operator.UpdateMeterReadingServlet`

- Ánh xạ URL: `POST /operator/meter-readings/update`
- **Guard:** Gọi `isInvoicePaidForMonth()` — nếu true → flash error + redirect, không xử lý tiếp.
- Xử lý upload ảnh, insert hoặc update bản ghi `meter_readings`.

---

## 4. Xử lý logic kỳ trước / kỳ này

- Nếu tồn tại bản ghi trong `meter_readings` có `MONTH(reading_date) == currentMonth` thì `status = DA_CAP_NHAT`, ngược lại là `CHUA_CAP_NHAT`.
- Số điện/nước kỳ trước: lấy bản ghi có `reading_date` lớn nhất nhưng nhỏ hơn tháng hiện tại (OUTER APPLY TOP 1 ORDER BY DESC).
- `invoicePaid`: LEFT JOIN `invoices` ON `inv.meter_id = curr_mr.meter_id` → `CASE WHEN inv.status = 'PAID' THEN 1 ELSE 0 END`.
