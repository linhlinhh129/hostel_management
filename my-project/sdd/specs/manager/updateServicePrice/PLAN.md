# PLAN.md

## Mục tiêu
- Xây dựng chức năng quản lý khoản phí và giá dịch vụ.
- Cho phép Ban quản lý tạo mới khoản phí, cập nhật giá dịch vụ và điều chỉnh giá khoản phí hiện có.
- Lưu lại lịch sử thay đổi để đảm bảo minh bạch và audit.

## Phạm vi
- Tạo mới khoản phí tùy chỉnh.
- Cập nhật giá khoản phí hiện có.
- Cập nhật giá dịch vụ mặc định (ELECTRICITY, WATER, SERVICE).
- Validate giá hợp lệ và chống trùng tên khoản phí.
- Lưu lịch sử thay đổi giá.

## Giải pháp kỹ thuật
### Dữ liệu
- `ServiceFee`: id, feeName, price, description, createdAt, createdBy, updatedAt, updatedBy.
- `ServicePrice`: serviceType, price, updatedAt, updatedBy.
- `ServicePriceHistory` / `ServiceFeeHistory`: id, entityId, oldPrice, newPrice, changedBy, changedAt, comment.

### Luồng xử lý
1. Tạo khoản phí mới: validate `feeName`, `price`, chống trùng tên, tạo record mới.
2. Cập nhật giá khoản phí: validate `price > 0`, update giá mới, lưu lịch sử thay đổi.
3. Cập nhật giá dịch vụ mặc định: validate `serviceType`, `price > 0`, update giá, lưu history.

### API đề xuất
- `POST /api/v1/service-fees`
- `PUT /api/v1/service-fees/{feeId}`
- `PUT /api/v1/service-prices/{serviceType}`

### Quy tắc nghiệp vụ
- Giá phải lớn hơn 0. Nếu <= 0, trả về lỗi `INVALID_PRICE`.
- `feeName` phải là duy nhất. Nếu trùng, trả về `DUPLICATE_SERVICE_FEE`.
- Nếu thiếu thông tin bắt buộc, trả về `REQUIRED_FIELD_MISSING`.
- Lưu audit trail khi tạo mới hoặc cập nhật.

### Bảo mật
- Chỉ người dùng role `Management Board` hoặc `Finance Manager` (theo yêu cầu phân quyền cụ thể) được phép thực hiện.
- Trả về 401 khi chưa đăng nhập.
- Trả về 403 khi sai role.

### Hiệu năng
- Mỗi API phản hồi trong < 500ms.
- Áp dụng rate limit 100 requests/phút/người dùng.

## Rủi ro
- Dữ liệu giá không chính xác nếu không validate số liệu đầu vào.
- Tên khoản phí trùng lặp gây nhầm lẫn trên hóa đơn.
- Lịch sử thay đổi không đầy đủ nếu audit trail không được ghi chính xác.

## Giả định
- Danh mục `serviceType` mặc định không bị xóa và chỉ có các giá trị ELECTRICITY, WATER, SERVICE.
- Mức giá mới áp dụng cho các hóa đơn được tạo sau thời điểm cập nhật.
- Các API điều chỉnh giá này không cần hỗ trợ rollback tự động ngoài ghi history.

## Tài liệu tham khảo
- `SPEC.md`
- `CONTEXT.md`
