# Quản lý hợp đồng

# Người viết: @BuiDinh | Ngày: 2026-06-21

## 1. PROBLEM STATEMENT

Trong hệ thống quản lý nhà trọ, hợp đồng thuê phòng là căn cứ quan trọng để ghi nhận thỏa thuận giữa bên cho thuê và người thuê. Nếu thông tin hợp đồng không được quản lý tập trung, Ban quản lý sẽ khó theo dõi phòng nào đang có khách thuê, hợp đồng nào còn hiệu lực, hợp đồng nào đã hết hạn hoặc không còn sử dụng.

Nỗi đau chính của Ban quản lý là quá trình lập hợp đồng có nhiều thông tin cần nhập và dễ sai sót, bao gồm thông tin cá nhân người thuê, phòng thuê, thời hạn hợp đồng, tiền phòng, tiền cọc và các khoản phí liên quan. Nếu Ban quản lý phải nhập thủ công toàn bộ thông tin phòng, tầng, tiền phòng hoặc tiền cọc, hợp đồng có thể sai dữ liệu so với hệ thống thực tế.

Người thuê cũng bị ảnh hưởng nếu hợp đồng ghi sai thông tin cá nhân, phòng thuê, thời hạn hoặc số tiền. Những sai lệch này có thể gây tranh chấp khi thanh toán, trả phòng, hoàn cọc hoặc xử lý trách nhiệm giữa hai bên.

Ngoài ra, hợp đồng là tài liệu cần in ra để lưu trữ hoặc đưa cho người thuê ký xác nhận. Nếu dữ liệu hợp đồng không được lấy đúng từ hệ thống, bản in hợp đồng có thể thiếu thông tin, sai mẫu hoặc không đủ căn cứ để đối chiếu sau này.

Một vấn đề quan trọng khác là phân quyền theo cơ sở. Ban quản lý chỉ được xem và tạo hợp đồng cho phòng thuộc cơ sở mình phụ trách. Nếu hệ thống cho phép truy cập hoặc tạo hợp đồng cho cơ sở khác, dữ liệu hợp đồng có thể bị lộ hoặc bị thao tác sai quyền.

## 2. DOMAIN KNOWLEDGE

- `contract` / hợp đồng: tài liệu ghi nhận thỏa thuận thuê phòng giữa bên cho thuê và người thuê.

- `contracts`: bảng chính dùng để lưu dữ liệu hợp đồng.

- `contract_id`: mã định danh nội bộ của hợp đồng, do hệ thống hoặc database tự sinh tăng dần.

- `code`: mã hợp đồng duy nhất, được hệ thống tự sinh.

- Định dạng mã hợp đồng đề xuất: `HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}`.

- `tenant`: người thuê phòng.

- `tenant_full_name`: họ tên khách thuê được ghi trên hợp đồng.

- `tenant_identity_number`: số CMND/CCCD của khách thuê.

- `room_id`: ID phòng được thuê, dùng để liên kết hợp đồng với bảng `rooms`.

- `roomCode`: mã phòng được hiển thị trên danh sách, chi tiết và bản in hợp đồng.

- `facility`: cơ sở/khu nhà mà phòng thuộc về.

- `signed_date`: ngày lập hợp đồng.

- `start_date`: ngày bắt đầu hợp đồng. Nếu không nhập riêng, mặc định bằng `signed_date`.

- `end_date`: ngày hết hạn hợp đồng.

- `amount_in_words`: số tiền bằng chữ, dùng để hiển thị trong hợp đồng in ra.

- `ACTIVE`: hợp đồng đang có hiệu lực.

- `INACTIVE`: hợp đồng không còn hiệu lực.

- Khi tạo mới, hợp đồng mặc định có trạng thái `ACTIVE`.

- Một phòng đang có hợp đồng `ACTIVE` thì không được tạo thêm hợp đồng mới.

- Khi in hợp đồng, dữ liệu phải được lấy từ database, không yêu cầu nhập lại thủ công.

- Ban quản lý chỉ được xem và tạo hợp đồng cho phòng thuộc cơ sở mà mình phụ trách.

## 3. STAKEHOLDERS

- Ban quản lý / Management Board:

  - Người dùng chính của feature.

  - Cần xem danh sách hợp đồng, xem chi tiết, tạo hợp đồng mới và in hợp đồng.

  - Chịu trách nhiệm đảm bảo hợp đồng đúng thông tin người thuê, phòng thuê, thời hạn và trạng thái.

- Người thuê:

  - Người ký hợp đồng và chịu ảnh hưởng trực tiếp bởi nội dung hợp đồng.

  - Cần hợp đồng ghi đúng thông tin cá nhân, phòng thuê, tiền thuê, tiền cọc, thời hạn và trách nhiệm hai bên.

- Chủ cơ sở / đại diện bên cho thuê:

  - Là bên A trong hợp đồng.

  - Cần hợp đồng làm căn cứ quản lý thuê phòng, thu tiền, hoàn cọc và xử lý tranh chấp.

- Bộ phận tài chính / kế toán nếu có:

  - Cần thông tin hợp đồng để đối chiếu tiền phòng, tiền cọc, kỳ hạn thuê và các khoản phí liên quan.

- Quản trị hệ thống / kỹ thuật:

  - Đảm bảo phân quyền theo cơ sở, dữ liệu liên kết đúng giữa `contracts`, `rooms`, `users`, `facilities`.

  - Đảm bảo mã hợp đồng duy nhất, API ổn định và bản in lấy đúng dữ liệu.

- Người có quyền quyết định nghiệp vụ:

  - Ban quản lý cấp cao hoặc chủ cơ sở.

  - Cần xác nhận mẫu hợp đồng, quy tắc tạo hợp đồng, trạng thái hợp đồng và quy định khi phòng đã có hợp đồng hiệu lực.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- Chỉ Ban quản lý được truy cập chức năng Quản lý hợp đồng.

- Ban quản lý chỉ được xem hợp đồng của cơ sở mình phụ trách.

- Không được xem hợp đồng thuộc cơ sở khác.

- Không được tạo hợp đồng cho phòng thuộc cơ sở khác.

- Khi truy xuất hợp đồng, hệ thống phải join `contracts` với `rooms` để xác định cơ sở của phòng.

- Mỗi hợp đồng phải liên kết với một phòng hợp lệ.

- Mỗi hợp đồng phải liên kết với một người thuê hợp lệ.

- Mỗi hợp đồng phải có mã hợp đồng duy nhất.

- Hệ thống phải tự sinh `contract_id`.

- Hệ thống phải tự sinh `code`.

- Khi tạo mới, hợp đồng có trạng thái mặc định là `ACTIVE`.

- Ban quản lý bắt buộc nhập họ tên khách thuê.

- Ban quản lý bắt buộc nhập số CMND/CCCD.

- Ban quản lý bắt buộc chọn phòng thuê.

- Ban quản lý bắt buộc nhập ngày lập hợp đồng.

- Ban quản lý bắt buộc nhập ngày hết hạn hợp đồng.

- `end_date` phải lớn hơn hoặc bằng `signed_date`.

- Nếu không nhập `start_date`, hệ thống mặc định `start_date = signed_date`.

- Không được tạo hợp đồng mới cho phòng đang có hợp đồng `ACTIVE`.

- Khi chọn phòng, hệ thống phải kiểm tra phòng tồn tại.

- Khi chọn phòng, hệ thống phải kiểm tra phòng thuộc cơ sở mà Ban quản lý phụ trách.

- Khi chọn phòng hợp lệ, hệ thống tự động lấy mã phòng, tầng, tiền phòng, tiền cọc và thông tin cơ sở.

- Khi in hợp đồng, hệ thống không được yêu cầu nhập lại dữ liệu thủ công.

- Dữ liệu in hợp đồng phải lấy từ database.

- Hợp đồng in ra phải theo mẫu hợp đồng thuê phòng trọ đã định nghĩa.

- API danh sách hợp đồng phải phản hồi dưới `1000ms (p95)`.

- API chi tiết hợp đồng phải phản hồi dưới `500ms (p95)`.

- API tạo hợp đồng phải phản hồi dưới `1000ms (p95)`.

- API in hợp đồng phải phản hồi dưới `2000ms (p95)`.

- Không bao gồm ký hợp đồng điện tử, chữ ký số, upload file scan hợp đồng, gia hạn hợp đồng, thanh lý hợp đồng, phụ lục hợp đồng hoặc tự động chuyển hợp đồng hết hạn sang `INACTIVE`.

## 5. ASSUMPTIONS (giả định cần confirm)

- Giả định vai trò Ban quản lý đã tồn tại trong hệ thống phân quyền.

- Giả định hệ thống đã có cơ chế xác định cơ sở mà Ban quản lý phụ trách.

- Giả định bảng `contracts` đã tồn tại hoặc sẽ được tạo theo cấu trúc trong SPEC.

- Giả định bảng `rooms` có thông tin `room_id`, `room_code`, tầng, giá phòng, tiền cọc và cơ sở.

- Giả định bảng `facilities` có thông tin địa chỉ cơ sở và thông tin đại diện bên A hoặc có thể lấy từ cấu hình khác.

- Giả định mỗi hợp đồng chỉ gắn với một phòng.

- Giả định mỗi hợp đồng chỉ gắn với một người thuê chính.

- Giả định khách thuê đã có hoặc sẽ được tạo user tương ứng trước khi tạo hợp đồng.

- Giả định thông tin người thuê được lưu snapshot trong bảng `contracts` để bản hợp đồng không bị thay đổi khi thông tin user thay đổi sau này.

- Giả định tiền phòng và tiền cọc lấy từ dữ liệu phòng tại thời điểm in hoặc tạo hợp đồng.

- Giả định nếu cần giữ nguyên tiền phòng/tiền cọc tại thời điểm tạo, hệ thống sẽ bổ sung cột snapshot.

- Giả định mã hợp đồng sinh theo format `HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}`.

- Giả định bản in hợp đồng có thể là HTML print view hoặc PDF tùy thiết kế kỹ thuật.

- Giả định trạng thái hợp đồng chỉ gồm `ACTIVE` và `INACTIVE` trong phạm vi hiện tại.

- Giả định hệ thống chưa tự động chuyển hợp đồng hết hạn sang `INACTIVE`.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- Role Ban quản lý trong database tương ứng với role nào: `MANAGER`, `ADMIN`, `Management Board` hay một role riêng?

- Một Ban quản lý có thể phụ trách nhiều cơ sở không? Nếu có, khi tạo hợp đồng sẽ chọn cơ sở trước hay chọn phòng trực tiếp?

- Thông tin bên A lấy từ đâu: bảng `facilities`, bảng cấu hình chủ cơ sở hay nhập thủ công?

- Bên A có cần lưu họ tên, ngày sinh, CMND/CCCD, địa chỉ và số điện thoại trong database không?

- Địa chỉ cơ sở dùng trong hợp đồng in ra lấy từ trường nào?

- Tiền phòng và tiền cọc có cần lưu snapshot trong bảng `contracts` không?

- Nếu giá phòng thay đổi sau khi hợp đồng được tạo, bản in hợp đồng cũ dùng giá cũ hay giá hiện tại?

- Nếu phòng đã có hợp đồng `ACTIVE` nhưng người thuê sắp trả phòng, có cho tạo hợp đồng mới trước ngày kết thúc không?

- Khi hợp đồng hết hạn, hệ thống có cần tự động chuyển sang `INACTIVE` không?

- Ai có quyền cập nhật trạng thái hợp đồng từ `ACTIVE` sang `INACTIVE`?

- Có cần chức năng gia hạn hợp đồng không, hay tạo hợp đồng mới hoàn toàn?

- Có cần chức năng thanh lý hợp đồng không?

- Có cần cho phép chỉnh sửa hợp đồng sau khi tạo không?

- Nếu nhập sai thông tin người thuê sau khi đã in hợp đồng, quy trình xử lý là chỉnh sửa, hủy hay tạo hợp đồng mới?

- Có cần lưu lịch sử thay đổi hợp đồng không?

- Có cần upload bản scan hợp đồng đã ký không?

- Có cần hỗ trợ nhiều người thuê trong cùng một hợp đồng không?

- Số tiền bằng chữ có bắt buộc nhập tay không, hay hệ thống tự chuyển từ tiền phòng sang chữ?

- Mẫu hợp đồng có cố định cho mọi cơ sở không, hay mỗi cơ sở được tùy chỉnh?

- Bản in hợp đồng cần xuất PDF hay chỉ cần HTML print view?

- Có cần đánh số thứ tự hợp đồng theo từng phòng, từng cơ sở hay toàn hệ thống?

- Khi tạo hợp đồng, có cần tự động cập nhật trạng thái phòng sang đang thuê không?

- Khi hợp đồng chuyển `INACTIVE`, có cần tự động cập nhật trạng thái phòng không?

- Có cần kiểm tra định dạng số điện thoại và CMND/CCCD không?

- Có cần kiểm tra ngày sinh người thuê phải đủ tuổi thuê phòng không?