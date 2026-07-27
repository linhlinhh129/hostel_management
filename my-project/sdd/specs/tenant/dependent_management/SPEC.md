# Feature: Dependent Management (Quản lý Người phụ thuộc)

**Status:** Approved
**Author:** AI Agent
**Date:** 2026-06-21

---

# 1. Bối cảnh & Mục tiêu
Tính năng **Dependent Management** cho phép Người thuê (Tenant) quản lý danh sách và xem thông tin chi tiết của những người phụ thuộc ở cùng phòng với mình (vợ/chồng, con cái...). 
Mục tiêu là hỗ trợ theo dõi nhân khẩu chính xác mà vẫn tuân thủ nghiêm ngặt bảo vệ quyền riêng tư (SEC-01) và không sửa đổi schema CSDL.

---

# 2. Phân quyền và Ràng buộc (Constraints)
- **Tenant:** CHỈ được xem những người phụ thuộc có `tenant_id` khớp với mình. KHÔNG được xem của người khác.
- **Data Protection:** Tuyệt đối không xóa vật lý (Hard Delete), chỉ thao tác qua cột `deleted_at` (Soft delete).
- **Database:** Giữ nguyên bảng `dependents`, không thêm cột mới. Truy xuất thuần túy qua DAO.

---

# 3. Yêu cầu chức năng (FR)

## 3.1. Danh sách người phụ thuộc
- **Hành động:** Tenant truy cập mục Quản lý Người phụ thuộc.
- **Hệ thống xử lý:** Lấy danh sách từ CSDL điều kiện `tenant_id = [current_user]` VÀ `deleted_at IS NULL`.
- **Hiển thị:** Danh sách tóm tắt (Mã, Họ tên, Mối quan hệ, Số điện thoại).

## 3.2. Chi tiết người phụ thuộc
- **Hành động:** Tenant bấm vào xem chi tiết 1 người phụ thuộc.
- **Hệ thống xử lý:** Xác thực IDOR (ID truyền lên phải thuộc `tenant_id` hiện tại). Nếu sai báo lỗi 403.
- **Hiển thị:** 
- Mã người phụ thuộc
- Họ và tên
  - Ngày tháng năm sinh
- Số điện thoại
  - CCCD (Phải được mask thông tin nhạy cảm, VD: `0790******123`)
  - Email
- Mối quan hệ

- Số điện thoại

- Email

- CCCD/CMND (đã che thông tin)

- Người thuê bảo trợ

- Ngày đăng ký

- Trạng thái xác thực

---

### FR06

**THE SYSTEM SHALL**

Mask thông tin CCCD/CMND trước khi hiển thị.

Ví dụ:

```
0790******123
```

---

## Empty State

### FR07

**WHERE** Tenant chưa có người phụ thuộc

**THE SYSTEM SHALL**

Hiển thị:

> "Hiện chưa có người phụ thuộc nào được đăng ký."

---

## Authorization

### FR08

**WHERE** Tenant truy cập người phụ thuộc không thuộc quyền quản lý

**THE SYSTEM SHALL**

- Từ chối truy cập

- Trả HTTP 403 Forbidden

---

### FR09

**WHEN** người dùng chưa đăng nhập

**THE SYSTEM SHALL**

Chuyển hướng đến màn hình Login.

---

### FR10

**WHEN** dependentId không tồn tại hoặc đã bị Soft Delete

**THE SYSTEM SHALL**

Trả HTTP 404 Not Found.

---

### FR11

**WHERE** hệ thống không thể tải danh sách hoặc chi tiết người phụ thuộc

**THE SYSTEM SHALL**

- Hiển thị thông báo lỗi phù hợp.

- Cho phép người dùng Retry.

---

# 5. Non-functional Requirements

## Performance

- API danh sách phản hồi &lt; **200ms (P95)**.

- API chi tiết phản hồi &lt; **200ms (P95)**.

---

## Security

- Chỉ Tenant đã xác thực được truy cập.

- Chỉ xem dữ liệu thuộc quyền sở hữu.

- CCCD/CMND phải được mask theo chuẩn SEC-01.

- Không trả về dữ liệu của bản ghi đã Soft Delete.

---

## Privacy

Các trường sau được xem là PII:

- CCCD/CMND

- Email

- Số điện thoại

- Ngày sinh

Các trường này phải tuân thủ chính sách bảo vệ dữ liệu của hệ thống.

---

## Availability

API đạt **99.9% uptime**.

---

# 6. Servlet Routes & Page Controller Contract

## 6.1 Màn hình Danh sách người phụ thuộc

### Servlet Mapping

```http
GET /tenant/dependents
```

### Xử lý Request & View
- **Servlet:** `TenantDependentListServlet`
- **Mô tả:** Tiếp nhận request từ người thuê, lấy `tenant_id` từ `HttpSession`, gọi Service lấy danh sách người phụ thuộc (`deleted_at IS NULL`) thuộc người thuê này.
- **Scope & Attribute Name:** `request.setAttribute("dependentList", List<DependentDTO>)`
- **Forward View:** `/WEB-INF/views/tenant/dependent-list.jsp`

---

## 6.2 Màn hình Chi tiết người phụ thuộc

### Servlet Mapping

```http
GET /tenant/dependent-detail?id={dependentId}
```

### Xử lý Request & View
- **Servlet:** `TenantDependentDetailServlet`
- **Parameter:** `id` (mã định danh người phụ thuộc `dependentId`)
- **Mô tả:** Lấy thông tin chi tiết người phụ thuộc theo `id`. Kiểm tra xem bản ghi đó có thuộc sở hữu của `tenant_id` đang đăng nhập hay không và mask CCCD theo chuẩn SEC-01 (`0790******123`).
- **Scope & Attribute Name:** `request.setAttribute("dependent", DependentDetailDTO)`
- **Forward View:** `/WEB-INF/views/tenant/dependent-detail.jsp`
- **Trường hợp lỗi:**
  - Nếu `id` không tồn tại hoặc đã bị soft delete: Forward tới trang lỗi 404 (Not Found).
  - Nếu bản ghi không thuộc `tenant_id` hiện tại: Forward tới trang lỗi 403 (Access Denied).

---

# 7. Error Handling & Redirection

| Error Code | Status / Action | Description |
| --- | --- | --- |
| UNAUTHORIZED | Redirect `/login` | Chưa đăng nhập (Session không tồn tại) |
| FORBIDDEN | Forward 403 Page | Không có quyền xem thông tin người phụ thuộc này |
| DEPENDENT_NOT_FOUND | Forward 404 Page | Không tìm thấy người phụ thuộc hoặc bản ghi đã bị xóa |
| INTERNAL_ERROR | Forward 500 Page | Lỗi hệ thống server |

---

# 9. Acceptance Criteria

- Tenant chỉ xem được người phụ thuộc thuộc phòng của mình.

- Danh sách hiển thị đúng họ tên, mối quan hệ, số điện thoại và trạng thái xác thực.

- Danh sách được sắp xếp theo họ tên tăng dần.

- Có thể xem đầy đủ thông tin chi tiết của người phụ thuộc.

- CCCD/CMND được che (mask) đúng chuẩn.

- Không hiển thị các bản ghi đã Soft Delete.

- Empty State hiển thị khi chưa có người phụ thuộc.

- Truy cập dữ liệu của Tenant khác trả về HTTP 403.

- dependentId không tồn tại trả về HTTP 404.

- Hệ thống hiển thị Error State và Retry khi API lỗi.

---

# 10. UI Components

- Dependent List

- Dependent Card

- Dependent Detail View

- Avatar

- Verification Badge

- Back Button

- Empty State

- Loading State

- Error State

- Retry Button

---

# 11. Out of Scope

Không nằm trong phạm vi feature này:

- Thêm người phụ thuộc.

- Chỉnh sửa thông tin người phụ thuộc.

- Xóa người phụ thuộc.

- Gửi yêu cầu phê duyệt người phụ thuộc.

- Phê duyệt người phụ thuộc bởi Ban quản lý.

- Nhận diện khuôn mặt (Facial Recognition).

- Đồng bộ dữ liệu với Cơ sở dữ liệu quốc gia về dân cư.

- Xuất danh sách người phụ thuộc ra Excel hoặc PDF.