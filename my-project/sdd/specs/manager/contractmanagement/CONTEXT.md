# CONTEXT.md \[Quản lý hợp đồng\]

# Người viết: Bùi Đỉnh | Ngày: 2026-06-13

## 1. PROBLEM STATEMENT

- **Nhập liệu thủ công rập khuôn & Dễ sai sót cực kỳ cao**: Ban quản lý khi lập một hợp đồng thuê phòng trọ mới phải copy/paste hoặc gõ lại tay hàng loạt trường thông tin tẻ nhạt sẵn có của phòng (mã phòng, tầng, tiền phòng, tiền cọc, thông tin cơ sở) từ danh mục quản lý phòng sang form hợp đồng, dễ dẫn đến tình trạng gõ nhầm giá tiền, lệch tầng, hoặc gõ sai mã phòng.
- **Sai lệch dữ liệu lịch sử**: Nếu sau này giá phòng hoặc giá dịch vụ của cơ sở thay đổi, các hợp đồng cũ bị cập nhật động theo nếu hệ thống chỉ tham chiếu (join) động, làm sai lệch doanh thu lịch sử và thông tin cam kết ban đầu với khách thuê.
- **Tốn thời gian tạo tài khoản thủ công cho người thuê**: Sau khi ký xong hợp đồng giấy/lưu hệ thống, Ban quản lý lại phải qua một phân hệ khác tạo tài khoản thủ công cho khách thuê (nhập lại họ tên, email, SĐT, CCCD), gây tốn thời gian gấp đôi và dễ mất đồng bộ dữ liệu.
- **Mất an toàn dữ liệu cơ sở**: Ban quản lý của cơ sở này có nguy cơ nhìn thấy hoặc can thiệp (xem, xóa, tạo) vào hợp đồng thuộc cơ sở của quản lý khác nếu hệ thống không phân quyền chặt chẽ theo phạm vi quản lý cơ sở.
- **Rác dữ liệu**: Các hợp đồng cũ đã hết hạn (`INACTIVE`) bị xếp chồng lấn át các hợp đồng đang chạy (`ACTIVE`), cần có cơ chế dọn dẹp sạch sẽ nhưng tránh xóa nhầm các hợp đồng đang có hiệu lực.
- **Không có mẫu in hợp đồng chuẩn hóa**: Ban quản lý tốn thời gian soạn thảo hợp đồng bằng file Word bên ngoài rồi điền tay thông tin, thay vì có một file in chuẩn hóa tự động đổ dữ liệu hệ thống ra đầy đủ các điều khoản pháp lý quy định để khách ký ngay.

## 2. DOMAIN KNOWLEDGE

- **Hợp đồng (Contract)**: Thỏa thuận pháp lý ràng buộc giữa Ban quản lý (Bên A - Đại diện cơ sở) và Khách thuê (Bên B - Tenant) về việc thuê một phòng cụ thể trong một khoảng thời gian nhất định với mức giá cố định.
- **Trạng thái Hợp đồng**:
  - `ACTIVE`: Hợp đồng đang có hiệu lực pháp lý, phòng tương ứng sẽ được coi là đã có người ở (`OCCUPIED`). Một phòng tại một thời điểm chỉ được phép có duy nhất một hợp đồng `ACTIVE`.
  - `INACTIVE`: Hợp đồng đã hết hiệu lực (hết hạn, thanh lý hoặc bị hủy). Chỉ các hợp đồng ở trạng thái này mới được phép xóa (soft-delete).
- **Mã hợp đồng (Contract Code)**: Định dạng duy nhất bắt buộc tuân thủ cấu trúc: `HD-{roomCode}-{signedDate:yyyyMMdd}-{sequence}` (Ví dụ: `HD-402-20240223-001`).
- **Dữ liệu Snapshot tại thời điểm tạo**: Lưu lại bản sao cứng của các giá trị phòng (`rent_price`, `deposit_amount`, `room_code_snapshot`, `floor_snapshot`) ngay khi nhấn lưu hợp đồng để bảo vệ tính toàn vẹn dữ liệu lịch sử kể cả khi phòng thay đổi giá trong tương lai.
- **Tenant (Người thuê)**: Đối tượng khách thuê phòng, có tài khoản hệ thống với role `TENANT` liên kết trực tiếp với trường `tenant_id` trong hợp đồng.

## 3. STAKEHOLDERS

- **Ban quản lý (Manager)**: Người trực tiếp vận hành, thụ hưởng việc giảm tải nhập liệu, quản lý vòng đời hợp đồng, tạo tài khoản cho khách và in ấn biểu mẫu trực tiếp.
- **Khách thuê (Tenant)**: Người chịu ảnh hưởng trực tiếp bởi các điều khoản hợp đồng, được nhận tài khoản hệ thống tự sinh để đăng nhập và theo dõi dịch vụ.
- **Chủ cơ sở / Admin**: Người có quyền quyết định cao nhất, giám sát toàn bộ hợp đồng của tất cả các cơ sở, xem Audit Log để kiểm tra tính minh bạch của dữ liệu.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- **Kiến trúc kĩ thuật**: Phải tuân thủ mô hình MVC truyền thống (JSP + Servlet + Service + DAO).
- **Công nghệ database**: Chỉ dùng JDBC thuần và `PreparedStatement`, tuyệt đối nghiêm cấm sử dụng các framework ORM (như Hibernate, JPA, MyBatis).
- **Giao diện JSP**: Tuyệt đối không dùng Scriptlet (`<% ... %>`) trong file JSP.
- **Hiệu năng**: Thời gian phản hồi tối đa của hệ thống phải đạt dưới 500ms (P95).
- **Toàn vẹn dữ liệu**: Bắt buộc sử dụng Database Transaction khi thực hiện cập nhật đồng thời nhiều bảng dữ liệu (ví dụ: vừa thêm user vừa cập nhật trạng thái phòng và gán ID hợp đồng).
- **Bảo mật & Phân quyền**: Chỉ người dùng có vai trò `MANAGER` hoặc `ADMIN` mới được truy cập servlet. Ban quản lý chỉ được quyền thao tác dữ liệu thuộc cơ sở mình được giao phụ trách (Phân quyền dữ liệu theo `room_id` -&gt; `facility_id`).
- **Kiểm toán dữ liệu**: Bắt buộc ghi log tập trung bằng SLF4J và tạo bản ghi Audit Log đối với các hành động: Tạo hợp đồng, Xóa hợp đồng và Thêm người thuê.

## 5. ASSUMPTIONS (giả định cần confirm)

- **Giả định 1**: Hệ thống đã có sẵn dữ liệu đầy đủ về thông tin Bên A (Thông tin đại diện chủ cơ sở/ban quản lý) trong bảng `facilities` hoặc bảng cấu hình để tự động đổ ra bản in. *Rủi ro nếu sai*: Bản in sẽ bị trống thông tin Bên A hoặc phải nhập tay bừa bãi.
- **Giả định 2**: Quy trình tạo tài khoản tenant từ hợp đồng giả định rằng email hoặc SĐT của khách thuê là duy nhất trên toàn hệ thống. *Rủi ro nếu sai*: Nếu khách thuê cũ quay lại thuê ở cơ sở khác, hệ thống sẽ báo trùng lặp email/SĐT khi tạo mới thay vì tái kích hoạt (`Reactivate`) một cách trơn tru nếu logic so khớp bị lỗi.
- **Giả định 3**: Khi phòng đã ở trạng thái `ACTIVE` hợp đồng, hệ thống coi phòng đó là không khả dụng để tạo hợp đồng khác. Giả định không có trường hợp nhiều người cùng đứng tên các hợp đồng độc lập trên cùng một phòng tại một thời điểm (hệ thống hiện tại Out Of Scope việc quản lý nhiều người thuê chung 1 hợp đồng hoặc nhiều hợp đồng song song).

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- **Câu hỏi 1**: Hệ thống lấy thông tin đơn giá điện, nước, Internet, rác thải ở đâu để hiển thị vào "Điều 2" của bản in hợp đồng? (Lấy từ cấu hình mặc định của cơ sở `facilities`, hay Ban quản lý sẽ phải tự điền vào form khi tạo hợp đồng?)
- **Câu hỏi 2**: Số tiền bằng chữ (`amountInWords`) cho phần tiền phòng hiện tại đang bắt nhập tay trên form tạo hợp đồng. Hệ thống có cần tích hợp thư viện tự động chuyển số thành chữ từ trường `rent_price` để tránh Ban quản lý gõ sai lệch giữa số tiền bằng số và bằng chữ không?
- **Câu hỏi 3**: Đối với việc Soft-delete hợp đồng `INACTIVE`, hệ thống chỉ đánh dấu xóa dữ liệu (`is_deleted = true`), vậy phòng liên kết với hợp đồng đó có tự động giải phóng trạng thái về trống (`AVAILABLE`) hay nghiệp vụ đó phải được xử lý ở một phân hệ trả phòng độc lập khác?
- **Câu hỏi 4**: Khi tạo tài khoản Tenant từ hợp đồng, mật khẩu tạm thời được gửi qua email. Nếu hệ thống chưa cấu hình Mail Server thành công thì có cơ chế hiển thị trực tiếp mật khẩu tạm thời lên màn hình cho Ban quản lý copy gửi cho khách không?