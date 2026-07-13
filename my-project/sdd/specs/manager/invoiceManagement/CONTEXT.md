# CONTEXT.md \[Quản lý hóa đơn\]

# Người viết: Bùi Đỉnh | Ngày: 2026-07-13

## 1. PROBLEM STATEMENT

- **Gánh nặng nhập liệu thủ công và rủi ro sai sót tài chính**: Ban quản lý phải đối mặt với việc nhập tay lặp đi lặp lại hàng loạt danh mục phí (đơn giá điện, nước, internet, dịch vụ cố định) và các chỉ số tiêu thụ của từng phòng vào mỗi kỳ hạn\[cite: 4\]. Việc này không chỉ tốn thời gian mà còn dễ dẫn đến sai sót số liệu, gây thất thoát doanh thu hoặc khiếu nại từ người thuê\[cite: 4\].
- **Lệch dữ liệu lịch sử khi có biến động giá**: Khi chủ nhà trọ điều chỉnh đơn giá dịch vụ (ví dụ: tăng giá điện hoặc giá phòng) trong danh mục hệ thống, các hóa đơn cũ đã phát hành trong quá khứ có nguy cơ bị cập nhật động theo\[cite: 4\]. Điều này làm sai lệch toàn bộ báo cáo doanh thu lịch sử và phá vỡ cam kết tài chính ban đầu với khách thuê\[cite: 4\].
- **Quy trình tạo hóa đơn trùng lặp và xung đột**: Thiếu cơ chế kiểm soát chặt chẽ khiến Ban quản lý có thể vô tình tạo nhiều hóa đơn cho cùng một phòng trong cùng một kỳ hạn, dẫn đến việc trùng lặp công nợ và gây hỗn loạn hệ thống dữ liệu\[cite: 4\].
- **Khó khăn và tốn kém tài nguyên khi in ấn/lưu trữ**: Ban quản lý gặp trở ngại khi cần xuất file hoặc in ấn hóa đơn định dạng chuẩn hóa để gửi cho khách thuê\[cite: 4\]. Nếu xử lý xuất file PDF ở backend sẽ dễ gây quá tải tài nguyên server khi có lượng lớn yêu cầu đồng thời\[cite: 4\].
- **Rủi ro rò rỉ và can thiệp dữ liệu tài chính**: Hệ thống đứng trước nguy cơ bị các đối tượng không có thẩm quyền hoặc người dùng chưa đăng nhập truy cập, chỉnh sửa trái phép các hóa đơn nhạy cảm, đặc biệt là các hóa đơn đã được thanh toán xong\[cite: 4\].

## 2. DOMAIN KNOWLEDGE

- **Kỳ hạn hóa đơn (Billing Period)**: Chu kỳ tính tiền của phòng, được định dạng theo cấu trúc bắt buộc `YYYYMM` (Ví dụ: `202606`)\[cite: 4\].
- **Mã hóa đơn (Invoice Code)**: Chuỗi ký tự duy nhất tự động sinh theo cấu trúc định sẵn: `INV-{roomCode}-{billingPeriod}` (Ví dụ: `INV-HN0101-202606`)\[cite: 4\].
- **Trạng thái hóa đơn (Invoice Status)**: Vòng đời của hóa đơn gồm 3 trạng thái nghiêm ngặt:
  - `UNPAID`: Hóa đơn mới tạo, chưa thanh toán (trạng thái mặc định)\[cite: 4\].
  - `PAID`: Hóa đơn đã được xác nhận thanh toán thành công\[cite: 4\]. **Quy tắc bất biến:** Tuyệt đối không cho phép điều chỉnh thông tin đối với hóa đơn đã ở trạng thái này\[cite: 4\].
  - `OVERDUE`: Hóa đơn chưa thanh toán và đã vượt quá hạn thanh toán (`due_date`)\[cite: 4\].
- **Lưu Snapshot giá**: Cơ chế bắt buộc phải sao lưu cứng toàn bộ đơn giá điện, nước, phí dịch vụ, internet và tiền phòng tại đúng thời điểm nhấn nút tạo hóa đơn\[cite: 4\]. Snapshot này đóng vai trò đóng băng dữ liệu của hóa đơn đó, biệt lập hoàn toàn với các thay đổi bảng giá sau này của hệ thống\[cite: 4\].
- **Công thức tính tiền tự động**:
  - $\\text{Số tiêu thụ} = \\text{Chỉ số mới} - \\text{Chỉ số cũ}$\[cite: 4\].
  - $\\text{Tạm tính} = \\text{Tiền phòng} + \\text{Tiền điện} + \\text{Tiền nước} + \\text{Phí dịch vụ} + \\text{Tiền Internet} + \\text{Phí khác}$\[cite: 4\].
  - $\\text{Tiền thuế} = \\text{Tạm tính} \\times \\text{Thuế (%)} $\[cite: 4\].
  - $\\text{Tổng tiền phải nộp} = \\text{Tạm tính} + \\text{Tiền thuế}$\[cite: 4\].

## 3. STAKEHOLDERS

- **Ban quản lý (Management Board / MANAGER)**: Người trực tiếp vận hành hệ thống, thụ hưởng việc tự động hóa tính toán, có quyền tạo, xem, điều chỉnh, tìm kiếm và in ấn hóa đơn\[cite: 4\].
- **Người thuê (Tenant)**: Người nhận hóa đơn, chịu ảnh hưởng trực tiếp bởi tính chính xác của các số liệu tính toán và có nghĩa vụ thanh toán dựa trên tài liệu này\[cite: 4\].
- **Hệ thống / Admin**: Người thiết lập cấu hình giá nền tảng, giám sát tính toàn vẹn dữ liệu và kiểm soát phân quyền hệ thống\[cite: 4\].

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- **Ràng buộc kiến trúc**: Phải xây dựng dựa trên các cấu trúc Servlet cụ thể (`InvoiceServlet` và `InvoiceDetailServlet`) kết hợp giao diện hiển thị JSP (`list.jsp`, `detail.jsp`, `edit.jsp`, `create.jsp`)\[cite: 4\].
- **Xử lý in ấn / Xuất bản**: Logic in ấn và xuất PDF bắt buộc phải xử lý hoàn toàn ở phía Client-side (sử dụng `window.print()` kết hợp CSS `@media print`), tuyệt đối không dùng các thư viện render PDF ở backend nhằm tránh gây nghẽn và quá tải server\[cite: 4\].
- **Toàn vẹn giao dịch (Transaction)**: Thao tác tạo hóa đơn (bao gồm ghi nhận bảng `invoices` và lấy snapshot giá từ bảng `facilities`) bắt buộc phải được bọc trong một Database Transaction duy nhất\[cite: 4\].
- **Hiệu năng hệ thống**: Thời gian phản hồi (`Response time`) cho các tác vụ tạo hóa đơn hoặc tải dữ liệu chi tiết không được phép vượt quá ngưỡng **500ms (P95)**\[cite: 4\].
- **Ràng buộc nghiệp vụ duy nhất**: Mỗi phòng chỉ được phép tồn tại duy nhất một hóa đơn trong một kỳ hạn\[cite: 4\]. Hệ thống phải chặn ngay lập tức nếu phát hiện trùng lặp (`INVOICE_ALREADY_EXISTS`)\[cite: 4\].
- **Kiểm soát quyền hạn**: Chỉ tài khoản có vai trò `MANAGER` hoặc `ADMIN` mới được phép thực thi các API và URL thuộc module này\[cite: 4\].

## 5. ASSUMPTIONS (giả định cần confirm)

- **Giả định 1**: Bảng ghi nhận chỉ số điện nước luôn có sẵn dữ liệu chuẩn xác (chỉ số cũ, chỉ số mới, hình ảnh minh chứng) được nhập trước khi Ban quản lý tiến hành tạo hóa đơn\[cite: 4\]. *Rủi ro nếu sai:* Nếu phân hệ nhập chỉ số điện nước chưa hoàn thành hoặc bị trễ hạn, form tạo hóa đơn sẽ liên tục báo lỗi hệ thống do thiếu dữ liệu liên kết\[cite: 4\].
- **Giả định 2**: Giả định rằng hệ thống xử lý logic tính tiền dựa trên việc người thuê chịu trách nhiệm trả $100%$ chi phí của phòng đó theo hợp đồng (chưa tính tới nghiệp vụ chia nhỏ hóa đơn cho nhiều người ở ghép tự chi trả độc lập)\[cite: 4\].

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- **Câu hỏi 1**: Chức năng "Xóa hóa đơn" được đặc tả là "sẽ giải phóng chỉ số điện nước nếu có"\[cite: 4\]. Cụ thể việc giải phóng này nghĩa là gì? Hệ thống sẽ xóa bản ghi chỉ số điện nước của kỳ đó, hay chỉ đơn thuần là gỡ liên kết gán với hóa đơn để chỉ số đó có thể được dùng cho một hóa đơn khác?
- **Câu hỏi 2**: Khi Ban quản lý bấm "Báo cáo sai số" ở màn hình chi tiết hóa đơn\[cite: 4\], hệ thống sẽ xử lý như thế nào? Luồng nghiệp vụ sẽ chuyển trạng thái hóa đơn về một trạng thái chờ xử lý riêng biệt, hay sẽ gửi thông báo đến phân hệ ghi nhận chỉ số điện nước để yêu cầu kiểm tra lại công tơ?
- **Câu hỏi 3**: Đối với trường nhập "Thuế (%)" khi tạo hóa đơn\[cite: 4\], hệ thống có cần quy định mức trần tối đa (ví dụ: không quá 10%) để tránh trường hợp người dùng gõ nhầm số quá lớn làm sai lệch nghiêm trọng tổng số tiền phải nộp không?