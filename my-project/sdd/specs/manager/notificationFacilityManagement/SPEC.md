# Feature Specification: Quản lý thông báo & Báo cáo sai số điện nước cho Ban quản lý và Operator

**Feature Branch**: `notificationFacilityManagement`  
**Created**: 2026-07-25  
**Status**: Draft (Updated: Only View Action in List & Removed Delete Flow)  
**Input**: "Trong module Hóa đơn mọi thao tác phải nằm trong nút 'xem', tức là mọi thao tác 'Báo cáo sai số điện nước' sẽ nằm trong xem chi tiết chứ không phải nằm ở cột thao tác bên ngoài hãy xóa bỏ. Ở tại module hóa đơn đang có luồng xóa hóa đơn đối với hóa đơn chưa thanh toán bạn hãy xóa bỏ luồng đó cho tôi."

---

## 1. Business Context

Tính năng Quản lý thông báo & Báo cáo sai số điện nước chuẩn hóa luồng giao diện cho Manager trong Module Hóa Đơn:
1. **Danh sách Hóa đơn (`/manager/invoices`)**: Cột thao tác của bảng danh sách **chỉ chứa nút "Xem"**. Tất cả các nút bấm khác (như "Báo sai số" bên ngoài và "Xóa hóa đơn") đều bị loại bỏ khỏi bảng danh sách.
2. **Chi tiết Hóa đơn (`/manager/invoices/{id}`)**: Sau khi Manager nhấn "Xem" để vào trang chi tiết, nút **"Báo cáo sai số"** mới xuất hiện cho các hóa đơn chưa thanh toán.
3. **Loại bỏ Luồng Xóa Hóa đơn**: Hệ thống hoàn toàn loại bỏ tính năng/luồng xóa hóa đơn (Delete Invoice) đối với các hóa đơn chưa thanh toán.
4. **Form Báo cáo sai số (`send_operator.jsp`)**: Khi nhấn "Báo cáo sai số" từ trang chi tiết hóa đơn, hệ thống chuyển hướng sang `GET /manager/notifications/send-operator?invoiceId={id}` tự động điền Tiêu đề và Nội dung chi tiết chỉ số điện/nước.
5. **Đẩy sang Operator (`/operator/requests`)**: Manager gửi yêu cầu, hệ thống tạo bản ghi công việc `UTILITY` `PENDING` đẩy sang Module Danh sách Yêu cầu của Operator để xem, tiếp nhận (`IN_PROGRESS`) và báo cáo hoàn thành (`COMPLETED`).

---

## Clarifications

### Session 2026-07-25

- Q: Thao tác bên ngoài bảng danh sách Hóa đơn (`/manager/invoices`) có các nút nào? → A: Chỉ duy nhất nút **"Xem"** nằm trong cột thao tác của bảng danh sách. Nút "Báo sai số" bên ngoài đã bị xóa bỏ.
- Q: Luồng xóa hóa đơn (Delete Invoice) chưa thanh toán xử lý thế nào? → A: Luồng xóa hóa đơn chưa thanh toán đã bị hoàn toàn loại bỏ khỏi giao diện Module Hóa Đơn.
- Q: Thao tác "Báo cáo sai số" nằm ở đâu? → A: Nằm duy nhất trong trang **Xem chi tiết Hóa đơn (`/manager/invoices/{id}`)**.

---

## 2. User Scenarios & Testing *(mandatory)*

### User Story 1 - Thao tác Báo cáo sai số từ trang Xem Chi tiết Hóa đơn (Priority: P1)

As a Manager, tại trang danh sách Hóa đơn (`/manager/invoices`), I want to chỉ nhìn thấy nút **"Xem"** trên mỗi dòng hóa đơn so that tôi nhấn nút "Xem" để truy cập vào trang Chi tiết Hóa đơn (`/manager/invoices/{id}`) và thực hiện nút bấm "Báo cáo sai số" bên trong đó.

**Acceptance Scenarios**:

1. **Giao diện Bảng Danh sách Hóa đơn**:
   - **Given** Manager truy cập `/manager/invoices`,
   - **Then** Cột thao tác của bảng danh sách chỉ hiển thị duy nhất nút **"Xem"**. Không hiển thị nút "Báo sai số" hay nút "Xóa".

2. **Giao diện Trang Chi tiết Hóa đơn**:
   - **Given** Manager nhấn nút "Xem" và vào trang `/manager/invoices/{id}`,
   - **Then** Trang chi tiết hiển thị nút **"Báo cáo sai số"** (đối với hóa đơn chưa thanh toán). Không hiển thị nút "Xóa Hóa Đơn".

3. **Chuyển hướng sang Form Gửi Operator**:
   - **When** Manager nhấn nút "Báo cáo sai số" trong trang chi tiết,
   - **Then** Hệ thống chuyển hướng tới `GET /manager/notifications/send-operator?invoiceId={id}` nạp sẵn Tiêu đề và Nội dung chi tiết chỉ số.

---

### User Story 2 - Operator xem và xử lý Yêu cầu báo sai số (Priority: P1)

As an Operator, khi truy cập Module Danh sách Yêu cầu (`/operator/requests`), I want to xem tiêu đề và nội dung chi tiết về báo cáo sai số điện nước, bấm **"Xác nhận & Đặt lịch xử lý"** (`IN_PROGRESS`), sau đó bấm **"Xác nhận hoàn thành"** (`COMPLETED`) với ghi chú hoàn thành tùy chọn.

---

## 3. Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Hệ thống MUST chỉ hiển thị duy nhất nút **"Xem"** trong cột thao tác của trang Danh sách Hóa đơn (`/manager/invoices`).
- **FR-002**: Hệ thống MUST hoàn toàn xóa bỏ nút và luồng **Xóa Hóa đơn** khỏi giao diện Module Hóa Đơn (`/manager/invoices` và `/manager/invoices/{id}`).
- **FR-003**: Hệ thống MUST đặt nút **"Báo cáo sai số"** duy nhất bên trong **Trang Chi tiết Hóa đơn (`/manager/invoices/{id}`)** cho các hóa đơn chưa thanh toán.
- **FR-004**: Khi Manager bấm "Báo cáo sai số" trong trang chi tiết hóa đơn, hệ thống MUST chuyển hướng tới `/manager/notifications/send-operator?invoiceId={id}` tự động điền Tiêu đề và Nội dung chi tiết chỉ số điện/nước.
- **FR-005**: Khi Manager gửi yêu cầu, hệ thống MUST thực hiện Database Transaction: cập nhật trạng thái chỉ số sang `REPORTED` và chèn yêu cầu `UTILITY` `PENDING` phân công cho Operator được chọn trong `/operator/requests`.
- **FR-006**: Operator MUST có khả năng tiếp nhận (`IN_PROGRESS`) và báo cáo hoàn thành (`COMPLETED`) với ghi chú optional.
- **FR-007**: Cư dân MUST bị tạm khóa tính năng thanh toán khi hóa đơn ở trạng thái `REPORTED`.

---

## 4. Success Criteria *(mandatory)*

- **SC-001**: 100% cột thao tác bảng danh sách hóa đơn chỉ có nút "Xem".
- **SC-002**: 100% nút và luồng Xóa Hóa đơn bị loại bỏ khỏi giao diện Module Hóa Đơn.
- **SC-003**: Nút "Báo cáo sai số" chỉ hiển thị trong trang Chi tiết Hóa đơn và chuyển hướng thành công tới form gửi Operator.
