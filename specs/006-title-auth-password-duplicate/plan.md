# Kế hoạch triển khai: Chống trùng lặp Mật khẩu

**Feature Branch**: `[006-title-auth-password-duplicate]`
**Created**: 2026-06-30

## Bối cảnh & Phương pháp tiếp cận
Người dùng yêu cầu không được phép đổi mật khẩu mới giống hệt mật khẩu hiện tại. Do không yêu cầu lưu trữ lịch sử mật khẩu hay giới hạn thời gian đổi, chúng ta chỉ cần so sánh trực tiếp chuỗi mật khẩu mới với mật khẩu cũ (plain-text) tại Controller trước khi tiến hành mã hóa và lưu xuống CSDL. 

Nếu phát hiện trùng, hệ thống sẽ từ chối và trả về mã lỗi `password_duplicate`, sau đó hiển thị cảnh báo trên giao diện (JSP). Việc này cần được áp dụng ở cả hai nơi: **Profile (Đổi mật khẩu)** và **First Login (Bắt buộc đổi mật khẩu lần đầu)**.

## Các thay đổi dự kiến

### 1. Backend (Java)

#### [MODIFY] [ProfileServlet.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/controller/auth/ProfileServlet.java)
- Tại phương thức `doPost`, trong nhánh `"change_password".equals(action)`.
- Thêm logic kiểm tra: `if (newPassword.equals(currentPassword))` 
- Nếu trùng: `response.sendRedirect(request.getContextPath() + "/profile?error=password_duplicate");`
- Lưu ý: Logic này được đặt TRƯỚC bước `PasswordUtil.verify(...)` và hash password.

#### [MODIFY] [FirstLoginServlet.java](file:///f:/SU26/New%20folder/hostel_management/src/main/java/com/quanlyphongtro/controller/auth/FirstLoginServlet.java)
- Tại phương thức `doPost`.
- Tương tự, lấy `currentPassword` từ CSDL và so sánh với `newPassword`. Tuy nhiên, vì form first-login không yêu cầu nhập `currentPassword` (chỉ có nhập password mới và xác nhận), chúng ta cần kiểm tra `PasswordUtil.verify(newPassword, user.getPasswordHash())`. Nếu trả về `true` tức là mật khẩu mới giống hệt mật khẩu hiện tại trong Database.
- Nếu trùng: `response.sendRedirect(request.getContextPath() + "/first-login?error=password_duplicate");`

### 2. Frontend (JSP)

#### [MODIFY] [common/profile.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/common/profile.jsp)
- Tại đoạn bắt tham số `param.error`, bổ sung trường hợp:
  ```jsp
  <c:when test="${param.error == 'password_duplicate'}">
      <div class="alert alert-danger alert-dismissible fade show">
          <i class="bi bi-exclamation-triangle-fill me-2"></i> Mật khẩu mới không được trùng với mật khẩu cũ.
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
  </c:when>
  ```

#### [MODIFY] [auth/first-login.jsp](file:///f:/SU26/New%20folder/hostel_management/src/main/webapp/WEB-INF/views/auth/first-login.jsp)
- Tương tự, bổ sung trường hợp hiển thị lỗi cho `password_duplicate` (với nội dung lỗi: "Mật khẩu mới không được trùng với mật khẩu cũ").

## Yêu cầu người dùng xem xét

> [!TIP]
> Việc dùng `PasswordUtil.verify(newPassword, hash)` ở bước Đăng nhập lần đầu giúp kiểm tra chống trùng lặp mà không cần bắt người dùng nhập lại mật khẩu hiện tại (tránh phiền hà thêm ô input). Ở trang Profile thì vì đã có sẵn ô "Mật khẩu hiện tại" nên ta có thể check `newPassword.equals(currentPassword)` luôn. Vui lòng xác nhận luồng này là chuẩn.

## Kế hoạch kiểm thử

### Kiểm thử thủ công
1. Đăng nhập và truy cập trang Profile. 
2. Chuyển sang tab Đổi mật khẩu.
3. Nhập mật khẩu hiện tại (VD: `123456`) vào cả ô "Mật khẩu hiện tại" và "Mật khẩu mới".
4. Ấn Đổi mật khẩu. Xác minh xuất hiện thông báo: "Mật khẩu mới không được trùng với mật khẩu cũ".
5. Lặp lại với một tài khoản vừa tạo (đăng nhập lần đầu). Tại màn hình First Login, đặt mật khẩu mới giống y chang mật khẩu được cấp. Phải bị chặn lại với thông báo tương tự.
