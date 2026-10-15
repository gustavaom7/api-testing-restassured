# QA API Automation — RestAssured + JUnit5 + Allure

[![API Tests](https://github.com/gustavaom7/api-testing-restassured/actions/workflows/api-tests.yml/badge.svg?branch=main)](https://github.com/gustavaom7/api-testing-restassured/actions/workflows/api-tests.yml)

A professional-grade **API automation testing framework** built with RestAssured, JUnit5, and Allure Reports. This repository demonstrates contract testing, security testing, and automated CI/CD reporting for REST APIs.

> **Portfolio Project**: Built to showcase best practices in API automation testing alongside mobile (Maestro) and E2E (Cypress/Playwright) testing suites.

## 🎯 Project Goals

| Goal | Implementation | Status |
|---|---|---|
| REST API testing at scale | RestAssured + parameterized tests | ✅ |
| Contract testing | JSON Schema validation | ✅ |
| Security testing (basics) | Injection, buffer overflow tests | ✅ |
| CI/CD automation | GitHub Actions on PR + manual trigger (daily/push cron disabled to control Actions minutes) | ✅ |
| Slack notifications | Pass/fail alerts to team | ✅ |
| Type-safe models | Gson-based POJOs for responses | ✅ |
| Best practices | Builder pattern, logging, custom assertions | ✅ |

## 🏗️ Architecture

```
api-testing-restassured/
├── src/test/java/com/gustavaom7/
│   ├── api/              # API client builders
│   ├── helpers/          # Utility classes, APIClient
│   ├── models/           # Response POJOs (Gson)
│   ├── tests/            # Test classes organized by type
│   │   ├── *SmokeTests.java
│   │   ├── *ContractTests.java
│   │   └── *SecurityTests.java
│   └── resources/
│       └── schemas/      # JSON Schema files for contract tests
├── .github/workflows/
│   └── api-tests.yml     # CI/CD pipeline
├── build.gradle          # Gradle config + dependencies
└── docs/
    ├── TESTING_STRATEGY.md
    └── API_GUIDE.md
```

## 🧪 Test Categories

### Smoke Tests (`WikipediaSearchSmokeTests`)
- Basic functionality verification
- Common search queries
- Response format validation
- Performance baseline (< 3 seconds)

**Key Tests:**
- Search returns results with valid structure
- Multiple queries return consistent results
- Empty search handled gracefully
- Response times within acceptable range

### Contract Tests (`WikipediaSearchContractTests`)
- JSON Schema validation
- Required field verification
- Type consistency checks
- Content-type validation

**Key Tests:**
- Response matches JSON schema
- All required fields present
- Numeric fields have correct types
- Field presence across all results

### Security Tests (`WikipediaSearchSecurityTests`)
- Injection attempt handling (SQL, XSS, template injection)
- Large input buffer overflow protection
- Special character handling
- URL-encoded malicious input

**Key Tests:**
- SQL injection attempts return 200 (not vulnerable)
- XSS payloads handled safely
- Large inputs don't crash API
- Special characters properly escaped

## 🚀 Quick Start

### Prerequisites
- Java 11+ (CI runs on 17)
- Gradle 7.0+
- Git

### Installation

```bash
# Clone and navigate
git clone https://github.com/gustavaom7/api-testing-restassured.git
cd api-testing-restassured

# Make Gradle executable (if needed)
chmod +x gradlew
```

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests WikipediaSearchSmokeTests

# Run tests matching pattern
./gradlew test --tests "*ContractTests"

# Run with verbose output
./gradlew test --info
```

### Generate Allure Report

```bash
# Results are written to build/allure-results; Allure's CLI needs Node and Java
npx allure-commandline serve build/allure-results
```

## 📊 Test Results & Reporting

### Allure Reports
After running tests, Allure Report provides:
- **Test Summary**: Pass/fail rates, duration
- **Test Timeline**: Execution sequence and timing
- **Test Details**: Logs, attachments, severity levels
- **History**: Trend analysis across runs

**Access Report:**
- Latest `main` run: https://gustavaom7.github.io/api-testing-restassured/
- Locally: `npx allure-commandline serve build/allure-results`

### GitHub Actions
- On every **push** to `main`, every **pull request**, weekly (Monday 09:17 UTC) and on demand
- The Allure report of the latest `main` run is published to GitHub Pages
- Slack notifications for pass/fail
- Artifacts: Allure reports, test results

**View Results:**
- GitHub Actions tab → API Automation Tests workflow
- Download allure-results artifact for detailed analysis

## 🔧 Configuration

### Base URL & Endpoints
Currently configured for **Wikipedia Search API** (`https://en.wikipedia.org/w/api.php`).

To test a different API, modify in test class:
```java
private static final String API_BASE = "https://your-api.com/v1";
```

### Timeout & Headers
Customize in `APIClient.java`:
```java
apiClient.withTimeout(5000)      // Response timeout in ms
         .withLogging(true)      // Enable request/response logging
         .build()
```

### Slack Notifications
Add webhook URL to GitHub Secrets:
1. Go to repository Settings → Secrets
2. Add `SLACK_WEBHOOK_URL` with your Slack webhook

## 📈 Metrics & Health

Test results tracked per-run:
- **Pass Rate**: Percentage of passing tests
- **Execution Time**: Total and per-test duration
- **Flakiness**: Retry/rerun frequency
- **Coverage**: APIs and scenarios tested

View in Allure Report → **Overview** tab.

## 🔐 Security Considerations

### Tests Include
- ✅ SQL injection simulation
- ✅ XSS payload handling
- ✅ Buffer overflow (large input)
- ✅ Special character escaping

### Not Included (Scope)
- Authentication/authorization testing (separate suite)
- HTTPS/TLS validation
- Rate limiting/DDoS testing
- Data privacy (GDPR, PII handling)

## 📚 Test Data

Tests use:
- **Public APIs**: Wikipedia (no auth required)
- **Fixed queries**: "Java", "Python", "API" (stable results)
- **Faker**: For generated data where needed (see `com.github.javafaker`)

## 🏆 Best Practices Demonstrated

1. **Builder Pattern**: `APIClient` for consistent configuration
2. **Type-Safe Models**: Gson POJOs for response deserialization
3. **Parameterized Tests**: `@ParameterizedTest` for scenario variation
4. **Allure Annotations**: `@Epic`, `@Feature`, `@Severity` for organization
5. **Logging**: SLF4J + Logback for debugging
6. **Assertions**: AssertJ for readable, fluent assertions
7. **Organized Tests**: Grouped by type (Smoke, Contract, Security)
8. **CI/CD Ready**: GitHub Actions with artifacts and notifications

## 📖 Documentation

- [Testing Strategy](docs/TESTING_STRATEGY.md) — Approach and trade-offs
- [API Guide](docs/API_GUIDE.md) — Endpoint details and assumptions

## 🔄 CI/CD Pipeline

```
Push/PR/Schedule
    ↓
Checkout Code
    ↓
Setup Java (17)
    ↓
Run Tests (./gradlew test)
    ↓
Generate Allure Report (artifact)
    ↓
Notify Slack (✅/❌, only if SLACK_WEBHOOK_URL is set)
    ↓
Publish report to GitHub Pages (main only)
```

**Schedule**: push to main, every PR, weekly on Monday and on demand

## 🚦 Current Status

32 tests against the live Wikipedia search API, green in CI (about 30 s of test time):

- ✅ **Smoke**: 8 tests (search results, several terms, timestamps, empty-search validation, response time)
- ✅ **Contract**: 4 tests (JSON schema, required fields, content type, numeric types)
- ✅ **Security**: 20 tests (injection payloads, special and URL-encoded characters, large input)
- ✅ **Allure report**: request/response attachments, [published from `main`](https://gustavaom7.github.io/api-testing-restassured/)
- ✅ **CI/CD**: push to main, PRs, weekly and on demand
- ⚪ **Slack**: optional, runs only when the `SLACK_WEBHOOK_URL` secret is set

## 📞 Support & Contributions

Questions or improvements? Open an issue or contact [@gustavaom7](https://github.com/gustavaom7).

---

**Part of**: QA Automation Portfolio ([Maestro Mobile](https://github.com/gustavaom7/maestro) • [Playwright Web](https://github.com/gustavaom7/playwright) • [Cypress Web](https://github.com/gustavaom7/cypress))
