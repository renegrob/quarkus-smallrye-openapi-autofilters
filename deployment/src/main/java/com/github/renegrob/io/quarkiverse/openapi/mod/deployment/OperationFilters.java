package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;

import io.smallrye.openapi.runtime.util.JandexUtil;

public class OperationFilters extends AbstractFiltersHolder {
    private final Map<String, ParameterFilters> refToParameterFilters = new HashMap<>();
    private final MethodInfo methodInfo;

    public OperationFilters(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public ParameterFilters createForParameter(MethodParameterInfo parameter) {
        final String parameterRef = JandexUtil.createUniqueMethodParameterRef(parameter);
        return refToParameterFilters.computeIfAbsent(parameterRef, k -> new ParameterFilters(parameter));
    }

    public MethodInfo methodInfo() {
        return methodInfo;
    }

    public ParameterFilters getByParameterRef(String paramRef) {
        return refToParameterFilters.get(paramRef);
    }
}
