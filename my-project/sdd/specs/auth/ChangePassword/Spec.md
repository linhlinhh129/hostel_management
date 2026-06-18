# Đặc tả Kỹ thuật (Specification) - Tính năng: Quên mật khẩu (Chưa đăng nhập)

**Trạng thái:** Draft  
**Người viết:** Phạm Anh Tú | **Ngày:** 2026-06-10  
**Mức độ ưu tiên:** Cao  

---

## 1. Bối cảnh Nghiệp vụ
Tính năng này cho phép người dùng khôi phục quyền truy cập vào tài khoản khi họ quên mật khẩu đăng nhập. Việc này giúp giảm thiểu lượng yêu cầu hỗ trợ (support tickets) đòi hỏi nhân viên phải cấp lại mật khẩu thủ công, đồng thời giữ chân người dùng tiếp tục sử dụng hệ thống một cách liền mạch.

## 2. Câu chuyện Người dùng (User Stories)
* **Story 1 (Yêu cầu gửi link khôi phục - Luồng chuẩn):** Là một người dùng đã được cấp tài khoản, tôi muốn yêu cầu gửi link khôi phục mật khẩu qua email đã đăng ký để tôi có thể bắt đầu quá trình lấy lại tài khoản.
* **Story 2 (Thực hiện đổi mật khẩu - Luồng chuẩn):** Là một người dùng đã được cấp tài khoản, khi tôi nhấp vào link khôi phục hợp lệ trong email, tôi muốn thiết lập mật khẩu mới để có thể đăng nhập và sử dụng hệ thống trở lại.
* **Story 3 (Token hết hạn/Không hợp lệ - Trường hợp ngoại lệ):** Là một người dùng đã được cấp tài khoản, khi tôi sử dụng link khôi phục đã hết hạn hoặc bị lỗi, tôi muốn thấy thông báo lỗi rõ ràng và được hướng dẫn yêu cầu gửi lại link mới.

## 3. Tiêu chí Chấp nhận (Theo chuẩn EARS)
* **KHI** người dùng gửi form "Quên mật khẩu" với một email đã tồn tại trong hệ thống  
  **HỆ THỐNG SẼ** tạo ra một mã xác thực (token) duy nhất có giới hạn thời gian VÀ gửi email khôi phục kèm link chứa token đó.
* **KHI** người dùng gửi form với một email chưa từng đăng ký  
  **HỆ THỐNG SẼ** trả về thông báo thành công chung chung (để kẻ xấu không thể lợi dụng dò quét xem email nào đã đăng ký) NHƯNG sẽ không gửi bất kỳ email nào ra ngoài.
* **KHI** người dùng gửi form "Đặt lại mật khẩu" với token hợp lệ và mật khẩu mới  
  **HỆ THỐNG SẼ** cập nhật mật khẩu một cách an toàn, vô hiệu hóa token vừa sử dụng để không thể dùng lại, VÀ trả về thông báo thành công.
* **TRONG KHI** token đã quá hạn (ví dụ: quá 15 phút kể từ lúc tạo)  
  **HỆ THỐNG SẼ** chặn lệnh cập nhật mật khẩu VÀ trả về lỗi HTTP 400.

## 4. Đặc tả API (API Contract)

### Endpoint 1: Yêu cầu Link Khôi phục
* **API:** `POST /api/v1/auth/forgot-password`
* **Dữ liệu gửi lên (Request):**

Kết quả chạy mã
File created successfully at: dac_ta_tinh_nang_quen_mat_khau.md

```json
  { 
    "email": "chuỗi ký tự (bắt buộc, đúng định dạng email)" 
  }

Phản hồi 200 (Thành công):
JSON
{ 
  "success": true, 
  "message": "Nếu email của bạn có trong hệ thống, link khôi phục đã được gửi đi." 
}




Phản hồi 400 (Lỗi):
JSON
{ 
  "success": false, 
  "error": { 
    "code": "INVALID_EMAIL", 
    "message": "Sai định dạng email" 
  } 
}




Endpoint 2: Đặt lại Mật khẩu
API: POST /api/v1/auth/reset-password
Dữ liệu gửi lên (Request):
JSON
{ 
  "token": "chuỗi ký tự (bắt buộc)", 
  "newPassword": "chuỗi ký tự (bắt buộc, tối thiểu 8 ký tự)" 
}




Phản hồi 200 (Thành công):
JSON
{ 
  "success": true, 
  "message": "Cập nhật mật khẩu thành công" 
}




Phản hồi 400 (Lỗi):
JSON
{ 
  "success": false, 
  "error": { 
    "code": "INVALID_TOKEN", 
    "message": "Token không hợp lệ hoặc đã hết hạn" 
  } 
}




5. Ràng buộc Kỹ thuật
Thời hạn Token: Link khôi phục chỉ có hiệu lực tối đa trong 15 phút.
Giới hạn tần suất (Rate limit): Tối đa 3 lần yêu cầu gửi email/giờ cho mỗi địa chỉ email (chống hành vi spam).
Ràng buộc Database: Các thao tác truy vấn kiểm tra email và cập nhật mật khẩu trong cơ sở dữ liệu sẽ sử dụng Basic SQL Statements. Cần chú ý viết code xử lý làm sạch dữ liệu đầu vào (sanitize) cẩn thận để phòng chống tấn công SQL Injection.
Bảo mật mật khẩu: Mật khẩu mới bắt buộc phải được băm (hash) bằng thuật toán an toàn (như BCrypt hoặc Argon2) trước khi lưu, tuyệt đối không lưu dưới dạng văn bản thuần (plain-text).
6. Nằm ngoài phạm vi (Out of Scope)
Gửi mã xác thực OTP qua tin nhắn SMS (Chưa làm ở giai đoạn này).
Chức năng tự động khóa tài khoản tạm thời nếu người dùng nhập sai hoặc dùng token lỗi quá nhiều lần.

