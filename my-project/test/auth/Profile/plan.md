# Implementation Plan: Profile Tests (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: `ProfileServlet.java`, `UserDAO`
- **Constraint**: Đảm bảo 100% Unit Test. Mock toàn bộ `HttpServletRequest`, đặc biệt là `Part` cho việc Upload file Avatar. *(Lưu ý: Logic ChangePassword đã được test ở module trước, class này tập trung vào View Profile và Update Profile)*.

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/auth/ProfileServletTest.java`

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoGet_ViewProfile_Success`: Fetch và hiển thị Profile.
- `testDoPost_UpdateProfile_Success`: Cập nhật thành công các thông tin cơ bản (Phone, Date of Birth).
- `testDoPost_UploadAvatar_Success`: Gửi kèm ảnh hợp lệ (Part) -> Lưu file mô phỏng và cập nhật URL.

### 3.2 Error Cases
- `testIDOR_SpoofingAttempt`: Cố tình truyền `userId` giả mạo lên form, verify hệ thống CHỈ dùng Session ID.
- `testUpdateProfile_InvalidFormats`: Truyền SDT 11 số / Email sai format -> Báo lỗi.
- `testUpdateProfile_DuplicateConstraints`: Truyền Email/SDT đã tồn tại (Mock DAO ném `DuplicateKeyException` hoặc trả false) -> Báo lỗi.

### 3.3 Boundary Values
- `testUploadAvatar_MaxSizeBoundary`: Gửi file lớn hơn 5MB -> Báo lỗi LimitExceeded.
- `testUpdateProfile_OptionalFieldsNull`: Gửi ngày sinh `dob` rỗng -> Lưu null thành công.

### 3.4 Concurrent Scenarios
- `testConcurrency_UniqueUpdate`: Hai người dùng đồng thời request sửa thông tin trùng 1 SDT. Xác minh MockDAO chỉ xử lý 1 request thành công.

## 4. Các bước thực hiện
1. Setup class test cho `ProfileServlet`.
2. Tạo Mock đối tượng `Part` để giả lập quá trình upload Avatar.
3. Viết test các kịch bản Update Profile.
