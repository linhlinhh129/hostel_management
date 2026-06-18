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
* **Trường hợp ngoại lệ (Edge Case) 2:** Là một người dùng, sau khi đã đổi mật khẩu thành công và thực hiện đăng nhập lại ở những lần sau, nếu tôi nhập sai mật khẩu quá 5 lần liên tiếp, tôi muốn hệ thống tự động vô hiệu hóa quyền đăng nhập của tôi trong vòng 1 phút để bảo vệ tài khoản khỏi các cuộc tấn công dò mật khẩu.

---

## 3. Tiêu chí Chấp thuận (Theo chuẩn EARS)
* **KHI** người dùng gửi form đăng nhập với tên đăng nhập và mật khẩu hợp lệ, **HỆ THỐNG PHẢI** xác thực người dùng **VÀ** trả về kết quả thành công kèm theo trạng thái yêu cầu đổi mật khẩu (nếu có).
* **KHI** người dùng đăng nhập thành công lần đầu bằng mật khẩu tạm thời, **HỆ THỐNG PHẢI** chặn quyền truy cập vào các tài nguyên khác **VÀ** điều hướng người dùng thẳng đến trang *"Bắt buộc đổi mật khẩu"*.
* **KHI** người dùng nhập sai mật khẩu vượt quá 5 lần liên tiếp, **HỆ THỐNG PHẢI** vô hiệu hóa tính năng đăng nhập của tài khoản đó trong vòng 1 phút **VÀ** trả về mã HTTP 403 kèm thông báo khóa tạm thời.
* **KHI** người dùng gửi form đăng nhập với tên đăng nhập chưa được đăng ký, **HỆ THỐNG PHẢI** trả về mã lỗi HTTP 404 với mã nội bộ `USER_NOT_FOUND`.

---

## 4. Giao tiếp API (API Contract)

* **Đường dẫn (Endpoint):** `POST /api/v1/auth/login`

### Dữ liệu gửi lên (Request)

Kết quả chạy mã
File created successfully at: dac_ta_tinh_nang_dang_nhap.md

```json
{
  "username": "chuỗi (bắt buộc)",
  "password": "chuỗi (bắt buộc)"
}

Phản hồi 200 (Thành công - Đăng nhập bằng mật khẩu tạm, bắt đổi mật khẩu)
JSON
{ 
  "success": true, 
  "data": { 
    "userId": 123, 
    "accessToken": "chuỗi_jwt_token_tạm_thời", 
    "expiresIn": 600,
    "requirePasswordChange": true 
  } 
}

Phản hồi 200 (Thành công - Đăng nhập bình thường sau khi đã đổi mật khẩu)
JSON
{ 
  "success": true, 
  "data": { 
    "userId": 123, 
    "accessToken": "chuỗi_jwt_token_chính_thức", 
    "expiresIn": 3600,
    "requirePasswordChange": false 
  } 
}

Phản hồi 403 (Bị từ chối - Sai quá 5 lần, vô hiệu hóa 1 phút)
JSON
{ 
  "success": false, 
  "error": { 
    "code": "LOGIN_DISABLED_1MIN", 
    "message": "Tài khoản của bạn đã bị vô hiệu hóa đăng nhập trong 1 phút do nhập sai mật khẩu quá 5 lần." 
  } 
}

Phản hồi 401 (Không được phép - Sai mật khẩu dưới 5 lần)
JSON
{ 
  "success": false, 
  "error": { 
    "code": "INVALID_CREDENTIALS", 
    "message": "Sai tên đăng nhập hoặc mật khẩu." 
  } 
}

5. Ràng buộc Kỹ thuật
Thời gian phản hồi tối đa: 500ms (p95).
Cơ chế khóa đăng nhập: Hệ thống sẽ đếm số lần đăng nhập sai theo từng username. Khi đạt ngưỡng kích hoạt (5 lần), hệ thống lưu mốc thời gian khóa (Lockout Timestamp) kéo dài đúng 60 giây.
Bảo mật: Token cấp cho trường hợp requirePasswordChange: true là token giới hạn quyền, chỉ có hiệu lực gọi duy nhất API cập nhật mật khẩu mới, không thể dùng để gọi các API nghiệp vụ khác.
6. Nằm ngoài phạm vi (Out of Scope)
Tính năng "Quên mật khẩu" (Forgot Password) dành cho người dùng tự chủ động lấy lại tài khoản sẽ được viết trong một tài liệu đặc tả riêng.
Đăng nhập bằng mạng xã hội (Google) sẽ thuộc chu kỳ phát triển (Sprint) tiếp theo. """

