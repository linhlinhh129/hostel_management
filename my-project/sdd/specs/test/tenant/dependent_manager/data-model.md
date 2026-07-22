# Data Model: Dependent Management Tests

## Entities to Mock/Test

### Dependent (Người phụ thuộc)
- **Fields**: 
  - `dependentId` (String): Mã định danh hợp lệ
  - `tenantId` (String): Mã người thuê bảo trợ
  - `fullName` (String): Tên người phụ thuộc
  - `relationship` (String): Mối quan hệ
  - `phoneNumber` (String): Số điện thoại
  - `citizenId` (String): CCCD/CMND (PII - cần được mask khi response)
  - `email` (String): Có thể null
  - `deletedAt` (Timestamp/Date): Dùng cho Soft Delete. Nếu null = Active.

### UserSession
- **Fields**: 
  - `userId` (String): Mã user đăng nhập
  - `role` (String): ROLE_TENANT
- **Logic**: Used for IDOR and authentication checks in test cases.

## Validation & Business Rules
- **Masking Rule**: `citizenId` must be masked (e.g., `0790******123`).
- **Authorization**: `Dependent.tenantId` must strictly equal `UserSession.userId` (or associated tenant profile).
- **Soft Delete**: Records where `deletedAt != null` must simulate HTTP 404 behavior (not found).
