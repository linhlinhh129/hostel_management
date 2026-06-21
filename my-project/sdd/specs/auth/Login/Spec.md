# Đặc tả Tính năng: Đăng nhập

## Thông tin chung
* **Trạng thái:** Bản nháp
* **Tác giả:** Phạm Anh Tú
* **Người duyệt:** Tech Lead
* **Ngày:** 2026-06-10
* **Độ ưu tiên:** Cao

---

## 1. Bối cảnh Nghiệp vụ
Chức năng đăng nhập là chốt chặn bảo mật đầu tiên của hệ thống, giúp xác thực danh tính người dùng trước khi cấp quyền truy cập vào các tài nguyên nội bộ. Đối với các tài khoản được cấp sẵn ban đầu, hệ thống cần cơ chế bắt buộc đổi mật khẩu ngay lần đầu đăng nhập thành công để đảm bảo an toàn thông tin cá nhân.

---

## 2. Câu chuyện Người dùng (User Stories)
* **Kịch bản chuẩn (Happy Path):** Là một người dùng đã được cấp tài khoản, tôi muốn đăng nhập bằng tên đăng nhập và mật khẩu của mình để có thể truy cập vào trang tổng quan cá nhân và các tính năng của hệ thống.
* **Trường hợp ngoại lệ (Edge Case) 1:** Là một người dùng đã được cấp tài khoản, khi tôi đăng nhập thành công bằng mật khẩu tạm thời (mật khẩu được cấp ban đầu), tôi muốn hệ thống bắt buộc tôi phải đổi mật khẩu ngay lập tức trước khi có thể sử dụng các chức năng khác.
* **Trường hợp ngoại lệ (Edge Case) 2:** Là một người dùng, sau khi đã đổi mật khẩu thành công và vào thẳng vào trang web luôn, nếu tôi nhập sai mật khẩu quá 5 lần liên tiếp, tôi muốn hệ thống tự động vô hiệu hóa tài khoản của tôi hoàn toàn dưới Database để bảo vệ tài khoản khỏi các cuộc tấn công dò mật khẩu, và yêu cầu tôi liên hệ Admin để mở khóa.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)
* **KHI** người dùng gửi form đăng nhập với tên đăng nhập và mật khẩu hợp lệ, **HỆ THỐNG PHẢI** xác thực người dùng **VÀ** khởi tạo phiên làm việc (Session) thành công kèm theo trạng thái yêu cầu đổi mật khẩu (nếu có).
* **KHI** người dùng đăng nhập thành công lần đầu bằng mật khẩu tạm thời, **HỆ THỐNG PHẢI** chặn quyền truy cập vào các tài nguyên khác **VÀ** điều hướng người dùng (Redirect) thẳng đến trang *"Bắt buộc đổi mật khẩu"*.
* **KHI** người dùng nhập sai mật khẩu vượt quá 5 lần liên tiếp, **HỆ THỐNG PHẢI** cập nhật trạng thái tài khoản thành `LOCKED` dưới Database **VÀ** hiển thị thông báo lỗi trên màn hình yêu cầu liên hệ Quản trị viên.
* **KHI** người dùng gửi form đăng nhập với tên đăng nhập chưa được đăng ký hoặc sai mật khẩu, **HỆ THỐNG PHẢI** trả về thông báo lỗi "Sai tên đăng nhập hoặc mật khẩu" trực tiếp trên giao diện JSP.

---

## 4. Giao tiếp Hệ thống (System Flow)

* **Đường dẫn (Endpoint):** `POST /login`
* **Loại dữ liệu (Content-Type):** `application/x-www-form-urlencoded`

### Dữ liệu gửi lên (Request Parameters)
* `username`: chuỗi (bắt buộc)
* `password`: chuỗi (bắt buộc)

### Phản hồi Hệ thống (System Response)

* **Thành công (Đăng nhập lần đầu, bắt đổi mật khẩu):**
  - Hệ thống khởi tạo Session với `currentUser`.
  - Redirect (Mã HTTP 302) đến trang `/auth/force-change-password`.
* **Thành công (Đăng nhập bình thường sau khi đã đổi mật khẩu):**
  - Hệ thống khởi tạo Session với `currentUser`.
  - Redirect (Mã HTTP 302) đến trang Dashboard tương ứng với Role của user (ví dụ: `/operator/dashboard`).
* **Lỗi (Bị từ chối - Sai quá 5 lần, khóa tài khoản):**
  - Forward lại về trang `/WEB-INF/views/auth/login.jsp`.
  - Đính kèm biến `errorMessage` = "Tài khoản của bạn đã bị khóa do nhập sai mật khẩu quá 5 lần. Vui lòng liên hệ Admin."
* **Lỗi (Không được phép - Sai thông tin dưới 5 lần):**
  - Forward lại về trang `/WEB-INF/views/auth/login.jsp`.
  - Đính kèm biến `errorMessage` = "Sai tên đăng nhập hoặc mật khẩu."

## 5. Ràng buộc Kỹ thuật
* Cơ chế khóa đăng nhập: Hệ thống sẽ đếm số lần đăng nhập sai theo từng username trên RAM bằng `LoginAttemptTracker`. Khi đạt ngưỡng kích hoạt (5 lần), hệ thống gọi lệnh `updateStatus(userId, LOCKED)` xuống Database.
* Bảo mật phiên: Cờ `firstLogin` sẽ được đính kèm trong object `UserSessionDTO`. Nếu `firstLogin == true`, các Controller khác hoặc Servlet Filter bắt buộc phải chặn (Redirect) về trang Đổi mật khẩu.

## 6. Nằm ngoài phạm vi (Out of Scope)
* Tính năng "Quên mật khẩu" (Forgot Password) dành cho người dùng tự chủ động lấy lại tài khoản sẽ được viết trong một tài liệu đặc tả riêng.
* Đăng nhập bằng mạng xã hội (Google) sẽ thuộc chu kỳ phát triển (Sprint) tiếp theo.

