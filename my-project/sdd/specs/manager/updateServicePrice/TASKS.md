# TASKS.md

## 1. Khảo sát và thiết kế
1.1. Đọc `SPEC.md` và `CONTEXT.md`.
1.2. Thiết kế mô hình `ServiceFee`, `ServicePrice`, và lịch sử thay đổi.
1.3. Xác nhận danh sách `serviceType` và ràng buộc role.

## 2. Implement API tạo khoản phí mới
2.1. `POST /api/v1/service-fees`.
2.2. Validate `feeName`, `price`, `description`.
2.3. Kiểm tra trùng tên trước khi lưu.
2.4. Tạo record mới và trả về 201.

## 3. Implement API cập nhật giá khoản phí
3.1. `PUT /api/v1/service-fees/{feeId}`.
3.2. Validate `price > 0`.
3.3. Cập nhật giá, ghi `updatedAt`, `updatedBy`.
3.4. Lưu lịch sử thay đổi giá cũ -> giá mới.

## 4. Implement API cập nhật giá dịch vụ
4.1. `PUT /api/v1/service-prices/{serviceType}`.
4.2. Validate `serviceType` hợp lệ và `price > 0`.
4.3. Cập nhật giá mới và lưu lịch sử.

## 5. Bảo mật và phân quyền
5.1. Thêm kiểm tra authentication.
5.2. Thêm kiểm tra role `Management Board` / `Finance Manager`.
5.3. Viết test 401 / 403.

## 6. Kiểm thử
6.1. Viết unit test cho validation giá và trùng tên.
6.2. Viết integration test cho các endpoint.
6.3. Kiểm thử lỗi `INVALID_PRICE`, `REQUIRED_FIELD_MISSING`, `DUPLICATE_SERVICE_FEE`.
6.4. Kiểm tra audit trail được ghi đúng.

## 7. Tài liệu và bàn giao
7.1. Cập nhật tài liệu API nếu cần.
7.2. Ghi rõ giả định và câu hỏi mở trong tài liệu.
