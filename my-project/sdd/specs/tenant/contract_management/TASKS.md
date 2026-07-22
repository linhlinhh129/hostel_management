# Tasks: Tenant Contract Tests

**Input**: Design documents from `specs/tenant/contract_management/`
**Prerequisites**: plan.md, spec.md, data-model.md

---

## Phase 1: Foundational (Setup Mock & Helpers)

**Goal**: Initialize test class and setup common mock behaviors.

- [x] T001 Setup `ContractServiceImplTest.java` class with Mockito extensions.
- [x] T002 [P] Mock dependencies (`ContractDAO`, `UserDAO`, etc.) and create `MockDataHelper`.
- [x] T003 Implement `@BeforeEach setUp()` to initialize common mock behaviors.

---

## Phase 2: User Story 1 (Create and Print Contract)

**Priority**: P1

- [x] T004 [P] [US1] Write `testCreateContract_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T005 [P] [US1] Write `testPrintContract_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T006 [P] [US1] Write `testCreateContract_AddTenant` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T007 [P] [US1] Write `testCreateContract_InvalidDates` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`

---

## Phase 3: User Story 2 (View Contract Details)

**Priority**: P1

- [x] T008 [P] [US2] Write `testViewContract_TenantOwner_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T009 [P] [US2] Write `testViewContract_TenantNotOwner_AccessDenied` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T010 [P] [US2] Write `testViewContract_ManagerSameFacility_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T011 [P] [US2] Write `testViewContract_ManagerOtherFacility_AccessDenied` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`

---

## Phase 4: User Story 3 (Extend Contract - Manager Flow)

**Priority**: P2

- [x] T012 [P] [US3] Write `testExtendContract_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T013 [P] [US3] Write `testExtendContract_InvalidDate` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T014 [P] [US3] Write `testExtendContract_NotManager` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`

---

## Phase 5: User Story 4 (Manage Contract Status)

**Priority**: P2

- [x] T015 [P] [US4] Write `testDeleteContract_Inactive_Success` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`
- [x] T016 [P] [US4] Write `testDeleteContract_Active_Fail` in `src/test/java/com/quanlyphongtro/service/impl/ContractServiceImplTest.java`

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T017 Execute `mvn test -Dtest=ContractServiceImplTest` and generate JaCoCo coverage report.
- [ ] T018 Refactor tests: Extract common assertions and mock setups into private helper methods to improve maintainability.

---

## Dependencies & Execution Order
- **Phase 1** must be completed first as it provides the mocks for all subsequent phases.
- **Phase 2 to 5** can be executed in parallel as unit tests are independent of each other.
- **Phase 6** must be executed after all tests are implemented.

## Parallel Execution Example
```bash
# Developer A
Task: "T012 [P] [US3] Write testSubmitRequest_Extension_Success"
# Developer B 
Task: "T013 [P] [US3] Write testSubmitRequest_Termination_Success"
```
