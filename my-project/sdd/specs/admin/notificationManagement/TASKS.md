# TASKS: Phân chia Chi tiết Đầu Việc - Quản lý Thông báo

**Total Story Points:** ~36 points  
**Sprint Duration:** 2 weeks × 3 sprints = 6 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Thiết kế & Chuẩn bị (8 points)

### Task 1.1: Xác định yêu cầu API và dữ liệu (2 points)
**Duration:** 1 day  
**Description:**
- Rà soát SPEC và CONTEXT
- Định nghĩa API contract cho POST /api/v1/notifications, GET /api/v1/notifications, GET /api/v1/notifications/{id}
- Xác định payload request/response và mã lỗi

**Acceptance Criteria:**
- ✓ API contract rõ ràng
- ✓ Payload hợp lệ với yêu cầu business
- ✓ Các trạng thái lỗi đã được xác định

---

### Task 1.2: Thiết kế schema Notification và Audit Log (3 points)
**Duration:** 1-2 ngày  
**Description:**
- Thiết kế bảng Notification (id, title, content, createdBy, createdAt, recipientCount)
- Thiết kế mở rộng audit log cho hành động tạo và xem
- Định nghĩa index cho tìm kiếm title và phân trang

**Acceptance Criteria:**
- ✓ Schema đầy đủ cho tính năng
- ✓ Index được xác định
- ✓ Audit log có thể ghi được user và hành động

---

### Task 1.3: Validation và phân quyền (3 points)
**Duration:** 1 ngày  
**Description:**
- Định nghĩa luật validate title/content
- Xác định lỗi `VALIDATION_ERROR`, `NO_RECIPIENT_FOUND`, `NOTIFICATION_NOT_FOUND`, `ACCESS_DENIED`
- Xác định cơ chế kiểm tra quyền Admin

**Acceptance Criteria:**
- ✓ Luật validate rõ ràng
- ✓ Lỗi mapped chính xác
- ✓ Phân quyền Admin rõ ràng

---

## Epic 2: Backend Implementation (16 points)

### Task 2.1: Triển khai Notification entity và repository (3 points)
**Duration:** 1-2 ngày  
**Description:**
- Tạo entity Notification
- Tạo repository CRUD cơ bản
- Thiết lập trường metadata và giới hạn nội dung

**Acceptance Criteria:**
- ✓ Entity map chính xác
- ✓ Repository hoạt động đúng
- ✓ Trường `recipientCount` hoặc tương đương được lưu

---

### Task 2.2: Tạo thông báo (4 points)
**Duration:** 2 ngày  
**Description:**
- Implement function createNotification
- Validate tiêu đề và nội dung
- Kiểm tra số lượng cư dân đang hoạt động
- Lưu thông báo và ghi audit log

**Acceptance Criteria:**
- ✓ Notification tạo thành công với dữ liệu hợp lệ
- ✓ `NO_RECIPIENT_FOUND` khi không có cư dân hoạt động
- ✓ Audit log ghi nhận hành động

---

### Task 2.3: Lấy danh sách thông báo với phân trang và tìm kiếm (4 points)
**Duration:** 2 ngày  
**Description:**
- Implement list API với pagination
- Support search title keyword
- Sắp xếp theo createdAt DESC
- Trả về total, page, size, items

**Acceptance Criteria:**
- ✓ Pagination hoạt động
- ✓ Tìm kiếm theo title hoạt động
- ✓ Trả về trạng thái empty khi không có dữ liệu

---

### Task 2.4: Xem chi tiết thông báo (3 points)
**Duration:** 1 ngày  
**Description:**
- Implement detail API
- Trả về id, title, content, createdAt, createdBy, recipientCount
- Xử lý lỗi NOTIFICATION_NOT_FOUND

**Acceptance Criteria:**
- ✓ Chi tiết hiển thị đầy đủ
- ✓ Lỗi 404 khi thông báo không tồn tại

---

### Task 2.5: Controller API và exception handling (2 points)
**Duration:** 1 ngày  
**Description:**
- Xây dựng REST controller
- Áp dụng authorization Admin
- Handle validation và business exceptions

**Acceptance Criteria:**
- ✓ Endpoints hoạt động
- ✓ Authorization được kiểm tra
- ✓ Lỗi trả về đúng định dạng

---

### Task 2.6: Audit log cho create và view (2 points)
**Duration:** 1 ngày  
**Description:**
- Mở rộng cơ chế audit log để ghi tạo và xem
- Đảm bảo lưu userId, action, timestamp
- Kiểm thử hành vi audit

**Acceptance Criteria:**
- ✓ Audit log ghi đầy đủ
- ✓ Review log dễ truy vấn

---

## Epic 3: Frontend Implementation (8 points)

### Task 3.1: Trang danh sách thông báo (3 points)
**Duration:** 2 ngày  
**Description:**
- Hiển thị list notifications
- Tìm kiếm theo title
- Phân trang và trạng thái empty
- Link tới chi tiết

**Acceptance Criteria:**
- ✓ List load đúng dữ liệu
- ✓ Search hoạt động
- ✓ Empty state hiển thị

---

### Task 3.2: Form tạo thông báo (3 points)
**Duration:** 2 ngày  
**Description:**
- Xây dựng form nhập title và content
- Validate client-side theo SPEC
- Hiển thị thông báo lỗi
- Submit tới API

**Acceptance Criteria:**
- ✓ Form tạo thông báo hoạt động
- ✓ Validation hiển thị đúng
- ✓ Người dùng nhận được phản hồi thành công

---

### Task 3.3: Trang chi tiết thông báo (2 points)
**Duration:** 1 ngày  
**Description:**
- Hiển thị id, title, content, createdAt, createdBy
- Điều hướng từ danh sách
- Hiển thị lỗi khi không tồn tại

**Acceptance Criteria:**
- ✓ Chi tiết hiển thị đầy đủ
- ✓ Back navigation hoạt động
- ✓ Lỗi 404 xử lý thân thiện

---

## Epic 4: Testing & Release (4 points)

### Task 4.1: Unit test backend (2 points)
**Duration:** 1-2 ngày  
**Description:**
- Viết unit test cho service và validation
- Kiểm tra các kịch bản tạo, tìm kiếm, chi tiết

**Acceptance Criteria:**
- ✓ Coverage backend đạt yêu cầu
- ✓ Kịch bản lỗi/đúng được bao phủ

---

### Task 4.2: Integration / UAT (2 points)
**Duration:** 1-2 ngày  
**Description:**
- Kiểm thử API end-to-end
- Test frontend flows: create, list, detail
- Xác nhận phân quyền Admin

**Acceptance Criteria:**
- ✓ Chức năng chạy end-to-end
- ✓ UAT pass với luồng người dùng chính
- ✓ Không còn lỗi nghiêm trọng

---

## Dependencies & Thứ tự
- Task 1.1 → Task 1.2 → Task 2.1-2.2
- Task 2.2 → Task 2.3, Task 2.4
- Task 2.5 → Task 3.1-3.3
- Task 4.1 → Task 4.2

- Create form with fields: title, content, recipientType, recipientIds
- Implement recipientType selector (ALL, FACILITY, ROOM)
- Implement multi-select for facility/room IDs
- Show recipient count
- Implement client-side validation
- Character counter for content (max 5000)
- Show validation errors

**Acceptance Criteria:**
- ✓ Form displays correctly
- ✓ Validation working
- ✓ Recipient type selection works
- ✓ Character counter showing
- ✓ API call correct
- ✓ Success/error handling

---

### Task 3.3: Notification Detail Page (3 points)
**Assignee:** Frontend Developer  
**Duration:** 1-2 days  
**Description:**
- Display notification info: ID, title, content, recipient type
- Display list of recipients
- Display metadata: created date, created by
- Add back button

**Acceptance Criteria:**
- ✓ Detail displays correctly
- ✓ Recipients list shown
- ✓ Responsive design

---

### Task 3.4: UI/UX Polish (2 points)
**Assignee:** Frontend Developer  
**Duration:** 1 day  
**Description:**
- Review UI consistency
- Test responsiveness
- Optimize performance
- Add accessibility features

**Acceptance Criteria:**
- ✓ Consistent styling
- ✓ Responsive
- ✓ Fast load times
- ✓ Accessible

---

## Epic 4: Testing & Quality (12 points)

### Task 4.1: Unit Tests (3 points)
**Assignee:** Backend Developer / QA  
**Duration:** 1-2 days  
**Description:**
- Test recipient resolution logic
- Test validation logic
- Test service methods
- Aim for >= 80% coverage

**Acceptance Criteria:**
- ✓ Coverage >= 80%
- ✓ All tests passing
- ✓ Edge cases covered

---

### Task 4.2: Integration Tests (3 points)
**Assignee:** QA Engineer  
**Duration:** 1-2 days  
**Description:**
- Test API endpoints
- Test create-to-detail workflows
- Test search functionality
- Test error scenarios

**Acceptance Criteria:**
- ✓ All APIs tested
- ✓ Workflows working
- ✓ Error handling verified

---

### Task 4.3: E2E & UAT (3 points)
**Assignee:** QA Engineer  
**Duration:** 1-2 days  
**Description:**
- Write E2E tests for user workflows
- Manual UAT
- Performance testing
- Load testing

**Acceptance Criteria:**
- ✓ E2E tests passing
- ✓ UAT completed
- ✓ Performance acceptable

---

### Task 4.4: Security Testing (3 points)
**Assignee:** QA Engineer  
**Duration:** 1 day  
**Description:**
- Test authorization (Admin only)
- Test input validation
- Test XSS prevention
- Test SQL injection prevention

**Acceptance Criteria:**
- ✓ All security tests passed
- ✓ No vulnerabilities
- ✓ Report documented

---

## Epic 5: Documentation & Deployment (4 points)

### Task 5.1: API Documentation (2 points)
**Assignee:** Tech Lead  
**Duration:** 1 day  
**Description:**
- Generate API documentation
- Document all endpoints
- Document error codes
- Add examples

**Acceptance Criteria:**
- ✓ Documentation complete
- ✓ All endpoints documented
- ✓ Examples clear

---

### Task 5.2: Deployment & Go-Live (2 points)
**Assignee:** DevOps / Tech Lead  
**Duration:** 1-2 days  
**Description:**
- Prepare environment
- Run migrations
- Deploy code
- Setup monitoring
- Verify go-live

**Acceptance Criteria:**
- ✓ Deployment successful
- ✓ No issues in production
- ✓ Monitoring active

---

## Summary by Sprint

| Sprint | Duration | Points | Focus |
|--------|----------|--------|-------|
| Sprint 1 | Wk 1 | 14 | DB design, entities, migrations |
| Sprint 2 | Wk 2-3 | 16 | Create, list, detail APIs |
| Sprint 3 | Wk 4 | 12 | Frontend implementation |
| Sprint 4 | Wk 5-8 | 10 | Testing, UAT, deployment |

---

## Critical Path

1. Task 1.1 (DB Schema) → Task 1.2 (Entities)
2. Task 1.2 → Task 2.1 (Recipient Logic)
3. Task 2.1 → Task 2.2 (Create)
4. Task 2.2 → Task 2.3 (List)
5. Task 2.3 → Task 3.1 (Frontend List)
6. Task 3.1-3.3 → Task 4.3 (E2E Testing)

---

## Effort Estimates by Role

| Role | Hours | Duration |
|------|-------|----------|
| Backend Developer | 120 | 3 weeks |
| Frontend Developer | 80 | 2 weeks |
| QA Engineer | 60 | 1.5 weeks |
| Database Admin | 20 | 2-3 days |
| Tech Lead | 40 | 1 week |
