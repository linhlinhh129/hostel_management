# Đặc tả Tính năng: Đăng nhập

## Thông tin chung

* **Trạng thái:** Bản nháp
* **Tác giả:** Phạm Anh Tú
* **Người duyệt:** Tech Lead
* **Ngày cập nhật:** 2026-06-11
* **Độ ưu tiên:** Cao

---

## 1. Bối cảnh Nghiệp vụ

Chức năng đăng nhập là chốt chặn bảo mật đầu tiên của hệ thống, giúp xác thực danh tính người dùng trước khi cấp quyền truy cập vào các tài nguyên nội bộ. Hệ thống cần giải quyết hai rủi ro bảo mật chính:

1. **Nguy cơ từ tài khoản cấp sẵn:** Người dùng sử dụng mật khẩu tạm thời dễ bị lộ. Bắt buộc phải có cơ chế đổi mật khẩu ngay lần đầu truy cập.
2. **Nguy cơ tấn công dò mật khẩu (Brute-force):** Kẻ tấn công thử mật khẩu tự động. Cần cơ chế giới hạn số lần nhập sai và vô hiệu hóa tài khoản.

---

## 2. Câu chuyện Người dùng (User Stories)

* **Kịch bản chuẩn (Happy Path):** Là một người dùng đã được cấp tài khoản và đã đổi mật khẩu, tôi muốn đăng nhập bằng tên đăng nhập và mật khẩu của mình để truy cập vào trang tổng quan cá nhân và các tính năng của hệ thống.
* **Trường hợp ngoại lệ (Edge Case) 1 - Bắt buộc đổi mật khẩu:** Là một người dùng mới, khi tôi đăng nhập thành công bằng mật khẩu tạm thời, tôi muốn hệ thống điều hướng tôi đến trang đổi mật khẩu và bắt buộc tôi phải tạo mật khẩu mới đạt chuẩn bảo mật trước khi sử dụng các chức năng khác.
* **Trường hợp ngoại lệ (Edge Case) 2 - Khóa tài khoản:** Là một người dùng, nếu tôi nhập sai mật khẩu quá 5 lần liên tiếp, tôi muốn hệ thống tự động vô hiệu hóa tài khoản của tôi hoàn toàn dưới cơ sở dữ liệu để bảo vệ tài khoản, đồng thời yêu cầu tôi liên hệ Admin để mở khóa.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)

* **KHI** người dùng gửi form đăng nhập hợp lệ và tài khoản có cờ `force_change_pass = 0`, **HỆ THỐNG PHẢI** xác thực thành công, khởi tạo Session **VÀ** điều hướng vào trang chủ tương ứng với quyền hạn.
* **KHI** người dùng đăng nhập thành công lần đầu bằng mật khẩu tạm thời (`force_change_pass = 1`), **HỆ THỐNG PHẢI** khởi tạo Session **VÀ** ngay lập tức điều hướng (Redirect) thẳng đến trang *"Bắt buộc đổi mật khẩu"*.
* **KHI** người dùng cập nhật mật khẩu mới, **HỆ THỐNG PHẢI** kiểm tra và từ chối nếu mật khẩu không thỏa mãn toàn bộ 7 quy tắc của Chính sách Mật khẩu.
* **KHI** người dùng gửi form đăng nhập với thông tin sai, **HỆ THỐNG PHẢI** trả về thông báo lỗi "Sai tên đăng nhập hoặc mật khẩu" trực tiếp trên giao diện JSP bằng thẻ `<c:if>`.
* **KHI** người dùng nhập sai mật khẩu vượt quá 5 lần liên tiếp, **HỆ THỐNG PHẢI** cập nhật trạng thái tài khoản thành `LOCKED` dưới Database **VÀ** hiển thị thông báo lỗi yêu cầu liên hệ Quản trị viên.

---

## 4. Giao tiếp Hệ thống (System Flow)

* **Đường dẫn (Endpoint):** `POST /login`
* **Loại dữ liệu (Content-Type):** `application/x-www-form-urlencoded`

### Dữ liệu gửi lên (Request Parameters)

* `username`: chuỗi (bắt buộc)
* `password`: chuỗi (bắt buộc)

### Phản hồi Hệ thống (System Response)

* **Thành công (Đăng nhập lần đầu):**
* Server thiết lập `HttpSession` với đối tượng `UserSessionDTO` (chứa cờ `firstLogin = true`).
* Redirect (HTTP 302) đến `/auth/force-change-password`.


* **Thành công (Đăng nhập bình thường):**
* Server thiết lập `HttpSession` với `UserSessionDTO` (chứa cờ `firstLogin = false`).
* Redirect (HTTP 302) đến Dashboard tương ứng (VD: `/operator/dashboard`).


* **Lỗi (Không được phép - Sai thông tin < 5 lần):**
* Forward về `/WEB-INF/views/auth/login.jsp`.
* Đính kèm attribute: `errorMessage` = "Sai tên đăng nhập hoặc mật khẩu."


* **Lỗi (Bị khóa tài khoản do sai >= 5 lần):**
* Forward về `/WEB-INF/views/auth/login.jsp`.
* Đính kèm attribute: `errorMessage` = "Tài khoản của bạn đã bị khóa do nhập sai mật khẩu quá 5 lần. Vui lòng liên hệ Admin."



---

## 5. Ràng buộc & Chính sách

### Ràng buộc Kỹ thuật (Technical Constraints)

* **Công nghệ:** Sử dụng Servlet/JSP Form-based authentication. Không sử dụng REST API hay JWT.
* **Xử lý hiển thị:** Bắt buộc dùng thẻ `<c:if>` trong file `.jsp` để render các thông báo lỗi trả về từ Controller/Servlet.
* **Cơ chế đếm lỗi:** Số lần đăng nhập sai được theo dõi theo từng `username` trên bộ nhớ tạm (RAM).
* **Khóa tài khoản:** Khi sai 5 lần, thực thi câu lệnh SQL cơ bản để cập nhật (ví dụ: `UPDATE Users SET status = 'LOCKED' WHERE username = '...'`) thay vì dùng PreparedStatements. Tài khoản bị vô hiệu hóa cho đến khi Admin mở khóa.
* **Bảo vệ luồng:** Sử dụng Servlet Filter để kiểm tra Session. Nếu `firstLogin == true` (hoặc `force_change_pass = 1`), mọi request truy cập tài nguyên khác đều bị Redirect về trang đổi mật khẩu.

### Chính sách Mật khẩu (Business Constraints)

Khi người dùng đổi mật khẩu bắt buộc, mật khẩu mới phải đáp ứng các tiêu chí sau:

1. **Độ dài:** Tối thiểu 8 ký tự.
2. **Chữ hoa:** Có ít nhất 1 chữ cái viết hoa (A-Z).
3. **Chữ thường:** Có ít nhất 1 chữ cái viết thường (a-z).
4. **Chữ số:** Có ít nhất 1 chữ số (0-9).
5. **Ký tự đặc biệt:** Có ít nhất 1 ký tự đặc biệt (!, @, #, $, %, ^, &, *, v.v.).
6. **Khoảng trắng:** Không được chứa khoảng trắng.
7. **Bảo mật chéo:** Không được trùng với tên đăng nhập hoặc email.

---

## 6. Giả định & Câu hỏi mở

* **Giả định:** Database đã có sẵn cột `force_change_pass` (giá trị 1 cho tài khoản cấp sẵn) và cột `status` để quản lý trạng thái khóa.
* **Câu hỏi mở cần Tech Lead xác nhận:**
* Có cần thiết kế một bảng riêng dưới DB để lưu Log bảo mật (Audit Log) lưu IP và thời gian khi tài khoản bị chuyển sang `LOCKED` không?
* Hệ thống có cần tự động gửi Notification/Email cho Admin khi xảy ra sự kiện khóa tài khoản không?



---

## 7. Nằm ngoài phạm vi (Out of Scope)

* Tính năng "Quên mật khẩu" (Forgot Password).
* Tính năng "Đăng nhập bằng bên thứ ba" (Google/Facebook).
* Tính năng "Admin mở khóa tài khoản" (thuộc module Quản lý người dùng).