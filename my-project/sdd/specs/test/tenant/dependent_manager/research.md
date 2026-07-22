# Research: Dependent Management (Tenant Test Strategy)

## 1. Technical Context Unknowns
- **Unknown 1:** What is the exact name of the Service and DAO classes for handling dependents?
  - *Decision*: We assume `DependentService`, `DependentServiceImpl`, and `DependentDAO` following the existing `ContractService` pattern.
  - *Rationale*: Consistency with standard MVC/DAO architecture in the project.

- **Unknown 2:** How is IDOR/Auth handled in the service layer?
  - *Decision*: Assume the service layer methods accept `UserSessionDTO` or a `tenantId` parameter to filter records.
  - *Rationale*: Based on existing patterns where the Controller passes session info down or filters it.

## 2. Best Practices for Testing this Domain
- **Mockito Usage**: We will extensively use Mockito to mock `DependentDAO` so tests are isolated and fast, focusing strictly on behavior (BDD) rather than database integration.
- **Exception Handling**: Use `assertThrows` from JUnit 5 or `assertThatThrownBy` from AssertJ to verify HTTP 403 / 404 equivalents (e.g. `AccessDeniedException` or `EntityNotFoundException` mapped by the controller).
