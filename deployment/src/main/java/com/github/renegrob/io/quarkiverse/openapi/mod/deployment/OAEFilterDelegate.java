package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.CLASS_NAME;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.COMPONENTS;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.HTTP_METHOD;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.METHOD_NAME;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.PATH;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.PATH_ITEM;
import static io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter.ContextKey.REF_RESOLVER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

import io.quarkiverse.smallrye.openapi.extras.runtime.utils.LocalRefResolver;
import io.smallrye.openapi.api.models.OperationImpl;
import io.smallrye.openapi.api.models.parameters.ParameterImpl;

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
    private OpenAPI appliedTo;

    public OAEFilterDelegate(OAEFilters filters) {
        this.filters = filters;
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (!filters.isEmpty()) {
            if (appliedTo == openAPI) {
                return;
            }
            appliedTo = openAPI;
            //            System.out.println("Apply filter: " + this);

            ensureModel(openAPI);

            Map<String, Object> context = new HashMap<>();
            context.put(REF_RESOLVER, new LocalRefResolver(openAPI));
            context.put(COMPONENTS, openAPI.getComponents());
            Paths paths = openAPI.getPaths();
            if (paths != null) {
                Map<String, PathItem> pathItems = paths.getPathItems();
                if (pathItems != null) {
                    Set<Map.Entry<String, PathItem>> pathItemsEntries = pathItems.entrySet();
                    for (Map.Entry<String, PathItem> pathItemEntry : pathItemsEntries) {
                        context.put(PATH, pathItemEntry.getKey());
                        final PathItem pathItem = pathItemEntry.getValue();
                        context.put(PATH_ITEM, pathItem);
                        Map<PathItem.HttpMethod, Operation> operations = pathItem.getOperations();
                        if (operations != null) {
                            for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : operations.entrySet()) {
                                Operation operation = operationEntry.getValue();
                                context.put(HTTP_METHOD, operationEntry.getKey());
                                filterOperation(operation, context);
                            }
                        }
                    }
                }
            }
        }
    }

    private void ensureModel(OpenAPI openAPI) {
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(OASFactory.createComponents());
        }
    }

    public void filterOperation(Operation operation, Map<String, Object> outerContext) {
        final OperationFilters operationFilters = filters.getByMethodRef(OperationImpl.getMethodRef(operation));
        if (operationFilters == null) {
            return;
        }
        Map<String, Object> context = new HashMap<>(outerContext);
        context.put(CLASS_NAME, operationFilters.methodInfo().declaringClass().name());
        context.put(METHOD_NAME, operationFilters.methodInfo().name());
        operation = operationFilters.filter(Operation.class, operation, context);
        operation.setRequestBody(operationFilters.filter(RequestBody.class, operation.getRequestBody(), context));

        final List<Parameter> parameters = operation.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                final ParameterFilters parameterFilters = operationFilters.getByParameterRef(
                        ParameterImpl.getParamRef(parameter));
                //                System.out.println(ParameterImpl.getParamRef(parameter) + " -> " + parameterFilters);
                if (parameterFilters != null) {
                    parameterFilters.filter(Parameter.class, parameter, context);
                }
            }
        }
    }
}
