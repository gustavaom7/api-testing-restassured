# Testing Strategy

## What is under test

The Wikipedia search API (`https://en.wikipedia.org/w/api.php?action=query&list=search`): public, read-only, no authentication. It is someone else's production system, which shapes every choice below: few requests, a descriptive User-Agent, and no attack traffic.

## Test layers

| Layer | Class | Tests | Question it answers |
|---|---|---|---|
| Smoke | `WikipediaSearchSmokeTests` | 8 | Does search return sane results for common terms, valid timestamps, a validation error for an empty query, and respond in time? |
| Contract | `WikipediaSearchContractTests` | 4 | Does the response still match the JSON schema, field types and content type the client relies on? |
| Input handling | `WikipediaSearchInputHandlingTests` | 20 | Are injection-style, special-character, percent-encoded and long queries handled as plain text, without an error object or server trace? |

Contract tests matter most here: a public API can change shape without notice, and the schema in `src/test/resources/schemas/` is what would catch it.

### Input handling is not security testing

The input-handling queries (`'; DROP TABLE users; --`, `<script>alert('XSS')</script>`, `{{7*7}}`, 5000-character strings) are strings any visitor could type into Wikipedia's search box. The tests check that they come back as ordinary search results. They do not probe for vulnerabilities, and the suite never should against a system it does not own. Real attack payloads belong against a local target (planned: a service started with Testcontainers or WireMock).

## Test data

- Fixed, stable search terms (`Java`, `Python`, `Database`, `API`, `Testing`): Wikipedia always has articles for them.
- Risk: if search ranking or article content changes, a smoke assertion can fail. That is acceptable for a demo and is exactly what the weekly run is there to surface.

## Flakiness

The suite talks to a live service over the internet, so a slow response or a transient network error can fail a run. There are no automatic retries: a red run is investigated, not re-run until green. The response-time test uses a generous budget so normal latency does not trip it.

## Continuous integration

`.github/workflows/api-tests.yml` runs on push to `main`, on every pull request, weekly (Monday 09:17 UTC) and on demand. It publishes the Allure report of `main` to GitHub Pages and posts to Slack when the `SLACK_WEBHOOK_URL` secret is set.
