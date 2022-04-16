package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import io.smallrye.openapi.api.models.OperationImpl;

public class OAEFilterDelegate implements OASFilter {

    private static final Set<Class> OperationBasedObjects = Set.of(Operation.class, Header.class, RequestBody.class,
            APIResponse.class);

    /*
     * Operation.class,
     * Header.class,
     * RequestBody.class,
     * APIResponse.class,
     * PathItem .class,
     * Parameter.class,
     * Schema.class,
     * SecurityScheme.class,
     * Server.class,
     * Tag.class,
     * Link.class,
     * Callback.class,
     * OpenAPI .class
     */

    private final OAEFilters filters;

    public OAEFilterDelegate(OAEFilters filters) {
        this.filters = filters;
    }

    @Override
    public Operation filterOperation(Operation operation) {
        final OperationFilters operationFilters = filters.getByMethodRef(OperationImpl.getMethodRef(operation));
        if (operationFilters == null) {
            return operation;
        }
        final List<FilterWrapper<Operation>> filtersByItemType = operationFilters.getFiltersByItemType(Operation.class);

        /*
         * 
         * final OperationAnnotationInfo.MethodAnnotationHolder methodAnnotationHolder = operationAnnotationInfo
         * .getByMethodRef(OperationImpl.getMethodRef(operation));
         * if (methodAnnotationHolder == null) {
         * return operation;
         * }
         * for (AnnotationFilters<Operation> annotationFilters : methodAnnotationHolder.configurations()) {
         * for (OAEFilter<Operation> operationFilter : annotationFilters.filters()) {
         * operation = operationFilter.filter(operation, Map.of());
         * }
         * }
         * // operation.getRequestBody()
         * final List<Parameter> parameters = operation.getParameters();
         * if (parameters != null) {
         * filterParameters(parameters, methodAnnotationHolder);
         * }
         * 
         */
        return operation;
    }

    //    private void filterParameters(List<Parameter> parameters,
    //            OperationAnnotationInfo.MethodAnnotationHolder methodAnnotationHolder) {
    //        for (Parameter parameter : parameters) {
    //            System.out.println("parameter: " + parameter);
    //            filterParameter(parameter, methodAnnotationHolder.byParamRef(ParameterImpl.getParamRef(parameter)));
    //        }
    //    }
    //
    //    private Parameter filterParameter(Parameter parameter,
    //            OperationAnnotationInfo.ParameterAnnotationHolder parameterAnnotationHolder) {
    //        System.out.println("parameterAnnotationHolder: " + parameterAnnotationHolder);
    //        if (parameterAnnotationHolder == null) {
    //            return parameter;
    //        }
    //        for (AnnotationFilters annotationFilters : parameterAnnotationHolder.allAnnotationFilters()) {
    //            for (OAEFilter<Parameter> parameterFilter : annotationFilters.filters()) {
    //                System.out.println("parameterFilter: " + parameterFilter);
    //                parameter = parameterFilter.filter(parameter, null, Map.of());
    //            }
    //        }
    //        return parameter;
    //    }
}
