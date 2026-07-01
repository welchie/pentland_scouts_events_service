# Stage 2 Dependency Upgrades Documentation

The following dependencies have been upgraded in the second stage of the dependency roadmap to ensure security patches and feature updates:

| Library / Dependency | Group ID | Artifact ID | Previous Version | Upgraded Version | Purpose |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **AWS SDK v1 (DynamoDB)** | `com.amazonaws` | `aws-java-sdk-dynamodb` | `1.12.512` | `1.12.797` | Upgraded to the latest v1 SDK release for bug fixes and AWS API coverage. |
| **AWS SDK v2 (Enhanced / Profiles)** | `software.amazon.awssdk` | `dynamodb-enhanced` / `profiles` | `2.20.110` | `2.46.19` | AWS SDK v2 client and profile configuration updates. |
| **H2 Database Engine** | `com.h2database` | `h2` | `2.2.220` | `2.4.240` | Upgraded in-memory testing database engine. |
| **Apache POI (Excel utility)** | `org.apache.poi` | `poi` / `poi-ooxml` | `5.2.5` | `5.5.1` | Stable release updates for Excel sheet reading/writing utilities. |

---

## Validation & Testing

To validate these changes:
1. **Integration Test Created**: Added a new test class `Stage2IntegrationTest` with two test cases:
   - `testApachePoiExcelReadWrite`: Verifies writing and reading Excel files via Apache POI `XSSFWorkbook` using the updated `5.5.1` dependencies.
   - `testH2DatabaseConnection`: Establishes an H2 in-memory connection and performs a schema creation, data insertion, and retrieval operation using H2 `2.4.240`.
2. **Execution**: Ran Maven test suite to ensure compile and test execution passes.
```bash
./mvnw clean test
```
*Status: Build Success (All tests passed).*
