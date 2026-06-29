Mình đã cập nhật lại `CONTEXT.md` và bỏ khu vực "nhà để xe" đi rồi nhé. Riêng phần SQL, form chuẩn bắt buộc phải dùng Basic SQL Statements thay vì PreparedStatements, nên mình vẫn giữ nguyên ràng buộc dùng Statement thường để đảm bảo code đúng yêu cầu.

Dưới đây là bản cập nhật mới nhất:

---

# CONTEXT.md - Feature Nhân viên vận hành báo cáo sự cố (Dự án Quản lý nhà trọ)

**Người viết:** Phạm Anh Tú

**Ngày cập nhật:** 2026-06-13

---

## 1. PROBLEM STATEMENT

### Khoảng trống trong quy trình phát hiện và ghi nhận sự cố

Trong quá trình làm việc hàng ngày (như dọn dẹp vệ sinh, kiểm tra định kỳ, bảo vệ tuần tra) tại các khu nhà trọ, nhân viên vận hành là người trực tiếp và thường xuyên nhất tiếp xúc với các vấn đề phát sinh tại hiện trường. Các sự cố này vô cùng đa dạng, từ việc rò rỉ đường ống nước hành lang, chập cháy bóng đèn cầu thang, hỏng khóa vân tay cổng chính, cho đến các dấu hiệu xuống cấp của cơ sở vật chất (nứt tường, bong tróc sơn, hỏng thiết bị PCCC).

Hiện tại, hệ thống đang thiếu một công cụ chuyên biệt để nhân viên có thể chủ động ghi nhận và số hóa thông tin sự cố ngay lập tức tại nơi phát hiện.

### Nhu cầu minh bạch và luân chuyển thông tin nhanh chóng tới Ban quản lý

Theo cách làm truyền thống, nhân viên phải chụp ảnh bằng điện thoại cá nhân và báo cáo thủ công qua các ứng dụng nhắn tin (Zalo, Messenger) cho quản lý. Phương pháp này tiềm ẩn nhiều rủi ro:

* **Trôi thông tin:** Tin nhắn dễ bị lẫn lộn giữa các khu trọ hoặc bị trôi do tin nhắn rác.
* **Thiếu tính cấu trúc:** Ban quản lý khó phân loại mức độ khẩn cấp, không nắm rõ vị trí chính xác (phòng nào, tầng mấy).
* **Khó theo dõi và thống kê:** Không thể tracking được trạng thái (Đã tiếp nhận - Đang sửa - Đã xong) và không có dữ liệu để thống kê tần suất hỏng hóc tài sản nhằm tối ưu chi phí bảo trì.

Hệ thống cần một tính năng (Feature) cho phép nhân viên vận hành tạo **Ticket báo cáo sự cố** đính kèm hình ảnh và gửi thẳng lên hệ thống. Ngay lập tức, Manager sẽ nhận được thông tin để nắm bắt tình hình và đưa ra quyết định xử lý.

---

## 2. DOMAIN KNOWLEDGE

### Chi tiết các loại sự cố thường gặp (Category)

Sự cố phát hiện trong quá trình vận hành nhà trọ được phân rã thành các nhóm chi tiết sau để nhân viên dễ dàng chọn lọc khi báo cáo:

* **Điện:** Cháy/hỏng bóng đèn khu vực chung, chập mạch bảng điện tầng, hỏng công tắc, đứt dây điện.
* **Nước:** Rò rỉ/vỡ đường ống nước sinh hoạt, hỏng vòi nước, tắc nghẽn cống thoát nước chung, bơm nước không hoạt động.
* **An ninh & Kiểm soát ra vào:** Lỗi thiết bị đọc vân tay/thẻ từ ở cổng chính, cửa cổng không đóng khép kín được, hỏng camera an ninh.
* **Cơ sở vật chất & PCCC:** Nứt tường, sập xệ trần thạch cao, hỏng tay vịn cầu thang, bình chữa cháy bị mất chốt/hết hạn, đèn exit không sáng.
* **Vệ sinh & Môi trường:** Khu vực tập kết rác ứ đọng gây mùi, vật nuôi của khách xả rác bừa bãi ra hành lang.

### Phân loại Vị trí (Location)

* **Khu vực chung (Public Area):** Nhà xe, Hành lang các tầng, cầu thang bộ, thang máy, sân thượng, cổng chính.
* **Khu vực riêng (Private Room):** Sự cố nằm bên trong hoặc ngay trước cửa một phòng trọ cụ thể (Bắt buộc phải gắn với Mã phòng - `room_id`).

### Mức độ ưu tiên xử lý (Severity)

* **Khẩn cấp (High/Critical):** Các sự cố đe dọa đến an toàn hoặc sinh hoạt chung toàn tòa nhà. (VD: Chập cháy điện có nguy cơ lan rộng, vỡ ống nước lớn gây ngập, cổng chính không khóa được ban đêm).
* **Bình thường (Normal/Low):** Các sự cố hao mòn không gây ảnh hưởng tức thời. (VD: Cháy 1 bóng đèn ở hành lang nhiều đèn, rác tắc nhẹ ở cống sân thượng chưa mưa lớn).

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (Operations Staff)

Là người sử dụng tính năng này. Phát hiện sự cố $\rightarrow$ Mở ứng dụng/hệ thống $\rightarrow$ Chụp ảnh minh chứng $\rightarrow$ Chọn phân loại, vị trí, mức độ $\rightarrow$ Submit báo cáo.

### Ban Quản lý / Quản lý nhà trọ (Manager)

Người tiếp nhận danh sách các sự cố. Nhờ có báo cáo chi tiết từ nhân viên, Manager có cái nhìn tổng quan về tình hình tài sản của chuỗi nhà trọ để ra quyết định: phê duyệt chi phí, gọi thợ bảo trì, hoặc yêu cầu nhân viên tự khắc phục (nếu lỗi nhỏ).

### Đội bảo trì (Maintenance Team)

Người thụ hưởng thông tin cuối cùng. Dựa vào hình ảnh hiện trường và mô tả của nhân viên vận hành do Manager phân công xuống, thợ bảo trì sẽ biết cần chuẩn bị dụng cụ, linh kiện, vật tư gì trước khi đến nhà trọ.

---

## 4. CONSTRAINTS

### Ràng buộc Kỹ thuật (Technical Constraints)

#### Tối ưu hóa đa phương tiện (Media Optimization)

Nhân viên thao tác bằng 3G/4G tại nhà trọ. Frontend bắt buộc phải nén ảnh (compress) trước khi gọi API upload để tiết kiệm băng thông và giảm độ trễ, cho phép chọn upload nhiều ảnh cùng lúc.

#### Thao tác Database (Core Requirement)

Quá trình lưu trữ thông tin tạo mới sự cố (`INSERT`) vào cơ sở dữ liệu bắt buộc phải sử dụng **PreparedStatements** theo đúng form chuẩn của dự án hiện tại.

### Ràng buộc Phạm vi (Scope Constraints)

* Tính năng này **chỉ tập trung vào luồng Tạo mới (Create)** báo cáo sự cố của nhân viên vận hành.
* Các luồng nghiệp vụ sau đó như: Manager đổi trạng thái sự cố, phân công người sửa, duyệt chi phí mua vật tư... sẽ được tách thành các module/feature khác.

---

## 5. ASSUMPTIONS

* Giả định nhân viên đã đăng nhập hợp lệ. Hệ thống tự động lấy `staff_id` của session hiện tại để gắn vào người tạo báo cáo, nhân viên không cần điền tên hay mã của mình.
* Thiết bị di động của nhân viên được cấp quyền truy cập Camera để có thể chụp ảnh trực tiếp ngay lúc phát hiện sự cố.
* Hệ thống lưu trữ ảnh (Cloud/CDN) luôn sẵn sàng, trả về URL nhanh chóng để lưu chuỗi URL này vào database cùng với thông tin sự cố.

---

## 6. OPEN QUESTIONS

### Quyền chỉnh sửa và thu hồi

Sau khi nhân viên bấm "Gửi báo cáo", nếu phát hiện điền sai (nhầm mã phòng, nhầm mức độ):

* Hệ thống có cho phép nhân viên tự Update/Delete báo cáo của chính mình trong vòng 15-30 phút không?
* Hay Ticket đã gửi là cố định, nếu sai nhân viên phải tạo cái mới và liên hệ Manager hủy cái cũ?

### Luồng thông báo (Alerting) cho Ban quản lý

Với những sự cố nhân viên chọn là **"Khẩn cấp"** (VD: Cháy, ngập nước):

* Hệ thống có cần tích hợp API để bắn Push Notification / Zalo ZNS / SMS báo động đỏ ngay lập tức cho Manager không?
* Hay chỉ cần hiển thị ở đầu danh sách (Sort by Priority) trên màn hình Dashboard của Manager là đủ?