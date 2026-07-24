# PLAN.md

## Mục tiêu
- Xây dựng chức năng quản lý khoản phí và giá dịch vụ (Điện, Nước, Phí dịch vụ) cho từng cơ sở.
- Cho phép Ban quản lý cập nhật giá mới và ghi chú lại lý do thay đổi.
- Lưu lại lịch sử cập nhật giá để đảm bảo tính minh bạch và audit theo thời gian.

## Phạm vi
- Hiển thị danh sách các giá dịch vụ hiện hành (`ELECTRICITY`, `WATER`, `SERVICE_FEE`).
- Cập nhật giá dịch vụ mới cho cơ sở mà Ban quản lý đang phụ trách thông qua giao diện pop-up.
- Hiển thị lịch sử cập nhật (old_price, new_price, ghi chú, người cập nhật, thời gian).
- Áp dụng Validation từ phía Server để đảm bảo tính toàn vẹn (giá > 0, kiểu số hợp lệ).

## Giải pháp kỹ thuật
### 1. Dữ liệu (Database schema)
- Bảng `facilities`: Lưu trực tiếp giá điện, nước, phí dịch vụ hiện tại (các cột `electricity_price`, `water_price`, `service_fee`).
- Bảng lịch sử (VD: `service_price_histories` hoặc tương đương):
  - `facility_id`, `price_type` (ELECTRICITY, WATER, SERVICE_FEE).
  - `old_price`, `new_price`, `note` (Ghi chú).
  - `created_by` (Manager ID), `created_at` (Thời gian thay đổi).

### 2. Luồng xử lý
- **Hiển thị danh sách (Index):** `ServicePricePageServlet` lấy giá trị hiện hành từ DB dựa trên `managerId` và forward tới `index.jsp`.
- **Lịch sử cập nhật (History):** Nhận tham số `priceType`, lấy danh sách lịch sử phân trang từ DB, forward tới `history.jsp`.
- **Cập nhật giá mới (Update):** Form gửi POST về `/manager/service-prices?action=update`. Server parse số tiền, validate (>= 0), cập nhật bảng `facilities` trong 1 Transaction, đồng thời insert 1 record vào bảng lịch sử. Chuyển hướng lại trang danh sách nếu thành công. Nếu thất bại, forward lại `index.jsp` kèm `errorMessage`.

### 3. Cấu trúc Servlet / JSP
- **Servlet:** `ServicePricePageServlet.java` (`@WebServlet("/manager/service-prices")`)
- **GET `/manager/service-prices`**: Load dữ liệu hiện hành -> `index.jsp`
- **GET `/manager/service-prices?action=history&priceType=...`**: Load lịch sử -> `history.jsp`
- **POST `/manager/service-prices?action=update`**: Form submit với các params `priceType`, `newPrice`, `note`. 

## Quy tắc nghiệp vụ
- `newPrice` phải hợp lệ (Kiểu số) và >= 0. Nếu không, bắt Exception và forward lại form với lỗi.
- `priceType` chỉ nằm trong danh sách cho phép (`ELECTRICITY`, `WATER`, `SERVICE_FEE`).
- Mức giá mới chỉ áp dụng cho các hóa đơn sinh ra **sau** thời điểm cập nhật. Hóa đơn cũ không bị thay đổi.
- Mọi lần cập nhật phải được log lại.

## Bảo mật và Phân quyền
- **Authentication & Authorization:** Chỉ user có role `MANAGER` mới được truy cập Servlet này. (Sử dụng `getCurrentUser()` và kiểm tra role).
- Trả về lỗi HTTP 403 nếu cố truy cập khi không có quyền.
- Trả về HTTP 400 hoặc đẩy Error Message khi tham số không hợp lệ.

## Hiệu năng
- Các truy vấn lấy giá và lịch sử cần có INDEX trên cột `facility_id` và `price_type`.
- Xử lý phân trang cho trang Lịch sử để tránh tải quá nhiều dữ liệu (`LIMIT/OFFSET`).

## Rủi ro
- Lỗi đồng bộ dữ liệu nếu cập nhật giá trong lúc hệ thống đang chạy batch sinh hóa đơn tự động.
- Không có cơ chế Rollback cho các hóa đơn cũ, phải xử lý cẩn thận thông báo UX.

## Giả định
- Ban quản lý chỉ quản lý duy nhất 1 cơ sở hoặc luồng lấy cơ sở sẽ tự động ánh xạ thông qua `managerId`.
- Dữ liệu hiển thị UI bao gồm 3 khoản cố định: Giá điện, giá nước, phí dịch vụ.
