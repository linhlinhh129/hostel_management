# RULE.md

## 1. Business Rules (Quy tắc Nghiệp vụ)

### 1.1 Quyền truy cập
- Chỉ người dùng có vai trò `ADMIN` mới được phép truy cập và cập nhật thông tin phòng.
- Nếu Tenant hoặc Manager truy cập, hệ thống phải trả về lỗi 403 Forbidden (hoặc 404 tuỳ thiết kế).

### 1.2 Trạng thái Cơ sở (Facility Status)
- Nếu phòng thuộc về một Cơ sở (Facility) có trạng thái `INACTIVE` (đã vô hiệu hoá):
  - Giao diện (Frontend) phải ẨN hoặc DISABLE form cập nhật diện tích và giá phòng.
  - Xử lý nghiệp vụ (Backend) phải từ chối mọi yêu cầu cập nhật liên quan đến phòng này và trả về lỗi: "Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng."

### 1.3 Validation cho Diện tích (Area)
- Cho phép giá trị rỗng (để trống). Nếu rỗng, lưu trữ dưới dạng `NULL` trong DB.
- Nếu có giá trị, phải là số thực lớn hơn hoặc bằng 0.
- Không cho phép nhập chữ cái hoặc ký tự đặc biệt.

### 1.4 Validation cho Giá phòng (Room Fee)
- Cho phép giá trị rỗng (để trống). Nếu rỗng, lưu trữ dưới dạng `NULL` trong DB.
- Nếu có giá trị, phải là số thực lớn hơn hoặc bằng 0.
- Không cho phép nhập chữ cái hoặc ký tự đặc biệt.

## 2. Coding Rules (Quy tắc Lập trình)

### 2.1 Xử lý Dữ liệu
- Dùng kiểu `BigDecimal` trong Java để đại diện cho `area` và `roomFee` nhằm đảm bảo độ chính xác.
- Khi parse dữ liệu từ `HttpServletRequest`, nếu giá trị là `""` (chuỗi rỗng), gán giá trị biến là `null`.

### 2.2 Xử lý Lỗi (Exception Handling)
- Các lỗi vi phạm Business Rules (số âm, sai định dạng, cơ sở INACTIVE) phải ném ra `ValidationException` (hoặc exception tương đương của project).
- Không in exception thô ra màn hình; catch exception ở Servlet và trả về Flash message (thuộc tính `error` trong request/session) để hiển thị lên giao diện.

### 2.3 Phản hồi UI
- Sau khi cập nhật thành công (POST xử lý xong), bắt buộc dùng **Redirect** (PRG pattern - Post/Redirect/Get) về lại trang chi tiết phòng (GET) kèm Flash message thành công. Không forward trực tiếp từ phương thức POST để tránh lỗi submit lại form (F5).
