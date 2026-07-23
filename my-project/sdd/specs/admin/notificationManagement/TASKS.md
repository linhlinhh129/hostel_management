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

### Task 1.2: Thiết kế schema Notification (3 points)
**Duration:** 1-2 ngày  
**Description:**
- Thiết kế bảng Notification (id, title, content, createdBy, createdAt, recipientCount)
- Định nghĩa index cho tìm kiếm title và phân trang

**Acceptance Criteria:**
- ✓ Schema đầy đủ cho tính năng
- ✓ Index được xác định

---

### Task 1.3: Validation và phân quyền (3 points)
**Duration:** 1 ngày  
**Description:**
- Định nghĩa luật validate title/content
- Xác định lỗi `VALIDATION_ERROR`, `NOTIFICATION_NOT_FOUND`
- Xác định cơ chế kiểm tra quyền Admin

**Acceptance Criteria:**
- ✓ Luật validate rõ ràng
- ✓ Lỗi mapped chính xác
- ✓ Phân quyền Admin rõ ràng

---

## Epic 2: Backend Implementation (14 points)

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
- Lưu thông báo

**Acceptance Criteria:**
- ✓ Notification tạo thành công với dữ liệu hợp lệ

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

