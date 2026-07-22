# Data Model: Tenant Contract Tests

## Entities to Mock/Test

### Contract
- **Fields**: `id`, `code`, `roomId`, `tenantId`, `managerId`, `status` (`ACTIVE`/`INACTIVE`), `startDate`, `endDate`.
- **Validation Rules**: `endDate` > `startDate`, `status` valid transitions.

### ContractRequest
- **Fields**: `contractId`, `requestType` (`EXTENSION`/`TERMINATION`), `status`, `tenantId`.

### UserSession
- **Fields**: `userId`, `role` (`TENANT` / `MANAGER` / `ADMIN`), `managedFacilityIds`.
- **Logic**: Used for Authorization/RBAC tests.
