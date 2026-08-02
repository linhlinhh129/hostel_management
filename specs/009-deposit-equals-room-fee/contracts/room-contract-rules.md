# Contract Rules: Room & Lease Agreement Deposit Equalization

## 1. Room Management API / DAO Contract
- **Rule**: When adding or updating a room record, if `deposit_amount` is null or zero, `deposit_amount` MUST be assigned equal to `room_fee`.
- **Validation**: `room_fee > 0`.

## 2. Contract Generation API / Service Contract
- **Rule**: `ContractService.createContract(contract, managerId)` MUST ensure `contract.depositAmount` equals the associated `room.roomFee`.
- **Snapshot Rule**: The deposit amount is stored permanently in `dbo.contracts` / `dbo.rooms` for the active lease.

## 3. UI Display Contract
- **Manager Detail View**: [manager/contracts/detail.jsp](file:///d:/Ki_5/hostel_management/src/main/webapp/WEB-INF/views/manager/contracts/detail.jsp)
- **Tenant Detail View**: [tenant/contracts/detail.jsp](file:///d:/Ki_5/hostel_management/src/main/webapp/WEB-INF/views/tenant/contracts/detail.jsp)
- **Print / Export View**: [manager/contracts/print.jsp](file:///d:/Ki_5/hostel_management/src/main/webapp/WEB-INF/views/manager/contracts/print.jsp)
- **Format**: `<fmt:formatNumber value="${contract.room.depositAmount}" pattern="#,##0"/> đ`
