package io.quarkiverse.smallrye.openapi.extras.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SmallryeOpenAPIExtrasResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/smallrye-openapi-extras")
                .then()
                .statusCode(200)
                .body(is("Hello smallrye-openapi-extras"));
    }
}
