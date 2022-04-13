package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.List;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;

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
        // operation.getRequestBody()
        final List<Parameter> parameters = operation.getParameters();
        if (parameters != null) {
            filterParameters(parameters, methodAnnotationHolder);
        }
        return operation;
    }

    private void filterParameters(List<Parameter> parameters,
            OperationAnnotationInfo.MethodAnnotationHolder methodAnnotationHolder) {
        for (Parameter parameter : parameters) {
            System.out.println("parameter: " + parameter);
            filterParameter(parameter, methodAnnotationHolder.byParamRef(ParameterImpl.getParamRef(parameter)));
        }
    }

    private Parameter filterParameter(Parameter parameter,
            OperationAnnotationInfo.ParameterAnnotationHolder parameterAnnotationHolder) {
        System.out.println("parameterAnnotationHolder: " + parameterAnnotationHolder);
        if (parameterAnnotationHolder == null) {
            return parameter;
        }
        for (AnnotationFilters<Parameter> annotationFilters : parameterAnnotationHolder.configurations()) {
            for (OAEBaseFilter<Parameter> parameterFilter : annotationFilters.filters()) {
                System.out.println("parameterFilter: " + parameterFilter);
                parameter = parameterFilter.filter(parameter, annotationFilters.getAnnotationInstance());
            }
        }
        return parameter;
    }
}
