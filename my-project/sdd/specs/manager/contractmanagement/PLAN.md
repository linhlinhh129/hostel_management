# PLAN: Kế hoạch Thực thi Quản lý Hợp đồng (Manager)

**Status:** Planned  
**Date:** 2026-07-24  
**Author:** Nhật 
**Priority:** High  
**Estimated Duration:** 3 Sprints (6 tuần)

---

## 1. Tổng quan Giải pháp

Feature **Quản lý hợp đồng** cung cấp quy trình quản lý vòng đời hợp đồng thuê phòng trọ dành cho Ban quản lý (Manager), bao gồm: lập hợp đồng mới, tra cứu danh sách, xem chi tiết, tự động trích xuất thông tin phòng/giá/cọc (tự động bằng giá cọc với giá phòng theo quy định mới), tạo nhanh tài khoản người thuê (`TENANT`) từ hợp đồng (nhận diện bằng số CMND/CCCD duy nhất), in hợp đồng theo mẫu chuẩn và xóa mềm các hợp đồng đã hết hiệu lực (`INACTIVE`).

**Kiến trúc:**
- **Backend Entry Point**: `ContractServlet.java` (`/manager/contracts/*`) xử lý điều phối request (List, Detail, Create, Print, Add-Tenant, Delete).
- **Service Layer**: `ContractServiceImpl.java` thực thi quy tắc nghiệp vụ validate dữ liệu, tự động sinh mã hợp đồng (`HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`), chụp ảnh snapshot thông tin phòng/giá tại thời điểm lập hợp đồng, kiểm tra xung đột hợp đồng `ACTIVE` trên phòng, và giao dịch liên kết người thuê (`tenant_id`).
- **Data Access Layer**: `ContractDAOImpl.java` / `ContractDAO.java` sử dụng JDBC thuần (`PreparedStatement`), truy vấn phân quyền theo cơ sở được phân công (`manager_facilities`), hỗ trợ transaction khi tạo hợp đồng & gán người thuê. Tra cứu tài khoản người thuê cũ theo CCCD (`getUserIdByIdentityNumber`).
- **Frontend JSP**: 
  - `list.jsp`: Danh sách hợp đồng phân trang, bộ lọc tìm kiếm theo tên khách thuê/mã hợp đồng.
  - `detail.jsp`: Chi tiết hợp đồng, hiển thị đầy đủ thông tin bên A, bên B, thông tin phòng, giá thuê, tiền cọc và nút hành động (In, Tạo tài khoản tenant, Xóa).
  - `create.jsp`: Form lập hợp đồng mới, tự động fill dữ liệu phòng và số tiền cọc bằng giá phòng khi chọn.
  - `add_tenant.jsp`: Form điền thông tin người thuê từ hợp đồng để cấp/tái kích hoạt tài khoản.
  - `print.jsp`: Giao diện in hợp đồng thuê phòng trọ đúng chuẩn pháp lý.

---

## 2. Giai đoạn Thực thi (Phân chia công việc)

### Giai đoạn 1: Cơ sở dữ liệu & Data Access (Database & DAO Layer)
- Cấu trúc bảng `contracts` (`contract_id`, `code`, `room_id`, `tenant_id`, `tenant_full_name`, `tenant_dob`, `tenant_address`, `tenant_identity`, `tenant_identity_date`, `tenant_identity_place`, `tenant_phone`, `rent_price`, `deposit_amount`, `amount_in_words`, `signed_date`, `start_date`, `end_date`, `status`, `created_by`, `created_at`, `updated_at`, `deleted_at`).
- Xây dựng `ContractDAO.java` với các hàm query: `getContractsByManager`, `getContractById`, `createContract`, `updateContractStatus`, `getUserIdByIdentityNumber`, `softDeleteContract`.
- Đảm bảo tất cả query đều join `rooms` và `facilities` để kiểm tra phân quyền Manager cơ sở (`manager_facilities`).

### Giai đoạn 2: Nghiệp vụ Backend & Servlet Routing (Service & Controller Layer)
- Xây dựng `ContractServiceImpl.java`:
  - Logic tự động sinh mã `code` (`HD-{roomCode}-{signedDate:yyyyMMdd}-{seq}`).
  - Validation: Bắt buộc họ tên, CCCD, ngày lập, ngày hết hạn (`endDate >= signedDate`), phòng không được có hợp đồng `ACTIVE` khác.
  - Giao dịch tạo tài khoản `TENANT` từ hợp đồng (kiểm tra đối soát tài khoản cũ theo CCCD duy nhất, gửi pass tạm, kích hoạt lại người dùng cũ nếu bị vô hiệu hóa).

### Giai đoạn 3: Giao diện & Tích hợp (Views & Testing)
- Hoàn thiện các giao diện `list.jsp`, `detail.jsp`, `create.jsp`, `add_tenant.jsp`, `print.jsp`.
- Kiểm thử tích hợp luồng lập hợp đồng, in ấn, thêm người thuê cũ và xóa mềm hợp đồng INACTIVE.

| Hành động | Endpoint | JSP tương ứng | Logic xử lý chính |
|---|---|---|---|
| Lưu tạo Tenant | `POST /manager/contracts/add-tenant`| `add_tenant.jsp`| Validate Account, tạo user, gán `tenant_id`, đổi status phòng `OCCUPIED`. |
| Xóa hợp đồng | `POST /manager/contracts/delete` | (Redirect) | Chuyển `status` = `INACTIVE` (hoặc soft delete) nếu đủ điều kiện. |

*(Lưu ý: Mặc dù code có thể chứa thêm endpoint `/manager/contracts/extend`, nhưng tính năng Gia hạn được đặt là Out Of Scope trong SPEC, do vậy có thể bỏ qua hoặc coi là tính năng bổ sung không chính thức).*

## 4. Quy tắc nghiệp vụ & Validation
- Form yêu cầu kiểm tra kiểu dữ liệu và bắt buộc nhập đối với các trường thông tin cá nhân khách thuê và phòng thuê.
- Validation Backend: `NumberFormatException`, `IllegalArgumentException`, `DateTimeParseException` khi nhận POST parameters. Nếu lỗi sẽ forward lại form kèm `errorMessage`.
- Không thể tạo hợp đồng nếu phòng đã có hợp đồng `ACTIVE`.
- Chỉ người có quyền `MANAGER` hoặc `ADMIN` (xác thực qua session `currentUser`) mới được sử dụng các endpoint này.
- Khi tạo tài khoản Tenant: Email phải duy nhất, CCCD/Phone không trùng lặp.

## 5. Rủi ro & Giả định
- **Rủi ro:** Khi giá dịch vụ (Điện, Nước) trong bảng `facilities` thay đổi, bản in hợp đồng cũ có thể hiển thị sai lệch nếu không lưu snapshot giá dịch vụ. (Cách khắc phục: Lưu text nội dung in hoặc snapshot toàn bộ chi phí tại thời điểm `signed_date`).
- **Giả định:** Người thuê có thể không cần tạo tài khoản hệ thống ngay lúc làm hợp đồng (lưu trữ offline), nên trường `tenant_id` trong hợp đồng được phép NULL ban đầu.
>>>>>>> 0a012ed157162344f6596bc5e8c74df558911c85
