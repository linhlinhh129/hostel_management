# Quản lý công nợ

# Người viết: @BuiDinh | Ngày: 2026-06-21

## 1. PROBLEM STATEMENT

Trong hệ thống quản lý nhà trọ, Ban quản lý cần theo dõi những khoản tiền người thuê chưa thanh toán hoặc đã quá hạn thanh toán. Nếu không có một cách nhìn rõ ràng về công nợ, Ban quản lý sẽ khó biết người thuê nào còn nợ, phòng nào đang phát sinh nợ, khoản nợ thuộc cơ sở nào và hóa đơn nào cần được xử lý trước.

Nỗi đau chính của Ban quản lý là dữ liệu công nợ không phải một khoản riêng biệt được nhập thủ công, mà được suy ra từ các hóa đơn chưa thanh toán hoặc quá hạn. Nếu hệ thống không truy xuất đúng từ hóa đơn, phòng, người thuê, cơ sở và thanh toán, thông tin công nợ có thể thiếu hoặc sai, dẫn đến khó thu tiền và khó đối soát.

Ban quản lý cũng cần biết số tiền còn nợ thực tế, số ngày nợ và mức độ quá hạn để ưu tiên xử lý. Nếu chỉ nhìn trạng thái hóa đơn mà không thấy số ngày nợ hoặc số tiền đã thanh toán một phần, việc đánh giá công nợ sẽ không chính xác.

Ngoài ra, phí chậm nộp là vấn đề dễ gây nhầm lẫn. Ban quản lý cần số tiền phạt chậm nộp để tham khảo, nhưng nếu hệ thống tự động cộng phí này vào hóa đơn hoặc lưu vào database khi chưa có quyết định thu, người thuê có thể bị tính sai tiền và phát sinh tranh chấp.

## 2. DOMAIN KNOWLEDGE

- `debt` / công nợ: trong feature này, công nợ là các hóa đơn chưa thanh toán hoặc đã quá hạn thanh toán.

- Công nợ không có bảng riêng. Dữ liệu công nợ được suy ra trực tiếp từ bảng `invoices`.

- `invoice`: hóa đơn ghi nhận các khoản người thuê cần thanh toán trong một kỳ.

- `UNPAID`: trạng thái hóa đơn chưa thanh toán.

- `OVERDUE`: trạng thái hóa đơn chưa thanh toán và đã quá hạn.

- `PAID`: trạng thái hóa đơn đã thanh toán, không hiển thị trong danh sách công nợ.

- `debtAmount`: số tiền còn nợ, được tính bằng tổng tiền hóa đơn trừ tổng tiền đã thanh toán thành công.

- `paidAmount`: tổng số tiền đã thanh toán thành công, lấy từ các bản ghi thanh toán hợp lệ trong bảng `payments`.

- `overdueDays`: số ngày nợ, được tính dựa trên ngày hiện tại và hạn thanh toán của hóa đơn.

- `lateFeePreview`: phí chậm nộp tạm tính, chỉ dùng để tham khảo trên màn hình công nợ.

- Phí chậm nộp tạm tính chỉ được tính sau khi quá hạn hơn 03 ngày.

- Từ ngày thứ 4 sau hạn thanh toán, mỗi ngày muộn được tính bằng `1%` giá trị tiền phòng/tháng.

- Phí chậm nộp tạm tính không được lưu vào `invoices`, `payments` hoặc bất kỳ bảng nào khác.

- Nếu Ban quản lý muốn thu phí chậm nộp, Ban quản lý phải tự nhập khoản này vào mục `Khoản phí khác` của hóa đơn.

- Dữ liệu công nợ cần kết hợp từ các bảng `invoices`, `rooms`, `users`, `facilities` và `payments`.

## 3. STAKEHOLDERS

- Ban quản lý / Management Board:

  - Người dùng chính của feature.

  - Cần xem danh sách công nợ, tìm kiếm, lọc, xem chi tiết hóa đơn nợ và chủ động xử lý khoản chưa thu.

  - Chịu trách nhiệm liên hệ người thuê và xử lý các hóa đơn chưa thanh toán hoặc quá hạn.

- Người thuê:

  - Người chịu ảnh hưởng trực tiếp bởi thông tin công nợ.

  - Có thể bị liên hệ nhắc nợ hoặc phải thanh toán thêm nếu Ban quản lý quyết định thu phí chậm nộp.

- Bộ phận tài chính / kế toán nếu có:

  - Cần dữ liệu công nợ chính xác để đối soát dòng tiền, khoản đã thu và khoản chưa thu.

- Quản trị hệ thống / kỹ thuật:

  - Đảm bảo dữ liệu được truy xuất đúng từ các bảng liên quan.

  - Đảm bảo phân quyền, hiệu năng API, bảo mật dữ liệu tài chính và xử lý lỗi phù hợp.

- Người có quyền quyết định nghiệp vụ:

  - Ban quản lý cấp cao hoặc chủ nhà trọ.

  - Cần quyết định quy tắc xử lý công nợ, cách áp dụng phí chậm nộp và quyền truy cập dữ liệu công nợ.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- Feature này chỉ dành cho người dùng có vai trò `Management Board`.

- Tất cả API phải yêu cầu authentication token.

- Backend phải kiểm tra quyền truy cập cho tất cả request.

- Không tạo bảng công nợ riêng.

- Danh sách công nợ phải được lấy từ bảng `invoices`.

- Chỉ lấy hóa đơn có trạng thái `UNPAID` hoặc `OVERDUE`.

- Hóa đơn có trạng thái `PAID` không hiển thị trong danh sách công nợ.

- Hệ thống phải join sang bảng `rooms` để lấy mã phòng.

- Hệ thống phải join sang bảng `users` để lấy thông tin người thuê.

- Hệ thống phải join sang bảng `facilities` để lấy thông tin cơ sở.

- Hệ thống phải join sang bảng `payments` để lấy thông tin thanh toán.

- Chỉ các thanh toán có trạng thái thành công mới được tính vào `paidAmount`.

- Số tiền còn nợ được tính khi hiển thị, không lưu vào bảng riêng.

- Số ngày nợ được tính khi hiển thị, không lưu vào bảng riêng.

- Phí chậm nộp tạm tính được tính khi hiển thị, không lưu vào bất kỳ bảng nào.

- Phí chậm nộp tạm tính không được tự động cộng vào tổng tiền hóa đơn.

- Phí chậm nộp tạm tính không được tự động tạo payment.

- Nếu Ban quản lý muốn thu phí chậm nộp, Ban quản lý tự nhập vào `Khoản phí khác` của hóa đơn.

- Trạng thái lọc công nợ chỉ hợp lệ với `UNPAID` hoặc `OVERDUE`.

- API danh sách công nợ phải phản hồi dưới `1000ms (p95)`.

- API chi tiết công nợ phải phản hồi dưới `500ms (p95)`.

- Rate limit: `100 requests/phút/người dùng`.

- Không bao gồm tạo công nợ thủ công, chỉnh sửa công nợ trực tiếp, xóa công nợ, thanh toán trực tuyến, tích hợp cổng thanh toán, nhắc nợ tự động hoặc tự động tạo hóa đơn phạt chậm nộp.

## 5. ASSUMPTIONS (giả định cần confirm)

- Giả định `Management Board` là vai trò đã tồn tại trong hệ thống phân quyền.

- Giả định bảng `invoices` đã có trạng thái `UNPAID`, `OVERDUE`, `PAID`.

- Giả định trạng thái `OVERDUE` đã được cập nhật trước đó bởi hệ thống hóa đơn hoặc một job riêng.

- Giả định mỗi hóa đơn liên kết được với một phòng thông qua `room_id`.

- Giả định mỗi phòng có thể xác định được người thuê hiện tại qua `tenant_id`.

- Giả định mỗi phòng có thể xác định được cơ sở qua `facility_id`.

- Giả định bảng `payments` có liên kết với hóa đơn qua `invoice_id`.

- Giả định thanh toán thành công trong bảng `payments` được nhận diện bằng trạng thái `SUCCESS`.

- Giả định hệ thống có thể hỗ trợ thanh toán một phần, vì công thức công nợ có tính `paidAmount`.

- Giả định nếu `debtAmount` nhỏ hơn 0 thì hệ thống chỉ hiển thị là 0, không xử lý hoàn tiền trong feature này.

- Giả định phí chậm nộp chỉ là số tiền tham khảo, không phải nghĩa vụ thanh toán chính thức cho đến khi Ban quản lý nhập vào khoản phí khác.

- Giả định tiền phòng/tháng dùng để tính phí chậm nộp lấy từ `roomFee` trong hóa đơn.

- Giả định ngày hiện tại được lấy theo timezone hệ thống.

- Giả định tìm kiếm keyword có thể áp dụng cho mã hóa đơn, mã phòng hoặc tên người thuê.

- Giả định màn hình công nợ chỉ phục vụ xem và tra cứu, không ghi nhận thanh toán mới.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- Role `Management Board` trong database tương ứng với role nào: `ADMIN`, `MANAGER`, hay một role riêng?

- Trạng thái `OVERDUE` được cập nhật tự động bởi job hằng ngày hay được tính động khi truy vấn công nợ?

- Nếu hóa đơn đã quá hạn nhưng trạng thái vẫn là `UNPAID`, màn hình công nợ có tự coi là quá hạn không?

- Công thức phí chậm nộp 1% tiền phòng/tháng mỗi ngày có phải quy định chính thức không?

- Phí chậm nộp tính từ ngày thứ 4 sau hạn thanh toán có bao gồm ngày hiện tại hay không?

- Nếu hóa đơn được thanh toán một phần, trạng thái hóa đơn vẫn là `UNPAID` hay có trạng thái riêng như `PARTIALLY_PAID`?

- Nếu người thuê thanh toán dư tiền, phần dư có được ghi nhận ở đâu không?

- Nếu không tìm thấy phòng, người thuê hoặc cơ sở liên quan, hệ thống chỉ hiển thị `Không xác định` hay cần báo lỗi dữ liệu?

- Có cần lọc công nợ theo cơ sở mà Ban quản lý phụ trách không?

- Nếu Ban quản lý chỉ phụ trách một số cơ sở, API có cần chặn xem công nợ của cơ sở khác không?

- Có cần hiển thị số điện thoại và email người thuê ngay ở danh sách hay chỉ ở màn chi tiết?

- Có cần sắp xếp mặc định theo số ngày nợ giảm dần hoặc hạn thanh toán gần nhất không?

- Có cần export danh sách công nợ ra Excel/PDF không?

- Khi Ban quản lý muốn thu phí chậm nộp, họ sửa hóa đơn cũ hay tạo một khoản phí mới ở kỳ sau?

- Nếu hóa đơn đã có nhiều payment liên quan, trạng thái nào của payment được coi là thanh toán thành công: `SUCCESS`, `PAID`, hay giá trị khác?

- Có cần lịch sử xử lý công nợ, ví dụ đã gọi điện, đã nhắc lần 1, đã nhắc lần 2 không?

- Có cần chức năng gửi nhắc nợ thủ công từ màn công nợ không?

- Có cần hiển thị tổng số tiền công nợ toàn hệ thống hoặc theo cơ sở không?

- Có cần phân biệt công nợ trong hạn và công nợ quá hạn không?

- Nếu hệ thống chạy ở nhiều timezone, ngày hiện tại dùng theo timezone nào?