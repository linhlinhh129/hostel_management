# Implementation Plan: Cập nhật Trạng thái Sửa chữa (Unit Test Only)

## 1. Technical Context
- **Frameworks**: JUnit 5, Mockito
- **Target**: Servlet xử lý hành động Hoàn thành (Ví dụ: `OperatorDetailRequestServlet` với `action=complete` và multipart/form-data), cùng với `RequestDAO`.
- **Constraint**: Đảm bảo 100% Unit Test. Mock `HttpServletRequest.getParts()` để giả lập file upload. Kiểm tra chặt chẽ Validation logic (Bắt buộc phải có ảnh và ghi chú).

## 2. Các file cần tạo/chỉnh sửa
- `src/test/java/com/quanlyphongtro/controller/operator/OperatorUpdateStatusServletTest.java` (Sẽ khởi tạo Servlet chịu trách nhiệm cập nhật trạng thái)

## 3. Danh sách Test Cases theo 4 khía cạnh

### 3.1 Happy Path
- `testDoPost_CompleteRequest_OneImage`: Gửi `action=complete`, `notes="Đã sửa"`, và 1 Part (Ảnh minh chứng). DAO phải được gọi hàm update với `rejection_reason` (workaround) chứa ghi chú, và `attachment_urls2` chứa chuỗi tên file. Trạng thái về `COMPLETED`.
- `testDoPost_CompleteRequest_MultipleImages`: Đính kèm 5 Part ảnh. Tên file lưu DB phải được nối bằng dấu phẩy.

### 3.2 Error Cases
- `testDoPost_MissingImages`: Gửi form hoàn thành nhưng không đính kèm file ảnh nào. Bắt Validation Error.
- `testDoPost_MissingNotes`: Đính kèm ảnh nhưng để trống ghi chú. Bắt Validation Error.
- `testDoPost_InvalidState`: Gửi `action=complete` lên một yêu cầu đang `PENDING`. Bắt Validation Error.
- `testDoPost_InvalidFileType`: Cố tình mock đính kèm file `.pdf` hoặc text. Servlet bắt Validation và từ chối xử lý.

### 3.3 Boundary Values
- `testDoPost_NotesMaxBoundary`: Ghi chú dài đúng 1000 ký tự. Pass validation thành công.
- `testDoPost_MaxFilesExceeded`: (Tuỳ chọn nếu logic Backend có giới hạn) Đính kèm 6 file, văng lỗi hoặc cắt bớt.
- `testDoPost_FileSizeExceeded`: Mock lỗi file lớn văng `IllegalStateException` từ Tomcat, test cách Servlet try-catch và báo lại UI.

### 3.4 Concurrent Scenarios
- `testConcurrency_DoubleComplete`: Hai thợ điện cùng bấm "Hoàn thành" 1 phiếu. DAO văng ngoại lệ `OptimisticLockException` ở thread thứ 2. Xử lý an toàn trên UI.

## 4. Các bước thực hiện
1. Thiết lập `OperatorUpdateStatusServletTest` với Mockito (`@Mock RequestDAO`, `@Mock Part`).
2. Viết Test method tuân thủ format comment `# EARS [...]`.
