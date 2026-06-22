# CONTEXT.md - Feature Nhân viên vận hành báo cáo sự cố (Dự án Quản lý nhà trọ)

**Người viết:** Phạm Anh Tú

**Ngày:** 2026-06-13

---

## 1. PROBLEM STATEMENT

### Khoảng trống trong quy trình ghi nhận

Trong quá trình làm việc, kiểm tra hoặc tuần tra tại các khu nhà trọ, nhân viên vận hành thường xuyên phát hiện các vấn đề phát sinh (ví dụ: hỏng hóc thiết bị điện nước, cơ sở vật chất xuống cấp).
Hệ thống hiện tại cần một chức năng để nhân viên có thể chủ động ghi nhận và báo cáo sự cố ngay lập tức tại hiện trường.

### Rủi ro trôi thông tin

Nếu không có hệ thống báo cáo chuẩn hóa, nhân viên thường phải chụp ảnh và gửi qua các kênh chat (Zalo, Messenger) cho quản lý. Việc này dẫn đến tình trạng dễ trôi tin nhắn, khó lưu trữ, khó theo dõi tiến độ xử lý và không thống kê được tần suất hỏng hóc của tài sản.

---

## 2. DOMAIN KNOWLEDGE

### Vị trí sự cố (Location)

Sự cố trong nhà trọ thường được chia thành hai nhóm vị trí chính:

* **Khu vực chung:** Nhà để xe, hành lang, cầu thang, sân thượng, cổng chính.
* **Khu vực riêng:** Nằm bên trong một phòng trọ cụ thể (cần ghi rõ mã phòng).

### Phân loại và Mức độ (Category & Severity)

* **Phân loại:** Điện, Nước, An ninh, Nội thất/Cơ sở vật chất, Vệ sinh.
* **Mức độ ưu tiên:** * *Khẩn cấp* (VD: Chập cháy điện, vỡ ống nước rò rỉ mạnh).
* *Bình thường* (VD: Cháy bóng đèn hành lang, kẹt rác cống thoát nước nhẹ).



---

## 3. STAKEHOLDERS

### Nhân viên vận hành (Operations Staff)

Người trực tiếp đi làm tại nhà trọ, phát hiện sự cố, chụp ảnh hiện trường và điền thông tin báo cáo lên hệ thống.

### Quản lý nhà trọ (Manager)

Người tiếp nhận danh sách các sự cố do nhân viên báo cáo để từ đó lên kế hoạch điều phối, phê duyệt chi phí và phân công thợ sửa chữa.

### Đội bảo trì (Maintenance Team)

(Bên thứ ba hoặc nhân viên nội bộ) Tiếp nhận thông tin chi tiết về sự cố đã được báo cáo để chuẩn bị vật tư và tiến hành khắc phục.

---

## 4. CONSTRAINTS

### Ràng buộc Kỹ thuật (Technical Constraints)

#### Tối ưu hóa đa phương tiện

Nhân viên thường thao tác trên điện thoại di động tại hiện trường bằng 3G/4G. Giao diện (Frontend) phải hỗ trợ nén ảnh trước khi tải lên server để tiết kiệm băng thông và tăng tốc độ xử lý, đồng thời cho phép chọn tải lên nhiều ảnh cùng lúc.

#### Thao tác Database

Quá trình lưu trữ thông tin tạo mới sự cố (`INSERT`) vào cơ sở dữ liệu bắt buộc phải sử dụng **Basic SQL Statements** theo đúng form chuẩn của dự án hiện tại, tuyệt đối không tự ý chuyển sang dùng PreparedStatements.

### Ràng buộc Phạm vi (Scope Constraints)

#### Nằm ngoài phạm vi

* Tính năng này chỉ bao gồm luồng **Tạo mới (Create)** báo cáo sự cố.
* Các thao tác phân công việc cho thợ, cập nhật trạng thái "Đang sửa", hoặc "Đã hoàn thành" sẽ nằm ở các Feature/Module khác.

---

## 5. ASSUMPTIONS

* Giả định nhân viên đã đăng nhập vào hệ thống/ứng dụng bằng tài khoản của mình. Mã nhân viên (`staff_id`) sẽ được tự động liên kết với báo cáo sự cố mà không cần người dùng nhập tay.
* Thiết bị di động của nhân viên được cấp quyền truy cập Camera và Thư viện ảnh để phục vụ việc đính kèm minh chứng sự cố.
* Hệ thống API lưu trữ (CDN/Cloud Storage) luôn sẵn sàng để nhận và trả về URL của hình ảnh ngay sau khi upload.

---

## 6. OPEN QUESTIONS

### Quyền chỉnh sửa và thu hồi

Sau khi nhân viên bấm "Gửi báo cáo", nếu phát hiện điền sai thông tin (ví dụ: nhầm mã phòng, chọn sai mức độ ưu tiên):

* Hệ thống có cho phép nhân viên tự chỉnh sửa/xóa báo cáo trong một khoảng thời gian nhất định không?
* Hay báo cáo sẽ bị "khóa" ngay lập tức và phải liên hệ Quản lý để hủy bỏ?

### Luồng thông báo khẩn cấp (Alerting)

Đối với các sự cố được nhân viên đánh dấu là **"Khẩn cấp"**:

* Hệ thống có cần bắn ngay thông báo (Push Notification/Zalo ZNS/SMS) trực tiếp đến thiết bị di động của Quản lý không?
* Hay chỉ hiển thị tag đỏ trên màn hình Dashboard của Quản lý?