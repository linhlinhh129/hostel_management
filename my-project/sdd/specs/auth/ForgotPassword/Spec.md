# Đặc tả Tính năng: Quên mật khẩu (Khôi phục qua Email)

## Thông tin chung

* **Trạng thái:** Bản nháp
* **Tác giả:** Phạm Anh Tú
* **Người duyệt:** Tech Lead
* **Ngày:** 2026-06-13
* **Độ ưu tiên:** Cao

---

## 1. Bối cảnh Nghiệp vụ

Hệ thống quản lý nhà trọ phục vụ đa dạng đối tượng người dùng. Để nâng cao mức độ bảo mật chéo và xác minh danh tính, quy trình khôi phục tài khoản sẽ sử dụng cơ chế xác thực qua Email đã đăng ký:
- Người dùng nhập Email vào form Quên mật khẩu.
- Hệ thống gửi một **Đường dẫn (Link) đặt lại mật khẩu** về email đó.
Quy trình này đảm bảo tính ẩn danh (không rò rỉ việc email có tồn tại trong hệ thống hay không) và bắt buộc thu hồi toàn bộ các phiên đăng nhập (sessions) ngay sau khi tạo mật khẩu mới.

---

## 2. Câu chuyện Người dùng (User Stories)

* **Kịch bản chuẩn (Happy Path):** Là một người dùng, khi quên mật khẩu, tôi muốn nhập email của mình. Hệ thống sẽ tự động gửi một đường dẫn (Link) vào hòm thư email liên kết với tài khoản của tôi. Nhấp vào đường dẫn đó, tôi có thể tạo mật khẩu mới.
* **Trường hợp ngoại lệ (Edge Case) 1 - Chống dò quét:** Là một người dùng, nếu tôi nhập một email chưa từng được đăng ký, tôi muốn hệ thống vẫn hiển thị thông báo "Link khôi phục đã được gửi" để ngăn chặn kẻ xấu lợi dụng tính năng này dò quét cơ sở dữ liệu.
* **Trường hợp ngoại lệ (Edge Case) 2 - Hết hạn Link:** Là một người dùng, nếu tôi nhấp vào đường dẫn đặt lại mật khẩu trong email khi đã quá thời gian hiệu lực (15 phút), tôi muốn hệ thống thông báo link đã hết hạn.
* **Trường hợp ngoại lệ (Edge Case) 3 - Thu hồi phiên (Session Revocation):** Là một người dùng, sau khi tôi đổi mật khẩu thành công, tôi muốn tất cả các thiết bị đang đăng nhập tài khoản của tôi lập tức bị đăng xuất để bảo đảm an toàn tuyệt đối.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)

* **KHI** người dùng gửi form yêu cầu khôi phục mật khẩu, **HỆ THỐNG PHẢI** luôn hiển thị thông báo chung chung "Nếu email tồn tại, link khôi phục đã được gửi" trên giao diện **VÀ** chỉ thực sự tạo token và gửi Email nếu tài khoản hợp lệ.
* **KHI** người dùng nhấp vào link khôi phục hợp lệ trong email, **HỆ THỐNG PHẢI** hiển thị form cho phép nhập mật khẩu mới.
* **KHI** người dùng gửi yêu cầu đặt lại mật khẩu mới với token hết hạn hoặc sai, **HỆ THỐNG PHẢI** từ chối và báo lỗi ngay trên giao diện JSP.
* **KHI** người dùng đặt lại mật khẩu mới thành công, **HỆ THỐNG PHẢI** cập nhật mật khẩu mới vào cơ sở dữ liệu **VÀ** vô hiệu hóa token đó **VÀ** thu hồi toàn bộ các phiên đăng nhập (session) hiện tại của tài khoản bằng `SessionRegistry`.

---

## 4. Giao tiếp Hệ thống (System Flow)

Luồng chức năng này yêu cầu 3 hành động HTTP (Servlet GET/POST).

### 4.1. Màn hình Nhập Email

* **Đường dẫn (Endpoint):** `GET /forgot-password`
* **Mục đích:** Hiển thị form nhập email cho người dùng.

### 4.2. Xử lý Gửi Link Khôi phục

* **Đường dẫn (Endpoint):** `POST /forgot-password`
* **Dữ liệu:** `email`
* **Phản hồi:** Forward lại trang JSP kèm thông báo thành công chung chung (dù email có tồn tại hay không). Ngầm sinh UUID Token (15 phút TTL) và gửi vào Email.

### 4.3. Màn hình Đặt Mật khẩu Mới

* **Đường dẫn (Endpoint):** `GET /reset-password?token=...`
* **Mục đích:** Kiểm tra token. Nếu hợp lệ, hiển thị form nhập `newPassword` và `confirmPassword`. Nếu không hợp lệ, hiện thông báo lỗi.

### 4.4. Xử lý Cập nhật Mật khẩu

* **Đường dẫn (Endpoint):** `POST /reset-password`
* **Dữ liệu:** `token`, `newPassword`, `confirmPassword`
* **Phản hồi:** 
  - Thành công: Redirect về `/login?success=reset`
  - Lỗi (token hết hạn / pass không khớp): Trả về lỗi trực tiếp trên giao diện JSP hiện tại.

---

## 5. Ràng buộc Kỹ thuật

* **Vòng đời Token:** Chuỗi UUID ngẫu nhiên, quản lý trong bộ nhớ (ví dụ: `ConcurrentHashMap`) với thời gian sống tối đa 15 phút.
* **Cơ chế Thu hồi Phiên (Session Revocation):** Sử dụng `HttpSessionListener` và một cấu trúc dữ liệu toàn cục (`SessionRegistry`) để tìm và vô hiệu hóa (`invalidate()`) tất cả các Session đang gán với `userId` vừa đổi mật khẩu.
* **Chống User Enumeration:** Cố tình luôn báo "Đã gửi" đối với bất kỳ email nào được nhập vào.

---

## 6. Nằm ngoài phạm vi (Out of Scope)

* Tính năng tự đổi mật khẩu (Change Password) khi người dùng vẫn đang duy trì phiên đăng nhập hợp lệ (Nằm trong Profile).