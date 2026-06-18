# **Feature: Quản lý công nợ**

Status: Draft

Author: Bùi Đỉnh

Reviewer: \[Tên\]

Date: 2026-06-10

Priority: Medium

---

# **1. Business Context**

Trong quá trình vận hành hệ thống quản lý nhà trọ, người thuê phát sinh các khoản tiền cần thanh toán định kỳ hoặc phát sinh theo thực tế sử dụng. Các khoản này có thể bao gồm tiền thuê phòng, tiền điện, tiền nước, tiền internet, phí dịch vụ, phí vệ sinh hoặc các khoản phụ phí khác. Feature Quản lý công nợ được xây dựng để Ban quản lý có thể theo dõi, kiểm tra và kiểm soát toàn bộ các khoản công nợ phát sinh trong hệ thống. Thông qua chức năng này, Ban quản lý có thể xem danh sách công nợ, tìm kiếm công nợ theo mã công nợ, tên người thuê hoặc mã phòng, lọc công nợ theo trạng thái, xem chi tiết từng khoản công nợ và gửi thông báo công nợ tới Ban quản lý cấp trên (hoặc lưu vết xử lý nội bộ) khi cần phối hợp xử lý. Mỗi khoản công nợ được liên kết với một phòng thuê, một hợp đồng thuê và một người thuê cụ thể. Trong phạm vi feature này, hệ thống không hỗ trợ thanh toán từng phần. Người thuê phải thanh toán đúng toàn bộ số tiền phải trả của khoản công nợ. Vì vậy, mỗi khoản công nợ chỉ có một giá trị tổng số tiền phải trả và không có khái niệm số tiền đã thanh toán hoặc số tiền còn lại. Trạng thái công nợ được hệ thống xác định dựa trên tình trạng thanh toán và hạn thanh toán. Nếu khoản công nợ chưa được thanh toán và chưa quá hạn, trạng thái là PENDING. Nếu khoản công nợ đã được xác nhận thanh toán đủ, trạng thái là PAID. Nếu khoản công nợ chưa được thanh toán và đã vượt quá hạn thanh toán, trạng thái là OVERDUE. Các trạng thái công nợ hợp lệ gồm:

- PENDING (Chưa thanh toán)
- PAID (Đã thanh toán)
- OVERDUE (Quá hạn) Các loại công nợ được hỗ trợ gồm:
- RENT_FEE (Tiền thuê phòng)
- ELECTRIC_FEE (Tiền điện)
- WATER_FEE (Tiền nước)
- INTERNET_FEE (Tiền Internet)
- SERVICE_FEE (Phí dịch vụ)
- CLEANING_FEE (Phí vệ sinh)
- OTHER_FEE (Khoản thu khác) Khi phát hiện một khoản công nợ cần được xử lý hoặc cần phối hợp nội bộ, Ban quản lý có thể gửi thông báo công nợ nội bộ hệ thống. Sau khi nhận thông báo, Ban quản lý sẽ tiếp tục làm việc trực tiếp với người thuê. Trong phạm vi feature này, Ban quản lý không gửi thông báo trực tiếp tự động từ hệ thống tới người thuê. Feature này giúp nâng cao hiệu quả quản lý tài chính trong hệ thống nhà trọ, hỗ trợ phát hiện công nợ quá hạn, giảm thất lạc thông tin, giúp Ban quản lý nắm được các trường hợp cần xử lý và tăng tính minh bạch trong quá trình vận hành.

---

# **2. User Stories**

## **Story 1: Xem danh sách công nợ**

Là Ban quản lý,

tôi muốn xem danh sách tất cả các khoản công nợ trong hệ thống

để theo dõi tình trạng thanh toán của người thuê.

---

## **Story 2: Xem chi tiết công nợ**

Là Ban quản lý,

tôi muốn xem chi tiết một khoản công nợ

để kiểm tra thông tin khoản thu, người thuê, phòng thuê, hạn thanh toán và trạng thái công nợ.

---

## **Story 3: Tìm kiếm công nợ**

Là Ban quản lý,

tôi muốn tìm kiếm công nợ theo mã công nợ, tên người thuê hoặc mã phòng

để nhanh chóng tra cứu đúng khoản công nợ cần kiểm tra.

---

## **Story 4: Lọc công nợ theo trạng thái**

Là Ban quản lý,

tôi muốn lọc công nợ theo trạng thái PENDING, PAID hoặc OVERDUE

để tập trung xử lý các khoản chưa thanh toán hoặc đã quá hạn.

---

## **Story 5: Gửi thông báo công nợ tới Ban quản lý**

Là Ban quản lý,

tôi muốn gửi thông báo về các khoản công nợ cần xử lý ghi nhận lên hệ thống Ban quản lý

để Ban quản lý có thể làm việc trực tiếp với người thuê và hỗ trợ thu hồi công nợ.

---

## **Story 6: Xem lịch sử thông báo công nợ**

Là Ban quản lý,

tôi muốn xem lịch sử các thông báo đã gửi liên quan đến một khoản công nợ

để theo dõi quá trình phối hợp xử lý trong Ban quản lý.

---

## **Story 7: Xử lý trường hợp công nợ không tồn tại**

Là Ban quản lý,

khi tôi truy cập hoặc gửi thông báo cho một khoản công nợ không tồn tại,

tôi muốn hệ thống từ chối yêu cầu và hiển thị lỗi phù hợp

để tránh thao tác trên dữ liệu không hợp lệ.

---

## **Story 8: Kiểm tra quyền truy cập**

Là hệ thống,

khi người dùng chưa đăng nhập hoặc không có vai trò Ban quản lý,

tôi muốn từ chối truy cập chức năng Quản lý công nợ

để bảo vệ dữ liệu tài chính của hệ thống nhà trọ.

---

# **3. Acceptance Criteria (EARS)**

## **3.1 Xem danh sách công nợ**

WHEN Management Board opens Debt Management page,

THE SYSTEM SHALL display all debt records in the system.

WHEN debt list is displayed,

THE SYSTEM SHALL show the following information:

- Debt ID
- Debt Code
- Tenant Name
- Room Code
- Debt Type
- Total Amount
- Due Date
- Status

WHEN Management Board searches by debt code,

THE SYSTEM SHALL return debt records matching the debt code.

WHEN Management Board searches by tenant name,

THE SYSTEM SHALL return debt records matching the tenant name.

WHEN Management Board searches by room code,

THE SYSTEM SHALL return debt records matching the room code.

WHEN Management Board filters by status = PENDING,

THE SYSTEM SHALL return only unpaid debt records that are not overdue.

WHEN Management Board filters by status = PAID,

THE SYSTEM SHALL return only fully paid debt records.

WHEN Management Board filters by status = OVERDUE,

THE SYSTEM SHALL return only unpaid debt records that are overdue.

WHEN the system has no debt records,

THE SYSTEM SHALL display message "Hiện tại không có công nợ nào."

WHEN no debt records match the search or filter condition,

THE SYSTEM SHALL return an empty list.

WHEN the number of debt records is greater than one page size,

THE SYSTEM SHALL return paginated data.

---

## **3.2 Xem chi tiết công nợ**

WHEN Management Board selects a debt record,

THE SYSTEM SHALL display debt detail.

WHEN debt detail is displayed,

THE SYSTEM SHALL show the following information:

- Debt ID
- Debt Code
- Tenant Name
- Room Code
- Contract ID
- Debt Type
- Description
- Total Amount
- Created Date
- Due Date
- Status
- Note

WHEN the debt status is PENDING,

THE SYSTEM SHALL display the debt as unpaid.

WHEN the debt status is PAID,

THE SYSTEM SHALL display the debt as paid.

WHEN the debt status is OVERDUE,

THE SYSTEM SHALL display overdue warning.

WHEN Management Board requests a non-existing debt record,

THE SYSTEM SHALL return HTTP 404 with error code DEBT_001.

WHEN Management Board requests debt detail without authentication,

THE SYSTEM SHALL return HTTP 401 with error code UNAUTHORIZED.

WHEN user role is not Management Board,

THE SYSTEM SHALL return HTTP 403 with error code FORBIDDEN.

---

## **3.3 Xác định trạng thái công nợ**

WHEN debt is not paid and current date is before or equal to due date,

THE SYSTEM SHALL set debt status to PENDING.

WHEN debt is confirmed as fully paid,

THE SYSTEM SHALL set debt status to PAID.

WHEN debt is not paid and current date is after due date,

THE SYSTEM SHALL set debt status to OVERDUE.

WHEN debt status changes,

THE SYSTEM SHALL update the status in debt list and debt detail.

WHEN debt status is not one of PENDING, PAID or OVERDUE,

THE SYSTEM SHALL reject the data and return HTTP 400 with error code DEBT_STATUS_INVALID.

---

## **3.4 Gửi thông báo công nợ tới Ban quản lý**

WHEN Management Board sends a management notification for a valid debt record,

THE SYSTEM SHALL create a new notification record AND return HTTP 201.

WHEN notification is created successfully,

THE SYSTEM SHALL link the notification with the selected debt record.

WHEN notification is created successfully,

THE SYSTEM SHALL record notification title, message, createdAt and createdBy.

WHEN notification is created successfully,

THE SYSTEM SHALL deliver the notification to Management Board.

WHEN Management Board receives the notification,

THE SYSTEM SHALL display the notification in Management Board notification list.

WHEN Management Board sends notification with empty title,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_TITLE_REQUIRED.

WHEN Management Board sends notification with title containing only spaces,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_TITLE_REQUIRED.

WHEN Management Board sends notification with title longer than 100 characters,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_TITLE_TOO_LONG.

WHEN Management Board sends notification with empty message,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_MESSAGE_REQUIRED.

WHEN Management Board sends notification with message containing only spaces,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_MESSAGE_REQUIRED.

WHEN Management Board sends notification with message longer than 500 characters,

THE SYSTEM SHALL return HTTP 400 with error code NOTIFICATION_MESSAGE_TOO_LONG.

WHEN Management Board sends notification for a non-existing debt record,

THE SYSTEM SHALL return HTTP 404 with error code DEBT_001.

WHEN the system cannot create notification,

THE SYSTEM SHALL return HTTP 500 with error code DEBT_002.

WHEN Management Board sends notification without authentication,

THE SYSTEM SHALL return HTTP 401 with error code UNAUTHORIZED.

WHEN user role is not Management Board,

THE SYSTEM SHALL return HTTP 403 with error code FORBIDDEN.

---

## **3.5 Xem lịch sử thông báo công nợ**

WHEN Management Board requests management notification history of a debt record,

THE SYSTEM SHALL return all notifications sent to Management Board for that debt record.

WHEN notification history is returned,

THE SYSTEM SHALL show:

- Notification ID
- Title
- Message
- Created By
- Created At
- Status

WHEN there is no notification history for the debt record,

THE SYSTEM SHALL return an empty list.

WHEN Management Board requests notification history for a non-existing debt record,

THE SYSTEM SHALL return HTTP 404 with error code DEBT_001.

WHEN Management Board requests notification history without authentication,

THE SYSTEM SHALL return HTTP 401 with error code UNAUTHORIZED.

WHEN user role is not Management Board,

THE SYSTEM SHALL return HTTP 403 with error code FORBIDDEN.

---

## **3.6 Phân quyền**

WHILE user is authenticated and has Management Board role,

THE SYSTEM SHALL allow access to Debt Management feature.

WHILE user is unauthenticated,

THE SYSTEM SHALL prevent access to Debt Management feature AND return HTTP 401 with error code UNAUTHORIZED.

WHILE user role is not Management Board,

THE SYSTEM SHALL prevent access to Debt Management feature AND return HTTP 403 with error code FORBIDDEN.

WHILE user role is not Management Board,

THE SYSTEM SHALL prevent sending management notification AND return HTTP 403 with error code FORBIDDEN.

---

# **4. API Contract**

## **4.1 Lấy danh sách công nợ**

Endpoint:

GET /api/v1/debts

Mục đích:

Cho phép Ban quản lý xem danh sách công nợ trong hệ thống.

Query Parameters:

keyword: string, optional

status: PENDING | PAID | OVERDUE, optional

debtType: RENT_FEE | ELECTRIC_FEE | WATER_FEE | INTERNET_FEE | SERVICE_FEE | CLEANING_FEE | OTHER_FEE, optional

page: number, optional

size: number, optional

Response 200:

{

"success": true,

"data": {

"content": \[

{

"debtId": "uuid",

"debtCode": "DEBT001",

"tenantName": "Nguyễn Văn A",

"roomCode": "A101",

"debtType": "RENT_FEE",

"totalAmount": 3000000,

"dueDate": "2026-06-30",

"status": "PENDING"

}

\],

"page": 0,

"size": 10,

"totalElements": 1,

"totalPages": 1

}

}

Response 400 — Invalid Status:

{

"success": false,

"error": {

"code": "DEBT_STATUS_INVALID",

"message": "Trạng thái công nợ không hợp lệ"

}

}

Response 400 — Invalid Debt Type:

{

"success": false,

"error": {

"code": "DEBT_TYPE_INVALID",

"message": "Loại công nợ không hợp lệ"

}

}

Response 401:

{

"success": false,

"error": {

"code": "UNAUTHORIZED",

"message": "Người dùng chưa đăng nhập"

}

}

Response 403:

{

"success": false,

"error": {

"code": "FORBIDDEN",

"message": "Không có quyền truy cập chức năng Quản lý công nợ"

}

}

---

## **4.2 Xem chi tiết công nợ**

Endpoint:

GET /api/v1/debts/{debtId}

Mục đích:

Cho phép Ban quản lý xem chi tiết một khoản công nợ.

Response 200:

{

"success": true,

"data": {

"debtId": "uuid",

"debtCode": "DEBT001",

"tenantName": "Nguyễn Văn A",

"roomCode": "A101",

"contractId": "uuid",

"debtType": "RENT_FEE",

"description": "Tiền thuê phòng tháng 06",

"totalAmount": 3000000,

"createdAt": "2026-06-01",

"dueDate": "2026-06-30",

"status": "PENDING",

"note": "Khoản thu định kỳ hằng tháng"

}

}

Response 404:

{

"success": false,

"error": {

"code": "DEBT_001",

"message": "Không tìm thấy công nợ"

}

}

Response 401:

{

"success": false,

"error": {

"code": "UNAUTHORIZED",

"message": "Người dùng chưa đăng nhập"

}

}

Response 403:

{

"success": false,

"error": {

"code": "FORBIDDEN",

"message": "Không có quyền xem chi tiết công nợ"

}

}

---

## **4.3 Gửi thông báo công nợ tới Ban quản lý**

Endpoint:

POST /api/v1/debts/{debtId}/management-notification

Mục đích:

Cho phép Ban quản lý gửi thông báo về một khoản công nợ cần xử lý tới Ban quản lý.

Request:

{

"title": "Công nợ quá hạn",

"message": "Khoản công nợ DEBT001 đã quá hạn thanh toán. Vui lòng Ban quản lý kiểm tra và làm việc với người thuê."

}

Field Rules:

title: string, required, max 100 characters

message: string, required, max 500 characters

Business Rules:

debtId phải tồn tại trong hệ thống.

Thông báo chỉ được gửi tới Ban quản lý.

Ban quản lý không được gửi thông báo trực tiếp tự động từ hệ thống tới người thuê.

Hệ thống tự động xác định người gửi từ tài khoản Ban quản lý đang đăng nhập.

Người dùng không được tự truyền createdBy.

Hệ thống tự động xác định người nhận là Ban quản lý.

Người dùng không được tự truyền recipientRole.

Thông báo sau khi gửi thành công có trạng thái SENT.

Response 201:

{

"success": true,

"data": {

"notificationId": "uuid",

"debtId": "uuid",

"title": "Công nợ quá hạn",

"status": "SENT",

"sentAt": "2026-06-10T09:00:00",

"createdBy": "Management Board",

"recipientRole": "MANAGEMENT_BOARD"

}

}

Response 400 — Missing Title:

{

"success": false,

"error": {

"code": "NOTIFICATION_TITLE_REQUIRED",

"message": "Tiêu đề thông báo là bắt buộc"

}

}

Response 400 — Title Too Long:

{

"success": false,

"error": {

"code": "NOTIFICATION_TITLE_TOO_LONG",

"message": "Tiêu đề thông báo không được vượt quá 100 ký tự"

}

}

Response 400 — Missing Message:

{

"success": false,

"error": {

"code": "NOTIFICATION_MESSAGE_REQUIRED",

"message": "Nội dung thông báo là bắt buộc"

}

}

Response 400 — Message Too Long:

{

"success": false,

"error": {

"code": "NOTIFICATION_MESSAGE_TOO_LONG",

"message": "Nội dung thông báo không được vượt quá 500 ký tự"

}

}

Response 404 — Debt Not Found:

{

"success": false,

"error": {

"code": "DEBT_001",

"message": "Không tìm thấy công nợ"

}

}

Response 500:

{

"success": false,

"error": {

"code": "DEBT_002",

"message": "Không thể tạo thông báo"

}

}

Response 401:

{

"success": false,

"error": {

"code": "UNAUTHORIZED",

"message": "Người dùng chưa đăng nhập"

}

}

Response 403:

{

"success": false,

"error": {

"code": "FORBIDDEN",

"message": "Không có quyền gửi thông báo công nợ tới Ban quản lý"

}

}

---

## **4.4 Xem lịch sử thông báo công nợ**

Endpoint:

GET /api/v1/debts/{debtId}/management-notifications

Mục đích:

Cho phép Ban quản lý xem lịch sử các thông báo đã gửi tới Ban quản lý liên quan đến một khoản công nợ.

Response 200:

{

"success": true,

"data": \[

{

"notificationId": "uuid",

"debtId": "uuid",

"title": "Công nợ quá hạn",

"message": "Khoản công nợ DEBT001 đã quá hạn thanh toán.",

"createdBy": "Management Board",

"createdAt": "2026-06-10T09:00:00",

"status": "SENT"

}

\]

}

Response 404:

{

"success": false,

"error": {

"code": "DEBT_001",

"message": "Không tìm thấy công nợ"

}

}

Response 401:

{

"success": false,

"error": {

"code": "UNAUTHORIZED",

"message": "Người dùng chưa đăng nhập"

}

}

Response 403:

{

"success": false,

"error": {

"code": "FORBIDDEN",

"message": "Không có quyền xem lịch sử thông báo công nợ"

}

}

---

# **5. Technical Constraints**

Feature này chỉ dành cho người dùng có vai trò Management Board.

Tất cả API phải yêu cầu authentication token.

Backend phải kiểm tra quyền truy cập cho tất cả request, không chỉ dựa vào việc ẩn chức năng trên giao diện.

Chỉ user có role Management Board mới được xem danh sách công nợ.

Chỉ user có role Management Board mới được xem chi tiết công nợ.

Chỉ user có role Management Board mới được gửi thông báo công nợ tới Ban quản lý.

Chỉ user có role Management Board mới được xem lịch sử thông báo công nợ.

Mỗi khoản công nợ phải thuộc một phòng thuê hợp lệ.

Mỗi khoản công nợ phải thuộc một người thuê hợp lệ.

Mỗi khoản công nợ có thể liên kết với một hợp đồng thuê hợp lệ.

Hệ thống không hỗ trợ thanh toán từng phần trong phạm vi feature này.

Mỗi khoản công nợ chỉ có một số tiền phải trả là totalAmount.

Không sử dụng các trường paidAmount hoặc remainingAmount trong feature này.

Người thuê phải thanh toán đúng toàn bộ totalAmount để công nợ được xem là đã thanh toán.

Trạng thái công nợ chỉ được phép là:

- PENDING
- PAID
- OVERDUE

Hệ thống phải tự động xác định trạng thái công nợ dựa trên tình trạng thanh toán và hạn thanh toán.

Nếu công nợ chưa được thanh toán và chưa quá hạn, trạng thái là PENDING.

Nếu công nợ đã được xác nhận thanh toán đủ, trạng thái là PAID.

Nếu công nợ chưa được thanh toán và đã quá hạn, trạng thái là OVERDUE.

Các loại công nợ hợp lệ gồm:

- RENT_FEE
- ELECTRIC_FEE
- WATER_FEE
- INTERNET_FEE
- SERVICE_FEE
- CLEANING_FEE
- OTHER_FEE

Nội dung thông báo gửi tới Ban quản lý tối đa 500 ký tự.

Tiêu đề thông báo gửi tới Ban quản lý tối đa 100 ký tự.

Thông báo công nợ chỉ được gửi tới Ban quản lý.

Ban quản lý không được gửi thông báo công nợ trực tiếp tới người thuê.

Hệ thống phải lưu lịch sử tất cả các lần gửi thông báo tới Ban quản lý.

Thông báo gửi thành công phải có trạng thái SENT.

Hệ thống phải tự động lưu:

- createdAt
- createdBy
- updatedAt
- updatedBy

Tất cả các thao tác gửi thông báo phải được ghi nhận Audit Log.

Tất cả thao tác xem chi tiết công nợ phải được ghi nhận Audit Log.

API lấy danh sách công nợ phải phản hồi dưới 1 giây (p95).

API xem chi tiết công nợ phải phản hồi dưới 500ms (p95).

API gửi thông báo phải phản hồi dưới 1 giây (p95).

Rate limit: 100 requests/phút/người dùng.

---

# **6. Out Of Scope**

Các chức năng sau không nằm trong phạm vi feature Quản lý công nợ:

- Thanh toán trực tuyến.
- Tích hợp cổng thanh toán.
- Thanh toán từng phần.
- Ghi nhận số tiền đã thanh toán.
- Tính số tiền còn lại.
- Hoàn tiền.
- Quản lý hóa đơn VAT.
- Quản lý doanh thu tổng hợp.
- Xuất báo cáo tài chính.
- Gửi email tự động cho người thuê.
- Gửi SMS tự động cho người thuê.
- Gửi thông báo trực tiếp từ Ban quản lý tới người thuê.
- Tính tiền phạt trả chậm tự động.
- Tự động khóa hợp đồng thuê khi quá hạn thanh toán.
- Xử lý khiếu nại tài chính.
- Quản lý thu chi nội bộ.
- Quy trình Ban quản lý làm việc trực tiếp với người thuê sau khi nhận thông báo.
- Chức năng nhắc nợ tự động theo lịch.
- Chỉnh sửa hoặc thu hồi thông báo công nợ sau khi đã gửi.