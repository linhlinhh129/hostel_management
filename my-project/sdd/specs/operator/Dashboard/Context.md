# CONTEXT.md [Dashboard Nhân viên vận hành]

**Người viết:** [Antigravity]  
**Ngày:** 2026-06-20

---

## 1. PROBLEM STATEMENT

### Bối cảnh
Nhân viên vận hành (Operator) hàng ngày phải xử lý nhiều luồng công việc khác nhau: tiếp nhận và xử lý sự cố (Request), chủ động đi tuần tra và báo cáo sự cố tại hiện trường (Incident Report), và chốt số điện nước hàng tháng (Meter Reading). 

### Nỗi đau (Pain Points)
- **Thiếu góc nhìn tổng quan:** Nhân viên phải chuyển đổi qua lại giữa nhiều màn hình danh sách (Danh sách yêu cầu, Danh sách điện nước, Lịch sử báo cáo) để biết mình cần làm gì tiếp theo trong ngày.
- **Rủi ro quên việc:** Khi không có màn hình tổng hợp để nhắc nhở các công việc "Đến hạn" hoặc "Quá hạn" (như lịch hẹn sửa chữa, phòng chưa chốt số điện nước), nhân viên dễ bị sót việc, ảnh hưởng trực tiếp đến KPI và tiến độ chung của toàn hệ thống.
- **Thao tác chậm:** Mỗi khi cần thao tác nhanh như "Báo cáo sự cố mới" hoặc "Ghi điện nước", nhân viên phải điều hướng qua nhiều cấp menu thay vì có một lối tắt (Shortcut) trực tiếp.

---

## 2. DOMAIN KNOWLEDGE

### Dashboard (Bảng điều khiển)
Là màn hình chính (trang chủ) xuất hiện ngay sau khi nhân viên đăng nhập. Màn hình này đóng vai trò như một trung tâm điều phối cá nhân, cung cấp số liệu tổng quan và các phím tắt nhanh tới các chức năng nghiệp vụ cốt lõi.

### KPI Vận hành (Operations KPI)
Các chỉ số đo lường hiệu suất của cá nhân nhân viên, bao gồm:
- Tỉ lệ giải quyết yêu cầu sửa chữa (Completed vs Pending).
- Tiến độ hoàn thành ghi số điện nước của tháng hiện tại.

### Quick Actions (Thao tác nhanh)
Các lối tắt (shortcuts) được đặt ở vị trí nổi bật nhất trên Dashboard giúp người dùng nhảy trực tiếp vào luồng thao tác tạo mới hoặc cập nhật mà không cần qua danh sách trung gian.

---

## 3. STAKEHOLDERS

### Nhân viên vận hành (End-user trực tiếp)
Sử dụng Dashboard như công cụ làm việc đầu tiên mỗi sáng để nắm bắt số lượng công việc còn tồn đọng, các lịch hẹn trong ngày và truy cập nhanh vào các tính năng báo cáo/cập nhật.

### Quản lý nhà trọ (Manager)
Người hưởng lợi gián tiếp. Việc Dashboard giúp nhắc việc cho nhân viên vận hành sẽ làm giảm tình trạng trễ hẹn, tăng hiệu suất xử lý công việc và đảm bảo dữ liệu chốt số điện nước đầu tháng luôn đúng hạn.

---

## 4. CONSTRAINTS

### Ràng buộc về dữ liệu (Data & Performance)
- **Aggregated Data:** Dữ liệu hiển thị trên Dashboard là dữ liệu tổng hợp (COUNT, SUM) từ nhiều bảng khác nhau. 
- **Tốc độ tải trang:** Do gọi nhiều API thống kê, tổng thời gian render Dashboard không được vượt quá 1 giây để đảm bảo trải nghiệm liền mạch.
- **Chỉ đọc (Read-only):** Dashboard chỉ đóng vai trò hiển thị số liệu và điều hướng. Tuyệt đối không cho phép thực hiện sửa đổi (Update/Delete) trực tiếp dữ liệu tại đây.

### Ràng buộc về UI/UX
- Giao diện cần tối ưu hóa cho thiết bị di động (Mobile-first) vì nhân viên vận hành thường xuyên xem trên điện thoại trong lúc đi lại tại tòa nhà.

---

## 5. ASSUMPTIONS

- **Giả định 1:** Dữ liệu thẻ (Cards) đếm số lượng "Yêu cầu chờ xử lý" chỉ tính các yêu cầu được giao đích danh cho nhân viên đang đăng nhập.
- **Giả định 2:** Dữ liệu thẻ "Tiến độ điện nước" mặc định chỉ tính tổng số lượng phòng thuộc các Cơ sở (Facilities) mà nhân viên này được phân quyền quản lý trong tháng hiện tại.
- **Giả định 3:** Dashboard được thiết lập làm màn hình mặc định (Landing Page) ngay sau khi nhân viên vận hành đăng nhập thành công.

---

## 6. OPEN QUESTIONS

### 1. Auto-refresh (Tự động làm mới)
- Dashboard có cần cơ chế tự động gọi lại API để làm mới số liệu (ví dụ: mỗi 5 phút) để cập nhật số lượng yêu cầu mới được giao không? Hay chỉ làm mới khi người dùng F5 / vuốt để làm mới (Pull-to-refresh)?

### 2. Gamification & KPI Charts
- Có cần bổ sung các biểu đồ trực quan (Pie chart thể hiện tỉ lệ hoàn thành điện nước, Bar chart thể hiện số sự cố theo ngày) hay chỉ cần dùng các thẻ số (Number Cards) đơn giản là đủ?
- Có áp dụng cơ chế thưởng điểm hoặc hiển thị thông báo khích lệ khi nhân viên hoàn thành 100% KPI trong tháng không?
