# Stage 1 Dependency Upgrades Documentation

The following dependencies have been upgraded in the first stage of the dependency upgrade roadmap to improve security, compatibility, and library stability:

| Library / Dependency | Group ID | Artifact ID | Previous Version | Upgraded Version | Purpose |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Lombok** | `org.projectlombok` | `lombok` | `1.18.30` | `1.18.46` | Improved compatibility with Java 17 and higher. |
| **SnakeYAML** | `org.yaml` | `snakeyaml` | `2.1` | `2.6` | Minor version security and parsing stability improvements. |
| **Jackson Core** | `com.fasterxml.jackson.core` | `jackson-core` | `2.15.2` | `2.17.2` | Stability and CVE remediation updates. |
| **Jackson Annotations** | `com.fasterxml.jackson.core` | `jackson-annotations` | `2.15.2` | `2.17.2` | Kept in sync with `jackson-core`. |
| **JSON** | `org.json` | `json` | `20231013` | `20240303` | Dependency updates and parsing stability. |

---

## Validation & Testing

To ensure the upgrades did not break the project compilation or runtime logic:
1. **Integration Test Created**: Added a new test class `JacksonIntegrationTest` to verify that Lombok annotation generation (getters, setters, equals, constructors) remains fully compatible and functions as expected with the updated Jackson `ObjectMapper` serialization/deserialization.
2. **Execution**: Ran Maven test suites to ensure compile and test execution passes.
```bash
./mvnw clean test
```
*Status: Build Success (All tests passed).*
