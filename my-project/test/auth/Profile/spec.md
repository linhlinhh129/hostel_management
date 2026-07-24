# Test Specification: Hồ sơ cá nhân (Profile - Unit Test Only)

**Status:** Draft
**Target Feature:** Hồ sơ cá nhân (`my-project/sdd/specs/auth/Profile`)
**Primary Principle:** Tests phải test BEHAVIOR (hành vi), không test IMPLEMENTATION (cách làm)

---

## 1. Mục tiêu kiểm thử (Test Objectives)
Sử dụng **Unit Test (JUnit 5 + Mockito)** để kiểm chứng logic xử lý tại lớp `ProfileServlet`. Tập trung vào luồng phân quyền dữ liệu (Role-based data), tính toàn vẹn của file ảnh tải lên, ràng buộc Unique Data (Email, Phone), và đặc biệt là phòng chống lỗ hổng **ID Spoofing (IDOR)**.

---

## 2. Tiêu chí chấp nhận & Chiến lược kiểm thử (Acceptance Criteria as Test Cases)

### 2.1 Happy Path (Các kịch bản thành công chính)
- **Xem Hồ sơ theo Vai trò**: 
  - KHI User có role `TENANT` GET profile, HỆ THỐNG PHẢI gọi Mock trả về thêm thông tin `tenantMetaData` (Mã phòng, Hợp đồng).
  - KHI User có role `ADMIN`, HỆ THỐNG PHẢI trả về thêm thông tin Lịch sử truy cập và Ngày tạo.
- **Cập nhật Thông tin cơ bản**: KHI POST Form với thông tin (Tên, SDT đúng format 10 số, CCCD 12 số, Ngày sinh), HỆ THỐNG PHẢI cập nhật DB và đồng bộ dữ liệu vào `UserSessionDTO` hiện tại.
- **Upload Avatar**: KHI đính kèm file ảnh hợp lệ (PNG, JPG < 5MB), HỆ THỐNG PHẢI mô phỏng việc lưu file và cập nhật `avatar_url`.

### 2.2 Error Cases (Các kịch bản lỗi từ "Unwanted" specs)
- **ID Spoofing (Chống sửa chéo)**: KHI một User (ID=1) truyền tham số ngầm (hidden field) `userId=2` lên để cố tình sửa thông tin của người khác, HỆ THỐNG PHẢI phớt lờ tham số này và CHỈ lấy ID từ `HttpSession` đang hoạt động (Hoặc ném lỗi `Forbidden`).
- **Lỗi Format Input**: KHI truyền SDT 11 số (hoặc chứa chữ cái), CCCD 10 số, HỆ THỐNG PHẢI từ chối ngay lập tức bằng Validation.
- **Upload File độc hại**: KHI cố tình đổi đuôi file `.exe` thành `.png` để upload, HỆ THỐNG PHẢI phát hiện thông qua Magic Bytes hoặc bắt lỗi Content-Type/Part Header từ chối lưu file.
- **Trùng lặp Dữ liệu (Unique Constraints)**: KHI cập nhật Email/SDT đã tồn tại của người khác, Mock DAO ném lỗi `DuplicateKeyException`, HỆ THỐNG PHẢI bắt lỗi và hiển thị thông báo thân thiện.

### 2.3 Boundary Values (Các giá trị biên)
- **Dung lượng File Ảnh**: KHI gửi file đúng mức biên giới hạn `maxFileSize = 5MB`, HỆ THỐNG PHẢI chấp nhận. KHI gửi file `5.01MB`, HỆ THỐNG PHẢI ném exception `FileSizeLimitExceededException` (Do cấu hình `@MultipartConfig`).
- **Dữ liệu Optional rỗng**: KHI cố tình truyền `dob` (Ngày sinh) rỗng, HỆ THỐNG PHẢI lưu `null` thay vì văng lỗi ParseDateException.

### 2.4 Concurrent Scenarios (Các kịch bản truy cập đồng thời)
- **Xung đột Unique Constraints**: KHI hai User cùng một lúc gửi lệnh POST yêu cầu cập nhật SĐT của mình thành `0999999999`. Do Race Condition, có thể cả 2 đều vượt qua hàm check `isPhoneExist`, NHƯNG hàm `update` ở DAO phải dựa vào DB Lock/Constraint. Hệ thống phải xử lý mượt mà khi Mock DAO văng exception cho 1 trong 2 thread.
