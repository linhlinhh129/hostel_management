# PLAN: Kế hoạch Thực thi Quản lý Yêu cầu (Tenant)

**Status:** Planning  
**Date:** 2026-06-21  
**Priority:** High  
**Estimated Duration:** 8-10 weeks  
**Target Release:** Sprint 3-4

---

## 1. Executive Summary

### Problem Statement
Người thuê cần một kênh chính thức để gửi các yêu cầu hỗ trợ, phản ánh sự cố hoặc đề xuất đến Ban quản lý. Hiện nay, nhiều tương tác vẫn diễn ra qua điện thoại hoặc tin nhắn, gây khó khăn trong việc theo dõi, quản lý và đối soát.

### Solution Overview
Xây dựng tính năng **Request Management** cho phép Tenant:
- Tạo và gửi yêu cầu hỗ trợ trực tuyến
- Xem danh sách yêu cầu đã gửi
- Xem chi tiết từng yêu cầu và trạng thái xử lý
- Đính kèm hình ảnh minh họa
- Bảo đảm cách ly dữ liệu giữa các Tenant

### Architecture
- Backend API: Create request, list requests, get request detail
- Storage: Request entity + attachment metadata
- Authorization: Tenant-only access to own requests
- Frontend: Request form, list view, detail view
- Audit: Lưu lịch sử trạng thái

---

## 2. Key Features & Requirements

### Business Goals
- Chuẩn hóa quy trình tiếp nhận yêu cầu từ Tenant
- Tăng tính minh bạch cho Tenant khi theo dõi tiến độ
- Nâng cao hiệu quả vận hành của Ban quản lý
- Đảm bảo dữ liệu được truy vết và kiểm toán được

### Functional Requirements
- **AC01:** Hiển thị danh sách yêu cầu do Tenant tạo
- **AC02:** Hiển thị tóm tắt yêu cầu (Mã, loại, tiêu đề, ngày tạo, trạng thái)
- **AC03:** Sắp xếp theo ngày tạo giảm dần
- **AC04:** Tạo yêu cầu mới trả HTTP 201 với trạng thái PENDING
- **AC05:** Đính kèm ảnh JPG/JPEG/PNG hợp lệ và lưu liên kết
- **AC06:** Xem chi tiết yêu cầu với toàn bộ thông tin
- **AC07:** Hiển thị trạng thái xử lý hiện tại
- **AC08:** Thiếu tiêu đề/nội dung trả HTTP 400 (REQ_001)
- **AC09:** Loại yêu cầu không tồn tại trả HTTP 400 (REQ_002)
- **AC10:** Tệp đính kèm không hợp lệ trả HTTP 400 (REQ_003)
- **AC11:** Chưa đăng nhập trả HTTP 401
- **AC12:** Tenant chỉ được xem yêu cầu của chính mình

### Non-functional Requirements
- API response < 500ms (P95)
- Uptime ≥ 99.9%
- JWT authentication + HTTPS only
- Tenant data isolation
- Support 10,000+ requests

### Security & Privacy
- JWT authentication bắt buộc
- Tenant chỉ truy cập dữ liệu của chính mình
- Soft delete filter: `deleted_at IS NULL`
- Tệp đính kèm validate định dạng và kích thước
- Audit logs cho trạng thái và truy cập

---

## 3. Implementation Phases

### Phase 1: Design & Specification (Week 1)
**Objective:** Finalize architecture, API contracts, validation rules

**Tasks:**
- Confirm request categories
- Define Request/Attachment data models
- Define API contracts and error codes
- Plan attachment handling and file storage
- Define tenant authorization and data isolation

### Phase 2: Backend Core (Weeks 2-4)
**Objective:** Implement request lifecycle services

**Tasks:**
- Create request domain model
- Implement create request service
- Implement list requests service
- Implement get request detail service
- Implement attachment metadata handling
- Implement request validation and authorization
- Implement status history records

### Phase 3: API & Integration (Weeks 4-5)
**Objective:** Expose endpoints and secure them

**Tasks:**
- Build REST endpoints for create/list/detail
- Implement request file upload handling
- Implement consistent error responses
- Document APIs
- Add pagination, filters, sorting

### Phase 4: Frontend Implementation (Weeks 5-7)
**Objective:** Build Tenant-facing UI

**Tasks:**
- Request creation form
- Request list view
- Request detail page
- Attachment preview/download
- Error/empty states

### Phase 5: Testing & Deployment (Weeks 7-8)
**Objective:** Validate quality and deploy

**Tasks:**
- Unit tests for backend and frontend
- Integration tests for API and file flows
- E2E tests for create → list → detail
- UAT with product owner
- Deploy to staging/production

---

## 4. Technical Design Highlights

### Data Model
- `requests`: requestId, tenantId, categoryId, title, content, status, createdAt, updatedAt, deletedAt
- `request_attachments`: attachmentId, requestId, fileUrl, fileName, fileType, fileSize, createdAt
- `request_status_history`: historyId, requestId, oldStatus, newStatus, changedBy, changedAt

### Request Statuses
- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`
- `REJECTED`

### API Endpoints
- `GET /api/v1/tenant/requests`
- `POST /api/v1/tenant/requests`
- `GET /api/v1/tenant/requests/{requestId}`
- `GET /api/v1/tenant/requests/{requestId}/attachments/{attachmentId}`

### Validation Rules
- `categoryId` must exist
- `title` required, non-empty
- `content` required, non-empty
- Attachment file type JPG/JPEG/PNG
- Attachment max size 5MB
- Tenant must own request
- Soft deleted records excluded

---

## 5. Success Criteria

- Tenant có thể tạo yêu cầu mới và nhận HTTP 201
- Tenant xem được danh sách và chi tiết yêu cầu của mình
- Danh sách sắp xếp theo ngày tạo giảm dần
- Attachment upload và liên kết hoạt động
- Truy cập trái phép bị chặn (401/403)
- Thiếu dữ liệu trả lỗi đúng mã
- API hiệu năng < 500ms P95
- Test coverage ≥ 80%
- UAT passed

---

## 6. Risk Assessment & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| Attachment upload failure | Medium | Medium | Validate file type/size client & server |
| Unauthorized access | Low | High | Enforce tenant filter in all queries |
| Invalid category | Medium | Medium | Validate categoryId early, use reference table |
| Soft delete leak | Low | High | Always add `deleted_at IS NULL` to queries |
| Performance at scale | Medium | Medium | Add pagination, indexing, cache if needed |

---

## 7. Team & Dependencies

### Team
- Backend Developer
- Frontend Developer
- QA/Tester
- Product Owner
- DevOps (storage/file handling)

### Dependencies
- Request categories configured
- File storage service available
- Authentication service with tenant context
- Existing tenant-room relation

---

## 8. Timeline

- **Week 1:** Design & requirements
- **Week 2-4:** Backend implementation
- **Week 4-5:** API development & documentation
- **Week 5-7:** Frontend implementation
- **Week 7-8:** Testing, UAT, deployment

**Total:** 8-10 weeks

---

## 9. Definition of Done

- Code reviewed and merged
- Unit tests passing ≥ 80% coverage
- Integration tests passing
- API docs updated
- No critical bugs
- UAT sign-off obtained
- Deployment checklist completed

