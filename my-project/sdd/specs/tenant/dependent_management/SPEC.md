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

# 6. Technical Notes

## Routing & Navigation

### Danh sách người phụ thuộc
- **Route:** `GET /tenant/dependents`
- **View Data:**
  - Forward sang view: `/WEB-INF/views/tenant/dependents/list.jsp`
  - Dữ liệu truyền xuống view (Request Attributes):
    - `dependents`: Danh sách người phụ thuộc (List<Dependent>)
    - `activeMenu`: "dependents"

---

### Chi tiết người phụ thuộc
- **Route:** `GET /tenant/dependents/{dependentId}` (hoặc `GET /tenant/dependents?id={dependentId}`)
- **View Data:**
  - Forward sang view: `/WEB-INF/views/tenant/dependents/detail.jsp`
  - Dữ liệu truyền xuống view (Request Attributes):
    - `dependent`: Chi tiết người phụ thuộc (Dependent)
    - `activeMenu`: "dependents"

---

## Database

Không thay đổi schema.

Hệ thống chỉ truy vấn các bản ghi:

```
deleted_at IS NULL
```

---

## Validation

### Authorization

- User đã đăng nhập.

- Role = Tenant.

- dependentId thuộc Tenant hiện tại.

### Dependent

- dependentId tồn tại.

- Chưa Soft Delete.

- Là mã hợp lệ.

---

# 7. Response Data

## Dependent List

```json
[
  {
    "dependentId": "DEP001",
    "fullName": "Nguyễn Văn B",
    "relationship": "Em trai",
    "phoneNumber": "0912345678",
    "isVerified": true
  },
  {
    "dependentId": "DEP002",
    "fullName": "Nguyễn Thị C",
    "relationship": "Mẹ",
    "phoneNumber": "0987654321",
    "isVerified": false
  }
]
```

---

## Dependent Detail

```json
{
  "dependentId": "DEP001",
  "fullName": "Nguyễn Văn B",
  "avatar": "https://...",
  "dateOfBirth": "2005-10-12",
  "gender": "Male",
  "phoneNumber": "0912345678",
  "citizenId": "0790******123",
  "email": "nguyenvanb@gmail.com",
  "relationship": "Em trai",
  "registeredDate": "2026-01-15",
  "isVerified": true,
  "sponsoredBy": {
    "tenantId": "TEN001",
    "fullName": "Nguyễn Văn A"
  }
}
```

---

# 8. Error Handling

| HTTP Code | Description | UI Action |
| --- | --- | --- |
| 401 | Unauthorized | Redirect Login |
| 403 | Forbidden | Hiển thị "Bạn không có quyền truy cập." |
| 404 | Dependent Not Found | Hiển thị màn hình Not Found |
| 500 | Internal Server Error | Hiển thị thông báo lỗi và Retry |

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