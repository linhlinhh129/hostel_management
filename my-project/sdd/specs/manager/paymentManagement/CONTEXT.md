# Quản lý thanh toán

# Người viết: @BuiDinh | Ngày: 2026-06-21

## 1. PROBLEM STATEMENT

Trong quá trình thuê phòng, người thuê cần thanh toán nhiều khoản chi phí như tiền thuê phòng, tiền điện, tiền nước, phí dịch vụ và các khoản phát sinh khác. Nếu việc ghi nhận thanh toán không rõ ràng hoặc phụ thuộc vào kiểm tra thủ công, Ban quản lý dễ gặp tình trạng khó xác định giao dịch nào đã thanh toán, giao dịch nào còn chờ duyệt và khoản công nợ nào cần được cập nhật.

Nỗi đau chính của Ban quản lý là cần kiểm tra bằng chứng thanh toán từ người thuê trước khi xác nhận giao dịch. Nếu ảnh xác nhận thanh toán bị thiếu, sai hoặc không được kiểm tra đúng, hệ thống có thể ghi nhận sai trạng thái thanh toán, làm lệch công nợ và ảnh hưởng đến quản lý dòng tiền.

Người thuê cũng bị ảnh hưởng nếu giao dịch đã chuyển khoản nhưng chưa được duyệt kịp thời hoặc bị ghi nhận sai. Điều này có thể dẫn đến việc công nợ vẫn hiển thị chưa thanh toán, gây nhầm lẫn và phát sinh khiếu nại.

Vì vậy, vấn đề cốt lõi của feature này là đảm bảo các giao dịch thanh toán được theo dõi, kiểm tra và xác nhận một cách chính xác, có bằng chứng rõ ràng và có trách nhiệm ghi nhận từ người duyệt.

## 2. DOMAIN KNOWLEDGE

- `payment transaction` / giao dịch thanh toán: bản ghi thể hiện một lần người thuê thực hiện thanh toán cho khoản công nợ hoặc chi phí liên quan.

- `transactionId`: mã định danh duy nhất của giao dịch, có thể dùng dạng UUID.

- `transactionCode`: mã giao dịch hiển thị cho người dùng, ví dụ `PAY001`.

- `tenant`: người thuê phòng, là người thực hiện thanh toán hoặc có khoản công nợ cần thanh toán.

- `roomCode`: mã phòng liên quan đến giao dịch thanh toán.

- `payment amount`: số tiền người thuê đã thanh toán.

- `payment date`: ngày người thuê thực hiện thanh toán hoặc ngày hệ thống ghi nhận giao dịch.

- `payment method`: phương thức thanh toán, ví dụ chuyển khoản.

- `payment proof image` / ảnh xác nhận thanh toán: ảnh bằng chứng người thuê tải lên sau khi chuyển khoản.

- `payment status`: trạng thái giao dịch thanh toán.

  - `PENDING`: giao dịch đang chờ Ban quản lý kiểm tra và duyệt.

  - `PAID`: giao dịch đã được Ban quản lý xác nhận thanh toán thành công.

- `debt` / công nợ: khoản tiền người thuê cần thanh toán. Khi giao dịch được duyệt thành công, công nợ liên quan cần được cập nhật trạng thái tương ứng.

- `approvedAt`: thời điểm Ban quản lý duyệt giao dịch thanh toán.

- `approvedBy`: người duyệt giao dịch thanh toán.

- Audit Log: lịch sử ghi lại thao tác duyệt thanh toán để phục vụ kiểm tra trách nhiệm và đối soát sau này.

## 3. STAKEHOLDERS

- Ban quản lý / Management Board:

  - Người dùng chính của feature.

  - Cần xem danh sách giao dịch, xem chi tiết giao dịch, kiểm tra ảnh xác nhận và duyệt thanh toán.

  - Chịu trách nhiệm đảm bảo giao dịch được ghi nhận chính xác.

- Người thuê:

  - Người thực hiện thanh toán và tải lên ảnh xác nhận thanh toán.

  - Bị ảnh hưởng trực tiếp nếu giao dịch bị duyệt sai, duyệt chậm hoặc công nợ không được cập nhật.

- Bộ phận tài chính / kế toán nếu có:

  - Cần dữ liệu thanh toán chính xác để theo dõi dòng tiền, công nợ và đối soát.

- Quản trị hệ thống / kỹ thuật:

  - Đảm bảo phân quyền, xác thực, lưu trữ ảnh xác nhận, cập nhật trạng thái và audit log hoạt động đúng.

- Người có quyền quyết định nghiệp vụ:

  - Ban quản lý hoặc chủ nhà trọ.

  - Cần xác nhận quy trình duyệt thanh toán, tiêu chí ảnh hợp lệ và cách cập nhật công nợ.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- Feature này chỉ dành cho `Management Board`.

- Tất cả API phải yêu cầu Authentication Token.

- Chỉ `Management Board` được xem và duyệt giao dịch thanh toán.

- Mỗi giao dịch phải liên kết với một khoản công nợ hợp lệ.

- Mỗi giao dịch phải có một người thuê hợp lệ.

- Ảnh xác nhận thanh toán phải được lưu trữ trước khi giao dịch được duyệt.

- Chỉ các giao dịch ở trạng thái `PENDING` mới được phép duyệt.

- Sau khi duyệt thành công, trạng thái giao dịch phải chuyển sang `PAID`.

- Sau khi duyệt thành công, trạng thái công nợ liên quan phải được cập nhật thành `PAID`.

- Hệ thống phải lưu `createdAt`, `createdBy`, `approvedAt`, `approvedBy`.

- Tất cả thao tác duyệt thanh toán phải được ghi Audit Log.

- API lấy danh sách giao dịch phải phản hồi dưới 1 giây ở p95.

- API xem chi tiết giao dịch phải phản hồi dưới 500ms ở p95.

- API duyệt giao dịch phải phản hồi dưới 1 giây ở p95.

- Rate limit là 100 requests/phút/user.

- Chưa tích hợp cổng thanh toán trực tuyến.

- Chưa có thanh toán tự động qua ngân hàng.

- Chưa hỗ trợ hoàn tiền.

- Chưa hỗ trợ hủy giao dịch đã duyệt.

- Chưa hỗ trợ chỉnh sửa ảnh xác nhận thanh toán.

- Chưa hỗ trợ OCR đọc nội dung ảnh chuyển khoản.

- Chưa hỗ trợ đối soát ngân hàng tự động.

- Chưa gửi email hoặc SMS xác nhận thanh toán.

- Chưa dùng AI để kiểm tra tính hợp lệ của ảnh chuyển khoản.

- Chưa bao gồm báo cáo doanh thu.

## 5. ASSUMPTIONS (giả định cần confirm)

- Giả định `Management Board` là role đã tồn tại trong hệ thống phân quyền.

- Giả định người thuê đã có cách tải ảnh xác nhận thanh toán lên trước khi Ban quản lý duyệt.

- Giả định mỗi giao dịch thanh toán chỉ liên kết với một khoản công nợ.

- Giả định một khoản công nợ được xem là `PAID` khi giao dịch liên quan được duyệt thành công.

- Giả định hệ thống chỉ cần trạng thái `PENDING` và `PAID` cho phạm vi hiện tại.

- Giả định ảnh xác nhận thanh toán được lưu dưới dạng URL như `paymentProofUrl`.

- Giả định Ban quản lý tự kiểm tra ảnh xác nhận bằng mắt, không có OCR hoặc tự động đối soát ngân hàng.

- Giả định giao dịch đã được duyệt thì không được hủy hoặc hoàn tiền trong phạm vi feature này.

- Giả định `paymentDate` là ngày người thuê thực hiện thanh toán, không nhất thiết là ngày Ban quản lý duyệt.

- Giả định `approvedBy` lưu ID của người dùng đang đăng nhập.

- Giả định hệ thống có sẵn bảng hoặc module quản lý công nợ để cập nhật trạng thái sau khi duyệt thanh toán.

- Giả định note khi duyệt thanh toán là thông tin bổ sung, không bắt buộc.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- Role `Management Board` trong database tương ứng với role nào: `ADMIN`, `MANAGER`, hay một role riêng?

- Người thuê tải ảnh xác nhận thanh toán ở màn hình/module nào?

- Ảnh xác nhận thanh toán cần giới hạn định dạng và dung lượng như thế nào?

- Nếu ảnh xác nhận thanh toán mờ, sai nội dung hoặc không khớp số tiền thì Ban quản lý xử lý ra sao?

- Có cần trạng thái `REJECTED` cho giao dịch bị từ chối không?

- Nếu giao dịch bị từ chối, người thuê có được tải lại ảnh xác nhận thanh toán không?

- Một khoản công nợ có thể được thanh toán bằng nhiều giao dịch nhỏ không?

- Một giao dịch có thể thanh toán cho nhiều khoản công nợ không?

- Nếu số tiền thanh toán nhỏ hơn công nợ thì hệ thống xử lý thanh toán một phần như thế nào?

- Nếu số tiền thanh toán lớn hơn công nợ thì có ghi nhận dư tiền không?

- Ai là người có quyền duyệt thanh toán cuối cùng?

- Có cần quy trình hai bước, ví dụ một người kiểm tra và một người duyệt không?

- Có cần lưu note duyệt thanh toán vào lịch sử giao dịch không?

- Khi duyệt giao dịch thành công, hệ thống có cần tự động cập nhật hóa đơn liên quan sang `PAID` không?

- Khi công nợ chuyển sang `PAID`, có cần cập nhật cả trạng thái hóa đơn hoặc booking liên quan không?

- Có cần thông báo cho người thuê sau khi giao dịch được duyệt không?

- Có cần lưu lịch sử xem ảnh xác nhận thanh toán không?

- Có cần phân biệt ngày người thuê thanh toán và ngày Ban quản lý duyệt không?

- Nếu người dùng gọi API duyệt nhiều lần cùng lúc cho một giao dịch thì hệ thống xử lý chống double-approve như thế nào?

- Có cần bộ lọc theo ngày thanh toán, mã phòng, tên người thuê hoặc phương thức thanh toán trong danh sách giao dịch không?