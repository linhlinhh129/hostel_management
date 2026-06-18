# TASKS: Phân chia Chi tiết - Xem Báo Cáo Doanh Thu

**Total Story Points:** ~24 points  
**Sprint Duration:** 2 weeks × 2 sprints = 4 weeks  
**Velocity:** ~12 points/sprint

---

## Epic 1: Thiết kế & Chuẩn bị (3 points)

### Task 1.1: Xác nhận yêu cầu và API contract (1 point)
- Duration: 0.5 ngày
- Output: `GET /api/v1/reports/revenue` contract, params, response schema, lỗi
- Acceptance: API contract đồng bộ với `SPEC.md` và `CONTEXT.md`

### Task 1.2: Lập kế hoạch truy vấn dữ liệu (2 points)
- Duration: 1 ngày
- Output: Query plan, danh sách trường cần select, index cần tạo
- Acceptance: Query tối ưu cho response < 500ms với tập dữ liệu mẫu

---

## Epic 2: Backend Implementation (12 points)

### Task 2.1: Implement service tổng hợp doanh thu (6 points)
- Duration: 3 ngày
- Description:
  - Validate `fromDate`/`toDate` (trả `INVALID_DATE_RANGE` nếu từ > đến)
  - Lấy các hóa đơn hợp lệ trong khoảng thời gian
  - Tính `totalRevenue`, `totalInvoices`, `paidAmount`, `unpaidAmount`
  - Group by facility: `facilityId`, `facilityName`, `revenue`
  - Hỗ trợ pagination cho danh sách facility
- Acceptance:
  - Kết quả chính xác với bộ dữ liệu kiểm thử
  - Thời gian phản hồi phù hợp

### Task 2.2: Controller và API endpoint (3 points)
- Duration: 1-2 ngày
- Description:
  - Tạo endpoint `GET /api/v1/reports/revenue`
  - Áp dụng authentication & Admin authorization
  - Map query params và trả lỗi hợp lệ
- Acceptance:
  - Endpoint trả 200/400/401 theo SPEC

### Task 2.3: Audit Log cho thao tác xem (3 points)
- Duration: 1 ngày
- Description:
  - Ghi log: userId, fromDate, toDate, timestamp
  - Lưu entry vào bảng audit hoặc service logging
- Acceptance:
  - Audit log có thể truy vấn và hiển thị thông tin cần thiết

---

## Epic 3: Frontend (Admin UI) (6 points)

### Task 3.1: UI Filter & Summary (3 points)
- Duration: 2 ngày
- Description:
  - Form lọc `fromDate` và `toDate`
  - Hiển thị summary `totalRevenue` và tổng hóa đơn
  - Client-side validate date range
- Acceptance:
  - Form gửi request đúng param
  - Summary hiển thị chính xác

### Task 3.2: Bảng facilities và pagination (3 points)
- Duration: 2 ngày
- Description:
  - Hiển thị danh sách `facilities[]` với các cột: `facilityId`, `facilityName`, `revenue`
  - Phân trang theo `page`/`size`
  - Hiển thị trạng thái empty khi không có dữ liệu
- Acceptance:
  - Pagination hoạt động
  - Empty state hiển thị

---

## Epic 4: Testing & Hoàn thiện (3 points)

### Task 4.1: Unit tests backend (2 points)
- Duration: 1-2 ngày
- Description:
  - Test validation date
  - Test tính toán tổng và group by
- Acceptance: Tests cover main scenarios

### Task 4.2: Integration/UAT (1 point)
- Duration: 1 ngày
- Description:
  - E2E flow: Admin truy cập, lọc, xem summary và bảng
  - Kiểm tra audit log
- Acceptance: UAT pass với luồng chính

---

## Dependencies & Thứ tự thực hiện
- Task 1.1 → Task 1.2 → Task 2.1
- Task 2.1 → Task 2.2, Task 2.3
- Task 2.2 → Task 3.1, Task 3.2
- Task 4.1 → Task 4.2

---

## Notes
- Nếu hệ thống có sẵn service báo cáo dùng chung, tái sử dụng để giảm thời gian.
- Nếu dataset lớn, cân nhắc chạy pre-aggregation hoặc materialized view cho báo cáo thường xuyên.
