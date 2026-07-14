# Feature: Tiếp nhận và xử lý yêu cầu người thuê

**Status:** Draft
**Author:** [Tên]
**Reviewer:** [Tên]
**Date:** 2026-07-13
**Priority:** High

## 1. Bối cảnh nghiệp vụ (Business Context)

Tính năng Tiếp nhận và xử lý yêu cầu người thuê cho phép Ban quản lý (Manager) tiếp nhận, từ chối, lên lịch hẹn xử lý, và xác nhận hoàn thành các yêu cầu hỗ trợ (gồm các sự cố cơ sở vật chất, điện nước hoặc dịch vụ khác) do Cư dân (Tenant) gửi lên hệ thống, cũng như các yêu cầu sửa đổi chỉ số từ Operator.

Tính năng này chuẩn hóa quy trình tiếp nhận và vận hành xử lý sự cố trong nhà trọ. Nó giúp Manager dễ dàng phân loại, giám sát tiến độ của từng phòng/cơ sở và phản hồi nhanh chóng cho người thuê. Đồng thời, hệ thống ghi nhận lịch sử thay đổi trạng thái tự động để đảm bảo tính minh bạch.

## 2. User Stories

### Story 1 (Luồng chính)

Là Manager, tôi muốn xem danh sách các yêu cầu/sự cố của các cơ sở tôi quản lý để nắm bắt các vấn đề phát sinh của cư dân.

### Story 2 (Luồng chính)

Là Manager, tôi muốn tiếp nhận một yêu cầu mới gửi lên để chuyển trạng thái sang tiếp nhận và đưa vào hàng chờ xử lý.

### Story 3 (Luồng chính)

Là Manager, tôi muốn đặt lịch hẹn ngày giờ xử lý sự cố để cư dân biết thời điểm Ban quản lý sẽ tiến hành sửa chữa, đồng thời hệ thống tự động cập nhật trạng thái yêu cầu sang Đang xử lý.

### Story 4 (Luồng chính)

Là Manager, tôi muốn cập nhật trạng thái hoàn thành yêu cầu, nhập ghi chú giải quyết và tải lên các ảnh sau khi sửa chữa để hoàn tất yêu cầu của cư dân.

### Story 5 (Ngoại lệ)

Là Manager, khi gặp yêu cầu không hợp lệ hoặc nằm ngoài khả năng, tôi muốn từ chối yêu cầu và ghi rõ lý do để cư dân nhận được phản hồi.

### Story 6 (Ngoại lệ)

Là Manager, tôi không được phép truy cập hoặc xử lý các yêu cầu thuộc phòng/cơ sở do Manager khác quản lý.

## 3. Tiêu chí chấp nhận (Acceptance Criteria - EARS)

### AC-01 Xem danh sách yêu cầu

KHI Manager mở trang danh sách yêu cầu
HỆ THỐNG PHẢI lọc ra các yêu cầu thuộc phạm vi quản lý của Manager đó. 

Danh sách hỗ trợ lọc theo:
* `type`: Phân loại đối tượng gửi (`TENANT` - Cư dân hoặc `OPERATOR` - Nhân viên vận hành)
* `status`: Trạng thái yêu cầu
* `keyword`: Tìm kiếm theo tiêu đề hoặc mã yêu cầu
* Phân trang 10 bản ghi/trang.

### AC-02 Tiếp nhận yêu cầu

KHI Manager thực hiện tiếp nhận yêu cầu đang có trạng thái `NEW` hoặc `PENDING`
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu thành `RECEIVED` và lưu lại thông tin cập nhật.

### AC-03 Đặt lịch hẹn xử lý (Schedule)

KHI Manager lên lịch xử lý yêu cầu và chọn ngày giờ cụ thể
HỆ THỐNG PHẢI lưu lịch hẹn (`appoint_schedule`), tự động chuyển trạng thái yêu cầu sang `IN_PROGRESS` (Đang xử lý) và hiển thị lịch hẹn lên dòng thời gian.

### AC-04 Hoàn thành yêu cầu

KHI Manager xác nhận hoàn thành yêu cầu và nhập đầy đủ ghi chú giải quyết cùng ảnh xác nhận (tùy chọn)
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu thành `DONE` và lưu trữ nội dung ghi chú cùng ảnh chụp hoàn thành.

### AC-05 Từ chối yêu cầu

KHI Manager từ chối yêu cầu và nhập lý do từ chối
HỆ THỐNG PHẢI cập nhật trạng thái yêu cầu thành `REJECTED`, lưu trữ lý do từ chối và hiển thị phản hồi cho cư dân.

### AC-06 Xem chi tiết & Lịch sử xử lý

KHI Manager truy cập trang chi tiết yêu cầu
HỆ THỐNG PHẢI hiển thị thông tin người gửi, phòng, cơ sở, nội dung mô tả, ảnh đính kèm ban đầu, ảnh hoàn thành (nếu có), lịch hẹn xử lý, và tái hiện dòng thời gian lịch sử trạng thái (từ lúc tạo, tiếp nhận, lên lịch, hoàn thành hoặc từ chối).

## 4. API Contract

Mọi hành động của Manager được điều phối bởi [ManagerTicketsServlet.java](file:///d:/Ki_5/hostel_management/src/main/java/com/quanlyphongtro/controller/manager/ManagerTicketsServlet.java) thông qua phương thức GET và POST:

### 4.1 Xem danh sách yêu cầu
* **URL:** `GET /manager/tickets`
* **Query Parameters:**
  * `type`: Phân loại nguồn gửi (`TENANT` hoặc `OPERATOR`, mặc định `TENANT`)
  * `keyword`: Tìm kiếm theo tiêu đề hoặc mã yêu cầu
  * `status`: Trạng thái cần lọc
  * `page`: Trang hiện tại (mặc định trang 1)

### 4.2 Xem chi tiết yêu cầu
* **URL:** `GET /manager/tickets/{id}`

### 4.3 Tiếp nhận yêu cầu
* **URL:** `POST /manager/tickets/{id}/receive`
* **Ràng buộc:** Chỉ cho phép tiếp nhận nếu yêu cầu ở trạng thái `NEW` hoặc `PENDING`.

### 4.4 Từ chối yêu cầu
* **URL:** `POST /manager/tickets/{id}/reject`
* **Form Parameters:** `reason` (Lý do từ chối - bắt buộc)

### 4.5 Đặt lịch hẹn xử lý
* **URL:** `POST /manager/tickets/{id}/schedule`
* **Form Parameters:** `appointmentDate` (Định dạng ngày giờ)

### 4.6 Hoàn thành yêu cầu
* **URL:** `POST /manager/tickets/{id}/complete`
* **Enctype:** `multipart/form-data`
* **Form Parameters:**
  * `notes`: Ghi chú hoàn thành (bắt buộc)
  * `after_images`: Các file ảnh thực tế sau khi sửa chữa (upload dạng Multipart)

## 5. Ràng buộc kỹ thuật (Technical Constraints)

* Dữ liệu yêu cầu được lưu trữ trong bảng `dbo.requests` trong cơ sở dữ liệu.
* Manager chỉ có quyền xem và xử lý các yêu cầu liên quan đến cơ sở mình phụ trách. Việc kiểm tra quyền quản lý được thực hiện bằng cách đối chiếu trường `manager_id` của cơ sở chứa phòng của người thuê gửi yêu cầu.
* Ảnh hoàn thành (`after_images`) tải lên qua form được xác thực định dạng đuôi file và kiểu MIME trước khi ghi vào thư mục `/uploads/requests/` và lưu tên file dạng UUID vào cột `attachment_urls2` (ngăn cách bởi dấu phẩy nếu có nhiều ảnh).
* Dòng thời gian lịch sử xử lý (History timeline) được sinh động hóa từ dữ liệu lịch sử trạng thái của yêu cầu trong DB.
* Hệ thống tuyệt đối không thực hiện xóa vật lý các bản ghi yêu cầu trong DB.

## 6. Phụ thuộc (Dependencies)

* **Quản lý Cư dân (Tenants) & Phòng:** Cung cấp thông tin người gửi, tên phòng và cơ sở của yêu cầu.
* **Quản lý Vận hành (Operators):** Dùng cho việc tiếp nhận hoặc gán xử lý yêu cầu hệ thống/chỉ số điện nước.
* **Bộ xác thực file tải lên:** Để kiểm tra độ an toàn của tệp ảnh trước khi lưu trữ máy chủ.
* **Audit Log:** Ghi log thao tác thay đổi trạng thái của Manager.

## 7. Quy tắc nghiệp vụ (Business Rules)

### BR-01
Yêu cầu mới gửi lên từ cư dân mặc định có trạng thái ban đầu là `PENDING` hoặc `NEW`.

### BR-02
Chỉ cho phép tiếp nhận yêu cầu khi trạng thái hiện tại là `PENDING` hoặc `NEW`. Các yêu cầu đã đóng (DONE, REJECTED, CANCELLED) không thể mở lại hay xử lý.

### BR-03
Khi Manager cập nhật lịch hẹn xử lý sự cố thành công, hệ thống sẽ tự động chuyển đổi trạng thái yêu cầu sang `IN_PROGRESS`.

### BR-04
Khi Manager từ chối yêu cầu, bắt buộc phải nhập lý do từ chối (`reason`). 

### BR-05
Khi Manager xác nhận hoàn thành yêu cầu, bắt buộc phải nhập nội dung ghi chú kết quả sửa chữa (`notes`).

## 8. Định nghĩa trạng thái yêu cầu

| Trạng thái | Ý nghĩa trong hệ thống |
| --- | --- |
| `NEW` / `PENDING` | Yêu cầu mới khởi tạo, chờ xử lý |
| `RECEIVED` | Ban quản lý đã tiếp nhận yêu cầu |
| `ASSIGNED` | Yêu cầu đã được phân phối cho nhân sự (áp dụng cho Operator) |
| `IN_PROGRESS` | Ban quản lý đang tiến hành xử lý (đã cập nhật lịch hẹn) |
| `DONE` | Yêu cầu đã được xử lý hoàn thành |
| `REJECTED` | Yêu cầu bị Ban quản lý từ chối |
| `CANCELLED` | Yêu cầu do Cư dân tự động hủy bỏ |

Luồng chuyển đổi trạng thái chính của cư dân:
```text
NEW / PENDING → RECEIVED → IN_PROGRESS → DONE
```

## 9. Ngoài phạm vi (Out of Scope)

* Tự động điều phối công việc/phân lịch thông minh cho nhân sự.
* Đánh giá chất lượng xử lý của cư dân (Rating/Feedback).
* Quản lý kho vật tư tiêu hao phục vụ sửa chữa.
* Thanh toán chi phí sửa chữa dịch vụ trực tiếp trên trang yêu cầu.
