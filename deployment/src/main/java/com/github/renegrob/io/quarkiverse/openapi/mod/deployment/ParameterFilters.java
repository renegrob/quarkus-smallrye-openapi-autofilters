package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import org.jboss.jandex.MethodParameterInfo;

public class ParameterFilters extends AbstractFiltersHolder {

    private final MethodParameterInfo parameterInfo;

    public ParameterFilters(MethodParameterInfo parameterInfo) {
        this.parameterInfo = parameterInfo;
    }

    public MethodParameterInfo parameterInfo() {
        return parameterInfo;
    }
}
