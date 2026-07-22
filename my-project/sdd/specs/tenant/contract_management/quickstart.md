# Quickstart Validation Guide: Contract Tests

1. Ensure dependencies are downloaded (Maven).
2. Run tests via Maven command:
   ```bash
   mvn test -Dtest=ContractServiceImplTest
   ```
3. **Expected Outcome**: 
   - Output shows `BUILD SUCCESS`
   - Test runner reports all tests passed.
   - Code coverage report (via JaCoCo) indicates `>= 85%` coverage on `ContractServiceImpl` logic for the tested methods.
