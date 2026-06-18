# Feature: Cập nhật trạng thái sửa chữa

**Status:** Draft  
**Author:** Phạm Anh Tú  
**Reviewer:** [Tên Reviewer]  
**Date:** 2026-06-10  
**Priority:** High

---

## 1. Business Context

Tính năng này là bước cuối cùng trong quy trình xử lý yêu cầu sửa chữa.

Sau khi hoàn thành công việc tại hiện trường, nhân viên vận hành cần cập nhật kết quả thực hiện, đính kèm hình ảnh minh chứng và ghi nhận ngày hoàn thành thực tế. Dữ liệu này phục vụ cho việc nghiệm thu, theo dõi lịch sử sửa chữa, báo cáo KPI và đánh giá hiệu quả vận hành.

---

## 2. User Stories

### Story 1 (Happy Path - Lưu kết quả thành công)

**As a** nhân viên vận hành,

**I want to** nhập ghi chú kết quả, đính kèm ảnh sau sửa chữa và chọn ngày hoàn thành,

**so that** tôi có thể xác nhận công việc đã hoàn tất và cập nhật trạng thái yêu cầu sang "Hoàn thành".

### Story 2 (Edge Case - Thiếu minh chứng hình ảnh)

**As a** manager,

**I want to** bắt buộc nhân viên vận hành phải đính kèm ít nhất 1 hình ảnh sau sửa chữa,

**so that** đảm bảo tính minh bạch và xác thực của kết quả xử lý.

---

## 3. Acceptance Criteria (EARS)

### AC01 – Hiển thị form cập nhật trạng thái

**WHEN** user truy cập màn hình "Cập nhật trạng thái sửa chữa"

**THE SYSTEM SHALL** hiển thị form gồm:

- Ghi chú kết quả (Textarea)
- Đính kèm ảnh sau sửa chữa (File Upload)
- Ngày hoàn thành (Date Picker)

### AC02 – Ngày hoàn thành mặc định

**WHEN** user mở trường "Ngày hoàn thành"

**THE SYSTEM SHALL**

- Tự động chọn ngày hiện tại
- Không cho phép chọn ngày trong tương lai

### AC03 – Cập nhật thành công

**WHEN** user nhấn nút **[Xác nhận lưu]** với đầy đủ dữ liệu hợp lệ

**THE SYSTEM SHALL**

- Gọi API cập nhật trạng thái
- Lưu ghi chú kết quả
- Lưu ảnh minh chứng
- Lưu ngày hoàn thành
- Cập nhật trạng thái yêu cầu thành `COMPLETED`
- Hiển thị thông báo thành công
- Điều hướng người dùng về màn hình "Danh sách yêu cầu sửa chữa"

### AC04 – Thiếu dữ liệu bắt buộc

**WHEN** user nhấn nút **[Xác nhận lưu]** nhưng chưa nhập ghi chú kết quả hoặc chưa đính kèm ảnh

**THE SYSTEM SHALL**

- Hiển thị lỗi validation tại trường tương ứng
- Không gửi dữ liệu lên server

### AC05 – Không có quyền cập nhật

**WHEN** user không có quyền cập nhật yêu cầu sửa chữa

**THE SYSTEM SHALL**

- Trả về HTTP 403
- Hiển thị thông báo từ chối truy cập

---

## 4. API Contract

### Endpoint

```http
PUT /api/v1/repair-requests/:id/complete
```

### Request Headers

```http
Content-Type: multipart/form-data
```

### Request Body (Form Data)

| Field | Type | Required | Description |
|---------|---------|----------|-------------|
| notes | string | Yes | Ghi chú kết quả sửa chữa |
| completed_at | string (YYYY-MM-DD) | Yes | Ngày hoàn thành thực tế |
| after_images | file[] | Yes | Ảnh sau sửa chữa |

### Validation

| Field | Rule |
|---------|------|
| notes | Không được để trống |
| completed_at | ≤ ngày hiện tại |
| after_images | Tối thiểu 1 ảnh, tối đa 5 ảnh |

### Response 200

```json
{
  "success": true,
  "message": "Cập nhật trạng thái hoàn thành thành công.",
  "data": {
    "id": 123,
    "status": "COMPLETED",
    "completed_at": "2026-06-10"
  }
}
```

### Response 400

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Vui lòng đính kèm ít nhất 1 hình ảnh sau sửa chữa hoặc ngày hoàn thành không hợp lệ."
  }
}
```

### Response 403

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Bạn không có quyền cập nhật yêu cầu này."
  }
}
```

---

## 5. Technical Constraints

### File Validation (Client & Server)

- Chỉ chấp nhận định dạng:
  - JPG
  - JPEG
  - PNG

- Dung lượng tối đa mỗi ảnh: 5MB

- Số lượng ảnh:
  - Tối thiểu: 1
  - Tối đa: 5

### Date Constraint

```text
completed_at <= current_date
```

Không cho phép nhập ngày hoàn thành trong tương lai.

### Database Changes

Lưu dữ liệu vào bảng:

```text
repair_requests
repair_details
```

Các trường cần cập nhật:

| Field |
|--------|
| notes |
| completed_at |
| status |
| image_url |

---

## 6. Out of Scope

- Ký xác nhận điện tử (Digital Signature)
- Đánh giá sao cho nhân viên vận hành
- Gửi khảo sát mức độ hài lòng
- Thông báo tự động qua Email/SMS sau khi hoàn thành
