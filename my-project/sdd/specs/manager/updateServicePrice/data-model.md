# Data Model: updateServicePrice

## Entities

### Facility (Cơ sở)
Bảng lưu trữ thông tin cơ sở và các loại giá dịch vụ hiện hành.
- `facility_id` (INT, PK): ID cơ sở.
- `electricity_price` (DECIMAL/INT): Giá điện hiện hành.
- `water_price` (DECIMAL/INT): Giá nước hiện hành.
- `service_fee` (DECIMAL/INT): Phí dịch vụ hiện hành.
- *(Các trường khác không bị ảnh hưởng)*

**Validation Rules:**
- Giá trị phải lớn hơn 0 và được lưu dưới dạng số nguyên dương.

### ServicePriceHistory (Lịch sử cập nhật giá)
Bảng lưu lại lịch sử các lần thay đổi giá.
- `history_id` (INT, PK): ID tự tăng.
- `facility_id` (INT, FK): Liên kết đến bảng `facilities`.
- `price_type` (VARCHAR): Loại giá được cập nhật (`ELECTRICITY`, `WATER`, `SERVICE_FEE`).
- `old_price` (INT): Giá cũ trước khi thay đổi.
- `new_price` (INT): Giá mới sau khi thay đổi.
- `created_by` (INT, FK): Người thực hiện (Manager ID).
- `created_at` (DATETIME): Thời gian cập nhật.

**Note:** Cột `note` (ghi chú) đã được loại bỏ theo yêu cầu mới.

## Relationships
- Một `Facility` có nhiều `ServicePriceHistory`.
- `ServicePriceHistory.facility_id` -> `Facility.facility_id`.
- `ServicePriceHistory.created_by` -> `Users.user_id`.
