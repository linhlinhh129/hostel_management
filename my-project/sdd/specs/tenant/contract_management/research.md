# Research: Tenant Contract Tests

## Unknowns extracted from Technical Context
- No unknowns remain. The testing stack (JUnit 5, Mockito, AssertJ) is well-defined in `pom.xml`.

## Best Practices for testing this domain
- **Decision**: Use Mockito to mock `ContractDAO` and `UserSessionDTO` (for RBAC testing).
- **Rationale**: Ensures tests are isolated from the database, eliminating flaky tests due to external data states, and run fast.
- **Alternatives considered**: Integration testing with an in-memory DB like H2. Rejected due to complexity and setup overhead compared to simple unit tests.
