# Test Specification: Quản lý Yêu cầu (Tenant Request Management)

**File bị ảnh hưởng**: `TenantRequestServletTest.java` (hoặc các servlet CRUD Request)
**Nguyên tắc**: Test Behavior, sử dụng Mockito để cô lập Database (`RequestService`). Tập trung vào bảo mật IDOR và validation dữ liệu đầu vào.

## 1. Happy Path (Các kịch bản thành công)

- `testDoGet_ViewRequestsList_Success`: KHI Tenant truy cập danh sách yêu cầu, THE SYSTEM SHALL trả về danh sách các yêu cầu do chính Tenant tạo, sắp xếp theo thời gian mới nhất (DESC).
- `testDoGet_ViewRequestDetail_Success`: KHI Tenant xem chi tiết một yêu cầu hợp lệ do mình tạo, THE SYSTEM SHALL hiển thị đầy đủ thông tin (Mã, Tiêu đề, Nội dung, Hình ảnh, Trạng thái).
- `testDoPost_CreateRequest_Success`: KHI Tenant tạo yêu cầu với dữ liệu hợp lệ (Title, Content, Category hợp lệ), THE SYSTEM SHALL lưu yêu cầu với trạng thái mặc định `PENDING` và trả về HTTP 201 (hoặc Redirect thành công).

## 2. Error Cases (Các kịch bản lỗi Unwanted từ EARS)

- `testDoPost_CreateRequest_MissingTitleOrContent_BadRequest`: KHI Tenant tạo yêu cầu thiếu Tiêu đề hoặc Nội dung, THE SYSTEM SHALL trả về lỗi HTTP 400 (hoặc thông báo lỗi Validation).
- `testDoPost_CreateRequest_InvalidCategory_BadRequest`: KHI Tenant chọn Thể loại (Category) không tồn tại, THE SYSTEM SHALL trả về lỗi HTTP 400.
- `testDoPost_CreateRequest_InvalidAttachmentType`: KHI Tenant upload file không phải ảnh (VD: .exe, .pdf thay vì jpg/png), THE SYSTEM SHALL trả về lỗi HTTP 400.
- `testDoGet_ViewRequestDetail_CrossTenant_NotFound`: KHI Tenant cố xem chi tiết một yêu cầu thuộc về Tenant khác (IDOR), THE SYSTEM SHALL trả về lỗi HTTP 404 (Không tìm thấy thay vì lộ việc tồn tại ID đó).
- `testDoGet_UnauthorizedAccess`: KHI người dùng chưa đăng nhập gọi API hoặc truy cập trang, THE SYSTEM SHALL trả về HTTP 401 hoặc Redirect tới trang Login.

## 3. Boundary Values (Giá trị biên)

- `testDoPost_CreateRequest_MaxAttachmentSize`: KHI Tenant tải lên ảnh lớn hơn 5MB, THE SYSTEM SHALL từ chối lưu và báo lỗi quá dung lượng.

## 4. Concurrent Scenarios (Kịch bản đồng thời)

- Không yêu cầu cụ thể (Chỉ Tenant mới tác động vào Request của chính mình, trạng thái do Manager thay đổi ở phân hệ khác).
