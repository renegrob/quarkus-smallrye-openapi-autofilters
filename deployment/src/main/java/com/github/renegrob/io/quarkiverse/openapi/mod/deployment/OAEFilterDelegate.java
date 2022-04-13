package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.smallrye.openapi.api.models.OperationImpl;

public class OAEFilterDelegate implements OASFilter {

    private final OperationAnnotationInfo operationAnnotationInfo;

    public OAEFilterDelegate(OperationAnnotationInfo operationAnnotationInfo) {
        this.operationAnnotationInfo = operationAnnotationInfo;
    }

    @Override
    public Operation filterOperation(Operation operation) {
        final OperationAnnotationInfo.MethodAnnotationHolder methodAnnotationHolder = operationAnnotationInfo
                .getByMethodRef(OperationImpl.getMethodRef(operation));
        if (methodAnnotationHolder == null) {
            return operation;
        }
        for (AnnotationFilters<Operation> annotationFilters : methodAnnotationHolder.configurations()) {
            for (OAEBaseFilter<Operation> operationFilter : annotationFilters.filters()) {
                operation = operationFilter.filter(operation, annotationFilters.getAnnotationInstance());
            }
        }
        return operation;
    }
}
