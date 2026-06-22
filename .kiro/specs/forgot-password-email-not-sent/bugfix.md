# Bugfix Requirements Document

## Introduction

Chức năng quên mật khẩu không gửi email đến người dùng khi họ yêu cầu khôi phục mật khẩu. Người dùng nhập email hợp lệ vào form quên mật khẩu, hệ thống hiển thị thông báo "Đã gửi" nhưng không có email nào thực sự được gửi đến hòm thư của người dùng. Lỗi này khiến người dùng không thể khôi phục tài khoản của mình, gây gián đoạn nghiệm vụ quan trọng của hệ thống quản lý nhà trọ.

**Thông tin kỹ thuật:**
- **Servlet:** `ForgotPasswordServlet` (`/forgot-password`)
- **Email Service:** `EmailService.sendResetLink()` đã được implement với SMTP Gmail
- **SMTP Configuration:** smtp.gmail.com:587 với STARTTLS
- **Sender:** atu023@gmail.com với App Password đã được cấu hình

## Bug Analysis

### Current Behavior (Defect)

**1.1** WHEN người dùng nhập email hợp lệ (đã tồn tại trong hệ thống) vào form quên mật khẩu và submit THEN hệ thống hiển thị thông báo "Link khôi phục đã được gửi" nhưng không có email nào thực sự được gửi đến hòm thư của người dùng

**1.2** WHEN người dùng kiểm tra hòm thư (inbox, spam, promotions) sau khi yêu cầu khôi phục mật khẩu THEN không tìm thấy email khôi phục nào từ hệ thống

**1.3** WHEN EmailService.sendResetLink() được gọi với tham số hợp lệ (email và resetLink) THEN phương thức in ra console "Đang gửi email..." và "Gửi email thành công!" nhưng email không đến được người nhận

### Expected Behavior (Correct)

**2.1** WHEN người dùng nhập email hợp lệ (đã tồn tại trong hệ thống) vào form quên mật khẩu và submit THEN hệ thống SHALL gửi email chứa link khôi phục mật khẩu đến địa chỉ email đó thông qua SMTP Gmail

**2.2** WHEN email khôi phục được gửi thành công THEN người dùng SHALL nhận được email trong hòm thư (inbox hoặc spam) với tiêu đề "Khôi phục mật khẩu - Quản lý Nhà trọ" và nội dung HTML chứa link khôi phục có hiệu lực 15 phút

**2.3** WHEN EmailService.sendResetLink() được gọi với tham số hợp lệ THEN phương thức SHALL thực hiện kết nối SMTP, xác thực với Gmail, và gửi email thực tế đến người nhận qua Transport.send()

**2.4** WHEN quá trình gửi email thất bại (lỗi SMTP, authentication, network) THEN hệ thống SHALL log chi tiết lỗi vào console hoặc error.log để dễ dàng debug

### Unchanged Behavior (Regression Prevention)

**3.1** WHEN người dùng nhập email không hợp lệ (chưa đăng ký trong hệ thống) vào form quên mật khẩu THEN hệ thống SHALL CONTINUE TO hiển thị thông báo chung "Link khôi phục đã được gửi" mà không tiết lộ email có tồn tại hay không (chống user enumeration)

**3.2** WHEN người dùng yêu cầu khôi phục mật khẩu quá số lần cho phép (3 lần/giờ theo RateLimitManager) THEN hệ thống SHALL CONTINUE TO từ chối yêu cầu với thông báo "Bạn đã vượt quá số lần yêu cầu"

**3.3** WHEN người dùng nhập email rỗng hoặc null vào form quên mật khẩu THEN hệ thống SHALL CONTINUE TO hiển thị thông báo lỗi "Vui lòng nhập địa chỉ email hợp lệ" mà không gọi EmailService

**3.4** WHEN token khôi phục mật khẩu được tạo bởi ResetTokenManager THEN token đó SHALL CONTINUE TO có thời gian sống tối đa 15 phút và được lưu trong bộ nhớ cache

**3.5** WHEN người dùng click vào link khôi phục trong email (sau khi lỗi gửi email được sửa) THEN hệ thống SHALL CONTINUE TO hiển thị form nhập mật khẩu mới nếu token hợp lệ

**3.6** WHEN người dùng đặt lại mật khẩu thành công (sau khi lỗi gửi email được sửa) THEN hệ thống SHALL CONTINUE TO thu hồi toàn bộ session đang tồn tại của tài khoản đó qua SessionRegistry

---

## Bug Condition Analysis

### Bug Condition Function

```pascal
FUNCTION isBugCondition(X)
  INPUT: X of type ForgotPasswordRequest
         where X = { email: String, userExists: Boolean }
  OUTPUT: boolean
  
  // Bug condition: Email hợp lệ và user tồn tại trong hệ thống
  // (Trường hợp mà EmailService.sendResetLink() được gọi)
  RETURN X.email IS NOT NULL 
         AND X.email IS NOT BLANK 
         AND X.userExists = true
END FUNCTION
```

### Property Specification

```pascal
// Property: Fix Checking - Email Must Be Sent
FOR ALL X WHERE isBugCondition(X) DO
  result ← forgotPasswordFlow'(X)
  
  // Sau khi fix, email phải được gửi thực tế
  ASSERT email_was_sent(X.email) = true
  AND email_contains_reset_link(X.email) = true
  AND smtp_connection_successful() = true
  AND no_exception_thrown() = true
END FOR
```

### Preservation Goal

```pascal
// Property: Preservation Checking - Non-buggy Inputs Unchanged
FOR ALL X WHERE NOT isBugCondition(X) DO
  ASSERT forgotPasswordFlow(X) = forgotPasswordFlow'(X)
END FOR

// Cụ thể:
// - Email không tồn tại: vẫn hiển thị "Đã gửi" (anti-enumeration)
// - Email rỗng/null: vẫn hiển thị lỗi validation
// - Rate limit vượt quá: vẫn từ chối yêu cầu
// - Token vẫn có TTL 15 phút
// - Session revocation vẫn hoạt động sau khi reset password
```

---

## Investigation Areas

Dựa trên phân tích code, có một số vùng cần điều tra để xác định nguyên nhân gốc rễ:

1. **SMTP Authentication**: App Password "ywgq bjng ymfo bpol" có được format đúng không? Gmail yêu cầu 16 ký tự liền không có khoảng trắng.

2. **JavaMail Dependencies**: Có đầy đủ các thư viện jakarta.mail trong classpath không? Thiếu dependency có thể khiến Transport.send() thất bại âm thầm.

3. **Exception Handling**: Block try-catch trong EmailService.sendResetLink() có đang ẩn exception không? Cần kiểm tra console/log để xem có lỗi nào bị bắt và in ra không.

4. **Network/Firewall**: Server có bị chặn kết nối outbound đến smtp.gmail.com:587 không?

5. **Gmail Security Settings**: Tài khoản atu023@gmail.com có bật "Less secure app access" hoặc đã tạo App Password đúng cách chưa?

6. **Console Output Misleading**: Message "Gửi email thành công!" được in ra TRƯỚC khi Transport.send() thực sự hoàn thành, có thể gây hiểu nhầm.

---

## Next Steps

Sau khi document này được duyệt, team sẽ:
1. Điều tra các nguyên nhân tiềm ẩn ở phần Investigation Areas
2. Tạo design document với giải pháp kỹ thuật cụ thể
3. Implement fix và verify bằng cách:
   - **Fix Checking**: Gửi email thử nghiệm và xác nhận nhận được trong hòm thư
   - **Preservation Checking**: Chạy test suite hiện có để đảm bảo các case khác không bị ảnh hưởng
