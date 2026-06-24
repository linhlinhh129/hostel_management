# TASKS: Phân chia Chi tiết Đầu Việc - Xem Nhật ký Hệ thống (Audit Log)

**Total Story Points:** ~38 points  
**Sprint Duration:** 2 tuần × 2-3 sprints = 5 tuần  
**Velocity:** ~12-14 points/sprint

---

## Epic 1: Thiết kế & Chuẩn bị (8 points)

### Task 1.1: Rà soát schema và xác định API contract (3 points)
**Duration:** 1-2 ngày  
**Description:**
- Kiểm tra schema bảng `audit_log` hiện có (các cột, kiểu dữ liệu, index)
- Xác định các giá trị hợp lệ của `entityType` (Tenant, Employee, Facility, Notification, Contract, ...)
- Xác định các giá trị hợp lệ của `action` (CREATE, UPDATE, DELETE)
- Định nghĩa API contract: `GET /api/v1/audit-logs`
- Xác định query parameters: entityType, action, createdBy, fromDate, toDate, page, size

**Acceptance Criteria:**
- ✓ Schema audit_log được xác nhận đầy đủ
- ✓ Danh sách entityType và action hợp lệ được liệt kê
- ✓ API contract rõ ràng, khớp với SPEC.md

---

### Task 1.2: Xác định validation và mã lỗi (2 points)
**Duration:** 1 ngày  
**Description:**
- Định nghĩa luật validate từng query parameter
- Xác định mã lỗi: `INVALID_FILTER`, `INVALID_DATE_RANGE`, `FORBIDDEN`, `UNAUTHORIZED`
- Xác định HTTP status tương ứng

**Acceptance Criteria:**
- ✓ Bảng validation rõ ràng cho từng tham số
- ✓ Mã lỗi mapped đúng

---

### Task 1.3: Thiết kế index database (3 points)
**Duration:** 1 ngày  
**Description:**
- Kiểm tra các index hiện có trên bảng `audit_log`
- Thêm index nếu thiếu: `entity_type`, `action`, `created_by`, `created_at`
- Tạo migration script nếu cần
- Kiểm tra query plan với EXPLAIN

**Acceptance Criteria:**
- ✓ Index đầy đủ cho các cột lọc
- ✓ Query performance chấp nhận được với dữ liệu lớn

---

## Epic 2: Backend Implementation (18 points)

### Task 2.1: Repository với dynamic filter (5 points)
**Duration:** 2 ngày  
**Description:**
- Tạo `AuditLogRepository` với query động theo filter
- Sử dụng JPA Specification hoặc JPQL để build câu query theo điều kiện có/không có filter
- Hỗ trợ phân trang (Pageable) và sắp xếp mặc định createdAt DESC
- Tối ưu tránh N+1 query

**Acceptance Criteria:**
- ✓ Query đúng với từng combination filter
- ✓ Pagination hoạt động chính xác
- ✓ Sắp xếp createdAt DESC mặc định
- ✓ Không có N+1 query

---

### Task 2.2: Service xem danh sách Audit Log (4 points)
**Duration:** 2 ngày  
**Description:**
- Implement `getAuditLogs(filter, pageable)` service
- Validate tham số đầu vào: kiểm tra fromDate/toDate, entityType, action hợp lệ
- Trả về `INVALID_DATE_RANGE` nếu fromDate > toDate
- Trả về `INVALID_FILTER` nếu tham số không hợp lệ
- Trả về danh sách rỗng nếu không có bản ghi khớp

**Acceptance Criteria:**
- ✓ Service trả về đúng dữ liệu theo filter
- ✓ Validation lỗi đúng mã lỗi
- ✓ Empty result được xử lý

**Unit Tests:**
- Test danh sách không filter
- Test filter từng tham số
- Test fromDate > toDate
- Test tham số không hợp lệ
- Test empty result

---

### Task 2.3: Controller và phân quyền (4 points)
**Duration:** 1-2 ngày  
**Description:**
- Tạo `AuditLogController` với endpoint `GET /api/v1/audit-logs`
- Kiểm tra role ADMIN ở controller layer
- Trả về 401 nếu chưa đăng nhập
- Trả về 403 nếu không phải ADMIN
- Chuẩn hóa response: `{ success, data: { items, page, size, totalElements, totalPages } }`

**Acceptance Criteria:**
- ✓ Endpoint hoạt động đúng
- ✓ 401/403 đúng trường hợp
- ✓ Response format chuẩn

---

### Task 2.4: DTO và response mapping (3 points)
**Duration:** 1 ngày  
**Description:**
- Tạo `AuditLogResponseDTO` với các trường: auditLogId, entityType, entityId, action, oldValue, newValue, ipAddress, comment, createdBy, createdAt
- Mapping từ entity sang DTO
- Chuẩn hóa format `oldValue`/`newValue` (JSON string)

**Acceptance Criteria:**
- ✓ DTO đầy đủ các trường theo SPEC
- ✓ Mapping chính xác
- ✓ oldValue/newValue hiển thị đúng theo rule CREATE/UPDATE/DELETE

---

### Task 2.5: Unit test backend (2 points)
**Duration:** 1 ngày  
**Description:**
- Viết unit test cho AuditLogService
- Kiểm tra các kịch bản filter, validation, empty result
- Coverage tối thiểu 80%

**Acceptance Criteria:**
- ✓ Coverage >= 80%
- ✓ Tất cả kịch bản chính được bao phủ

---

## Epic 3: Frontend Implementation (8 points)

### Task 3.1: Trang danh sách Audit Log (4 points)
**Duration:** 2 ngày  
**Description:**
- Xây dựng bảng hiển thị danh sách với các cột: auditLogId, entityType, entityId, action, createdBy, createdAt
- Hiển thị trạng thái "Không có dữ liệu" khi danh sách rỗng
- Hỗ trợ phân trang
- Link hoặc tooltip để xem oldValue/newValue chi tiết

**Acceptance Criteria:**
- ✓ Bảng hiển thị đúng dữ liệu
- ✓ Phân trang hoạt động
- ✓ Empty state hiển thị

---

### Task 3.2: Bộ lọc (Filter Panel) (3 points)
**Duration:** 1-2 ngày  
**Description:**
- Thêm bộ lọc: entityType (dropdown), action (dropdown), createdBy (input), fromDate/toDate (date picker)
- Validate fromDate/toDate ở client trước khi gọi API
- Hiển thị lỗi validation phù hợp
- Gọi API khi thay đổi filter hoặc nhấn nút tìm kiếm

**Acceptance Criteria:**
- ✓ Bộ lọc hoạt động đúng
- ✓ Validation client-side cho date range
- ✓ API call đúng với filter đã chọn

---

### Task 3.3: Hiển thị oldValue / newValue (1 point)
**Duration:** 0.5 ngày  
**Description:**
- Hiển thị oldValue / newValue dưới dạng JSON được format (pre-tag hoặc tooltip)
- Xử lý trường hợp null (CREATE/DELETE)

**Acceptance Criteria:**
- ✓ oldValue/newValue hiển thị rõ ràng
- ✓ null hiển thị là "—" hoặc "N/A"

---

## Epic 4: Testing & Release (4 points)

### Task 4.1: Integration test và kiểm thử phân quyền (2 points)
**Duration:** 1 ngày  
**Description:**
- Test API end-to-end với các combination filter
- Test trường hợp không có quyền (401, 403)
- Test pagination với nhiều trang dữ liệu
- Test fromDate > toDate

**Acceptance Criteria:**
- ✓ Tất cả API hoạt động end-to-end
- ✓ Phân quyền chính xác

---

### Task 4.2: UAT và hoàn thiện (2 points)
**Duration:** 1-2 ngày  
**Description:**
- Chạy UAT với Admin trên môi trường staging
- Kiểm tra performance với tập dữ liệu thực
- Xử lý các vấn đề phát sinh
- Hoàn thiện tài liệu

**Acceptance Criteria:**
- ✓ UAT pass
- ✓ Performance < 500ms P95
- ✓ Không còn lỗi nghiêm trọng

---

## Dependencies & Thứ tự

- Task 1.1 → Task 1.2 → Task 2.1
- Task 1.3 (song song với Task 1.2)
- Task 2.1 → Task 2.2 → Task 2.3
- Task 2.3 → Task 2.4
- Task 2.4 → Task 3.1
- Task 3.1 → Task 3.2, Task 3.3
- Task 2.5 → Task 4.1
- Task 3.3 → Task 4.1 → Task 4.2

---

## Summary by Sprint

| Sprint | Duration | Points | Focus |
|--------|----------|--------|-------|
| Sprint 1 | Tuần 1 | 11 | Thiết kế, schema, repository |
| Sprint 2 | Tuần 2-3 | 16 | Backend service, controller, DTO |
| Sprint 3 | Tuần 4-5 | 11 | Frontend, testing, UAT |

---

## Resource Allocation

| Role | Hours | Duration |
|------|-------|----------|
| Backend Developer | 60 | 2 tuần |
| Frontend Developer | 40 | 1 tuần |
| QA Engineer | 20 | 0.5 tuần |
| Database Admin | 8 | 1-2 ngày |
| Tech Lead | 10 | Review & oversight |
