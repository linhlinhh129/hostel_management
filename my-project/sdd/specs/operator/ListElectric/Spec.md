# Feature: Danh sách chỉ số điện nước các phòng

**Status:** Draft
**Author:** Tú Anh
**Date:** 2026-06-11

---

## User Story

**As a** nhân viên vận hành,

**I want to** xem danh sách chỉ số điện nước của tất cả các phòng,

**so that** tôi có thể theo dõi tình trạng cập nhật chỉ số điện nước trong kỳ hiện tại và thực hiện công tác quản lý, đối soát dữ liệu.

---

## Acceptance Criteria (EARS)

### AC01 – Hiển thị danh sách thành công

**WHEN** người dùng truy cập màn hình danh sách chỉ số điện nước

**THE SYSTEM SHALL** hiển thị danh sách các phòng gồm:

* Mã phòng
* Số điện kỳ trước
* Số nước kỳ trước
* Thời gian cập nhật gần nhất
* Trạng thái cập nhật

### AC02 – Trạng thái chưa cập nhật

**WHEN** phòng chưa có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
CHUA_CAP_NHAT
```

### AC03 – Trạng thái đã cập nhật

**WHEN** phòng đã có bản ghi chỉ số điện nước trong kỳ hiện tại

**THE SYSTEM SHALL** hiển thị trạng thái:

```text
DA_CAP_NHAT
```

### AC04 – Không có dữ liệu

**WHEN** hệ thống không tìm thấy dữ liệu phòng

**THE SYSTEM SHALL** hiển thị danh sách rỗng.

---

## Technical Notes

### API Endpoint

```http
GET /api/v1/meter-readings
```

### Database Source

```text
rooms
meter_readings
```

### Hiển thị dữ liệu

| Cột | Mô tả |
|------|--------|
| roomCode | Mã phòng |
| previousElectricReading | Số điện kỳ trước |
| previousWaterReading | Số nước kỳ trước |
| updatedAt | Thời gian cập nhật gần nhất |
| status | Trạng thái cập nhật |

### Mapping trạng thái

| Điều kiện | Giá trị hiển thị |
|------------|------------------|
| Chưa có dữ liệu kỳ hiện tại | Chưa cập nhật |
| Đã có dữ liệu kỳ hiện tại | Đã cập nhật |

### Validation

* Không yêu cầu nhập liệu.
* Chỉ đọc dữ liệu từ cơ sở dữ liệu.
* Chỉ hiển thị các phòng đang hoạt động.

---

## Response Sample

```json
{
  "success": true,
  "data": [
    {
      "roomCode": "P101",
      "previousElectricReading": 1200,
      "previousWaterReading": 350,
      "updatedAt": "2026-06-11T09:30:00Z",
      "status": "DA_CAP_NHAT"
    },
    {
      "roomCode": "P102",
      "previousElectricReading": 980,
      "previousWaterReading": 270,
      "updatedAt": null,
      "status": "CHUA_CAP_NHAT"
    }
  ]
}
```

---

## Out of Scope

* Chỉnh sửa chỉ số điện nước.
* Xóa bản ghi chỉ số điện nước.
* Upload ảnh công tơ.
* Xuất Excel/PDF.
* Tìm kiếm và lọc dữ liệu.
* Chức năng chốt sổ điện nước.
