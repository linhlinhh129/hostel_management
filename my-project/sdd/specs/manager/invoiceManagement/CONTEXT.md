# Quản lý hóa đơn

# Người viết: @BuiDinh | Ngày: 2026-06-21

## 1. PROBLEM STATEMENT

Trong hệ thống quản lý nhà trọ, Ban quản lý cần theo dõi và thu các khoản tiền định kỳ từ người thuê như tiền phòng, tiền điện, tiền nước, phí dịch vụ, phí phát sinh và thuế. Nếu quy trình lập hóa đơn phụ thuộc nhiều vào nhập liệu thủ công, dữ liệu dễ bị sai do nhập nhầm chỉ số điện nước, sai đơn giá, thiếu phí hoặc tính nhầm tổng tiền.

Nỗi đau chính của Ban quản lý là khó đảm bảo hóa đơn chính xác, nhất quán và có thể kiểm tra lại khi dữ liệu liên quan nằm rải rác ở nhiều nơi như thông tin phòng, cơ sở, bảng giá dịch vụ, chỉ số điện nước và hợp đồng/cấu hình giá phòng.

Người thuê cũng bị ảnh hưởng nếu hóa đơn thiếu minh bạch hoặc sai số tiền, vì họ cần hóa đơn làm căn cứ thanh toán và đối chiếu các khoản phải nộp trong từng kỳ.

Ngoài ra, Ban quản lý cần theo dõi trạng thái thu tiền của từng hóa đơn. Nếu không quản lý tốt danh sách hóa đơn, hạn thanh toán và trạng thái thanh toán, hệ thống sẽ khó kiểm soát công nợ, khó phát hiện hóa đơn quá hạn và khó tra cứu lại lịch sử thu tiền.

## 2. DOMAIN KNOWLEDGE

- `invoice` / hóa đơn: tài liệu tài chính ghi nhận các khoản phí người thuê phải thanh toán trong một kỳ hóa đơn.

- `invoice_id`: mã định danh nội bộ của hóa đơn, do hệ thống hoặc database tự sinh tăng dần.

- `invoice_code`: mã hóa đơn hiển thị cho người dùng, có định dạng `INV-{roomCode}-{billingPeriod}`.

- `roomCode`: mã phòng được dùng để xác định phòng cần tạo hóa đơn.

- `billingPeriod`: kỳ hóa đơn theo tháng/năm, lưu theo định dạng `YYYYMM`, ví dụ `202606`.

- `dueDate`: hạn thanh toán của hóa đơn.

- `roomFee`: tiền phòng cố định của kỳ hóa đơn, lấy từ phòng, hợp đồng thuê hoặc cấu hình giá phòng.

- `facilities`: bảng/cấu hình lưu đơn giá hiện tại của cơ sở, gồm đơn giá điện, đơn giá nước và phí dịch vụ.

- `meter_readings`: bảng ghi nhận chỉ số điện nước theo phòng và kỳ hạn.

- Snapshot giá: hóa đơn phải lưu lại đơn giá điện, đơn giá nước, phí dịch vụ và tiền phòng tại thời điểm tạo để hóa đơn cũ không bị thay đổi khi bảng giá thay đổi sau này.

- Trạng thái hóa đơn:

  - `UNPAID`: chưa thanh toán.

  - `PAID`: đã thanh toán.

  - `OVERDUE`: chưa thanh toán và đã quá hạn.

- Hóa đơn đã thanh toán không được điều chỉnh.

- Mỗi phòng chỉ được có một hóa đơn trong một kỳ hóa đơn.

## 3. STAKEHOLDERS

- Ban quản lý / Management Board:

  - Người sử dụng chính của feature.

  - Cần tạo, kiểm tra, điều chỉnh, tra cứu và xuất hóa đơn.

  - Chịu trách nhiệm theo dõi công nợ và tình trạng thanh toán.

- Người thuê:

  - Người chịu ảnh hưởng trực tiếp bởi số tiền trên hóa đơn.

  - Cần hóa đơn rõ ràng để biết các khoản phải trả và đối chiếu khi có sai sót.

- Quản trị hệ thống / Admin kỹ thuật:

  - Đảm bảo dữ liệu phòng, cơ sở, chỉ số điện nước, phân quyền và audit log hoạt động đúng.

- Kế toán / bộ phận tài chính nếu có:

  - Cần dữ liệu hóa đơn chính xác để đối soát thu tiền và công nợ.

  - Có thể cần file PDF hóa đơn để lưu trữ hoặc cung cấp cho bên liên quan.

- Người có quyền quyết định nghiệp vụ:

  - Ban quản lý hoặc chủ nhà trọ.

  - Cần xác nhận các quy tắc như cách tính thuế, phí dịch vụ, phí khác, thời điểm chuyển quá hạn và quy trình xác nhận thanh toán.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- Chỉ người dùng có vai trò `Management Board` được truy cập chức năng quản lý hóa đơn.

- Mỗi hóa đơn phải liên kết với một phòng hợp lệ.

- `invoice_id` phải duy nhất và được sinh tự động theo thứ tự tăng dần.

- `invoice_code` phải duy nhất và sinh theo format `INV-{roomCode}-{billingPeriod}`.

- Kỳ hóa đơn được xác định theo tháng và năm, lưu theo định dạng `YYYYMM`.

- Mỗi phòng chỉ được có một hóa đơn trong một kỳ hạn.

- Khi tạo hóa đơn, hệ thống phải lấy đơn giá điện, đơn giá nước và phí dịch vụ từ bảng `facilities`.

- Khi tạo hóa đơn, hệ thống phải lấy chỉ số điện nước từ bảng ghi nhận chỉ số điện nước.

- Hóa đơn phải lưu snapshot đơn giá điện, đơn giá nước, phí dịch vụ và tiền phòng tại thời điểm tạo.

- Không được tự động cập nhật hóa đơn cũ khi giá trong bảng `facilities` thay đổi.

- Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ.

- Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ.

- Phí khác phải lớn hơn hoặc bằng 0.

- Thuế phải lớn hơn hoặc bằng 0 và lưu dưới dạng phần trăm `%`.

- Tiền điện, tiền nước, tiền thuế và tổng tiền phải nộp phải được hệ thống tính tự động.

- Người dùng không được nhập trực tiếp tiền điện, tiền nước, tiền thuế và tổng tiền phải nộp.

- Không được chỉnh sửa hóa đơn đã thanh toán.

- Hóa đơn xuất ra phải ở định dạng PDF.

- Tất cả thao tác tạo và điều chỉnh hóa đơn phải được ghi Audit Log.

## 5. ASSUMPTIONS (giả định cần confirm)

- Giả định `Management Board` là vai trò nghiệp vụ chính thức trong hệ thống và đã tồn tại trong cơ chế phân quyền.

- Giả định mỗi phòng chỉ thuộc về một cơ sở tại thời điểm tạo hóa đơn.

- Giả định bảng `facilities` đang lưu giá điện, giá nước và phí dịch vụ theo từng cơ sở.

- Giả định tiền phòng cố định có thể lấy từ phòng, hợp đồng thuê hoặc cấu hình giá phòng, nhưng nguồn ưu tiên chưa được xác nhận.

- Giả định bảng `meter_readings` đã có dữ liệu chỉ số điện nước trước khi Ban quản lý tạo hóa đơn.

- Giả định kỳ hóa đơn `YYYYMM` là đủ, không cần lưu ngày bắt đầu/kết thúc kỳ hóa đơn.

- Giả định thuế luôn tính trên tạm tính và theo phần trăm.

- Giả định `otherFee` là tổng phí phát sinh khác, không cần tách thành nhiều dòng chi tiết.

- Giả định trạng thái `OVERDUE` có thể được hệ thống tự xác định dựa trên `dueDate` và trạng thái chưa thanh toán.

- Giả định hóa đơn chỉ được điều chỉnh trước khi thanh toán, không có quy trình duyệt điều chỉnh.

- Giả định file PDF xuất ra chỉ phục vụ lưu trữ/cung cấp thông tin, chưa phải hóa đơn điện tử hợp pháp theo chuẩn thuế.

- Giả định người thuê không trực tiếp tạo hoặc chỉnh sửa hóa đơn.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- Vai trò `Management Board` trong hệ thống tương ứng với role nào ở database: `ADMIN`, `MANAGER`, `OPERATOR` hay một role riêng?

- Nguồn lấy tiền phòng cố định ưu tiên theo thứ tự nào: hợp đồng thuê, bảng phòng hay cấu hình giá phòng?

- Nếu phòng đổi giá giữa kỳ thì hóa đơn dùng giá cũ, giá mới hay cần tính theo số ngày áp dụng từng mức giá?

- Nếu người thuê chuyển phòng giữa kỳ thì hóa đơn tính theo phòng cũ, phòng mới hay tách thành nhiều hóa đơn?

- Kỳ hóa đơn có luôn theo tháng dương lịch không, hay có thể theo ngày bắt đầu hợp đồng?

- Hạn thanh toán mặc định là ngày nào nếu Ban quản lý không nhập?

- Thuế có bắt buộc áp dụng cho mọi hóa đơn không, hay có trường hợp thuế bằng 0?

- Phí dịch vụ là phí cố định theo phòng, theo người thuê, theo cơ sở hay theo số lượng người trong phòng?

- `otherFee` có cần nhập lý do/loại phí chi tiết hay chỉ cần một tổng số tiền và ghi chú?

- Hệ thống có cần lưu lịch sử các lần điều chỉnh hóa đơn không, ngoài `updatedAt` và Audit Log?

- Ai có quyền xác nhận hóa đơn đã thanh toán?

- Khi hóa đơn quá hạn, trạng thái `OVERDUE` được cập nhật tự động theo lịch hằng ngày hay chỉ tính động khi xem danh sách?

- Có cho phép xóa hóa đơn chưa thanh toán không, hay chỉ cho phép điều chỉnh?

- Nếu thiếu dữ liệu chỉ số điện nước, Ban quản lý có được nhập bổ sung ngay trong luồng tạo hóa đơn không?

- PDF hóa đơn cần theo mẫu thiết kế nào, có cần logo, chữ ký, thông tin chủ trọ hoặc thông tin người thuê không?