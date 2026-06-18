# Feature: Cập nhật chỉ số điện nước

**Status:** Draft  
**Author:** Tú Anh  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-11  
**Priority:** High

---

## 1. Business Context

Nhân viên vận hành cần cập nhật chỉ số điện và nước định kỳ cho từng phòng để phục vụ việc tính hóa đơn hàng tháng.

Hệ thống phải lưu lại chỉ số mới, hình ảnh công tơ làm minh chứng và lịch sử cập nhật nhằm đảm bảo tính minh bạch, hỗ trợ đối soát dữ liệu và xử lý khi phát sinh tranh chấp.

---

## 2. User Stories

### Story 1 (Happy Path)

**As a** nhân viên vận hành,

**I want to** cập nhật chỉ số điện nước của phòng,

**so that** hệ thống có dữ liệu tính chi phí điện nước cho kỳ hiện tại.

### Story 2 (Validation)

**As a** nhân viên vận hành,

**when** nhập chỉ số mới nhỏ hơn chỉ số kỳ trước,

**I want** hệ thống cảnh báo lỗi để tránh nhập sai dữ liệu.

### Story 3 (Evidence)

**As a** nhân viên vận hành,

**I want to** tải lên ảnh công tơ điện và nước,

**so that** hệ thống lưu lại bằng chứng cho lần ghi nhận chỉ số.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Cập nhật thành công

**WHEN** người dùng nhập mã phòng hợp lệ, chỉ số điện mới hợp lệ, chỉ số nước mới hợp lệ và tải đủ ảnh công tơ

**THE SYSTEM SHALL**

- Lấy chỉ số điện kỳ trước từ cơ sở dữ liệu.
- Lấy chỉ số nước kỳ trước từ cơ sở dữ liệu.
- Lưu chỉ số điện mới.
- Lưu chỉ số nước mới.
- Lưu ảnh công tơ điện.
- Lưu ảnh công tơ nước.
- Ghi nhận thời gian cập nhật.
- Ghi nhận người thực hiện cập nhật.
- Đặt trạng thái bản ghi là `UPDATED`.

**AND** trả về HTTP 200.

### AC02 – Chỉ số điện không hợp lệ

**WHEN** chỉ số điện mới nhỏ hơn chỉ số điện kỳ trước

**THE SYSTEM SHALL** trả về HTTP 400 với mã lỗi:

```text
ELECTRIC_READING_INVALID
```

### AC03 – Chỉ số nước không hợp lệ

**WHEN** chỉ số nước mới nhỏ hơn chỉ số nước kỳ trước

**THE SYSTEM SHALL** trả về HTTP 400 với mã lỗi:

```text
WATER_READING_INVALID
```

### AC04 – Thiếu ảnh công tơ điện

**WHEN** người dùng không tải lên ảnh công tơ điện

**THE SYSTEM SHALL** trả về HTTP 400 với mã lỗi:

```text
ELECTRIC_METER_IMAGE_REQUIRED
```

### AC05 – Thiếu ảnh công tơ nước

**WHEN** người dùng không tải lên ảnh công tơ nước

**THE SYSTEM SHALL** trả về HTTP 400 với mã lỗi:

```text
WATER_METER_IMAGE_REQUIRED
```

### AC06 – Mã phòng không tồn tại

**WHEN** người dùng nhập mã phòng không tồn tại

**THE SYSTEM SHALL** trả về HTTP 404 với mã lỗi:

```text
ROOM_NOT_FOUND
```

---

## 4. API Contract

### Endpoint

```http
POST /api/v1/meter-readings
```

### Request

> Nên sử dụng `multipart/form-data` vì có upload file ảnh.

```text
roomCode=P101
newElectricReading=1250
newWaterReading=350
electricMeterImage=<file>
waterMeterImage=<file>
```

### Response 200

```json
{
  "success": true,
  "data": {
    "roomCode": "P101",
    "previousElectricReading": 1200,
    "newElectricReading": 1250,
    "previousWaterReading": 330,
    "newWaterReading": 350,
    "status": "UPDATED",
    "updatedAt": "2026-06-11T09:30:00Z"
  }
}
```

### Response 400

```json
{
  "success": false,
  "error": {
    "code": "ELECTRIC_READING_INVALID",
    "message": "New electric reading must be greater than or equal to previous reading."
  }
}
```

### Response 404

```json
{
  "success": false,
  "error": {
    "code": "ROOM_NOT_FOUND",
    "message": "Room not found."
  }
}
```

---

## 5. Technical Constraints

- Chỉ số điện và nước phải là số nguyên không âm.
- Chỉ số mới phải lớn hơn hoặc bằng chỉ số kỳ trước.
- Chỉ chấp nhận file ảnh JPG, JPEG hoặc PNG.
- Kích thước mỗi ảnh tối đa 5MB.
- Thời gian phản hồi tối đa 500ms (P95).
- Toàn bộ thao tác lưu dữ liệu phải thực hiện trong một transaction.

---

## 6. Database Mapping

### Input Form

| Trường | Kiểu dữ liệu | Bắt buộc |
|---------|-------------|----------|
| roomCode | String | Yes |
| newElectricReading | Integer | Yes |
| newWaterReading | Integer | Yes |
| electricMeterImage | File | Yes |
| waterMeterImage | File | Yes |

### Dữ liệu đọc tự động từ DB

| Trường |
|---------|
| previousElectricReading |
| previousWaterReading |

### Dữ liệu hệ thống tự sinh

| Trường |
|---------|
| status |
| updatedAt |
| updatedBy |

---

## 7. Out of Scope

- OCR tự động đọc số từ ảnh công tơ.
- Chỉnh sửa dữ liệu sau khi đã khóa kỳ hóa đơn.
- Tự động tính tiền điện nước.
- Phê duyệt nhiều cấp cho lần cập nhật chỉ số.
