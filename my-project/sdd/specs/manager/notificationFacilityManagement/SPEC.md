# Feature: Quản lý Thông báo cho Ban quản lý

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** 2026-07-13
**Priority:** High

## 1. Business Context

Tính năng Quản lý Thông báo cho Ban quản lý cho phép Manager tạo và gửi các thông báo (thông báo vận hành chung, nhắc nhở nợ tiền phòng) đến người thuê trong phạm vi cơ sở được phân công quản lý, cũng như báo cáo sai số điện nước và yêu cầu Operator kiểm tra lại chỉ số.

Tính năng này đóng vai trò truyền tải thông tin vận hành, đôn đốc công nợ hiệu quả và kết nối giải quyết sự cố chỉ số điện nước giữa Manager và Operator. Đồng thời, hệ thống đảm bảo Manager chỉ hoạt động trong phạm vi cơ sở/phòng được giao quản lý, không được gửi thông báo toàn hệ thống.

## 2. User Stories

### Story 1 (Luồng chính)

As a Manager, I want to create a notification for my assigned facility or a specific room so that I can send important information to tenants.

### Story 2 (Luồng chính)

As a Manager, I want to send an overdue debt reminder notification to a specific room based on an unpaid invoice so that I can urge them to pay.

### Story 3 (Luồng chính)

As a Manager, when I notice an incorrect meter reading for an invoice, I want to report it as incorrect and send a request to the Operator to re-verify and update it.

### Story 4 (Luồng chính)

As a Manager, I want to view, search, and paginate the list of notifications (both general announcements received/sent, debt reminders, and reported incorrect utility invoices) within my assigned facilities.

### Story 5 (Edge Case)

As a Manager, when I attempt to access or send a notification to a facility or room not assigned to me, I want the system to reject the action with a Forbidden error.

### Story 6 (Edge Case)

As a Manager, when creating a notification with missing required fields (title, content), I want the system to reload the form and display validation errors.

### Story 7 (Edge Case)

As a Manager, when trying to send a global notification to the entire system, I want the system to prevent this action.

## 3. Acceptance Criteria (EARS)

### 3.1 Tạo thông báo

#### AC-01 Tạo thông báo hợp lệ

WHEN Manager gửi biểu mẫu tạo thông báo với dữ liệu hợp lệ (tiêu đề, nội dung, phạm vi nhận là cơ sở hoặc phòng được phân công)
THE SYSTEM SHALL tạo thông báo mới trong bảng `dbo.notifications` với trạng thái `SENT` và gửi đến cư dân.

#### AC-02 Kiểm tra tiêu đề và nội dung bắt buộc

WHEN Manager nhập tiêu đề hoặc nội dung trống khi tạo thông báo
THE SYSTEM SHALL hiển thị lỗi thông báo thiếu trường bắt buộc và giữ lại dữ liệu đã nhập.

#### AC-03 Gửi nhắc nợ tiền phòng quá hạn

WHEN Manager gửi nhắc nợ quá hạn cho một hóa đơn chưa thanh toán
THE SYSTEM SHALL tạo một thông báo nhắc nợ mới trong bảng `dbo.notifications` có tiền tố mã là `NTF-DEBT-` với phạm vi nhận là phòng tương ứng.

#### AC-04 Báo cáo sai chỉ số điện nước

WHEN Manager chọn hành động báo cáo sai số điện nước đối với một hóa đơn
THE SYSTEM SHALL cập nhật trạng thái của bản ghi chỉ số điện nước (`dbo.meter_readings`) thành `REPORTED` và hướng dẫn gửi yêu cầu kiểm tra cho Operator.

#### AC-05 Yêu cầu Operator sửa chỉ số điện nước

WHEN Manager gửi yêu cầu sửa chỉ số điện nước cho Operator phụ trách cơ sở
HỆ THỐNG PHẢI tạo một yêu cầu hỗ trợ mới trong bảng `dbo.requests` có phân loại là `UTILITY`, trạng thái `PENDING` và phân công trực tiếp cho Operator đã chọn.

#### AC-06 Không cho phép gửi toàn hệ thống hoặc sai phạm vi cơ sở

WHEN Manager cố gắng gửi thông báo toàn hệ thống hoặc gửi đến cơ sở/phòng không được phân công quản lý
THE SYSTEM SHALL từ chối hành động và trả về mã lỗi 403 Forbidden.

### 3.2 Danh sách thông báo

#### AC-07 Xem danh sách thông báo và bộ lọc

WHEN Manager truy cập trang danh sách thông báo
THE SYSTEM SHALL hiển thị các thông báo theo tabs:
* `general` (Thông báo chung: loại `received` nhận từ Admin hoặc loại `sent` do Manager tự gửi)
* `payment-reminder` (Thông báo nhắc nợ có mã `NTF-DEBT-`)
* `incorrect-utility` (Danh sách các hóa đơn có chỉ số điện nước đang bị báo sai ở trạng thái `INCORRECT` hoặc `REPORTED`).

#### AC-08 Tìm kiếm và Phân trang

WHEN Manager tìm kiếm thông báo bằng từ khóa hoặc lọc theo cơ sở
THE SYSTEM SHALL trả về kết quả tìm kiếm thích hợp trong phạm vi được phân quyền kèm phân trang (10 bản ghi/trang).

### 3.3 Chi tiết thông báo

#### AC-09 Xem chi tiết thông báo

WHEN Manager chọn một thông báo từ danh sách
THE SYSTEM SHALL hiển thị nội dung chi tiết thông báo, mã thông báo, người gửi, thời gian gửi và đối tượng nhận cụ thể.

## 4. API Contract

Mọi hành động được xử lý thông qua Controller servlet [ManagerNotificationsServlet.java](file:///d:/Ki_5/hostel_management/src/main/java/com/quanlyphongtro/controller/manager/ManagerNotificationsServlet.java) (sử dụng phương thức GET/POST):

### 4.1 Lấy danh sách thông báo và hóa đơn báo lỗi
* **URL:** `GET /manager/notifications`
* **Query Parameters:**
  * `tab`: Phân hệ hiển thị (`general`, `payment-reminder`, `incorrect-utility`)
  * `type`: Loại bộ lọc cho thông báo chung (`received` - nhận từ Admin, `sent` - do mình gửi)
  * `facilityId`: Lọc theo cơ sở được phân công
  * `keyword`: Tìm kiếm theo tiêu đề hoặc nội dung thông báo
  * `page`: Trang cần xem (mặc định trang 1)

### 4.2 Form tạo thông báo chung
* **URL:** `GET /manager/notifications/create`
* **Query Parameters:** `recipientType`, `roomId`, `facilityId` (tự điền thông tin phòng/cơ sở tương ứng)

### 4.3 Submit gửi thông báo chung
* **URL:** `POST /manager/notifications/create`
* **Request Parameters:**
  * `title`: Tiêu đề thông báo
  * `content`: Nội dung thông báo
  * `recipientType`: Đối tượng nhận (`FACILITY` hoặc `ROOM`)
  * `recipientId`: ID cơ sở hoặc ID phòng tương ứng
  * `isDebtReminder`: Xác định có phải là nhắc nợ không (`true`/`false`)

### 4.4 Báo cáo chỉ số điện nước hóa đơn bị sai
* **URL:** `GET /manager/notifications?action=report-incorrect&invoiceId={invoiceId}`
* **Redirect:** Sau khi báo cáo thành công, chuyển hướng đến form gửi yêu cầu cho Operator.

### 4.5 Form gửi yêu cầu cho Operator sửa chỉ số điện nước
* **URL:** `GET /manager/notifications/send-operator`
* **Query Parameters:** `invoiceId`
* **Chức năng:** Lấy thông tin hóa đơn và nạp danh sách các Operator đang hoạt động tại cơ sở để Manager lựa chọn.

### 4.6 Submit gửi yêu cầu sửa chỉ số điện nước cho Operator
* **URL:** `POST /manager/notifications/send-operator`
* **Request Parameters:** `invoiceId`, `operatorId`, `title`, `content`
* **Redirect:** Quay lại danh sách thông báo tab `incorrect-utility`.

### 4.7 Form nhắc nợ tiền phòng quá hạn
* **URL:** `GET /manager/notifications/send-debt-reminder`
* **Query Parameters:** `invoiceId`

### 4.8 Submit gửi nhắc nợ quá hạn
* **URL:** `POST /manager/notifications/send-debt-reminder`
* **Request Parameters:** `invoiceId`, `title`, `content`

### 4.9 Xem chi tiết thông báo
* **URL:** `GET /manager/notifications/{notificationId}`

## 5. Technical Constraints

* Phân trang danh sách thông báo (10 bản ghi trên mỗi trang).
* Việc phân quyền được kiểm tra nghiêm ngặt tại Service: Manager chỉ được thao tác trong phạm vi các cơ sở được phân công (kiểm tra `manager_id` trong bảng `dbo.facilities`).
* Log lịch sử thao tác: Mọi hành động gửi thông báo, gửi yêu cầu Operator, báo cáo sai số đều được ghi nhận vào bảng `dbo.audit_logs`.
* Hỗ trợ xác thực CSRF Token trên tất cả các form gửi dữ liệu (POST).

## 6. Dependencies

* **Quản lý Cơ sở:** Để xác minh cơ sở được giao quản lý và lấy danh sách Operator của cơ sở đó.
* **Quản lý Phòng:** Để gán người nhận là phòng hoặc kiểm tra phòng thuộc cơ sở được phân công.
* **Quản lý Hóa đơn & Chỉ số:** Cung cấp thông tin hóa đơn nợ quá hạn và chỉ số điện nước bị sai để đính kèm vào thông báo.
* **Quản lý Yêu cầu (Requests):** Dùng để tạo yêu cầu sửa chỉ số điện nước gửi Operator.
* **Audit Log:** Ghi lại lịch sử hoạt động của Manager.

## 7. Business Rules

### BR-01
Manager chỉ được gửi thông báo chung hoặc nhắc nợ đến các cơ sở/phòng thuộc phạm vi phân công quản lý.

### BR-02
Manager không được gửi thông báo toàn hệ thống (`target_type = 'ALL'` chỉ do Admin tạo).

### BR-03
Khi Manager báo cáo hóa đơn sai chỉ số, trạng thái chỉ số điện nước (`dbo.meter_readings`) phải được chuyển sang `REPORTED` để khóa hóa đơn và chờ Operator xác minh lại.

### BR-04
Hành động gửi yêu cầu sửa chỉ số điện nước cho Operator thực chất là tạo một yêu cầu hỗ trợ hệ thống (bảng `requests`) với phân loại là `UTILITY` và trạng thái `PENDING`.

### BR-05
Thông báo nhắc nợ tiền phòng được tạo tự động với mã dạng `NTF-DEBT-xxx` và gửi trực tiếp đến phòng có hóa đơn nợ quá hạn.

## 8. Notification Recipient Type

| Recipient Type | Ý nghĩa | Manager có được dùng không? |
| --- | --- | --- |
| `ALL` | Gửi toàn bộ hệ thống | Không (Chỉ xem các thông báo nhận từ Admin) |
| `FACILITY` | Gửi cho tất cả cư dân thuộc cơ sở | Có (Cơ sở trong phạm vi quản lý) |
| `ROOM` | Gửi cho các thành viên phòng cụ thể | Có (Phòng có người thuê đang hoạt động) |

## 9. Out of Scope

* Tự động lập lịch gửi thông báo.
* Thu hồi hoặc xóa thông báo sau khi đã gửi.
* Gửi tin nhắn qua kênh SMS hoặc Email bên thứ ba.
* Cho phép cư dân phản hồi/bình luận trực tiếp dưới thông báo chung.
