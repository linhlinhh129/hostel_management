# Feature: Quản lý hợp đồng (Contract Management) — FULL SPECIFICATION
Status: Approved | Reviewer: Tech Lead & Product Owner
Author: IT Business Analyst | Date: 2026-06-13
Risk Level: High | Related Specs: db-schema.spec.md, auth.spec.md

## 1. Business Context & Goals
Hợp đồng thuê phòng là văn bản pháp lý trung tâm giữa Ban Quản Lý (MANAGER) và Khách thuê (TENANT). Việc số hóa dữ liệu hợp đồng giúp quản lý tập trung giá thuê, tình trạng tiền cọc, tự động hóa cảnh báo sắp hết hạn/quá hạn và lưu trữ hình ảnh chứng từ an toàn.
**Business Goals:**
- Chuẩn hóa luồng tạo hợp đồng và cập nhật trạng thái phòng.
- Giảm rủi ro mất mát chứng từ bằng cách lưu trữ file số hóa.
- Tự động hóa cảnh báo quá hạn bằng Scheduler (Không cần người dùng F5).

## 2. Stakeholders & User Personas
- **MANAGER (Ban Quản Lý):** Người thao tác chính. Cần xem danh sách, lọc trạng thái, theo dõi tiền cọc và tải lên (upload) bản scan hợp đồng cho các cơ sở được Admin giao phó.

## 3. User Stories (all paths)
- Story 1 (Happy Path): As a MANAGER, I want to xem danh sách tất cả hợp đồng thuộc cơ sở của tôi so that tôi nắm được thông tin giá thuê, tiền cọc, thời hạn của từng phòng.
- Story 2 (Happy Path): As a MANAGER, I want to lọc hợp đồng theo các trạng thái (ACTIVE, EXPIRING_SOON, OVERDUE) so that tôi có thể theo dõi và hối thúc khách gia hạn.
- Story 3 (Happy Path): As a MANAGER, I want to tải lên (upload) file scan hợp đồng giấy so that hệ thống lưu trữ làm minh chứng pháp lý.
- Story 4 (System Path): As a SYSTEM, when hợp đồng qua ngày hết hạn, I want tự động chuyển trạng thái thành OVERDUE so that MANAGER không cần theo dõi thủ công.

## 4. Acceptance Criteria (EARS — exhaustive)
# AC01 - Hiển thị danh sách (List View)
- WHEN MANAGER truy cập URL `/manager/contracts`
- THE SYSTEM SHALL truy xuất dữ liệu từ Database thông qua `ContractDAO`
- AND forward sang `/WEB-INF/views/contracts/list.jsp` để hiển thị danh sách.

# AC02 - Phân quyền dữ liệu (Data Isolation)
- WHILE MANAGER đang sử dụng tính năng này
- THE SYSTEM SHALL chỉ truy vấn các hợp đồng có `room_id` thuộc về `facility_id` mà MANAGER đó được phân quyền (Lọc qua `INNER JOIN`).

# AC03 - Upload file chứng từ
- WHEN MANAGER tải lên (upload) file chứng từ hợp đồng
- THE SYSTEM SHALL lưu file vật lý vào thư mục `/uploads` trên server
- AND chỉ lưu đường dẫn (`file_path`) vào Database (Theo đúng chuẩn ADR-005).

# AC04 - Quét trạng thái tự động
- WHEN `ContractScheduler` chạy ngầm vào 00:00 hằng ngày
- THE SYSTEM SHALL tự động UPDATE trạng thái thành `OVERDUE` đối với các hợp đồng có `expiry_date < CURRENT_DATE`.

# AC05 - Trạng thái tiền cọc
- WHEN MANAGER cập nhật thông tin tiền cọc
- THE SYSTEM SHALL lưu trữ dưới một trong các Enum: `UNPAID`, `PARTIAL`, `PAID`.

## 5. Servlet Contracts (HTTP Requests & Views)
*Lưu ý: Tuân thủ kiến trúc Servlet -> JSP. Không dùng JSON API.*

# 5.1. Xem danh sách hợp đồng
- Endpoint: GET `/manager/contracts`
- Query Params: `status` (string, optional), `page` (number, optional)
- Action:
  1. Kiểm tra session `currentUser` (Yêu cầu Role: MANAGER).
  2. Lấy dữ liệu từ `ContractService`.
  3. Set `request.setAttribute("contracts", contractList)`.
  4. Forward tới `/WEB-INF/views/contracts/list.jsp`.

# 5.2. Cập nhật trạng thái tiền cọc
- Endpoint: POST `/manager/contracts/deposit`
- Form Data: `contractId` (BIGINT, required), `depositStatus` (String, required).
- Action:
  1. Validate dữ liệu đầu vào.
  2. `ContractDAO.updateDepositStatus()`.
  3. Chuyển hướng `response.sendRedirect(request.getContextPath() + "/manager/contracts?success=true")`.

## 6. Data Models & DB Schema Changes
Table `contracts` (Tạo mới):
- `id`: BIGINT (PK, IDENTITY)
- `room_id`: BIGINT (FK -> rooms.id)
- `tenant_rep_id`: BIGINT (FK -> users.id)
- `rent_price`: DECIMAL(18,2)
- `deposit_amount`: DECIMAL(18,2)
- `deposit_status`: VARCHAR(20) [UNPAID, PARTIAL, PAID]
- `billing_cycle`: INT
- `move_in_date`: DATE
- `expiry_date`: DATE
- `document_path`: VARCHAR(255) (Chỉ lưu path, VD: /uploads/contracts/file.pdf)
- `status`: VARCHAR(20) [ACTIVE, TERMINATING, EXPIRING_SOON, OVERDUE]

## 7. Non-Functional Requirements
- Performance: Trang `/manager/contracts` hiển thị < 500ms (p95).
- Security: Chặn triệt để SQL Injection bằng `PreparedStatement`. Toàn bộ Servlet được bảo vệ bởi `RoleFilter`.
- Scalability: `ContractScheduler` phải thiết kế để quét và update hàng ngàn record nhanh chóng mà không gây lock table (Table Lock).
- Availability: Uptime 99.9%.

## 8. Error Handling Matrix
- Error: Không có quyền truy cập (Sai Role / Sai Cơ sở)
  -> Action: Log bằng SLF4J (cảnh báo bảo mật) -> Forward tới `/WEB-INF/views/error/403.jsp`.
- Error: Lỗi đứt kết nối Database (SQLException)
  -> Action: Log error stack trace -> Forward tới `/WEB-INF/views/error/500.jsp`.
- Error: Upload file không đúng định dạng (VD: exe, bat)
  -> Action: Set `request.setAttribute("error", "File không hợp lệ")` -> Forward lại trang hiện tại.

## 9. Edge Cases & Corner Cases
- Khách hàng đã quá hạn hợp đồng (OVERDUE) nhưng vẫn ở và đóng tiền (MANAGER quên gia hạn trên hệ thống). -> Giải pháp: Hệ thống không khóa hoạt động đóng tiền, chỉ hiện cảnh báo đỏ trên giao diện.
- Upload file dung lượng quá lớn (VD: > 10MB) gây đầy ổ cứng Server Tomcat. -> Cần validation filter file size.

## 10. Dependencies & Integration Points
- Rooms Module: Khi insert hợp đồng mới có status `ACTIVE`, bắt buộc phải trigger Update trạng thái phòng trong bảng `rooms` thành `OCCUPIED` (Sử dụng JDBC Transaction `conn.setAutoCommit(false)`).
- File System: Thư mục `/uploads` phải được cấu hình Read/Write permission đúng đắn trên máy chủ triển khai Tomcat.

## 11. Testing Requirements
- Unit Test: Dùng Mockito để test `ContractService.java` khi gọi `ContractDAO` để kiểm tra phân quyền lấy đúng `facility_id`.
- Integration Test: Test JDBC `PreparedStatement` lưu dữ liệu hợp lệ vào SQL Server. Test Transaction khi Insert hợp đồng kết hợp Update phòng.
- E2E / UI Test: Dùng browser test tính năng submit Form Upload File từ JSP, đảm bảo file lưu vào đúng thư mục `/uploads`.

## 12. Rollout Plan
- Triển khai chức năng tạo hợp đồng và giao diện Dashboard trước (Sprint 2).
- Chức năng Upload File và Scheduler tính ngày quá hạn sẽ rollout ở Sprint 3 để tránh rủi ro quá tải.

## 13. Open Questions (must resolve before implementation)
- Q1: Việc gia hạn hợp đồng sẽ là UPDATE record hợp đồng cũ, hay INSERT record hợp đồng mới để giữ lịch sử? — Owner: System Architect — Due: 2026-06-15.
- Q2: Cấu hình Max File Size cho file chứng từ hợp đồng là bao nhiêu MB? — Owner: Product Owner — Due: 2026-06-15.

