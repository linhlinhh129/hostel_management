
## 2. personnelManagement — Quản lý Nhân sự

### 2.1 Phân quyền

* `BR-PM-01` Chỉ ADMIN được truy cập và thao tác chức năng Quản lý Nhân sự.
* `BR-PM-02` Chức năng này chỉ quản lý nhân sự có vai trò `MANAGER` và `OPERATOR`.

### 2.2 Vai trò

* `BR-PM-03` Hệ thống chỉ hỗ trợ 3 vai trò cố định: `ADMIN`, `MANAGER`, `OPERATOR`.
* `BR-PM-04` Không được tạo tài khoản `ADMIN` từ chức năng Quản lý Nhân sự.
* `BR-PM-05` Không được thay đổi vai trò của bất kỳ nhân sự nào thành `ADMIN`.
* `BR-PM-06` Chỉ tồn tại duy nhất 01 tài khoản `ADMIN`.

### 2.3 Gán cơ sở quản lý

* `BR-PM-07` Nhân sự có vai trò `MANAGER` hoặc `OPERATOR` bắt buộc phải được gán ít nhất một cơ sở.
* `BR-PM-08` Không gán cơ sở → lỗi `FACILITY_REQUIRED`.
* `BR-PM-09` Chỉ được gán cơ sở có trạng thái `ACTIVE`.
* `BR-PM-10` Cơ sở không tồn tại → lỗi `FACILITY_NOT_FOUND`.
* `BR-PM-11` Cơ sở không ở trạng thái `ACTIVE` → lỗi `FACILITY_NOT_ACTIVE`.
* `BR-PM-12` Một nhân sự có thể được gán một cơ sở.
* `BR-PM-13` Dữ liệu MANAGER và OPERATOR được giới hạn theo cơ sở được phân công.

### 2.4 Mã nhân sự

* `BR-PM-14` Mã nhân sự được hệ thống tự động sinh.
* `BR-PM-15` Admin không được nhập hoặc chỉnh sửa mã nhân sự.

### 2.5 Tài khoản đăng nhập

* `BR-PM-16` Khi tạo nhân sự thành công, hệ thống tự động tạo tài khoản đăng nhập.
* `BR-PM-17` Username đăng nhập được tạo bằng email của nhân sự.
* `BR-PM-18` Username không được phép chỉnh sửa sau khi tạo.

### 2.6 Thông tin liên lạc

* `BR-PM-19` Email là bắt buộc.

* `BR-PM-20` Email phải có định dạng Gmail hợp lệ và kết thúc bằng `@gmail.com`.

* `BR-PM-21` Email không hợp lệ → lỗi `INVALID_EMAIL_FORMAT`.

* `BR-PM-22` Email phải là duy nhất → lỗi `EMAIL_ALREADY_EXISTS`.

* `BR-PM-23` Số điện thoại là bắt buộc.

* `BR-PM-24` Số điện thoại chỉ được chứa các chữ số từ 0–9.

* `BR-PM-25` Số điện thoại phải có đúng 10 chữ số.

* `BR-PM-26` Số điện thoại không hợp lệ → lỗi `INVALID_PHONE_FORMAT`.

* `BR-PM-27` Số điện thoại phải là duy nhất → lỗi `PHONE_ALREADY_EXISTS`.

### 2.7 Mật khẩu và Email

* `BR-PM-28` Hệ thống tự động sinh mật khẩu tạm thời ngẫu nhiên khi tạo nhân sự.
* `BR-PM-29` Mật khẩu tạm thời phải có độ dài từ 10 đến 12 ký tự.
* `BR-PM-30` Mật khẩu phải được mã hóa bằng BCrypt trước khi lưu.
* `BR-PM-31` Không được lưu mật khẩu dưới dạng plain text.
* `BR-PM-32` Hệ thống phải gửi email chứa username và mật khẩu tạm thời tới Gmail của nhân sự.
* `BR-PM-33` Nếu gửi email thất bại → lỗi `EMAIL_SEND_FAILED`.

### 2.8 Đăng nhập lần đầu

* `BR-PM-34` Tài khoản mới tạo có trạng thái `first_login = true`.
* `BR-PM-35` Người dùng bắt buộc phải đổi mật khẩu ở lần đăng nhập đầu tiên.
* `BR-PM-36` Khi chưa đổi mật khẩu lần đầu, không được sử dụng chức năng nghiệp vụ.
* `BR-PM-37` Sau khi đổi mật khẩu thành công, hệ thống cập nhật `first_login = false`.

### 2.9 Trạng thái tài khoản

* `BR-PM-38` Nhân sự mới tạo có trạng thái mặc định là `ACTIVE`.
* `BR-PM-39` Tài khoản ở trạng thái `INACTIVE` không được phép đăng nhập.
* `BR-PM-40` Admin không được khóa tài khoản của chính mình → lỗi `CANNOT_DEACTIVATE_SELF`.

### 2.10 Toàn vẹn dữ liệu

* `BR-PM-41` Không được xóa cứng nhân sự khỏi cơ sở dữ liệu.
* `BR-PM-42` Mọi thao tác tạo, cập nhật, khóa/mở khóa và gán cơ sở được ghi nhận qua system log file của máy chủ ứng dụng.
* `BR-PM-43` Tạo nhân sự, tạo tài khoản và gửi email phải nằm trong cùng một transaction nghiệp vụ.
* `BR-PM-44` Nếu gửi email thất bại thì không được hoàn tất việc tạo nhân sự.

---