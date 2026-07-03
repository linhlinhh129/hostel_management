# TASKS: Xem hợp đồng thuê (Tenant Contract)

Dựa trên yêu cầu trong `SPEC.md` và `CONTEXT.md`, dưới đây là danh sách các công việc (tasks) cần thực hiện để hoàn thành chức năng **Xem hợp đồng thuê** dành cho Khách thuê (Tenant).

## 1. Database & DAO (Data Access Object)
- [ ] Kiểm tra và cập nhật Entity/Model `Contract` đảm bảo có đầy đủ các trường thông tin cần thiết: Mã hợp đồng, Mã phòng, Ngày bắt đầu, Ngày hết hạn, Tiền thuê, Tiền cọc, Trạng thái hợp đồng, các loại phí (Điện, nước, internet, dịch vụ), và `tenant_id`.
- [ ] Viết hàm `getContractsByTenantId(int tenantId)` trong `ContractDAO` để lấy danh sách hợp đồng của một user.
- [ ] Viết hàm `getContractByIdAndTenantId(int contractId, int tenantId)` trong `ContractDAO` để lấy chi tiết một hợp đồng và đảm bảo hợp đồng đó thuộc về user đang truy cập.

## 2. Controller (Servlet)
- [ ] Tạo `TenantContractServlet` để xử lý các yêu cầu liên quan đến hợp đồng của khách thuê.
- [ ] Cấu hình routing cho Servlet (ví dụ: `/tenant/contracts/*`).
- [ ] Xử lý `doGet`:
  - Kiểm tra xem người dùng đã đăng nhập và có role `TENANT` hay chưa (nếu chưa có Filter xử lý chung).
  - Lấy `tenantId` từ Session.
  - Nếu URL là `/tenant/contracts` (hoặc không truyền ID): Gọi `getContractsByTenantId`, set attribute và forward sang trang danh sách hợp đồng.
  - Nếu URL có truyền ID hợp đồng (ví dụ `/tenant/contracts?id=...` hoặc path variable): Gọi `getContractByIdAndTenantId`.
    - Nếu hợp đồng không tồn tại hoặc không thuộc `tenantId`, trả về lỗi `403` hoặc `404` (redirect/forward tới trang báo lỗi).
    - Nếu thành công, set attribute và forward sang trang chi tiết hợp đồng.

## 3. View (JSP/UI)
- [ ] Tạo file `tenant/contracts/list.jsp` (hoặc tương tự):
  - Hiển thị danh sách các hợp đồng dưới dạng bảng hoặc card.
  - Hiển thị các trường: Mã hợp đồng, Mã phòng, Ngày bắt đầu, Ngày hết hạn, Tiền thuê, Tiền cọc, Trạng thái.
  - Xử lý trạng thái rỗng ("Bạn chưa có hợp đồng thuê nào.").
  - Nút "Xem chi tiết" dẫn đến trang chi tiết của hợp đồng tương ứng.
- [ ] Tạo file `tenant/contracts/detail.jsp`:
  - Hiển thị đầy đủ thông tin hợp đồng ở chế độ chỉ đọc (Read-only).
  - Bao gồm: Mã hợp đồng, Thông tin phòng, Địa chỉ, Ngày lập, Ngày bắt đầu, Ngày hết hạn, Tiền thuê, Tiền cọc, các loại phí (Điện, nước, Internet, dịch vụ), Điều khoản, Trạng thái.
  - Đảm bảo KHÔNG có các nút thao tác như Thêm/Sửa/Xóa.

## 4. Kiểm thử (Testing & Validation)
- [ ] Test trường hợp user khách thuê xem danh sách hợp đồng của mình.
- [ ] Test trường hợp user khách thuê chưa có hợp đồng nào.
- [ ] Test trường hợp user xem chi tiết một hợp đồng hợp lệ.
- [ ] Test bảo mật (IDOR): Đăng nhập với tư cách Khách thuê A, cố tình thay đổi URL để truy cập ID hợp đồng của Khách thuê B xem có bị chặn (403) không.
- [ ] Test trường hợp ID hợp đồng không tồn tại (404).
- [ ] Test trường hợp chưa đăng nhập mà truy cập link (401).
