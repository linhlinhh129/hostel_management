# Context Document – Request Management

## Feature Overview

**Feature Name:** Request Management  
**Status:** Draft  
**Author:** Business Analyst  
**Reviewer:** Product Owner  
**Date:** 2026-06-10  
**Priority:** High

---

## Business Context

Người thuê cần một kênh chính thức để gửi các yêu cầu hỗ trợ, phản ánh sự cố hoặc đề xuất đến Ban quản lý trong suốt thời gian thuê phòng.

Hiện nay nhiều hoạt động trao đổi vẫn được thực hiện qua điện thoại, tin nhắn hoặc các kênh không chính thức, gây khó khăn trong việc theo dõi, phân loại và quản lý tiến độ xử lý.

Tính năng Request Management được xây dựng nhằm:

- Chuẩn hóa quy trình tiếp nhận yêu cầu từ người thuê.
- Giảm phụ thuộc vào trao đổi thủ công.
- Cho phép người thuê theo dõi tiến độ xử lý.
- Tăng tính minh bạch trong quá trình vận hành.
- Hỗ trợ lưu trữ lịch sử yêu cầu phục vụ kiểm tra và đối soát.

Tính năng này là một phần quan trọng trong chiến lược số hóa quy trình vận hành của hệ thống quản lý nhà trọ.

---

## Business Goals

### Goal 1: Chuẩn hóa quy trình tiếp nhận yêu cầu

Tất cả yêu cầu từ người thuê phải được gửi thông qua hệ thống thay vì các kênh trao đổi rời rạc.

### Goal 2: Tăng khả năng theo dõi

Người thuê có thể xem trạng thái xử lý của từng yêu cầu trong suốt vòng đời xử lý.

### Goal 3: Tăng hiệu quả vận hành

Ban quản lý có thể dễ dàng tiếp nhận, phân loại và xử lý yêu cầu từ người thuê.

### Goal 4: Đảm bảo truy vết dữ liệu

Mọi yêu cầu và thay đổi trạng thái đều được lưu trữ để phục vụ kiểm tra, báo cáo và kiểm toán.

---

## User Personas

### Tenant (Người thuê)

Là người đang thuê phòng trong hệ thống.

Nhu cầu:

- Gửi yêu cầu hỗ trợ.
- Báo cáo sự cố phát sinh.
- Theo dõi trạng thái xử lý.
- Xem lại lịch sử yêu cầu đã gửi.

---

## User Journey

### Journey 1 – Tạo yêu cầu mới

1. Người thuê truy cập chức năng Quản lý yêu cầu.
2. Người thuê chọn Tạo yêu cầu mới.
3. Người thuê nhập:
   - Thể loại yêu cầu
   - Tiêu đề
   - Nội dung
   - Tệp đính kèm (nếu có)
4. Hệ thống kiểm tra dữ liệu.
5. Hệ thống tạo yêu cầu với trạng thái mặc định là PENDING.
6. Hệ thống trả kết quả thành công.

### Journey 2 – Xem danh sách yêu cầu

1. Người thuê truy cập chức năng Quản lý yêu cầu.
2. Hệ thống tải danh sách yêu cầu thuộc Tenant hiện tại.
3. Hệ thống sắp xếp theo thời gian tạo mới nhất.
4. Người thuê xem danh sách yêu cầu.

### Journey 3 – Xem chi tiết yêu cầu

1. Người thuê chọn một yêu cầu.
2. Hệ thống kiểm tra quyền truy cập.
3. Hệ thống hiển thị chi tiết yêu cầu.
4. Hệ thống hiển thị trạng thái xử lý hiện tại.

---

## Core Business Rules

### BR-01

Mỗi yêu cầu phải thuộc về đúng một Tenant.

### BR-02

Tenant chỉ được xem các yêu cầu do chính mình tạo.

### BR-03

Khi tạo mới, trạng thái mặc định của yêu cầu là:

```text
PENDING
```

### BR-04

Tiêu đề và Nội dung là thông tin bắt buộc.

### BR-05

Thể loại yêu cầu phải tồn tại trong hệ thống.

### BR-06

Tệp đính kèm chỉ được phép là:

```text
JPG
JPEG
PNG
```

### BR-07

Dung lượng tệp tối đa:

```text
5 MB
```

### BR-08

Mọi thay đổi trạng thái phải được lưu lịch sử.

---

## Request Lifecycle

### Initial State

```text
PENDING
```

### Available Statuses

```text
PENDING
IN_PROGRESS
COMPLETED
REJECTED
```

### State Description

| Status | Meaning |
|----------|----------|
| PENDING | Chờ xử lý |
| IN_PROGRESS | Đang xử lý |
| COMPLETED | Hoàn thành |
| REJECTED | Từ chối |

---

## Security Context

### Authentication

Người dùng phải đăng nhập hợp lệ trước khi truy cập các API của Request Management.

### Authorization

Tenant chỉ được phép:

- Tạo yêu cầu của chính mình.
- Xem yêu cầu của chính mình.

Tenant không được phép:

- Xem yêu cầu của Tenant khác.
- Chỉnh sửa dữ liệu của Tenant khác.

### Data Isolation

Toàn bộ dữ liệu yêu cầu phải được lọc theo Tenant ID.

Ví dụ:

```text
request.tenant_id = current_user.tenant_id
```

---

## Data Ownership

### Owner

Tenant

### Accessible By

- Tenant sở hữu yêu cầu
- Manager được phân quyền
- Administrator

---

## Integration Context

### Internal Systems

#### Authentication Service

Xác thực người dùng và cung cấp thông tin Tenant.

#### Tenant Service

Quản lý thông tin người thuê.

#### File Storage Service

Lưu trữ hình ảnh đính kèm.

#### Audit Log Service

Lưu lịch sử thay đổi trạng thái.

### Related Features

- Tenant Dashboard
- Security & Access Control
- Notification Management
- Profile Management

---

## Data Entities

### Request

Thông tin yêu cầu do Tenant tạo.

Các thuộc tính chính:

- requestId
- tenantId
- categoryId
- title
- content
- attachmentUrl
- status
- createdAt
- updatedAt

### Request Category

Danh mục yêu cầu.

Ví dụ:

- Bảo trì
- Khiếu nại
- Hỗ trợ kỹ thuật
- Đề xuất

### Request Status History

Lưu lịch sử thay đổi trạng thái.

Các thuộc tính:

- historyId
- requestId
- oldStatus
- newStatus
- changedBy
- changedAt

---

## Non-Functional Requirements

### Performance

- API Response Time < 500ms (p95)

### Scalability

- Hỗ trợ tối thiểu 10.000 yêu cầu.
- Hỗ trợ đồng thời nhiều Tenant.

### Security

- JWT Authentication
- HTTPS Only
- Tenant Data Isolation
- Audit Logging

### Availability

- Uptime ≥ 99.9%

---

## Error Scenarios

### Missing Required Fields

Điều kiện:

- Thiếu tiêu đề
- Thiếu nội dung

Kết quả:

```text
HTTP 400
REQ_001
```

### Invalid Category

Điều kiện:

- categoryId không tồn tại

Kết quả:

```text
HTTP 400
REQ_002
```

### Invalid Attachment

Điều kiện:

- Sai định dạng tệp

Kết quả:

```text
HTTP 400
REQ_003
```

### Unauthorized Access

Điều kiện:

- Chưa đăng nhập

Kết quả:

```text
HTTP 401
```

### Forbidden Access

Điều kiện:

- Truy cập yêu cầu của Tenant khác

Kết quả:

```text
HTTP 403
```

---

## Assumptions

- Tenant đã có tài khoản hợp lệ.
- Tenant đã được liên kết với phòng thuê.
- Danh mục yêu cầu đã được cấu hình trước trong hệ thống.
- Hệ thống lưu trữ tệp đã sẵn sàng.

---

## Out of Scope

Các chức năng sau không thuộc phạm vi của Request Management phiên bản hiện tại:

- Chat trực tiếp giữa Tenant và Ban quản lý.
- Bình luận trên yêu cầu.
- Đánh giá chất lượng xử lý yêu cầu.
- Push Notification thời gian thực.
- Email thông báo khi trạng thái yêu cầu thay đổi.
- SLA Monitoring.
- Escalation Workflow.