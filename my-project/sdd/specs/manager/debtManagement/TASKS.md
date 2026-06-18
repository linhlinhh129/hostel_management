# TASKS.md

## 1. Thiết kế và chuẩn bị
1.1. Đọc và xác nhận yêu cầu từ `SPEC.md` và `CONTEXT.md`.
1.2. Thiết kế mô hình dữ liệu `Debt` và `DebtNotification`.
1.3. Xác định các status hợp lệ: PENDING, PAID, OVERDUE.
1.4. Xác định các trường cần hiển thị cho danh sách và chi tiết.

## 2. Xây dựng API danh sách công nợ
2.1. Implement endpoint `GET /api/v1/debts`.
2.2. Hỗ trợ tìm kiếm theo `debtCode`, `tenantName`, `roomCode`.
2.3. Hỗ trợ lọc theo `status`.
2.4. Thêm phân trang với `page` và `size`.
2.5. Trả về thông báo khi không có công nợ.

## 3. Xây dựng API chi tiết công nợ
3.1. Implement endpoint `GET /api/v1/debts/{debtId}`.
3.2. Trả về các trường chi tiết: contractId, description, createdDate, note, status.
3.3. Kiểm tra công nợ tồn tại và xử lý 404 nếu không có.

## 4. Xây dựng API gửi thông báo công nợ
4.1. Implement endpoint `POST /api/v1/debts/{debtId}/notifications`.
4.2. Validate title và message theo ràng buộc độ dài và không rỗng.
4.3. Tạo bản ghi notification và liên kết với debt.
4.4. Trả về HTTP 201 khi tạo thành công.
4.5. Xử lý lỗi `NOTIFICATION_TITLE_REQUIRED`, `NOTIFICATION_TITLE_TOO_LONG`, `NOTIFICATION_MESSAGE_REQUIRED`, `NOTIFICATION_MESSAGE_TOO_LONG`.

## 5. Xây dựng API lịch sử thông báo
5.1. Implement endpoint `GET /api/v1/debts/{debtId}/notifications`.
5.2. Trả về danh sách notification với các trường: id, title, message, createdBy, createdAt, status.
5.3. Trả về danh sách rỗng nếu chưa có thông báo.

## 6. Bảo mật và phân quyền
6.1. Thêm kiểm tra xác thực cho mọi endpoint.
6.2. Thêm kiểm tra role `Management Board`.
6.3. Kiểm thử các trường hợp 401 và 403.

## 7. UI/UX
7.1. Cập nhật màn hình quản lý công nợ để hiển thị search/filter.
7.2. Cập nhật màn hình chi tiết công nợ.
7.3. Thêm form gửi thông báo nội bộ.
7.4. Thêm bảng lịch sử thông báo.

## 8. Kiểm thử
8.1. Viết unit test cho validation và business logic.
8.2. Viết integration test cho các endpoint chính.
8.3. Kiểm tra các trường hợp lỗi 404, 400, 401, 403.
8.4. Đánh giá hiệu năng danh sách và gửi thông báo.

## 9. Tài liệu và bàn giao
9.1. Cập nhật tài liệu API nếu cần.
9.2. Đính kèm kết quả nghiệm thu với `SPEC.md` và `CONTEXT.md`.
9.3. Chuẩn bị nhận xét cho reviewer.
