# **Feature: Quản lý hóa đơn**
Status: Draft

Author: Bùi Đỉnh

Reviewer: [Tên]

Date: [YYYY-MM-DD]

Priority: High

-----
# **1. Business Context**
Trong hệ thống quản lý nhà trọ, hóa đơn là tài liệu tài chính được sử dụng để ghi nhận các khoản phí mà người thuê phải thanh toán trong từng kỳ.

Mỗi hóa đơn được tạo dựa trên thông tin phòng thuê, chỉ số điện, chỉ số nước, tiền phòng cố định, các khoản phí dịch vụ phát sinh trong kỳ và thuế áp dụng theo quy định của hệ thống. 

Hóa đơn đóng vai trò là căn cứ để người thuê thực hiện thanh toán và để Ban quản lý theo dõi tình trạng thu tiền.

Feature Quản lý hóa đơn cho phép Ban quản lý xem danh sách hóa đơn, xem chi tiết hóa đơn, điều chỉnh thông tin hóa đơn trước khi phát hành và xuất hóa đơn dưới dạng tài liệu để lưu trữ hoặc gửi cho các bên liên quan.

Feature này giúp chuẩn hóa quy trình quản lý tài chính, đảm bảo tính chính xác của dữ liệu hóa đơn và hỗ trợ kiểm soát công nợ trong hệ thống nhà trọ.

-----
# **2. User Stories**
## **Story 1: Xem danh sách hóa đơn**
Là Ban quản lý,

tôi muốn xem danh sách hóa đơn trong hệ thống

để theo dõi các khoản phí cần thu của người thuê.

-----
## **Story 2: Xem chi tiết hóa đơn**
Là Ban quản lý,

tôi muốn xem chi tiết hóa đơn

để kiểm tra thông tin tính phí.

-----
## **Story 3: Điều chỉnh hóa đơn**
Là Ban quản lý,

tôi muốn điều chỉnh thông tin hóa đơn

để sửa các sai sót trước khi phát hành hóa đơn.

-----
## **Story 4: Xuất hóa đơn**
Là Ban quản lý,

tôi muốn xuất hóa đơn

để lưu trữ hoặc cung cấp cho các bên liên quan.

-----
## **Story 5: Tìm kiếm hóa đơn**
Là Ban quản lý,

tôi muốn tìm kiếm hóa đơn theo mã hóa đơn hoặc phòng

để nhanh chóng tra cứu dữ liệu.

-----
## **Story 6: Kiểm tra hóa đơn không tồn tại**
Là Ban quản lý,

khi hóa đơn không tồn tại,

tôi muốn hệ thống hiển thị lỗi phù hợp.

-----
## **Story 7: Kiểm tra quyền truy cập**
Là hệ thống,

khi người dùng không có quyền,

tôi muốn từ chối truy cập chức năng quản lý hóa đơn.

-----
# **3. Acceptance Criteria (EARS)**
## **3.1 Xem danh sách hóa đơn**
KHI Ban quản lý truy cập màn hình Quản lý hóa đơn,

THE SYSTEM SHALL hiển thị danh sách hóa đơn.

KHI danh sách hóa đơn được hiển thị,

THE SYSTEM SHALL hiển thị:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán

KHI Ban quản lý tìm kiếm theo mã hóa đơn,

THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI Ban quản lý tìm kiếm theo phòng,

THE SYSTEM SHALL hiển thị các hóa đơn phù hợp.

KHI không tồn tại hóa đơn nào,

THE SYSTEM SHALL hiển thị thông báo:

"Hiện tại chưa có hóa đơn nào."

-----
## **3.2 Xem chi tiết hóa đơn**
KHI Ban quản lý chọn một hóa đơn,

THE SYSTEM SHALL hiển thị:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn (tháng/năm)
- Tiền phòng cố định
- Chỉ số điện cũ
- Chỉ số điện mới
- Số điện tiêu thụ
- Thành tiền điện
- Chỉ số nước cũ
- Chỉ số nước mới
- Số nước tiêu thụ
- Thành tiền nước
- Phí dịch vụ
- Thuế (%)
- Tiền thuế
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán

KHI hóa đơn không tồn tại,

THE SYSTEM SHALL trả về HTTP 404 với mã lỗi INVOICE\_NOT\_FOUND.

-----
## **3.3 Điều chỉnh hóa đơn**
KHI Ban quản lý cập nhật hóa đơn với dữ liệu hợp lệ,

THE SYSTEM SHALL lưu thông tin hóa đơn mới.

KHI chỉ số điện mới được thay đổi,

THE SYSTEM SHALL tính lại số điện tiêu thụ và thành tiền điện.

KHI chỉ số nước mới được thay đổi,

THE SYSTEM SHALL tính lại số nước tiêu thụ và thành tiền nước.

KHI tiền phòng cố định thay đổi,

THE SYSTEM SHALL cập nhật tổng tiền phải nộp.

KHI phí dịch vụ thay đổi,

THE SYSTEM SHALL cập nhật tổng tiền phải nộp.

KHI thuế thay đổi,

THE SYSTEM SHALL tính lại tiền thuế và cập nhật tổng tiền phải nộp.

KHI hóa đơn đã thanh toán,

THE SYSTEM SHALL từ chối điều chỉnh và trả về HTTP 400 với mã lỗi PAID\_INVOICE\_CANNOT\_BE\_UPDATED.

KHI chỉ số điện mới nhỏ hơn chỉ số điện cũ,

THE SYSTEM SHALL trả về HTTP 400 với mã lỗi INVALID\_ELECTRIC\_READING.

KHI chỉ số nước mới nhỏ hơn chỉ số nước cũ,

THE SYSTEM SHALL trả về HTTP 400 với mã lỗi INVALID\_WATER\_READING.

KHI hạn thanh toán nhỏ hơn ngày hiện tại,

THE SYSTEM SHALL trả về HTTP 400 với mã lỗi INVALID\_DUE\_DATE.

-----
## **3.4 Xuất hóa đơn**
KHI Ban quản lý yêu cầu xuất hóa đơn,

THE SYSTEM SHALL tạo file hóa đơn PDF.

KHI xuất hóa đơn thành công,

THE SYSTEM SHALL trả về file hóa đơn.

KHI hóa đơn được xuất,

THE SYSTEM SHALL chứa đầy đủ:

- Mã hóa đơn
- Phòng
- Kỳ hóa đơn
- Tiền phòng
- Tiền điện
- Tiền nước
- Phí dịch vụ
- Thuế
- Tiền thuế
- Tổng tiền phải nộp
- Hạn thanh toán
- Trạng thái thanh toán

KHI hóa đơn không tồn tại,

THE SYSTEM SHALL trả về HTTP 404 với mã lỗi INVOICE\_NOT\_FOUND.

KHI hệ thống không thể tạo file hóa đơn,

THE SYSTEM SHALL trả về HTTP 500 với mã lỗi INVOICE\_EXPORT\_FAILED.

-----
## **3.5 Trạng thái thanh toán**
KHI hóa đơn chưa được thanh toán,

THE SYSTEM SHALL gán trạng thái UNPAID.

KHI hóa đơn đã được xác nhận thanh toán,

THE SYSTEM SHALL gán trạng thái PAID.

KHI hóa đơn chưa thanh toán và đã vượt quá hạn thanh toán,

THE SYSTEM SHALL gán trạng thái OVERDUE.

-----
## **3.6 Phân quyền**
KHI người dùng có vai trò Management Board,

THE SYSTEM SHALL cho phép truy cập chức năng quản lý hóa đơn.

KHI người dùng chưa đăng nhập,

THE SYSTEM SHALL trả về HTTP 401.

KHI người dùng không có vai trò Management Board,

THE SYSTEM SHALL trả về HTTP 403.

-----
# **4. API Contract**
## **4.1 Danh sách hóa đơn**
### **Endpoint**
GET /api/v1/invoices
### **Query Parameters**
- keyword
- roomCode
- status
- page
- size
### **Response 200**
{

  "success": true,

  "data": [

    {

      "invoiceId": "uuid",

      "invoiceCode": "INV202606001",

      "roomCode": "P101",

      "billingPeriod": "06/2026",

      "totalAmount": 4350000,

      "dueDate": "2026-06-30",

      "status": "UNPAID"

    }

  ]

}

-----
## **4.2 Xem chi tiết hóa đơn**
### **Endpoint**
GET /api/v1/invoices/{invoiceId}
### **Response 200**
{

  "success": true,

  "data": {

    "invoiceCode": "INV202606001",

    "roomCode": "P101",

    "billingPeriod": "06/2026",

    "roomFee": 3000000,

    "oldElectricReading": 250,

    "newElectricReading": 320,

    "electricAmount": 245000,

    "oldWaterReading": 120,

    "newWaterReading": 145,

    "waterAmount": 125000,

    "serviceFee": 200000,

    "taxRate": 10,

    "taxAmount": 357000,

    "totalAmount": 3927000,

    "dueDate": "2026-06-30",

    "status": "UNPAID"

  }

}

-----
## **4.3 Điều chỉnh hóa đơn**
### **Endpoint**
PUT /api/v1/invoices/{invoiceId}
### **Request**
{

  "roomFee": 3200000,

  "newElectricReading": 330,

  "newWaterReading": 150,

  "serviceFee": 250000,

  "taxRate": 10,

  "dueDate": "2026-07-05"

}

### **Response 200**
{

  "success": true,

  "data": {

    "invoiceId": "uuid",

    "updatedAt": "2026-06-10T10:00:00",

    "updatedBy": 5

  }

}

-----
## **4.4 Xuất hóa đơn**
### **Endpoint**
GET /api/v1/invoices/{invoiceId}/export
### **Response 200**
{

  "success": true,

  "data": {

    "fileName": "INV202606001.pdf",

    "downloadUrl": "/files/invoices/INV202606001.pdf"

  }

}

-----
# **5. Technical Constraints**
- Chỉ Management Board được phép quản lý hóa đơn.
- Mỗi hóa đơn phải liên kết với một phòng thuê hợp lệ.
- Mỗi hóa đơn phải có mã hóa đơn duy nhất.
- Kỳ hóa đơn được xác định theo tháng và năm.
- Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ.
- Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ.
- Tổng tiền phải nộp được tính tự động:

Tạm tính (Subtotal) =

Tiền phòng +

Tiền điện +

Tiền nước +

Phí dịch vụ

Tiền thuế =

Tạm tính × Thuế (%)

Tổng tiền phải nộp =

Tạm tính +

Tiền thuế

- Thuế phải lớn hơn hoặc bằng 0.
- Thuế được lưu dưới dạng phần trăm (%).
- Tiền thuế phải được hệ thống tính tự động.
- Người dùng không được nhập trực tiếp tiền thuế.
- Không được chỉnh sửa hóa đơn đã thanh toán.
- Hóa đơn xuất ra phải ở định dạng PDF.
- Hệ thống phải lưu:
  - createdAt
  - createdBy
  - updatedAt
  - updatedBy
- Tất cả thao tác điều chỉnh hóa đơn phải được ghi Audit Log.
- API danh sách hóa đơn: < 1000ms (p95).
- API chi tiết hóa đơn: < 500ms (p95).
- API điều chỉnh hóa đơn: < 1000ms (p95).
- API xuất hóa đơn: < 2000ms (p95).
-----
# **6. Out Of Scope**
- Hóa đơn điện tử theo chuẩn thuế.
- Chữ ký số.
- Tích hợp cơ quan thuế.
- Thanh toán trực tiếp từ hóa đơn.
- Hoàn tiền.
- Hủy hóa đơn đã thanh toán.
- Gửi email hóa đơn tự động.
- Gửi SMS hóa đơn.
- OCR hóa đơn.
- AI kiểm tra hóa đơn.
- Báo cáo doanh thu.
- Báo cáo tài chính tổng hợp.