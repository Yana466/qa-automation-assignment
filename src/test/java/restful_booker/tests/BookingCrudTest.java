package restful_booker.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import restful_booker.models.AuthResponse;
import restful_booker.models.Booking;
import restful_booker.models.CreateBookingResponse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static restful_booker.config.RestfulBookerSpecifications.jsonRequest;
import static restful_booker.config.RestfulBookerSpecifications.jsonResponse;
import static restful_booker.testdata.BookingTestData.adminCredentials;
import static restful_booker.testdata.BookingTestData.originalBooking;
import static restful_booker.testdata.BookingTestData.updatedBooking;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class BookingCrudTest {
    private final Booking originalBooking = originalBooking();
    private final Booking updatedBooking = updatedBooking();

    private Integer bookingId;
    private String authToken;
    private boolean bookingDeleted;

    @BeforeAll
    void enableFailureLogging() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void createBookingReturnsGeneratedIdAndExpectedBooking() {
        CreateBookingResponse createdBooking = given()
                .spec(jsonRequest())
                .body(originalBooking)
                .when()
                .post("/booking")
                .then()
                .spec(jsonResponse())
                .statusCode(200)
                .extract()
                .as(CreateBookingResponse.class);

        assertAll(
                () -> assertTrue(createdBooking.bookingId() > 0, "The API should return a positive booking ID"),
                () -> assertEquals(originalBooking, createdBooking.booking())
        );

        bookingId = createdBooking.bookingId();
    }

    @Test
    @Order(2)
    void getBookingReturnsCreatedBooking() {
        Booking retrievedBooking = getBooking(requireBookingId());

        assertEquals(originalBooking, retrievedBooking);
    }

    @Test
    @Order(3)
    void authenticateReturnsReusableToken() {
        AuthResponse authResponse = authenticate();

        assertNotNull(authResponse.token(), "The authentication response should contain a token");
        assertFalse(authResponse.token().isBlank(), "The authentication token should not be blank");
        authToken = authResponse.token();
    }

    @Test
    @Order(4)
    void updateBookingPersistsChangedData() {
        int id = requireBookingId();
        String token = requireAuthToken();

        Booking updateResponse = given()
                .spec(jsonRequest())
                .cookie("token", token)
                .body(updatedBooking)
                .when()
                .put("/booking/{id}", id)
                .then()
                .spec(jsonResponse())
                .statusCode(200)
                .extract()
                .as(Booking.class);

        assertEquals(updatedBooking, updateResponse);
        assertEquals(updatedBooking, getBooking(id), "A follow-up GET should return the persisted update");
    }

    @Test
    @Order(5)
    void deleteBookingMakesBookingUnavailable() {
        int id = requireBookingId();

        given()
                .spec(jsonRequest())
                .cookie("token", requireAuthToken())
                .when()
                .delete("/booking/{id}", id)
                .then()
                .statusCode(201);

        given()
                .spec(jsonRequest())
                .when()
                .get("/booking/{id}", id)
                .then()
                .statusCode(404);

        bookingDeleted = true;
    }

    @AfterAll
    void cleanUpCreatedBooking() {
        try {
            if (bookingId == null || bookingDeleted) {
                return;
            }

            String cleanupToken = authToken;
            if (cleanupToken == null || cleanupToken.isBlank()) {
                cleanupToken = authenticate().token();
            }

            if (cleanupToken == null || cleanupToken.isBlank()) {
                System.err.println("Cleanup skipped because no authentication token was available.");
                return;
            }

            Response cleanupResponse = given()
                    .spec(jsonRequest())
                    .cookie("token", cleanupToken)
                    .when()
                    .delete("/booking/{id}", bookingId);

            if (cleanupResponse.statusCode() != 201 && cleanupResponse.statusCode() != 404) {
                System.err.printf(
                        "Cleanup failed for booking %d: HTTP %d%n",
                        bookingId,
                        cleanupResponse.statusCode()
                );
            }
        } catch (RuntimeException exception) {
            System.err.printf("Cleanup failed for booking %s: %s%n", bookingId, exception.getMessage());
        } finally {
            RestAssured.reset();
        }
    }

    private AuthResponse authenticate() {
        return given()
                .spec(jsonRequest())
                .body(adminCredentials())
                .when()
                .post("/auth")
                .then()
                .spec(jsonResponse())
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);
    }

    private Booking getBooking(int id) {
        return given()
                .spec(jsonRequest())
                .when()
                .get("/booking/{id}", id)
                .then()
                .spec(jsonResponse())
                .statusCode(200)
                .extract()
                .as(Booking.class);
    }

    private int requireBookingId() {
        assertNotNull(bookingId, "Create booking must succeed before this test runs");
        return bookingId;
    }

    private String requireAuthToken() {
        assertNotNull(authToken, "Authentication must succeed before this test runs");
        assertFalse(authToken.isBlank(), "Authentication token should not be blank");
        return authToken;
    }
}
