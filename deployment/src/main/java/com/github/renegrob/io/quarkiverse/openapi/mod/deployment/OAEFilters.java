package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.MethodInfo;

import io.smallrye.openapi.runtime.util.JandexUtil;

public class OAEFilters {

    private Map<String, OperationFilters> filtersMap = new HashMap<>();

    public OperationFilters getByMethodRef(String methodRef) {
        return filtersMap.get(methodRef);
    }

    public OperationFilters createForMethod(MethodInfo method) {
        String methodRef = JandexUtil.createUniqueMethodReference(method.declaringClass(), method);
        return filtersMap.computeIfAbsent(methodRef, k -> new OperationFilters(method));
    }
}
