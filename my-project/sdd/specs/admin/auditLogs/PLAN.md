# PLAN: Kế hoạch Thực thi Xem Nhật ký Hệ thống (Audit Log)

**Status:** Planning  
**Date:** 2026-06-19  
**Priority:** Medium  
**Estimated Duration:** 4-5 tuần

---

## 1. Tổng quan Giải pháp

Feature Xem Nhật ký Hệ thống cho phép Admin truy vết toàn bộ hành động quan trọng được ghi nhận trong hệ thống quản lý nhà trọ.

Phạm vi feature:
- Xem danh sách Audit Log với lọc và phân trang
- Lọc theo entityType, action, createdBy, fromDate, toDate
- Danh sách chỉ đọc — không cho phép tạo, sửa, xóa
- Chỉ Admin có quyền truy cập

Ngoại trừ:
- Không hỗ trợ tạo Audit Log thủ công
- Không hỗ trợ chỉnh sửa hoặc xóa bản ghi
- Không xuất Excel/PDF
- Không theo dõi real-time

**Kiến trúc:**
- Backend API: GET /api/v1/audit-logs với query filter + pagination
- Database: Bảng `audit_log` đã tồn tại, được ghi từ các module nghiệp vụ khác
- Frontend UI: Danh sách với bộ lọc, phân trang
- Bảo mật: Kiểm tra role ADMIN

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Xác định schema, API contract, validation

**Công việc:**
- Rà soát SPEC và CONTEXT để xác định yêu cầu chi tiết
- Kiểm tra schema bảng `audit_log` hiện có trong database
- Xác định các giá trị hợp lệ của `entityType` và `action`
- Định nghĩa API contract cho `GET /api/v1/audit-logs`
- Định nghĩa các mã lỗi: `INVALID_FILTER`, `INVALID_DATE_RANGE`, `FORBIDDEN`, `UNAUTHORIZED`

**Deliverables:**
- API contract rõ ràng
- Bảng giá trị hợp lệ của entityType và action
- Bảng lỗi và điều kiện kích hoạt

---

### Giai đoạn 2: Backend Development (Tuần 2-3)

**Mục tiêu:** Triển khai backend API và business logic

**Công việc:**
- Tạo repository truy vấn `audit_log` với dynamic filter
- Triển khai service lấy danh sách với filter + pagination
- Validate tham số lọc (fromDate/toDate, entityType, action)
- Kiểm tra quyền ADMIN trước khi xử lý
- Chuẩn hóa response theo format chuẩn

**Key Features:**
- Danh sách audit log có phân trang
- Lọc theo entityType, action, createdBy, fromDate, toDate
- Xử lý lỗi validation tham số

---

### Giai đoạn 3: Frontend Development (Tuần 3-4)

**Mục tiêu:** Xây dựng UI cho Admin xem nhật ký

**Công việc:**
- Xây dựng trang danh sách Audit Log với bảng dữ liệu
- Thêm bộ lọc: entityType, action, createdBy, fromDate, toDate
- Hỗ trợ phân trang và hiển thị trạng thái empty
- Hiển thị oldValue / newValue rõ ràng (dạng JSON diff hoặc text)
- Xử lý lỗi và hiển thị thông báo phù hợp

---

### Giai đoạn 4: Testing & Hoàn thiện (Tuần 5)

**Mục tiêu:** Đảm bảo chất lượng và bàn giao

**Công việc:**
- Viết unit test cho service và validation
- Viết integration test cho controller
- Kiểm thử các kịch bản lọc, phân quyền, lỗi
- UAT với Admin
- Hoàn thiện tài liệu feature

---

## 3. Key Technical Considerations

### Dynamic Query / Filter
- Sử dụng Specification hoặc JPQL động để build query theo filter
- Tránh N+1 query khi load danh sách
- Index trên các cột: `entity_type`, `action`, `created_by`, `created_at`

### Pagination
- Page 0-based, mặc định size=10
- Sắp xếp mặc định: `createdAt DESC`

### oldValue / newValue
- Lưu dạng JSON string trong database
- Frontend parse và hiển thị dạng diff hoặc pre-formatted JSON

### Authorization
- Kiểm tra role ADMIN ở cả controller và service layer
- Trả về 401 nếu chưa đăng nhập, 403 nếu không đủ quyền

---

## 4. Dependencies

### Nội bộ
- Bảng `audit_log` trong database (được ghi bởi các module nghiệp vụ)
- Authentication/Authorization service để xác thực role ADMIN
- Các module ghi log: personnelManagement, facilityManagement, notificationManagement, contractManagement, v.v.

### Blocking Issues
- Bảng `audit_log` phải tồn tại và có dữ liệu trước khi test
- Nếu chưa có cơ chế ghi log tập trung, cần triển khai trước

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Bảng audit_log chưa có index phù hợp | High | Thêm index trên entity_type, action, created_by, created_at |
| Query chậm khi dữ liệu lớn | Medium | Sử dụng pagination bắt buộc, giới hạn date range hợp lý |
| Phân quyền không chặt | High | Kiểm tra role ở nhiều lớp, viết test bảo mật |
| oldValue/newValue quá lớn | Low | Giới hạn độ dài hiển thị trên UI, truncate nếu cần |

---

## 6. Success Criteria

- ✓ Admin xem được danh sách audit log với phân trang
- ✓ Lọc theo entityType, action, createdBy, fromDate/toDate hoạt động đúng
- ✓ Trả về empty result khi không có dữ liệu khớp filter
- ✓ Validation lỗi khi fromDate > toDate
- ✓ Chỉ Admin truy cập được, 401/403 cho các trường hợp khác
- ✓ Response time < 500ms (P95)
- ✓ Audit Log không thể sửa/xóa qua API này
- ✓ UAT passed

---

## 7. Timeline

- **Tuần 1:** Thiết kế & chuẩn bị
- **Tuần 2-3:** Backend development
- **Tuần 3-4:** Frontend development
- **Tuần 5:** Testing & deployment

**Total:** 5 tuần

---

## 8. Constraints

- Chỉ Admin được truy cập
- Read-only — không tạo/sửa/xóa qua API
- Phân trang bắt buộc
- Sắp xếp mặc định createdAt DESC
