# CONTEXT.md - User Profile Management (Quản lý Thông tin Cá nhân)

**Người viết:** Tú Anh

**Ngày:** 2026-06-13

---

## 1. PROBLEM STATEMENT

* Hệ thống quản lý nhà trọ có nhiều đối tượng người dùng khác nhau (Admin, Ban quản lý, Người vận hành, Người thuê) nhưng hiện tại chưa có một trang quản lý thông tin cá nhân tập trung.
* Mỗi nhóm người dùng có các đặc thù thông tin khác nhau, tuy nhiên tất cả đều cần một giao diện chung để tự cập nhật thông tin liên lạc, thay đổi ảnh đại diện và quản lý bảo mật tài khoản (đổi mật khẩu).
* Người thuê cần xem nhanh các thông tin gắn liền với phòng ở của họ (hợp đồng, trạng thái cư trú), trong khi nhân viên (Ban quản lý, Người vận hành) cần kiểm tra thông tin chức vụ và khu vực quản lý được phân công.
* Thiếu chức năng này sẽ khiến việc cập nhật dữ liệu liên lạc bị gián đoạn, gây khó khăn cho Ban quản lý khi cần liên hệ khẩn cấp hoặc xác minh danh tính người dùng trong hệ thống.

---

## 2. DOMAIN KNOWLEDGE

### Thuật ngữ (Terms)

* **User Profile:** Hồ sơ cá nhân chứa thông tin cơ bản và thông tin định danh của người dùng trên hệ thống.
* **Account Credentials:** Thông tin đăng nhập bao gồm tên tài khoản/email và mật khẩu đã mã hóa.
* **Role-based View:** Giao diện hiển thị được tùy biến dựa trên quyền hạn và vai trò cụ thể của từng tài khoản.
* **Tenant Profile:** Hồ sơ mở rộng dành riêng cho người thuê, liên kết với dữ liệu phòng ở và hợp đồng.
* **Staff Profile:** Hồ sơ mở rộng dành cho nhân viên (Ban quản lý, Người vận hành), liên kết với chức vụ và phạm vi công việc.

### Quy tắc nghiệp vụ (Business Rules)

* **Tính duy nhất:** Số điện thoại và Email trong hồ sơ không được trùng lặp với bất kỳ người dùng nào khác trong hệ thống.
* **Cập nhật có kiểm soát:** Người dùng có thể tự thay đổi các thông tin cơ bản (Họ tên, Số điện thoại, Ảnh đại diện, Ngày sinh, Giới tính). Tuy nhiên, vai trò (`Role`) và mã định danh (Mã nhân viên/Mã người thuê) chỉ có thể được chỉnh sửa bởi hệ thống hoặc Admin.
* **Quy tắc mật khẩu:** Mật khẩu mới khi thay đổi không được trùng với mật khẩu hiện tại và phải đáp ứng độ dài tối thiểu (ví dụ: từ 6 đến 8 ký tự trở lên).
* **Đồng bộ dữ liệu:** Khi người thuê thay đổi số điện thoại trong trang Profile, thông tin này phải tự động cập nhật trong danh sách liên hệ của phòng ở để Ban quản lý theo dõi.

---

## 3. STAKEHOLDERS & PERMISSIONS

### 1. Tất cả người dùng (All Users)

* **Quyền hạn chung:**
* Xem thông tin cá nhân của chính mình.
* Cập nhật thông tin liên hệ (Số điện thoại, Email, Địa chỉ thường trú).
* Thay đổi ảnh đại diện (Avatar).
* Thực hiện đổi mật khẩu tài khoản.



### 2. Administrator (Admin)

* **Thông tin hiển thị thêm:** Ngày tạo tài khoản, lịch sử đăng nhập hệ thống gần nhất.
* **Đặc quyền mở rộng (trong trang quản lý tổng):** Có quyền can thiệp để khóa/mở khóa tài khoản hoặc reset mật khẩu mặc định cho các user khác khi có yêu cầu hỗ trợ.

### 3. Building Manager (Ban quản lý)

* **Thông tin hiển thị thêm:** Chức vụ (Quản lý), Ngày bắt đầu làm việc, danh sách các cơ sở/tòa nhà trọ đang được phân quyền quản lý tổng thể.

### 4. Operator (Người vận hành)

* **Thông tin hiển thị thêm:** Chức vụ (Nhân viên vận hành), Ca làm việc trực (nếu có), số điện thoại hotline nội bộ, danh sách các đầu việc hoặc khu vực phòng trọ được giao phụ trách sửa chữa/vận hành.

### 5. Tenant (Người thuê)

* **Thông tin hiển thị thêm:** * Mã định danh người thuê.
* Thông tin phòng đang thuê hiện tại (Số phòng, Tên tòa nhà/Khu nhà trọ).
* Trạng thái hợp đồng (Ngày bắt đầu, Ngày hết hạn).
* Số lượng thành viên ở cùng phòng (được hiển thị dưới dạng danh sách rút gọn).



---

## 4. CONSTRAINTS

### Technical Constraints

* Backend sử dụng **Java Servlet/JSP**.
* Database sử dụng **PostgreSQL** (Bảng `Users`, `Profiles`, và các bảng liên kết `Rooms`, `Contracts`).
* Việc lưu trữ và xử lý tải lên tệp tin ảnh đại diện (`Avatar`) cần giới hạn dung lượng (ví dụ: tối đa 2MB, định dạng `.jpg`, `.png`).
* Toàn bộ biểu mẫu cập nhật thông tin phải được validation ở cả hai phía: Frontend (Javascript) và Backend (Java Java Validation / Regex).

### Security Constraints

* Mật khẩu phải được mã hóa một chiều (ví dụ: sử dụng Bcrypt hoặc SHA-256) trước khi lưu vào PostgreSQL. Không bao giờ lưu mật khẩu dưới dạng text thô.
* Khi thực hiện đổi mật khẩu, bắt buộc người dùng phải nhập đúng mật khẩu hiện tại (Current Password).
* Hệ thống không được hiển thị hoặc trả về các thông tin nhạy cảm của cấu hình hệ thống trong trang profile công khai của nhân viên.
* Chống lỗi phân quyền: Đảm bảo kiểm tra Session/Token để người dùng không thể thay đổi thông tin hồ sơ của người khác thông qua việc thay đổi tham số ID trên URL (`ID Spoofing`).

---

## 5. ASSUMPTIONS

* Mỗi tài khoản đăng nhập vào hệ thống bắt buộc phải gắn liền với một bản ghi hồ sơ cá nhân duy nhất.
* Người thuê đã được Ban quản lý xếp phòng và tạo hợp đồng trước đó thì hệ thống mới hiển thị được thông tin phòng ở trong Profile.
* Người dùng sử dụng các trình duyệt web hiện đại có hỗ trợ tải tệp tin lên cho chức năng đổi ảnh đại diện.

---

## 6. OPEN QUESTIONS

* Hệ thống có cần tích hợp chức năng xác thực OTP qua số điện thoại hoặc liên kết xác nhận qua Email khi người dùng thay đổi Email/Số điện thoại trong Profile không?
* Người thuê có được quyền tự cập nhật thông tin căn cước công dân (CCCD) và ảnh chụp CCCD trên trang cá nhân của họ hay việc này bắt buộc phải do Ban quản lý nhập khi làm hợp đồng?
* Có cần lưu trữ lại lịch sử thay đổi thông tin (Log thay đổi dữ liệu hồ sơ) để phục vụ đối soát khi xảy ra tranh chấp hoặc sự cố không?
* Khi người thuê kết thúc hợp đồng và rời đi, tài khoản và thông tin hồ sơ của họ sẽ bị xóa hoàn toàn hay chuyển sang trạng thái lưu trữ (`ARCHIVED`)?