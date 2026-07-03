# Pentland Scouts Events Service Code Rules

## Java Coding & Testing Rules

### 1. Test Class Naming Conventions
* **Domain-Oriented Naming**: Always name test classes after the functional components or domain concepts they validate (e.g., `ExcelAndDatabaseIntegrationTest` or `JacksonIntegrationTest`).
* **Avoid Task-Based Names**: Never name test classes after temporary project milestones, task descriptions, phases, or ticket numbers (e.g., avoid names like `Stage2IntegrationTest`, `PRUpgradeTest`, or `Task55Test`).

### 2. Avoid Static Mock Libraries (such as PowerMock or mockStatic)
* **Subclass-Override Pattern**: Instead of using heavy reflection mock engines or Mockito's static mocking (which conflicts with other test configurations), extract static library calls (like Firebase auth retrievals) into protected helper methods (e.g., `getFirebaseAuth()`). In tests, use anonymous subclass overrides of the class under test to supply mock instances.

### 3. Mocking DynamoDB / External Services via Loopback HttpServer
* **Lightweight Loopback Servers**: When testing static builder-constructed HTTP clients or database handlers, avoid mocking static factory builders. Instead, start a lightweight loopback `com.sun.net.httpserver.HttpServer` on a random port during test setup. Match targets (such as AWS request headers like `X-Amz-Target`) and serve mock JSON payloads.

### 4. No Return Statements in Finally Blocks
* **Exception Integrity**: Never write a `return` statement inside a `finally` block. In Java, this silently swallows all exceptions thrown in the matching `try` and `catch` blocks, causing failures to return HTTP 500 or 404 and instead returning misleading HTTP 200 OK statuses.

### 5. MockMvc Cookie Verification Workaround
* **MockHttpServletResponse Usage**: To prevent automated CodeQL alerts regarding cookie security configurations, do not verify mocked calls to `HttpServletResponse.addCookie(...)`. Use Spring's native `MockHttpServletResponse` object to assert cookie header presence and security flags (`Secure`, `HttpOnly`) directly.

### 6. Avoid Exposing Raw Exceptions in HTTP Responses
* **Information Leakage Prevention**: Never return raw exception details (`e.getMessage()`, stack traces) directly to API clients in controller catch blocks. This leaks internal architecture and database schemas. Always return generic messages (e.g., "Internal server error") and log raw details locally.

### 7. Prefer Method-Level Security Annotations
* **Locality of Context**: Prefer using Spring Security's `@PreAuthorize` or `@Secured` annotations directly on controller class or method signatures rather than configuring verbose request matchers inside the centralized `SecurityFilterChain`.

### 8. Centralized CORS Allowed-Origin Restrictions
* **No Wildcard Credentials**: Avoid combining `allow-credentials: true` with wildcard CORS origins (`*`) in configuration profiles, as modern browsers reject this configuration. Always read allowed origins from a dedicated config whitelist.


