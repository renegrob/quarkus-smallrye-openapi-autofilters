package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import java.util.Map;

import org.eclipse.microprofile.openapi.models.Operation;
import org.jboss.jandex.AnnotationInstance;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilterSelector;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;

@OAEFilterSelector(annotationTypes = MyDefaultSummary.class)
public class MyOperationFilter extends OAEBaseFilter<Operation> {

    public MyOperationFilter() {
        super(Operation.class);
    }

    @Override
    public Operation filter(Operation operation, AnnotationInstance annotationInstance, Map<String, Object> context) {
        if (operation.getSummary() == null || operation.getSummary().isEmpty()) {
            operation.setSummary(annotationInstance.target().asMethod().name());
        }
        return operation;
    }
}
