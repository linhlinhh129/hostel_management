# Feature Specification: Kiểm Thử Luồng Quản Lý Hợp Đồng (Toàn Hệ Thống)

**Feature Branch**: `tenant-contract-tests`

**Created**: 2026-07-21

**Status**: Clarified & Updated

**Input**: User description: "viết file test luồng hoạt động của 'quản lí hợp đồng của người thuê ' của cả hệ thống vào file"

## Clarifications

### Session 2026-07-21
- Q: Mâu thuẫn về tính năng Gia hạn/Chấm dứt giữa Manager và Tenant? → A: Giữ lại luồng Yêu cầu ở Tenant, Manager sẽ nhận yêu cầu nhưng xử lý offline.
- Q: Mâu thuẫn về việc Ký hợp đồng điện tử? → A: Xóa luồng Ký online của Tenant. Hệ thống chỉ quản lý hợp đồng giấy (in ra).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Tạo và In Hợp Đồng Mới (Manager) (Priority: P1)

Ban quản lý có thể tạo hợp đồng mới, hệ thống tự động điền thông tin phòng và có thể in ra bản cứng cho người thuê ký. Hệ thống cũng có thể tạo tài khoản cho người thuê từ hợp đồng này.

**Why this priority**: Đây là điểm khởi đầu của vòng đời hợp đồng, tạo ra dữ liệu cho toàn bộ hệ thống.

**Independent Test**: Test API tạo hợp đồng, kiểm tra thông tin tự động điền và tạo account.

**Acceptance Scenarios**:

1. **Given** Ban quản lý chọn phòng trống, **When** tạo hợp đồng, **Then** hệ thống tự động sinh `contract_id` và mã hợp đồng chuẩn `HD-{roomCode}-{date}-{seq}`.
2. **Given** một hợp đồng mới được tạo, **When** Manager chọn tính năng in, **Then** hệ thống xuất ra mẫu in hợp đồng giấy với đầy đủ dữ liệu.
3. **Given** Manager nhập email người thuê, **When** nhấn thêm người thuê, **Then** hệ thống tạo tài khoản ROLE_TENANT và gán `tenant_id` vào hợp đồng.

---

### User Story 2 - Xem Chi Tiết Hợp Đồng (Tenant & Manager) (Priority: P1)

Người thuê có thể xem chi tiết hợp đồng đang có hiệu lực của họ. Ban quản lý có thể xem tất cả các hợp đồng thuộc cơ sở mình quản lý.

**Why this priority**: Đảm bảo quyền truy cập và tính minh bạch thông tin, đồng thời bảo mật dữ liệu giữa các Tenant.

**Independent Test**: Kiểm tra phân quyền (Authorization) dựa trên ROLE và sở hữu.

**Acceptance Scenarios**:

1. **Given** Tenant đăng nhập, **When** truy cập hợp đồng của mình, **Then** hệ thống hiển thị đúng thông tin.
2. **Given** Tenant cố tình truy cập ID hợp đồng của người khác, **When** gửi request, **Then** hệ thống báo lỗi 403 Access Denied.
3. **Given** Manager quản lý Cơ sở A, **When** cố gắng xem hợp đồng thuộc Cơ sở B, **Then** hệ thống báo lỗi 403.

---

### User Story 3 - Gửi Yêu Cầu Chấm Dứt/Gia Hạn (Tenant) (Priority: P2)

Người thuê có thể gửi yêu cầu chấm dứt hợp đồng trước hạn hoặc gia hạn khi sắp hết hạn. Yêu cầu này sẽ được Manager tiếp nhận nhưng việc gia hạn/chấm dứt thực tế diễn ra offline ngoài hệ thống (hệ thống chỉ lưu trạng thái yêu cầu).

**Why this priority**: Hỗ trợ quy trình nghiệp vụ giao tiếp giữa Tenant và Manager.

**Independent Test**: Kiểm tra việc tạo mới bản ghi yêu cầu (request) liên kết với hợp đồng hiện tại.

**Acceptance Scenarios**:

1. **Given** hợp đồng đang có hiệu lực, **When** người thuê gửi yêu cầu chấm dứt/gia hạn, **Then** hệ thống ghi nhận yêu cầu và hiển thị cho Manager.

---

### User Story 4 - Quản Lý Trạng Thái Hợp Đồng (Manager) (Priority: P2)

Manager có thể chuyển trạng thái hợp đồng thành INACTIVE khi hết hiệu lực và xóa (soft delete) các hợp đồng INACTIVE.

**Acceptance Scenarios**:

1. **Given** hợp đồng đang ở trạng thái INACTIVE, **When** Manager bấm Xóa, **Then** hệ thống thực hiện soft delete thành công và ghi AuditLog.
2. **Given** hợp đồng đang ACTIVE, **When** Manager bấm Xóa, **Then** hệ thống từ chối và báo lỗi.

---

### Edge Cases

- Xử lý khi Manager nhập sai định dạng ngày tháng (ngày kết thúc < ngày lập).
- Hệ thống xử lý thế nào nếu người thuê nhấn gửi yêu cầu gia hạn nhiều lần liên tiếp?
- Tạo hợp đồng cho phòng đã có hợp đồng ACTIVE khác.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Kịch bản kiểm thử (Test Suite) PHẢI bao phủ luồng tạo, in hợp đồng và tạo tài khoản của Manager.
- **FR-002**: Kịch bản kiểm thử PHẢI xác minh cơ chế phân quyền: Tenant chỉ xem hợp đồng của mình, Manager chỉ xem hợp đồng thuộc cơ sở quản lý.
- **FR-003**: Kịch bản kiểm thử PHẢI kiểm tra việc gửi yêu cầu gia hạn hoặc chấm dứt từ Tenant và ghi nhận ở phía Manager.
- **FR-004**: Kịch bản kiểm thử PHẢI kiểm tra luồng xóa hợp đồng (chỉ cho phép khi INACTIVE).
- **FR-005**: Kịch bản kiểm thử PHẢI xác minh AuditLog được ghi lại khi Tạo, Thêm Tenant và Xóa.

### Key Entities

- **Contract**: Hợp đồng thuê phòng (chứa thông tin về phòng, giá, trạng thái, thời hạn).
- **Tenant**: Người thuê.
- **Manager**: Ban quản lý.
- **ContractRequest**: Yêu cầu gia hạn/chấm dứt từ người thuê.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% các Acceptance Scenarios được tự động hóa.
- **SC-002**: Test Coverage cho luồng Contract của cả Manager và Tenant đạt tối thiểu 85%.
- **SC-003**: Các bài test chạy hoàn tất dưới 2 phút.

## Assumptions

- Tính năng "Xử lý gia hạn/thanh lý hợp đồng" không được làm trong hệ thống (Manager tự làm ở ngoài).
- Tính năng ký điện tử là Out of scope.