# PLAN: Kế hoạch Thực thi Quản lý Thông báo

**Status:** Planning  
**Date:** 2026-06-13  
**Priority:** High  
**Estimated Duration:** 5-6 weeks

---

## 1. Tổng quan Giải pháp

Tính năng Quản lý Thông báo cho phép Admin tạo, lưu và xem các thông báo gửi đến toàn bộ cư dân đang hoạt động trong hệ thống.

Phạm vi feature:
- Admin tạo thông báo mới
- Hiển thị danh sách thông báo với tìm kiếm và phân trang
- Xem chi tiết thông báo
- Ghi nhận Audit Log cho thao tác tạo và xem thông báo
- Chỉ Admin có quyền truy cập

Ngoại trừ:
- Không hỗ trợ chỉnh sửa hoặc xóa thông báo
- Không hỗ trợ lập lịch gửi
- Không gửi email/SMS/Push
- Không hỗ trợ đánh dấu đã đọc

**Kiến trúc:**
- Backend API: create/list/detail notification
- Database: lưu Notification và liên kết audit
- Frontend UI: form tạo, danh sách, chi tiết
- Bảo mật: kiểm tra quyền Admin

---

## 2. Giai đoạn Thực thi

### Giai đoạn 1: Thiết kế & Chuẩn bị (Tuần 1)

**Mục tiêu:** Xác định yêu cầu, thiết kế dữ liệu, xác thực quyền

**Công việc:**
- Rà soát SPEC và CONTEXT để xác định yêu cầu chi tiết
- Thiết kế schema Notification, audit log
- Xác định API contract cho POST /api/v1/notifications, GET /api/v1/notifications, GET /api/v1/notifications/{id}
- Định nghĩa validation, lỗi business và mã lỗi

**Deliverables:**
- Thiết kế ERD
- API contract rõ ràng
- Bảng lỗi và quyền truy cập

---

### Giai đoạn 2: Backend Development (Tuần 2-3)

**Mục tiêu:** Triển khai chức năng backend và business logic

**Công việc:**
- Tạo entity và repository Notification
- Triển khai service tạo thông báo, lưu notification và ghi audit
- Triển khai service lấy danh sách với search + pagination
- Triển khai service lấy chi tiết thông báo
- Áp dụng validation: tiêu đề, nội dung, độ dài, recipient
- Áp dụng kiểm tra quyền Admin
- Ghi log cho thao tác tạo và xem

**Key Features:**
- Create notification
- List notifications with search and pagination
- View notification detail
- Admin-only access
- Audit logging

---

### Giai đoạn 3: Frontend Development (Tuần 4)

**Mục tiêu:** Build UI cho Admin quản lý thông báo

**Công việc:**
- Xây dựng danh sách thông báo với tìm kiếm và phân trang
- Xây dựng form tạo thông báo với validation client-side
- Xây dựng trang chi tiết thông báo
- Hiển thị trạng thái “Không có dữ liệu” khi không có kết quả
- Xử lý lỗi và thông báo người dùng

---

### Giai đoạn 4: Testing & Hoàn thiện (Tuần 5-6)

**Mục tiêu:** Bảo đảm chất lượng và bàn giao

**Công việc:**
- Viết unit test cho backend service và API
- Viết integration test cho controller
- Kiểm thử chức năng frontend và kịch bản người dùng
- Chạy UAT cho luồng tạo, tìm kiếm, xem chi tiết
- Hoàn thiện tài liệu feature và release notes

---

## 3. Technical Considerations

### Validation
- Title không được để trống
- Content không được để trống, tối đa 5000 ký tự
- Nếu không có cư dân đang hoạt động, trả về lỗi `NO_RECIPIENT_FOUND`
- Chỉ Admin mới được phép truy cập và thao tác

### Performance
- Giữ response time < 500ms (P95)
- Hỗ trợ phân trang `page` + `size`
- Tìm kiếm theo title với keyword

### Audit Logging
- Ghi lại hành động tạo thông báo
- Ghi lại hành động xem chi tiết/danh sách nếu cần
- Lưu userId, thời điểm, hành động

### Security
- Kiểm tra role Admin trước khi cho phép POST và truy cập trang quản lý
- Tránh tiết lộ dữ liệu cho người không có quyền

---

## 4. Dependencies

### Nội bộ
- Authentication/Authorization service để xác thực role Admin
- Thành phần Tenant/Resident để xác định số lượng cư dân đang hoạt động
- Cơ sở dữ liệu chung của hệ thống

### Blocking Issues
- Nếu chưa có sẵn hệ thống audit logging, cần triển khai song song
- Nếu chưa có API kiểm tra quyền Admin, cần bổ sung trước

---

## 5. Risk Management

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Dữ liệu cư dân hoạt động không chính xác | High | Xác thực lại nguồn dữ liệu tenant, test với dữ liệu thật |
| Response time tăng với danh sách lớn | Medium | Sử dụng pagination, index title, giới hạn select |
| Validation không đủ chặt | Medium | Viết unit test và kiểm thử kịch bản lỗi |
| Phân quyền không đúng | High | Thực hiện kiểm tra role ở nhiều lớp (service + controller) |

---

## 6. Success Criteria

- ✓ Admin có thể tạo thông báo mới thành công
- ✓ Thông báo được lưu và hiển thị trong danh sách
- ✓ Tìm kiếm theo tiêu đề trả về kết quả phù hợp
- ✓ Chi tiết thông báo hiển thị đầy đủ: mã, tiêu đề, nội dung, ngày tạo, người tạo
- ✓ Phân trang hoạt động và hiển thị trạng thái khi không có dữ liệu
- ✓ Audit log ghi nhận thao tác tạo và xem
- ✓ Chỉ Admin có thể truy cập chức năng
- ✓ Backend và frontend được kiểm thử đầy đủ
