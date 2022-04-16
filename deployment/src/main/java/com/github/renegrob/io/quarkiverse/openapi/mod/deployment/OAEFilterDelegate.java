package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.METHOD;

import java.util.Map;
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
        Map<String, Object> context = Map.of(METHOD, operationFilters.methodInfo().name());
        operation = operationFilters.filter(Operation.class, operation, context);
        operation.setRequestBody(operationFilters.filter(RequestBody.class, operation.getRequestBody(), context));

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
