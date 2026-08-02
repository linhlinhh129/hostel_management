# Quickstart Validation Guide: Equalize Deposit Amount with Room Fee

## Prerequisites
- Application running locally (e.g. `http://localhost:8080/HostelManagement`)
- Login credentials for Manager and Tenant accounts.

## Validation Scenarios

### Scenario 1: Verify Room Deposit Equalization on Contract Creation
1. Navigate to **Manager Dashboard** -> **Quản lý hợp đồng** -> **Tạo Hợp Đồng Mới**.
2. Select a room with room fee = `2,700,000 đ`.
3. Verify that the deposit amount automatically reflects `2,700,000 đ`.

### Scenario 2: Verify Tenant Contract View
1. Login as Tenant.
2. Navigate to **Chi tiết hợp đồng**.
3. Check **Điều 2: Giá thuê và hình thức thanh toán**.
4. Confirm text reads: `Bên B đặt cọc cho bên A số tiền là: 2,700,000 đ` (matching room fee).

### Scenario 3: Verify Manager Printable Contract
1. Login as Manager.
2. Open contract detail and click **In Hợp Đồng / Lưu PDF**.
3. Verify Điều 2 in print preview shows deposit equal to monthly rent.
