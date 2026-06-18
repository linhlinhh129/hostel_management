# PLAN.md

## Mục tiêu
- Xây dựng chức năng quản lý công nợ cho Ban quản lý.
- Cho phép xem danh sách công nợ, tìm kiếm, lọc theo trạng thái, xem chi tiết, gửi thông báo nội bộ và xem lịch sử thông báo.
- Đảm bảo toàn vẹn dữ liệu, phân quyền chặt chẽ và phản hồi nhanh theo yêu cầu hiệu năng.

## Phạm vi
- Danh sách công nợ với các trường: Debt ID, Debt Code, Tenant Name, Room Code, Debt Type, Total Amount, Due Date, Status.
- Chi tiết một khoản công nợ với bổ sung Contract ID, Description, Created Date, Note.
- Tìm kiếm theo mã công nợ, tên người thuê, mã phòng.
- Lọc theo trạng thái PENDING / PAID / OVERDUE.
- Gửi thông báo công nợ nội bộ và lưu lịch sử thông báo cho mỗi khoản công nợ.
- Xử lý nghiệp vụ công nợ không tồn tại và phân quyền role Management Board.

## Giải pháp kỹ thuật
### Dữ liệu
- `Debt`: id, code, tenantId, roomId, contractId, debtType, description, totalAmount, dueDate, createdDate, status, note.
- `DebtStatus`: PENDING, PAID, OVERDUE.
- `DebtNotification`: id, debtId, title, message, createdBy, createdAt, status.

### Luồng xử lý chính
1. Khi truy vấn danh sách công nợ, hệ thống đọc dữ liệu `Debt` và áp dụng bộ lọc search/filter, phân trang.
2. Khi xem chi tiết công nợ, hệ thống trả về dữ liệu đầy đủ và status hiện tại.
3. Khi gửi thông báo, hệ thống xác thực debt tồn tại, role Management Board, validate title/message, tạo bản ghi `DebtNotification` và liên kết với `Debt`.
4. Khi xem lịch sử thông báo, hệ thống trả về danh sách thông báo liên quan đến debt.

### API đề xuất
- `GET /api/v1/debts` — danh sách công nợ, query: `keyword`, `status`, `page`, `size`.
- `GET /api/v1/debts/{debtId}` — detail công nợ.
- `POST /api/v1/debts/{debtId}/notifications` — gửi thông báo nội bộ.
- `GET /api/v1/debts/{debtId}/notifications` — lịch sử thông báo.

### Bảo mật và phân quyền
- Chỉ người dùng xác thực và có role `Management Board` được phép truy cập.
- Trả về HTTP 401 nếu chưa đăng nhập.
- Trả về HTTP 403 nếu không phải Management Board.
- Kiểm tra phân quyền ở tầng middleware/servlet và trước khi thực hiện mọi thao tác dữ liệu.

### Validation
- `debtId` phải tồn tại; nếu không, trả về 404 với `DEBT_001`.
- `title` không được để trống, không được chỉ chứa khoảng trắng, không vượt quá 100 ký tự.
- `message` không được để trống, không được chỉ chứa khoảng trắng, không vượt quá 500 ký tự.
- Trạng thái `Debt` chỉ được chấp nhận PENDING/PAID/OVERDUE khi nhập từ nguồn dữ liệu.

### Hiệu năng
- API trả về danh sách và gửi thông báo phải hoàn thành dưới 1s.
- Sử dụng phân trang và chỉ lấy các trường cần thiết khi truy vấn danh sách.

## Rủi ro
- Dữ liệu công nợ không đồng bộ với trạng thái thanh toán nếu module thanh toán xử lý độc lập.
- Việc gửi thông báo trùng lặp nhiều lần có thể gây tốn tài nguyên nếu không kiểm soát frontend.
- Thiếu dữ liệu người thuê/phòng hợp lệ dẫn đến báo lỗi khi hiển thị danh sách.

## Giả định
- Hệ thống có cơ chế cập nhật trạng thái OVERDUE tự động dựa trên ngày hiện tại và dueDate.
- Việc chuyển trạng thái sang PAID được cập nhật từ module thanh toán khác.
- Tất cả người dùng `Management Board` có quyền ngang nhau để gửi và xem thông báo.

## Tài liệu tham khảo
- `SPEC.md`
- `CONTEXT.md`
