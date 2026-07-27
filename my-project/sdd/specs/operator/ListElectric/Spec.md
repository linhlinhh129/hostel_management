# Feature: Danh sách chỉ số điện nước các phòng

**Status:** Implemented
**Author:** Tú Anh
**Date:** 2026-06-11
**Last Updated:** 2026-07-27

---

## User Story

**As a** nhân viên vận hành,

**I want to** xem danh sách chỉ số điện nước của tất cả các phòng, lọc theo mã phòng hoặc cơ sở, và cập nhật chỉ số điện nước cho từng phòng,

**so that** tôi có thể theo dõi tình trạng cập nhật chỉ số điện nước trong kỳ hiện tại, thực hiện công tác quản lý, đối soát dữ liệu và nhập liệu kịp thời trước khi xuất hóa đơn.

---

## Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách thành công

**WHEN** người dùng truy cập màn hình danh sách chỉ số điện nước

**THE SYSTEM SHALL** hiển thị danh sách các phòng gồm các cột:

* Mã phòng
* Số điện kỳ này (currentElectricReading)
* Số nước kỳ này (currentWaterReading)
* Thời gian cập nhật gần nhất (updatedAt)
* Trạng thái cập nhật (status)
* Thao tác (nút Cập nhật / Sửa / badge khóa)

### AC02 – Trạng thái chưa cập nhật

**WHEN** phòng chưa có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
CHUA_CAP_NHAT
```

và hiển thị nút **"Cập nhật"** cho phép nhân viên nhập mới chỉ số.

### AC03 – Trạng thái đã cập nhật

**WHEN** phòng đã có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
DA_CAP_NHAT
```

và hiển thị nút **"Sửa"** cho phép chỉnh lại chỉ số (nếu hóa đơn chưa thanh toán).

### AC04 – Không có dữ liệu

**WHEN** hệ thống không tìm thấy dữ liệu phòng

**THE SYSTEM SHALL** hiển thị dòng thông báo "Không có dữ liệu hiển thị." thay cho bảng trống.

### AC05 – Khóa cập nhật khi hóa đơn đã thanh toán

**WHEN** hóa đơn của phòng trong tháng hiện tại có `status = 'PAID'`

**THE SYSTEM SHALL:**
* Hiển thị badge **"🔒 Đã thanh toán"** thay cho nút hành động trong cột Thao tác.
* Chặn mọi request POST đến `/operator/meter-readings/update` cho phòng và tháng đó, trả về flash error: *"Không thể cập nhật chỉ số điện nước… vì hóa đơn tháng này đã được thanh toán."*
* Không cho phép cập nhật cho đến kỳ tháng tiếp theo.

### AC06 – Bộ lọc tìm kiếm

**WHEN** người dùng nhập mã phòng hoặc chọn cơ sở rồi nhấn "Lọc danh sách"

**THE SYSTEM SHALL** trả về danh sách chỉ gồm các phòng khớp với điều kiện lọc.

**WHEN** người dùng nhấn "Xóa bộ lọc"

**THE SYSTEM SHALL** hiển thị lại toàn bộ danh sách không lọc.

### AC07 – Phân trang client-side

**WHEN** số lượng phòng vượt quá ngưỡng hiển thị mặc định

**THE SYSTEM SHALL** hiển thị phân trang phía client, cho phép điều hướng qua các trang mà không reload.

---

## 4. Giao tiếp Hệ thống (System Flow)

### Đường dẫn (Endpoint)
* **Endpoint:** `GET /operator/meter-readings`
* **Query params:** `roomCode` (optional), `facility` (optional)
* **Loại dữ liệu (Content-Type):** Trả về HTML (JSP)

### Phản hồi Hệ thống (System Response)
* **Thành công (OK):** Forward đến trang giao diện `/WEB-INF/views/operator/meter_readings/list.jsp`.
* Các dữ liệu render trên JSP bao gồm:

| Attribute JSP | Nguồn gốc | Mô tả |
|---|---|---|
| `roomCode` | `rooms.code` | Mã phòng |
| `previousElectricReading` | `meter_readings` kỳ trước | Số điện kỳ trước |
| `previousWaterReading` | `meter_readings` kỳ trước | Số nước kỳ trước |
| `currentElectricReading` | `meter_readings` kỳ này | Số điện kỳ này |
| `currentWaterReading` | `meter_readings` kỳ này | Số nước kỳ này |
| `updatedAt` | `meter_readings.updated_at` | Thời gian cập nhật |
| `status` | Computed | `DA_CAP_NHAT` / `CHUA_CAP_NHAT` |
| `meterId` | `meter_readings.meter_id` | ID bản ghi |
| `electricImg` | `meter_readings.electric_img` | Ảnh công tơ điện |
| `waterImg` | `meter_readings.water_img` | Ảnh công tơ nước |
| `updatedByName` | `users.full_name` | Người cập nhật |
| `invoicePaid` | `invoices.status = 'PAID'` | Cờ khóa cập nhật |

---

## Out of Scope

* Xóa bản ghi chỉ số điện nước.
* Xuất Excel/PDF.
* Chức năng chốt sổ điện nước hàng loạt.
* Lịch sử xem theo tháng trước (chỉ hiển thị tháng hiện tại).
