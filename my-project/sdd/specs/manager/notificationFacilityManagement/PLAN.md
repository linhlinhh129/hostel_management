# PLAN: Kế hoạch Thiết kế & Triển khai liên module Manager & Operator (Cập nhật UI Nút "Xem" duy nhất)

**Status:** Approved (Updated: Single View Button & Removed Delete Flow)  
**Date:** 2026-07-25  
**Priority:** High  
**Feature Branch:** `notificationFacilityManagement`

---

## 1. Tổng quan Thiết kế Chuẩn hóa

Kế hoạch này cập nhật cấu trúc giao diện của **Module Hóa Đơn (`manager`)** theo quy chuẩn mới:
1. **Trang Danh sách Hóa đơn (`/manager/invoices`)**:
   - Cột thao tác trên bảng chỉ chứa duy nhất **Nút "Xem"** (`href="${ctx}/manager/invoices/${invoice.invoiceId}"`).
   - Loại bỏ các nút "Báo sai số" bên ngoài và nút "Xóa" khỏi bảng danh sách.
2. **Trang Chi tiết Hóa đơn (`/manager/invoices/{id}`)**:
   - Nút **"Báo cáo sai số"** nằm bên trong trang chi tiết dành cho các hóa đơn chưa thanh toán.
   - **Loại bỏ hoàn toàn luồng Xóa Hóa đơn** khỏi hệ thống.
3. **Form Gửi Operator & Module Operator (`/operator/requests`)**:
   - Khi bấm "Báo cáo sai số" từ trang chi tiết hóa đơn, hệ thống tới `/manager/notifications/send-operator?invoiceId={id}` nạp sẵn Tiêu đề và Nội dung.
   - Đẩy bản ghi công việc `UTILITY` `PENDING` sang **Module Danh sách Yêu cầu của Operator**.
   - Operator tiếp nhận (`IN_PROGRESS`) và báo cáo hoàn thành (`COMPLETED`) với ghi chú optional.
4. **Tạm khóa thanh toán Cư dân**:
   - Cư dân bị ngưng thanh toán hóa đơn và hiển thị nhãn *"⚠️ Đang xử lý sai số điện nước"* khi chỉ số ở trạng thái `REPORTED`.

---

## 2. Chi tiết Giao diện & Component

- `src/main/webapp/WEB-INF/views/manager/invoices/list.jsp`: Cột thao tác hiển thị duy nhất nút "Xem".
- `src/main/webapp/WEB-INF/views/manager/invoices/detail.jsp`: Hiển thị nút "Báo cáo sai số" & "Sửa Hóa Đơn". Đã xóa bỏ nút/form "Xóa Hóa Đơn".
- `src/main/webapp/WEB-INF/views/manager/notifications/send_operator.jsp`: Luôn hiển thị Form tạo yêu cầu cho Operator.

---

## 3. Success Criteria

- ✓ 100% dòng trong bảng danh sách hóa đơn chỉ có nút "Xem".
- ✓ 100% luồng xóa hóa đơn chưa thanh toán đã được gỡ bỏ khỏi giao diện.
- ✓ Nút "Báo cáo sai số" xuất hiện duy nhất trong trang xem chi tiết và liên kết đúng form gửi Operator.
