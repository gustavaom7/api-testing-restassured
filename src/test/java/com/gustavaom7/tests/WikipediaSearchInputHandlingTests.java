package com.gustavaom7.tests;

import com.gustavaom7.helpers.APIClient;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Wikipedia API")
/**
 * Input handling: injection-style, oversized and special-character queries must come back as an
 * ordinary search result. These are read-only searches any visitor could type into the search box,
 * a few dozen requests a week; they check robustness of a public API, not its security. Attack
 * testing belongs against a target you own.
 */
@Feature("Input handling")
@DisplayName("Wikipedia Search - Input Handling Tests")
class WikipediaSearchInputHandlingTests {

    private static final String WIKIPEDIA_API_BASE = "https://en.wikipedia.org/w/api.php";

    @BeforeAll
    static void setUp() {
        RestAssured.requestSpecification = new APIClient(WIKIPEDIA_API_BASE).withLogging(true).build();
    }

    @ParameterizedTest(name = "Injection-style query: {0}")
    @ValueSource(strings = {
            "'; DROP TABLE users; --",
            "1' OR '1'='1",
            "<script>alert('XSS')</script>",
            "{{7*7}}",
            "${7*7}",
            "java.lang.Runtime"
    })
    @DisplayName("Injection-style strings are searched as plain text")
    @Description("The query comes back as a normal search response, with no error object or server trace")
    @Severity(SeverityLevel.NORMAL)
    void searchTreatsInjectionStringsAsText(String maliciousInput) {
        Response response = given()
                .queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("list", "search")
                .queryParam("srsearch", maliciousInput)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();

        // The payload is treated as plain search text: a normal search response, no error object.
        // (The body itself is not scanned for words like "Exception": search snippets legitimately
        // contain them, e.g. "java.lang.Runtime" matches articles about RuntimeException.)
        assertThat(response.jsonPath().getMap("error")).as("error object").isNull();
        assertThat(response.jsonPath().getString("batchcomplete")).as("batchcomplete").isNotNull();
        assertThat(response.jsonPath().getInt("query.searchinfo.totalhits")).as("totalhits").isGreaterThanOrEqualTo(0);
        assertThat(response.jsonPath().getList("query.search")).as("search results").isNotNull();

        // Server-side failure signatures must never reach the client.
        assertThat(response.getBody().asString())
                .doesNotContain("Exception in thread")
                .doesNotContain("Traceback (most recent call last)")
                .doesNotContain("Fatal error:");
    }

    @ParameterizedTest(name = "Long query: {0} chars")
    @ValueSource(ints = {100, 1000, 5000})
    @DisplayName("Long queries get a normal response or a clean 400")
    @Severity(SeverityLevel.NORMAL)
    void searchShouldHandleLargeInput(int inputLength) {
        String largeInput = "a".repeat(inputLength);

        Response response = given()
                .queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("list", "search")
                .queryParam("srsearch", largeInput)
                .when()
                .get()
                .then()
                .extract()
                .response();

        assertThat(response.getStatusCode())
                .as("long query handled without a server error")
                .isIn(200, 400);
    }

    @ParameterizedTest(name = "Special chars: {0}")
    @ValueSource(strings = {"<>", "&&", "||", "%%", "$$", "@@", "##"})
    @DisplayName("API should handle special characters safely")
    @Severity(SeverityLevel.NORMAL)
    void searchShouldHandleSpecialCharacters(String specialChars) {
        Response response = given()
                .queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("list", "search")
                .queryParam("srsearch", specialChars)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.getBody().asString()).contains("batchcomplete");
    }

    @ParameterizedTest(name = "Encoding: {0}")
    @ValueSource(strings = {
            "%27", // Single quote
            "%22", // Double quote
            "%3C", // <
            "%3E"  // >
    })
    @DisplayName("Percent-encoded characters are searched as text")
    @Severity(SeverityLevel.NORMAL)
    void searchShouldHandleEncodedInput(String encodedInput) {
        // RestAssured encodes the value again, so the API receives the literal "%27" text.
        Response response = given()
                .queryParam("action", "query")
                .queryParam("format", "json")
                .queryParam("list", "search")
                .queryParam("srsearch", encodedInput)
                .when()
                .get()
                .then()
                .extract()
                .response();

        assertThat(response.getStatusCode()).isIn(200, 400);
        // Checked on the error object, not on the word "error": search snippets can contain it.
        assertThat(response.jsonPath().getMap("error")).as("error object").isNull();
    }
}
