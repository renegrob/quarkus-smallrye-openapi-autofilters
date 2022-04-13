package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.microprofile.openapi.models.Operation;
import org.jboss.jandex.AnnotationInstance;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@OAEFilter(filterClass = MyDefaultSummary.MyOperationFilter.class)
public @interface MyDefaultSummary {

    class MyOperationFilter implements OAEBaseFilter<Operation> {
        @Override
        public Operation filter(Operation operation, AnnotationInstance annotationInstance) {
            if (operation.getSummary() == null || operation.getSummary().isEmpty()) {
                operation.setSummary(annotationInstance.target().asMethod().name());
            }
            return operation;
        }
    }
}
