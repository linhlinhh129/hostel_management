# PLAN: Kế hoạch Thực thi Quản lý Thông báo (Tenant)

**Status:** Planning  
**Date:** 2026-06-21  
**Priority:** Medium  
**Estimated Duration:** 4-5 weeks  
**Target Release:** Sprint 2

---

## 1. Executive Summary

### Problem Statement
Người thuê hiện không có một nơi tập trung để xem lại các thông báo từ Chủ nhà hoặc Ban quản lý. Các thông tin quan trọng như lịch bảo trì, thông báo thu tiền phòng, hoặc các thông báo khẩn cấp có thể bị bỏ lỡ khi chỉ được gửi qua các kênh bên ngoài.

### Solution Overview
Triển khai tính năng **Notification Management** cho phép Tenant:
- Xem danh sách thông báo theo thời gian mới nhất trước
- Xem chi tiết nội dung từng thông báo
- Chỉ xem các thông báo được gửi cho mình hoặc thông báo công khai
- Truy cập thông báo bất cứ lúc nào qua hệ thống tập trung

### Architecture
```
Frontend UI          →  Backend API (REST)          →  Database
┌─────────────────┐    ┌──────────────────┐         ┌────────────┐
│ List View       │───→│ GET /notifications│         │ Notification
│ Detail View     │    │ with permission   │────────→│ Table
│ Empty State     │←───│ filtering         │         │
│ Error Handling  │    │                  │         │
└─────────────────┘    └──────────────────┘         └────────────┘
```

---

## 2. Key Features & Requirements

### Functional Requirements (FR01-FR11)
- **FR01:** Hiển thị danh sách thông báo
- **FR02-03:** Hiển thị thông tin tóm tắt (tiêu đề, ngày, giờ), sắp xếp mới nhất trước
- **FR04:** Thông báo mới ở đầu danh sách
- **FR05-06:** Xem chi tiết với đầy đủ thông tin
- **FR07:** Nút Back quay lại danh sách
- **FR08:** Empty State khi không có thông báo
- **FR09:** Error handling & Retry
- **FR10:** Redirect Login nếu chưa đăng nhập
- **FR11:** HTTP 404 nếu notification không tồn tại/no access

### Non-Functional Requirements
| Yêu cầu | Chỉ số |
|---------|--------|
| **Performance** | API response < 300ms (P95) |
| **Availability** | Dịch vụ 99.9% uptime |
| **Security** | Xác thực + kiểm tra quyền truy cập |
| **Pagination** | 20 notifications/page (default) |

### Notification Visibility Rules
1. **Public Notifications:** Gửi cho tất cả tenant → tất cả tenant xem được
2. **Private Notifications:** Gửi cho specific tenant(s) → chỉ recipients xem được
3. **Access Control:** Tenant chỉ xem thông báo của chính mình, không được xem của người khác

---

## 3. Implementation Phases

### Phase 1: Design & Specification (Tuần 1)
**Duration:** 3-4 ngày  
**Deliverables:**
- ✓ API contract finalization
- ✓ Permission model design
- ✓ Database query optimization plan
- ✓ Frontend wireframe review

**Tasks:**
1. Define DTO structures (request/response)
2. Design notification filtering algorithm
3. Plan database indexes
4. Prepare test data (scenarios)

### Phase 2: Backend Implementation (Tuần 2-3)
**Duration:** 5-7 ngày  
**Deliverables:**
- ✓ Controller Servlets (`TenantNotificationListServlet`, `TenantNotificationDetailServlet`)
- ✓ Query Optimization & Visibility Filter (Public vs Specific Tenant)
- ✓ Unit & Integration tests ≥80% coverage

**Key Components:**
1. `NotificationDAO` (query public + tenant-specific notifications)
2. `TenantNotificationListServlet` (`/tenant/notifications`)
3. `TenantNotificationDetailServlet` (`/tenant/notification-detail`)
4. Session Permission Validator
5. Error Handler (Forward 403, 404, 500)

### Phase 3: Frontend & Views Implementation (Tuần 3-4)
**Duration:** 6-8 ngày  
**Deliverables:**
- ✓ JSP Views completed (`/WEB-INF/views/tenant/notification-list.jsp`, `notification-detail.jsp`)
- ✓ Responsive design verified

5. Deployment & monitoring

---

## 4. Technical Design Decisions

### Servlet Endpoints & Controller Contract
```http
GET /tenant/notifications?page=1&pageSize=10
  → TenantNotificationListServlet (Forward: /WEB-INF/views/tenant/notification-list.jsp)

GET /tenant/notification-detail?id={notificationId}
  → TenantNotificationDetailServlet (Forward: /WEB-INF/views/tenant/notification-detail.jsp)
```

### View Data Scope Attributes
```jsp
// Notification List Scope Attribute:
request.setAttribute("notificationPage", NotificationPageDTO)

// Notification Detail Scope Attribute:
request.setAttribute("notification", NotificationDTO)
```

### Error Handling Strategy
| Error | Status | Action |
|-------|--------|--------|
| Chưa đăng nhập | 401 | Redirect Login |
| Không có quyền | 403 | Show error message |
| Không tìm thấy | 404 | Show not found page |
| Server error | 500 | Show error + Retry |

### Notification Visibility Filter
```
SELECT * FROM notifications
WHERE tenantId IS NULL (Public)
   OR tenantId = ? (For this tenant)
ORDER BY createdAt DESC
LIMIT 20 OFFSET ?
```

---

## 5. Success Criteria

### Functional Success
- ✅ Tenant xem được danh sách thông báo
- ✅ Danh sách sắp xếp mới nhất trước
- ✅ Xem được chi tiết từng thông báo
- ✅ Visibility logic hoạt động chính xác
- ✅ Empty state hiển thị khi không có thông báo
- ✅ Error handling & Retry hoạt động

### Non-Functional Success
- ✅ API response time < 300ms (P95)
- ✅ Code coverage ≥ 80%
- ✅ No security vulnerabilities
- ✅ Mobile responsive

### Business Success
- ✅ User acceptance testing passed
- ✅ Product owner signed off
- ✅ Deployment to production successful
- ✅ Zero critical bugs in production

---

## 6. Risk Assessment & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| Performance degradation | Medium | High | Add database indexes, optimize queries |
| Permission bypass | Low | Critical | Thorough security testing |
| High volume notifications | Medium | Medium | Implement pagination, caching |
| API contract changes | Low | Medium | Finalize spec early, API versioning |

---

## 7. Team & Resource

### Team Composition
- **Backend Developer:** 1 person (5-7 days)
- **Frontend Developer:** 1 person (6-8 days)
- **QA/Tester:** 1 person (3-4 days)
- **Product Owner:** Review & sign-off
- **Tech Lead:** Architecture review, code review

### Dependencies
- Backend APIs must be completed before frontend development
- Database schema must be available
- Authentication service already implemented
- Existing notification table in database

---

## 8. Timeline & Milestones

```
Week 1  : Design & Specification
Week 2-3: Backend + Frontend Development
Week 4  : Testing, UAT, Deployment
        
Total: 4-5 weeks
```

### Key Milestones
- **M1 (End of Week 1):** API contract & design approved
- **M2 (End of Week 2):** Backend completed & tested
- **M3 (End of Week 3):** Frontend completed & integrated
- **M4 (End of Week 4):** UAT completed & deployed

---

## 9. Definition of Done

For each task to be considered complete:
- ✅ Code written & reviewed
- ✅ Unit tests passed (≥80% coverage)
- ✅ Integration tests passed
- ✅ Code follows project standards
- ✅ Documentation updated
- ✅ No critical/blocker issues
- ✅ Product owner approved
