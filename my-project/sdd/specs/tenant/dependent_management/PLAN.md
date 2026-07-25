# PLAN: Kế hoạch Thực thi Quản lý Người phụ thuộc (Tenant)

**Status:** Planning  
**Date:** 2026-06-21  
**Priority:** Medium  
**Estimated Duration:** 4-5 weeks

---

## 1. Executive Summary

Dependent Management cung cấp cho Tenant khả năng:
- xem danh sách người phụ thuộc đang hoạt động thuộc phòng/hợp đồng thuê của mình,
- xem chi tiết từng người phụ thuộc,
- đảm bảo thông tin PII được bảo vệ và không trả về dữ liệu đã Soft Delete.

Tính năng này là read-only đối với Tenant; Ban Quản Lý vẫn có thể truy vấn toàn bộ người phụ thuộc theo phân quyền hiện có.

---

## 2. Business Goals

- Giúp Tenant kiểm tra thông tin cư trú của các thành viên trong phòng.
- Hỗ trợ Ban Quản Lý xác minh nhân khẩu và quản lý cư dân.
- Đảm bảo dữ liệu nhạy cảm không bị lộ khi hiển thị cho Tenant.
- Củng cố tính bảo mật và quyền truy cập dữ liệu tenant-scoped.

---

## 3. Scope

### In scope
- `GET /tenant/dependents` (`TenantDependentListServlet`) → Forward `/WEB-INF/views/tenant/dependent-list.jsp`
- `GET /tenant/dependent-detail?id={dependentId}` (`TenantDependentDetailServlet`) → Forward `/WEB-INF/views/tenant/dependent-detail.jsp`
- Hiển thị danh sách người phụ thuộc hoạt động (`deleted_at IS NULL`)
- Mask CCCD/CMND theo quy định SEC-01 (`0790******123`)
- Chỉ cho Tenant xem người phụ thuộc thuộc phòng/hợp đồng thuê hiện tại của chính mình
- Hiển thị empty state khi không có người phụ thuộc
- Xử lý các trang lỗi / chuyển hướng 401 (Login redirect), 403 (Forbidden), 404 (Not Found), 500

### Out of scope
- Thêm/sửa/xóa người phụ thuộc
- Gửi yêu cầu phê duyệt người phụ thuộc
- Phê duyệt/đánh giá người phụ thuộc
- Tải/xuất PDF/Excel
- Nhận diện khuôn mặt hoặc QR

---

## 4. Architecture & Design

### Data Access & DAO
- `DependentDAO.getDependentsByTenantId(int tenantId)`: Lấy danh sách người phụ thuộc `deleted_at IS NULL`.
- `DependentDAO.getDependentByIdAndTenantId(int dependentId, int tenantId)`: Lấy chi tiết 1 người phụ thuộc, đảm bảo sở hữu theo `tenant_id`.

### PII Protection & Controller
- CCCD/CMND phải được mask server-side (`0790******123`) trước khi chuyển tới JSP.
- Xác thực phiên làm việc người dùng thông qua `request.getSession()`.

### Servlet Mappings & Views
- `TenantDependentListServlet` (`/tenant/dependents`): `request.setAttribute("dependentList", List<DependentDTO>)` → Forward `/WEB-INF/views/tenant/dependent-list.jsp`.
- `TenantDependentDetailServlet` (`/tenant/dependent-detail`): `request.setAttribute("dependent", DependentDetailDTO)` → Forward `/WEB-INF/views/tenant/dependent-detail.jsp`.

---

## 5. Functional Requirements

### Tenant List
- Hiển thị người phụ thuộc thuộc tenant hiện tại và chưa Soft Delete.
- Trường hiển thị: dependentId, fullName, relationship, phoneNumber, isVerified.
- Sắp xếp theo họ tên tăng dần.
- Hiển thị empty state khi không có dữ liệu.

### Tenant Detail
- Hiển thị thông tin chi tiết: dependentId, fullName, avatar, dateOfBirth, gender, relationship, phoneNumber, email, citizenId (masked), sponsoredBy, registeredDate, isVerified.
- Chỉ xem được dependent cùng thuộc tenant.
- Trả HTTP 404 nếu dependentId không tồn tại hoặc bị Soft Delete.
- Trả HTTP 403 nếu tenant truy cập phụ thuộc của tenant khác.

---

## 6. Non-functional Requirements

- API danh sách và chi tiết phản hồi < 200ms (P95).
- Bắt buộc xác thực Access Token.
- Uptime ≥ 99.9%.
- Dữ liệu tenant-scoped và soft delete an toàn.
- Tuân thủ SEC-01 với masking CCCD/CMND.

---

## 7. Security & Authorization

- Chỉ Tenant hoặc Admin/Manager có quyền xem dữ liệu.
- Tenant chỉ truy cập dữ liệu thuộc phòng/hợp đồng thuê của mình.
- Soft-deleted records không hiển thị.
- Access token phải valid.

---

## 8. Success Criteria

- Tenant có thể xem danh sách và chi tiết người phụ thuộc của mình.
- Danh sách sắp xếp theo họ tên tăng dần.
- CCCD/CMND được mask chính xác.
- Truy cập không hợp lệ trả 403.
- dependentId không tồn tại hoặc Soft Delete trả 404.
- Empty state hiển thị khi không có dữ liệu.
- API đáp ứng yêu cầu hiệu năng.
- Test coverage đủ và UAT đạt.

---

## 9. Risks & Mitigations

- Risk: Tenant xem nhầm dữ liệu của người khác.
  - Mitigation: enforce tenant_id filter và authorization ở mọi endpoint.

- Risk: PII bị lộ qua CCCD/CMND.
  - Mitigation: mask server-side và kiểm tra trước trả dữ liệu.

- Risk: API chậm khi số lượng dependent lớn.
  - Mitigation: phân trang, chỉ chọn các trường cần thiết, thêm chỉ mục.

---

## 10. Timeline

- **Week 1:** Design, access control, API contract
- **Week 2:** Backend implementation, masking, queries
- **Week 3:** Frontend list/detail + empty state
- **Week 4:** Testing, performance tuning, UAT

**Total:** 4-5 weeks

