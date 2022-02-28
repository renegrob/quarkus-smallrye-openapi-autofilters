package io.quarkiverse.smallrye.openapi.extras.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class SmallryeOpenAPIExtrasProcessor {

    private static final String FEATURE = "smallrye-openapi-extras";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
}
