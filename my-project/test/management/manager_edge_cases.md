# 10 Edge Cases "Tử huyệt" bị lãng quên: Quản lý Hợp đồng & Dashboard (Manager)

Dưới đây là 10 ngóc ngách logic (Edge Cases) rất dễ bị Developer bỏ sót trong quá trình code 2 tính năng **Dashboard** và **Contract Management**. Nếu không bắt chặt, chúng có thể gây rò rỉ dữ liệu, sập hệ thống hoặc gây lỗi nghiêm trọng cho luồng kế toán.

## PHẦN 1: TÍNH NĂNG QUẢN LÝ HỢP ĐỒNG (Contract Management)

### 1. Ký hợp đồng kép (Double Booking Race Condition)
- **Tại sao nguy hiểm:** 2 Quản lý cùng mở form tạo hợp đồng cho phòng `P101`. Quản lý A và Quản lý B ấn nút "Lưu" cùng 1 mili-giây. Cả 2 luồng đều vượt qua câu lệnh `if(room.isAvailable())` vì DB chưa kịp cập nhật. Kết quả: 1 phòng sinh ra 2 hợp đồng `ACTIVE`, phòng ban kế toán sẽ thu tiền gấp đôi.
- **Test Case:** Dùng `ExecutorService` bắn 2 request POST `/create` cho cùng 1 `roomId` cùng lúc. Assert rằng 1 request thành công, request còn lại phải văng Exception `DataIntegrityViolation` hoặc bị chặn bởi Optimistic Lock.

### 2. Xóa mềm hợp đồng bằng API ẩn (Bypass UI Validation)
- **Tại sao nguy hiểm:** Frontend ẩn nút "Xóa" đối với hợp đồng đang `ACTIVE`. Tuy nhiên, kẻ gian (hoặc Quản lý tò mò) mở Postman gửi thẳng `POST /manager/contracts/delete?id=[ID_đang_Active]`. Nếu Backend (Servlet) không check lại `if(contract.getStatus() == INACTIVE)`, hợp đồng đang thuê sẽ bị xóa mất tích.
- **Test Case:** Gửi POST delete với tham số của 1 hợp đồng `ACTIVE`. Assert nhận về lỗi báo từ chối thao tác.

### 3. Đánh cắp tài khoản Admin qua Form thêm người thuê (Privilege Escalation)
- **Tại sao nguy hiểm:** Quản lý tạo tài khoản người thuê bằng tính năng "Add Tenant". Nhưng Quản lý lại nhập email của ông Giám Đốc (`admin@hostel.com`). Nếu hệ thống dùng hàm `UPDATE users SET role = 'TENANT' WHERE email = ?`, tài khoản Admin sẽ bị giáng cấp xuống thành Tenant, sập toàn bộ quyền quản trị.
- **Test Case:** Thử Add Tenant với email đã tồn tại nhưng có `role = ADMIN`. Assert hệ thống ném lỗi "Email đã thuộc về quản trị viên, không thể thêm".

### 4. Lỗi năm nhuận ở Ngày hết hạn (Leap Year Crash)
- **Tại sao nguy hiểm:** Ngày bắt đầu hợp đồng là `29/02/2024`. Quản lý chọn thời hạn 1 năm. Nếu code dùng cộng ngày chuỗi string thô sơ thành `29/02/2025` thì DB sẽ văng lỗi vì tháng 2 năm 2025 chỉ có 28 ngày.
- **Test Case:** Nhập `startDate = 2024-02-29`, `endDate = 2025-02-29`. Assert hệ thống văng validation hoặc tự điều chỉnh về `2025-02-28`.

### 5. IDOR qua Form In Hợp Đồng
- **Tại sao nguy hiểm:** Manager A chỉ quản lý cơ sở 1. Manager A gõ URL `GET /manager/contracts/detail?id=999` (ID 999 là của cơ sở 2). Nếu `ContractServlet` chỉ `SELECT * FROM contracts WHERE id = 999` mà quên `AND facility_id = 1`, Manager A sẽ xem được thông tin bảo mật (CCCD, SĐT) của khách hàng cơ sở khác.
- **Test Case:** Truy cập detail hợp đồng của cơ sở khác. Assert nhận về mã lỗi HTTP 403 Forbidden.

### 6. Cấm ký hợp đồng "Hồi tố" (Retroactive Paperwork)
- **Tại sao nguy hiểm:** Dev viết validation `startDate >= signedDate`. Nhưng thực tế có khách chuyển vào ở gấp từ ngày 01/05 (`startDate`), đến ngày 05/05 mới rảnh rỗi lên ký giấy (`signedDate`). Validation của Dev đã chặn đứng luồng làm việc thực tế của cơ sở.
- **Test Case:** Nhập `signedDate = 05/05` và `startDate = 01/05`. Assert hệ thống VẪN PHẢI cho phép lưu (chỉ chặn `endDate < startDate`).

### 7. Khách thuê 2 phòng (Unique CCCD Violation)
- **Tại sao nguy hiểm:** Một ông sếp thuê 2 phòng trọ cho 2 nhân viên (Hợp đồng A và Hợp đồng B cùng dùng chung 1 CCCD của ông sếp). Nếu Database set ràng buộc `UNIQUE (cccd)`, thì khi tạo hợp đồng thứ 2 hệ thống sẽ sập 500 lỗi trùng lặp.
- **Test Case:** Tạo hợp đồng thứ 2 với CCCD đã tồn tại trong 1 hợp đồng `ACTIVE` khác. Assert hệ thống vẫn cho phép (bởi 1 người được quyền đứng tên nhiều phòng).

---

## PHẦN 2: TÍNH NĂNG DASHBOARD (Manager Dashboard)

### 8. Lỗi sập nguyên trang Dashboard vì "Chia cho 0"
- **Tại sao nguy hiểm:** Cơ sở mới khai trương, chưa có phòng nào trong hệ thống (`totalRooms = 0`). Hàm tính tỷ lệ lấp đầy: `occupancyRate = occupiedRooms / totalRooms * 100`. Toán học không cho phép chia cho 0, Java sẽ nổ tung `ArithmeticException`, làm trắng toàn bộ trang Dashboard.
- **Test Case:** Mock `totalRooms = 0`. Assert `occupancyRate = 0` và trang vẫn render thành công.

### 9. N+1 Query / Chết bộ nhớ do "5 sự cố gần nhất"
- **Tại sao nguy hiểm:** Yêu cầu là lấy "5 sự cố mới gửi". Dev lười nên viết `TicketDAO.getAllTickets()`, sau đó dùng Java `tickets.stream().limit(5)` để cắt lấy 5 cái. Khi cơ sở có 100.000 sự cố, mỗi lần load Dashboard sẽ kéo toàn bộ 100.000 dòng từ DB lên RAM, gây OutOfMemory (OOM) chết server.
- **Test Case:** Không thể test bằng JUnit đơn thuần, nhưng phải Verify trong code SQL có chữ `LIMIT 5` hoặc `FETCH NEXT 5 ROWS ONLY`.

### 10. Số nợ tồn đọng bị "Âm" (Negative Debt)
- **Tại sao nguy hiểm:** Khách hàng đáng lẽ đóng 4 triệu, nhưng họ chuyển nhầm 5 triệu. Hệ thống ghi nhận Dư nợ = `-1.000.000`. Dashboard Manager có hàm `Tính tổng các hóa đơn chưa thanh toán`. Nếu Dev cứ thế `SUM(debt_amount)`, số nợ của cơ sở sẽ bị giảm ảo (lấy nợ của người này bù trừ cho số tiền dư của người kia).
- **Test Case:** Tính tổng nợ khi có 1 hóa đơn nợ 3 triệu và 1 hóa đơn dư 1 triệu (debt = -1tr). Tổng nợ chưa thu CẦN PHẢI là 3 triệu (không được lấy số âm để trừ). Dữ liệu này phải được xử lý ở Dashboard Service.
