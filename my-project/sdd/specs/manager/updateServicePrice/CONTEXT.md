# Quản lý khoản phí và giá dịch vụ

# Người viết: @BuiDinh | Ngày: 2026-06-21

## 1. PROBLEM STATEMENT

Trong quá trình vận hành chung cư hoặc cơ sở cho thuê, Ban quản lý cần duy trì các mức giá như giá điện, giá nước và phí dịch vụ theo đúng chính sách vận hành từng thời kỳ. Các mức giá này ảnh hưởng trực tiếp đến hóa đơn và công nợ của cư dân/người thuê.

Nỗi đau chính của Ban quản lý là nếu giá điện, giá nước hoặc phí dịch vụ không được cập nhật đúng lúc, hóa đơn phát sinh sau đó có thể bị tính sai. Điều này làm giảm tính minh bạch, gây khó khăn khi giải thích với cư dân/người thuê và có thể tạo ra sai lệch trong quản lý tài chính.

Một nỗi đau khác là mỗi Ban quản lý chỉ phụ trách một hoặc một số cơ sở nhất định. Nếu hệ thống hiển thị hoặc cho phép cập nhật nhầm dữ liệu của cơ sở khác, dữ liệu giá có thể bị thay đổi sai quyền, dẫn đến hóa đơn của nhiều người thuê bị ảnh hưởng.

Ngoài ra, việc thay đổi giá cần có khả năng truy vết. Nếu không lưu lại giá cũ, giá mới, người cập nhật và thời gian cập nhật, Ban quản lý sẽ khó kiểm tra lại nguyên nhân sai lệch khi có tranh chấp hoặc khi cần đối soát hóa đơn.

## 2. DOMAIN KNOWLEDGE

- `facility` / cơ sở: địa điểm hoặc khu nhà mà Ban quản lý phụ trách vận hành.

- `facilities`: bảng lưu thông tin cơ sở, đồng thời lưu các mức giá hiện tại như giá điện, giá nước và phí dịch vụ.

- `priceType`: loại giá/khoản phí được cập nhật trong hệ thống.

  - `ELECTRICITY`: giá điện.

  - `WATER`: giá nước.

  - `SERVICE_FEE`: phí dịch vụ.

- Giá điện: mức tiền tính theo đơn vị VNĐ/kWh.

- Giá nước: mức tiền tính theo đơn vị VNĐ/m3 hoặc VNĐ/m³.

- Phí dịch vụ: khoản phí cố định theo tháng, thường tính theo VNĐ/tháng.

- Giá hiện tại: mức giá đang được hệ thống áp dụng để tạo các hóa đơn mới.

- Giá cũ: mức giá trước khi Ban quản lý cập nhật.

- Giá mới: mức giá sau khi Ban quản lý cập nhật.

- Lịch sử thay đổi giá: bản ghi lưu lại loại giá được thay đổi, giá cũ, giá mới, ghi chú, người cập nhật và thời gian cập nhật.

- Hóa đơn đã phát hành trước thời điểm cập nhật giá không bị tính lại tự động.

- Hóa đơn được tạo sau thời điểm cập nhật sẽ sử dụng mức giá mới nhất.

- Ban quản lý chỉ được xem và cập nhật giá của cơ sở mà mình phụ trách.

## 3. STAKEHOLDERS

- Ban quản lý:

  - Người dùng chính của feature.

  - Cần xem giá hiện tại, cập nhật giá mới và kiểm tra lịch sử thay đổi giá.

  - Chịu trách nhiệm đảm bảo dữ liệu giá của cơ sở mình phụ trách là chính xác.

- Cư dân / người thuê:

  - Người bị ảnh hưởng trực tiếp bởi giá điện, giá nước và phí dịch vụ khi hệ thống tạo hóa đơn.

  - Cần hóa đơn minh bạch, đúng giá và không bị tính sai do dữ liệu giá lỗi thời.

- Bộ phận tài chính / kế toán nếu có:

  - Cần dữ liệu giá chính xác để đối soát hóa đơn, công nợ và dòng tiền.

  - Cần lịch sử thay đổi giá để kiểm tra khi có sai lệch.

- Quản trị hệ thống / kỹ thuật:

  - Đảm bảo phân quyền đúng cơ sở, API hoạt động ổn định, dữ liệu cập nhật đúng bảng và audit log được lưu đầy đủ.

- Người có quyền quyết định nghiệp vụ:

  - Ban quản lý cấp cao, chủ cơ sở hoặc người phụ trách vận hành.

  - Cần quyết định khi nào được thay đổi giá, ai được đổi giá và có cần quy trình duyệt thay đổi giá hay không.

## 4. CONSTRAINTS (ràng buộc không thể thay đổi)

- Chỉ người dùng có vai trò Ban quản lý được phép truy cập chức năng.

- Ban quản lý chỉ được xem và cập nhật dữ liệu giá của cơ sở mình phụ trách.

- Hệ thống không cho phép Ban quản lý cập nhật giá của cơ sở khác.

- Giá điện, giá nước và phí dịch vụ được lưu trong bảng `facilities`.

- Khi cập nhật giá, hệ thống chỉ cập nhật đúng cột tương ứng với `priceType`.

- Các loại giá hợp lệ gồm `ELECTRICITY`, `WATER`, `SERVICE_FEE`.

- Giá mới phải lớn hơn 0.

- Giá mới không được để trống.

- Giá mới phải là số hợp lệ.

- Tên khoản phí/dịch vụ và loại khoản phí/dịch vụ không được chỉnh sửa trực tiếp trong pop-up cập nhật.

- Hệ thống phải lưu lịch sử thay đổi giá sau mỗi lần cập nhật thành công.

- Lịch sử thay đổi phải lưu giá cũ, giá mới, loại giá, ghi chú, người cập nhật và thời gian cập nhật.

- Các hóa đơn đã phát hành trước thời điểm cập nhật không bị ảnh hưởng bởi giá mới.

- Các hóa đơn được tạo sau thời điểm cập nhật sẽ sử dụng giá mới nhất.

- Hệ thống phải ngăn gửi trùng yêu cầu cập nhật trong khi đang xử lý.

- API danh sách giá hiện tại phải phản hồi dưới 500ms ở p95.

- API cập nhật giá phải phản hồi dưới 500ms ở p95.

- API lịch sử thay đổi giá phải phản hồi dưới 1000ms ở p95.

- Giới hạn request là 100 requests/phút/người dùng.

## 5. ASSUMPTIONS (giả định cần confirm)

- Giả định vai trò Ban quản lý đã tồn tại trong hệ thống phân quyền.

- Giả định mỗi Ban quản lý có thể được ánh xạ tới một hoặc nhiều cơ sở.

- Giả định hệ thống xác định cơ sở dựa trên tài khoản đăng nhập của Ban quản lý.

- Giả định bảng `facilities` đã có các cột `electricity_price`, `water_price`, `service_fee`, `updated_at`, `updated_by`.

- Giả định mỗi cơ sở luôn có tối đa một bộ giá hiện tại cho điện, nước và phí dịch vụ.

- Giả định giá mới có hiệu lực ngay sau khi cập nhật thành công.

- Giả định hóa đơn lưu snapshot giá tại thời điểm tạo nên không cần tính lại hóa đơn cũ.

- Giả định Ban quản lý không cần quy trình phê duyệt nhiều bước khi thay đổi giá.

- Giả định ghi chú thay đổi giá là không bắt buộc.

- Giả định đơn vị tính của từng loại giá là cố định và không cho phép chỉnh sửa.

- Giả định lịch sử thay đổi giá được lưu trong bảng riêng, ví dụ `facility_price_histories`.

- Giả định người cập nhật được lấy từ user đang đăng nhập.

- Giả định chỉ có ba loại giá trong phạm vi hiện tại: điện, nước và phí dịch vụ.

- Giả định hệ thống chưa cần quản lý ngày hiệu lực trong tương lai cho giá mới.

## 6. OPEN QUESTIONS (câu hỏi chưa có câu trả lời)

- Role Ban quản lý trong database tương ứng với role nào: `MANAGER`, `ADMIN`, `Management Board` hay một role riêng?

- Một Ban quản lý có thể phụ trách nhiều cơ sở không? Nếu có, màn hình sẽ chọn cơ sở như thế nào?

- Nếu Ban quản lý phụ trách nhiều cơ sở, API `/facilities/current/prices` có trả về một cơ sở mặc định hay cần truyền `facilityId`?

- Giá mới có hiệu lực ngay lập tức hay có thể chọn ngày bắt đầu hiệu lực?

- Nếu giá thay đổi giữa kỳ hóa đơn, hóa đơn tháng đó dùng giá cũ, giá mới hay cần chia theo số ngày?

- Có cần quy trình duyệt thay đổi giá trước khi áp dụng không?

- Ai là người có quyền cuối cùng được cập nhật giá: Ban quản lý thường, quản lý cấp cao hay admin?

- Có cần giới hạn mức tăng/giảm giá tối đa trong một lần cập nhật không?

- Có cần cảnh báo nếu giá mới chênh lệch quá lớn so với giá cũ không?

- Ghi chú thay đổi giá có bắt buộc trong một số trường hợp không?

- Có cần gửi thông báo cho cư dân/người thuê khi giá điện, giá nước hoặc phí dịch vụ thay đổi không?

- Có cần hiển thị lịch sử thay đổi giá ngay trên màn hình danh sách không, hay tách thành màn hình riêng?

- Có cần cho phép rollback về giá cũ nếu nhập sai không?

- Nếu hai người cùng cập nhật một loại giá cùng lúc, hệ thống ưu tiên xử lý như thế nào?

- Có cần kiểm tra trùng giá, ví dụ giá mới bằng giá hiện tại thì có cho lưu không?

- Đơn vị giá nước thống nhất là `VNĐ/m3` hay `VNĐ/m³`?

- Khi giá thay đổi, công nợ chưa phát hành nhưng đã được chuẩn bị trước có bị ảnh hưởng không?