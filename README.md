# QA API Automation — RestAssured + JUnit5 + Allure

[![API Tests](https://github.com/gustavaom7/api-testing-restassured/actions/workflows/api-tests.yml/badge.svg?branch=main)](https://github.com/gustavaom7/api-testing-restassured/actions/workflows/api-tests.yml)

A professional-grade **API automation testing framework** built with RestAssured, JUnit5, and Allure Reports. This repository demonstrates smoke, contract and input-handling tests against a live public API, with automated CI/CD reporting.

> **Portfolio Project**: Built to showcase best practices in API automation testing alongside mobile (Maestro) and E2E (Cypress/Playwright) testing suites.

## 🎯 Project Goals

| Goal | Implementation | Status |
|---|---|---|
| REST API testing at scale | RestAssured + parameterized tests | ✅ |
| Contract testing | JSON Schema validation | ✅ |
| Input handling | Injection-style, special-character and long queries come back as ordinary results | ✅ |
| CI/CD automation | GitHub Actions on push to main, PRs, weekly and on demand | ✅ |
| Slack notifications | Pass/fail alerts to team | ✅ |
| Type-safe models | Jackson-mapped POJOs for responses | ✅ |
| Best practices | Builder pattern, logging, custom assertions | ✅ |

## 🏗️ Architecture

```
api-testing-restassured/
├── src/test/java/com/gustavaom7/
│   ├── helpers/          # Utility classes, APIClient
│   ├── models/           # Response POJOs (Jackson)
│   ├── tests/            # Test classes organized by type
│   │   ├── *SmokeTests.java
│   │   ├── *ContractTests.java
│   │   └── *InputHandlingTests.java
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

### Input Handling Tests (`WikipediaSearchInputHandlingTests`)
- Injection-style strings (SQL, XSS, template syntax) are searched as plain text
- Long queries (100 to 5000 characters) get a normal response or a clean 400
- Special and percent-encoded characters

**Key Tests:**
- An injection-style query returns a normal search response with no error object or server trace
- Special characters and percent-encoded input never produce an API error

These are read-only searches that any visitor could type into Wikipedia's search box, a few dozen requests a week with a descriptive User-Agent. They check how a public API handles unusual input. They are **not** security testing: attack payloads belong against a target you own, which is the next step for this repo (a local service under Testcontainers/WireMock).

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

## 🔐 Scope

### Included
- ✅ Injection-style and special-character queries handled as text
- ✅ Long queries handled without a server error

### Not included
- Security testing of any kind against Wikipedia (it is someone else's production system)
- Authentication/authorization, TLS, rate limiting, data privacy

## 📚 Test Data

Tests use:
- **Public APIs**: Wikipedia (no auth required)
- **Fixed queries**: "Java", "Python", "API" (stable results)

## 🏆 Best Practices Demonstrated

1. **Builder Pattern**: `APIClient` for consistent configuration
2. **Type-Safe Models**: Jackson-mapped POJOs for response deserialization
3. **Parameterized Tests**: `@ParameterizedTest` for scenario variation
4. **Allure Annotations**: `@Epic`, `@Feature`, `@Severity` for organization
5. **Logging**: SLF4J + Logback for debugging
6. **Assertions**: AssertJ for readable, fluent assertions
7. **Organized Tests**: Grouped by type (Smoke, Contract, Input handling)
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
- ✅ **Input handling**: 20 tests (injection-style, special and percent-encoded characters, long queries)
- ✅ **Allure report**: request/response attachments, [published from `main`](https://gustavaom7.github.io/api-testing-restassured/)
- ✅ **CI/CD**: push to main, PRs, weekly and on demand
- ⚪ **Slack**: optional, runs only when the `SLACK_WEBHOOK_URL` secret is set

## 📞 Support & Contributions

Questions or improvements? Open an issue or contact [@gustavaom7](https://github.com/gustavaom7).

---

**Part of**: QA Automation Portfolio ([Playwright Web](https://github.com/gustavaom7/playwright-typescript-e2e-framework) • [Appium Mobile](https://github.com/gustavaom7/appium-mobile-e2e-framework) • [Cypress Web + API](https://github.com/gustavaom7/cypress-hybrid-suite) • [Maestro Mobile](https://github.com/gustavaom7/maestro-mobile-ai-qa) • [Portfolio](https://gustavaom7.github.io/))
