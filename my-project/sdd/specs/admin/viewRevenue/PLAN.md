# PLAN: Kế hoạch Thực thi - Xem Báo Cáo Doanh Thu

**Status:** Planning  
**Date:** 2026-06-13  
**Priority:** High  
**Estimated Duration:** 3-4 weeks

---

## 1. Tổng quan

Tính năng cho phép Admin xem báo cáo doanh thu tổng hợp của toàn bộ cơ sở, lọc theo khoảng thời gian và hiển thị các chỉ số: tổng doanh thu, tổng hóa đơn, tổng đã thanh toán, tổng chưa thanh toán theo từng cơ sở.

Phạm vi:
- API read-only cho Admin: lọc theo `fromDate`/`toDate`
- Hỗ trợ phân trang khi số lượng cơ sở lớn
- Ghi nhận Audit Log cho thao tác xem

Không bao gồm: xuất Excel/PDF, biểu đồ nâng cao, dự báo.

---

## 2. Giai đoạn triển khai

### Giai đoạn 1 — Thiết kế & Chuẩn bị (0.5 tuần)
- Xác nhận yêu cầu từ `SPEC.md` và `CONTEXT.md`.
- Định nghĩa API contract: `GET /api/v1/reports/revenue` (params `fromDate`, `toDate`, `page`, `size`).
- Thiết kế response payload và mã lỗi (`INVALID_DATE_RANGE`, `UNAUTHORIZED`).
- Xác định nguồn dữ liệu (hóa đơn hợp lệ) và truy vấn cần thiết.

Deliverables: API contract, query plan, list các trường dữ liệu cần tính.

### Giai đoạn 2 — Backend Implementation (1.5 tuần)
- Implement service tổng hợp doanh thu:
  - Validate ngày (from <= to)
  - Lọc hóa đơn hợp lệ trong khoảng thời gian
  - Tính các tổng: `totalRevenue`, `totalInvoices`, `paidAmount`, `unpaidAmount`
  - Group by facility (facilityId, facilityName)
- Thêm pagination cho danh sách facilities
- Kiểm tra phân quyền (Admin only)
- Ghi Audit Log khi user xem báo cáo (userId, fromDate, toDate, timestamp)

Deliverables: API endpoint, unit tests cho các kịch bản tính toán.

### Giai đoạn 3 — Frontend (Admin UI) (0.5 tuần)
- Tạo trang báo cáo: form lọc `fromDate`/`toDate`, nút xem
- Hiển thị tổng hợp (summary) và bảng `facilities` với phân trang
- Hiển thị trạng thái `No data` khi không có dữ liệu
- Hiển thị lỗi khi `INVALID_DATE_RANGE` hoặc `UNAUTHORIZED`

Deliverables: Page UI, client-side validation, calls API

### Giai đoạn 4 — Testing & Hoàn thiện (0.5–1 tuần)
- Unit & integration tests cho backend
- E2E/UAT flow: kiểm tra phân quyền, date validation, empty state
- Hoàn thiện docs API và hướng dẫn sử dụng cho Admin

---

## 3. Yêu cầu kỹ thuật và ràng buộc
- Thời gian phản hồi tối đa: 500ms (P95) — tối ưu truy vấn và pagination
- Rate limit: 100 req/phút/người dùng
- Chỉ tính các hóa đơn hợp lệ (status = PAID|UNPAID theo business rule)
- Hỗ trợ phân trang khi nhiều facility
- Ghi Audit Log cho thao tác xem (userId, fromDate, toDate)

---

## 4. Dependencies
- Hệ thống Billing/Invoice để truy vấn hóa đơn hợp lệ
- Authentication/Authorization (role Admin)
- Database với chỉ số phù hợp cho truy vấn theo ngày và facility

---

## 5. Rủi ro & Biện pháp giảm thiểu
- Dữ liệu hóa đơn không sạch → Kiểm tra và làm sạch dữ liệu, thêm bộ lọc status
- Query lớn gây chậm → Sử dụng pagination, chỉ select các trường cần thiết, thêm index trên `invoiceDate` và `facilityId`
- Phân quyền sai → Kiểm tra role ở controller và service

---

## 6. Success Criteria
- API `GET /api/v1/reports/revenue` trả về tổng hợp chính xác trong khoảng thời gian
- Response có `totalRevenue` và `facilities[]` (facilityId, facilityName, revenue)
- Phân trang hoạt động khi nhiều cơ sở
- `INVALID_DATE_RANGE` trả về 400 khi fromDate > toDate
- Audit log ghi nhận hành động xem báo cáo

---

## 7. Next steps
- Triển khai backend service và API
- Tạo UI đơn giản cho Admin
- Viết unit & integration tests
