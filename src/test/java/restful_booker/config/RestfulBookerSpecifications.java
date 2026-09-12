package restful_booker.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class RestfulBookerSpecifications {
    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    private static final RequestSpecification JSON_REQUEST = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .addHeader("Accept", "application/json")
            .build();

    private static final ResponseSpecification JSON_RESPONSE = new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .build();

    private RestfulBookerSpecifications() {
    }

    public static RequestSpecification jsonRequest() {
        return JSON_REQUEST;
    }

    public static ResponseSpecification jsonResponse() {
        return JSON_RESPONSE;
    }
}
