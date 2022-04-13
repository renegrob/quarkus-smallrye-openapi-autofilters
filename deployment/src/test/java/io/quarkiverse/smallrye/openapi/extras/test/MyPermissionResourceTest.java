package io.quarkiverse.smallrye.openapi.extras.test;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.quarkiverse.smallrye.openapi.extras.test.annotations.MyPermission;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;

public class MyPermissionResourceTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot(root -> root
                    .addClass(MyPermissionResource.class)
                    .addClass(MyPermission.class)
                    .addClass(OAEFilter.class)
                    .addClass(OAEBaseFilter.class));

    @Test
    public void testMyDefaultSummary() {
        RestAssured.given().header("Accept", "application/json")
                .when().get("/q/openapi")
                .then().log().ifValidationFails(LogDetail.BODY)
                .body("paths.'/my-permission'.delete.summary", Matchers.equalTo("deletion"));
    }
}