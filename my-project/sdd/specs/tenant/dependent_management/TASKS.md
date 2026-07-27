# TASKS: Phân chia Chi Tiết Đầu Việc - Quản lý Người phụ thuộc (Tenant)

**Date:** 2026-06-21  
**Total Story Points:** ~36 points  
**Sprint Duration:** 2 weeks × 2 sprints = 4 weeks  
**Velocity:** ~18 points/sprint

---

## Epic 1: Requirements & Security Design (5 points)

### Task 1.1: Confirm Authorization Model (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Description:**
- Review Tenant/Manager/Admin access rules
- Confirm tenant-scoped dependent ownership mapping
- Document `deleted_at IS NULL` soft-delete policy
- Define PII masking rules for CCCD/CMND

**Acceptance Criteria:**
- ✅ Authorization matrix documented
- ✅ Tenant-only and admin/manager access clarified
- ✅ Soft delete and masking requirements captured

---

### Task 1.2: Define API Contract & Response Schema (3 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Description:**
- Define list endpoint contract and response payload
- Define detail endpoint contract and response payload
- Define error responses for 401, 403, 404, 500
- Document empty state behavior

**Acceptance Criteria:**
- ✅ API contracts documented
- ✅ Response examples match SPEC
- ✅ Error handling schema defined

---

## Epic 2: Backend Implementation - Servlet & DAO (14 points)

### Task 2.1: Dependent DAO (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Description:**
- Viết `getDependentsByTenantId(int tenantId)` trong `DependentDAO` (truy vấn `deleted_at IS NULL`, sắp xếp theo `full_name`).
- Viết `getDependentByIdAndTenantId(int dependentId, int tenantId)` trong `DependentDAO` (kiểm tra sở hữu theo `tenant_id`).

**Acceptance Criteria:**
- ✅ SQL queries đúng, bảo mật và chống SQL Injection.
- ✅ Trả về dữ liệu chính xác theo tenant_id.

---

### Task 2.2: PII Masking Utility (3 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Description:**
- Xây dựng helper mask thông tin CCCD/CMND theo chuẩn SEC-01 (`0790******123`).
- Tích hợp masking vào DTO trước khi trả về View.

**Acceptance Criteria:**
- ✅ CCCD được che đúng 6 số giữa.

---

## Epic 3: Servlet Controllers & Views (12 points)

### Task 3.1: TenantDependentListServlet (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Description:**
- Tạo `TenantDependentListServlet` mapped với `@WebServlet("/tenant/dependents")`.
- Kiểm tra session đăng nhập & role `TENANT`.
- Gọi `DependentDAO`, gán `request.setAttribute("dependentList", list)` và forward sang `/WEB-INF/views/tenant/dependent-list.jsp`.

**Acceptance Criteria:**
- ✅ Servlet hoạt động đúng route `/tenant/dependents`.
- ✅ Forward giao diện thành công.

---

### Task 3.2: TenantDependentDetailServlet (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Description:**
- Tạo `TenantDependentDetailServlet` mapped với `@WebServlet("/tenant/dependent-detail")`.
- Nhận tham số `id`, kiểm tra quyền sở hữu của tenant.
- Nếu không hợp lệ: Forward tới 403/404 Page.
- Nếu hợp lệ: Mask CCCD, gán `request.setAttribute("dependent", dto)` và forward sang `/WEB-INF/views/tenant/dependent-detail.jsp`.

**Acceptance Criteria:**
- ✅ Servlet hoạt động đúng route `/tenant/dependent-detail`.
- ✅ Chặn các request xem dependent của tenant khác (403).

---

### Task 3.3: JSP Views Development (4 points)
**Priority:** HIGH  
**Duration:** 2 days  
**Description:**
- Xây dựng giao diện JSP `/WEB-INF/views/tenant/dependent-list.jsp` (sử dụng JSTL `<c:forEach>`, xử lý Empty State).
- Xây dựng giao diện JSP `/WEB-INF/views/tenant/dependent-detail.jsp` (hiển thị thông tin read-only, nút Back).

**Acceptance Criteria:**
- ✅ Hiển thị giao diện mạch lạc, chuẩn responsive.

---

### Task 4.3: UI Polish & Mobile Review (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 4.1, 4.2  
**Description:**
- Review visual consistency and spacing
- Verify responsive layout
- Ensure accessibility for text, buttons, and labels

**Acceptance Criteria:**
- ✅ UI is consistent across screens
- ✅ Responsive layout validated
- ✅ No major accessibility issues

---

## Epic 5: Testing & Validation (5 points)

### Task 5.1: Backend Unit Tests (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 2.1-2.4  
**Description:**
- Test list/detail service logic
- Test authorization and soft-delete filtering
- Test PII masking behavior

**Acceptance Criteria:**
- ✅ Unit tests pass
- ✅ Key negative scenarios covered
- ✅ PII masking validated

---

### Task 5.2: API Integration Tests (2 points)
**Priority:** HIGH  
**Duration:** 1 day  
**Dependencies:** Task 3.1-3.3  
**Description:**
- Test endpoint flows for tenant list/detail
- Test 401, 403, 404 responses
- Test empty state and valid response payloads

**Acceptance Criteria:**
- ✅ Integration tests pass
- ✅ Auth and access control verified
- ✅ Response schema validated

---

### Task 5.3: UAT & Sign-off (1 point)
**Priority:** MEDIUM  
**Duration:** 1 day  
**Dependencies:** Task 4.1-4.3, Task 5.1-5.2  
**Description:**
- Execute acceptance criteria from SPEC
- Validate tenant experience with product owner
- Capture issues and approve release readiness

**Acceptance Criteria:**
- ✅ UAT checklist completed
- ✅ No critical issues remaining
- ✅ Product owner approves

---

## Sprint Plan

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 15 | Authorization, backend list/detail, API contract |
| Sprint 2 | 15 | API endpoints, frontend list/detail |
| Sprint 3 | 6 | UI polish, testing, UAT |

---

## Critical Dependencies

- Task 1.1-1.2 → Task 2.1-2.4 (design before implementation)
- Task 2.1-2.4 → Task 3.1-3.3 (backend services before API)
- Task 3.1-3.3 → Task 4.1-4.2 (API before frontend)
- Task 4.1-4.2 → Task 5.1-5.3 (features before testing)

---

## Definition of Done

- Tenant can view dependents list and detail through API and UI
- CCCD/CMND masking is enforced server-side
- Tenant can only access their own dependent records
- `deleted_at IS NULL` rule is enforced
- API responds with correct 401/403/404 behavior
- UI shows empty state and error states
- Tests pass and UAT signed off

