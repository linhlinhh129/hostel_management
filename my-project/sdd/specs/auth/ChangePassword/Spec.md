# Đặc tả Kỹ thuật (Specification) - Tính năng: Đổi mật khẩu (Khi đã đăng nhập)

**Trạng thái:** Bản nháp
**Người viết:** @Phạm Anh Tú
**Người duyệt:** Tech Lead
**Ngày cập nhật:** 2026-06-24
**Mức độ ưu tiên:** Cao

---

## 1. Bối cảnh Nghiệp vụ

Khác với luồng "Quên mật khẩu" từ màn hình ngoài, tính năng này phục vụ người dùng đang có phiên đăng nhập (Session) hợp lệ trên hệ thống. Việc cho phép người dùng chủ động đổi mật khẩu định kỳ giúp tăng cường bảo mật cho tài khoản cá nhân. Đồng thời, hệ thống cũng cần cung cấp một lối thoát (luồng phụ) gửi email khôi phục trong trường hợp người dùng đang đăng nhập nhưng lại không nhớ mật khẩu hiện tại để thực hiện đổi.

---

## 2. Câu chuyện Người dùng (User Stories)

* **Kịch bản chuẩn (Chủ động đổi mật khẩu):** Là một người dùng đang đăng nhập, tôi muốn đổi mật khẩu bằng cách nhập mật khẩu cũ và mật khẩu mới, để cập nhật bảo mật cho tài khoản của mình.
* **Trường hợp ngoại lệ 1 (Sai mật khẩu cũ):** Là một người dùng đang đăng nhập, nếu tôi nhập sai mật khẩu hiện tại, tôi muốn hệ thống báo lỗi rõ ràng trực tiếp trên giao diện để tôi có thể thử lại.
* **Trường hợp ngoại lệ 2 (Quên mật khẩu cũ khi đang đăng nhập):** Là một người dùng đang đăng nhập nhưng không nhớ mật khẩu cũ, tôi muốn có một nút "Quên mật khẩu hiện tại", khi nhấn vào hệ thống sẽ tự động gửi link xác thực về email của tôi (dựa trên thông tin phiên đăng nhập) mà không cần tôi phải tự nhập lại email.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)

* **KHI** người dùng gửi form "Đổi mật khẩu" kèm theo mật khẩu cũ chính xác và mật khẩu mới hợp lệ, **HỆ THỐNG PHẢI** cập nhật mật khẩu xuống cơ sở dữ liệu **VÀ** hiển thị thông báo thành công.
* **KHI** người dùng nhập sai mật khẩu cũ, **HỆ THỐNG PHẢI** từ chối cập nhật **VÀ** hiển thị lỗi "Mật khẩu hiện tại không chính xác" bằng thẻ `<c:if>` trên giao diện JSP.
* **KHI** người dùng nhập mật khẩu mới không đáp ứng đủ 7 quy tắc của Chính sách Mật khẩu, **HỆ THỐNG PHẢI** hiển thị cảnh báo yêu cầu nhập lại đúng định dạng.
* **KHI** người dùng chọn chức năng "Quên mật khẩu hiện tại", **HỆ THỐNG PHẢI** trích xuất email từ `HttpSession` của người dùng, tạo mã Token (hạn 15 phút) **VÀ** gửi link khôi phục qua email đó.

---

## 4. Giao tiếp Hệ thống (System Flow)

* **Đường dẫn (Endpoint):** `POST /user/change-password`
* **Loại dữ liệu (Content-Type):** `application/x-www-form-urlencoded`

### Dữ liệu gửi lên (Request Parameters)

* Hệ thống tự động lấy `userId` (hoặc `username`) từ `HttpSession`.
* `oldPassword`: chuỗi (bắt buộc).
* `newPassword`: chuỗi (bắt buộc).

### Phản hồi Hệ thống (System Response)

* **Thành công:**
* Cập nhật DB.
* Forward về trang `/WEB-INF/views/user/profile.jsp`.
* Đính kèm biến `successMessage` = "Đổi mật khẩu thành công. Vui lòng sử dụng mật khẩu mới cho lần đăng nhập sau."


* **Lỗi (Sai mật khẩu cũ):**
* Forward lại trang đổi mật khẩu.
* Đính kèm biến `errorMessage` = "Mật khẩu hiện tại không chính xác."


* **Lỗi (Mật khẩu mới vi phạm chính sách):**
* Forward lại trang đổi mật khẩu.
* Đính kèm biến `errorMessage` = "Mật khẩu mới chưa đáp ứng đủ điều kiện bảo mật."



---

## 5. Ràng buộc & Chính sách

### Ràng buộc Kỹ thuật (Technical Constraints)

* **Xác thực phiên (Session):** Controller/Servlet bắt buộc phải kiểm tra `HttpSession` trước khi thực thi logic. Nếu Session không tồn tại hoặc đã hết hạn, điều hướng ngay về trang Đăng nhập.
* **Bảo mật Cơ sở dữ liệu:** Các thao tác truy vấn (kiểm tra mật khẩu cũ, cập nhật mật khẩu mới) tuân thủ việc sử dụng **PreparedStatements** để đảm bảo an toàn và ngăn chặn rủi ro SQL Injection.
* **Mã hóa (Hashing):** Mật khẩu mới phải được băm bằng `BCrypt` trước khi tạo câu lệnh SQL `UPDATE`. Việc so sánh `oldPassword` cũng phải thực hiện bằng hàm `checkpw` với chuỗi hash lấy từ DB.

### Chính sách Mật khẩu (Business Constraints - Password Policy)

Mật khẩu mới (`newPassword`) phải khác biệt hoàn toàn với `oldPassword` và đáp ứng các tiêu chuẩn sau:

1. **Độ dài:** Tối thiểu 8 ký tự.
2. **Chữ hoa:** Có ít nhất 1 chữ cái viết hoa (A-Z).
3. **Chữ thường:** Có ít nhất 1 chữ cái viết thường (a-z).
4. **Chữ số:** Có ít nhất 1 chữ số (0-9).
5. **Ký tự đặc biệt:** Có ít nhất 1 ký tự đặc biệt (!, @, #, $, %, ^, &, *, v.v.).
6. **Khoảng trắng:** Không được chứa khoảng trắng.
7. **Bảo mật chéo:** Không được trùng với tên đăng nhập hoặc email.

---

## 6. Nằm ngoài phạm vi (Out of Scope)

* **Đăng xuất các phiên khác:** Hệ thống hiện tại sẽ không tự động hủy các phiên đăng nhập hợp lệ (Session) đang tồn tại trên các trình duyệt/thiết bị khác sau khi đổi mật khẩu thành công. Tính năng "Đăng xuất khỏi mọi thiết bị" sẽ được cân nhắc ở bản cập nhật sau.