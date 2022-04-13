package io.quarkiverse.smallrye.openapi.extras.test;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;

public class MyFruitResourceTest {

    // Start unit test with your extension loaded
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .withApplicationRoot(root -> root
                    .addClass(MyFruitResource.class)
                    .addClass(MyPermission.class)
                    .addClass(MyDefaultSummary.class)
                    .addClass(MyAdjustParameterExample.class)
                    .addClass(OAEFilter.class)
                    .addClass(OAEBaseFilter.class));

    @Test
    public void testMyDefaultSummary() {
        RestAssured.given().header("Accept", "application/json")
                .when().get("/q/openapi")
                .then().log().ifValidationFails(LogDetail.BODY)
                .body("paths.'/my-resource'.get.summary", Matchers.equalTo("list"));
    }

    /*
     * @Test
     * public void writeYourOwnUnitTest() throws Exception {
     * ClassLoader cl = Thread.currentThread().getContextClassLoader();
     * Yaml yaml = new Yaml();
     * Map<String, Object> openapi = yaml.load(cl.getResourceAsStream("META-INF/openapi.yaml"));
     * assertThat(openapi).extracting("bla");
     * 
     * }
     * 
     */
}
