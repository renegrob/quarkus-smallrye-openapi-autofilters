package io.quarkiverse.smallrye.openapi.extras.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.AnnotationInstance;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.smallrye.openapi.runtime.io.JsonUtil;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
@OAEFilter(filterClass = MyAdjustParameterExample.MyParameterFilter.class)
public @interface MyAdjustParameterExample {

    String value();

    class MyParameterFilter implements OAEBaseFilter<Parameter> {
        @Override
        public Parameter filter(Parameter parameter, AnnotationInstance annotationInstance) {
            Object value = JsonUtil.parseValue(annotationInstance.value().asString());
            if (value instanceof Map) {
                Object oldValue = parameter.getExample();
                if (parameter.getExample() instanceof Map) {
                    value = merge(oldValue, value);
                }
            }
            parameter.setExample(value);
            return parameter;
        }

        private Object merge(Object oldValue, Object newValue) {
            if (newValue instanceof Map) {
                Map<?, ?> newValues = (Map<?, ?>) newValue;
                if (oldValue instanceof Map) {
                    Map oldValues = (Map) oldValue;
                    for (Map.Entry<?, ?> entry : newValues.entrySet()) {
                        final Object key = entry.getKey();
                        oldValues.put(key, merge(oldValues.get(key), entry.getValue()));
                    }
                    return oldValues;
                }
            }
            return newValue;
        }
    }
}
