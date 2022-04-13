package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.jboss.jandex.AnnotationInstance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.smallrye.openapi.runtime.io.JsonUtil;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
@OAEFilter(filterClass = MyAdjustRequestBodyExample.MyRequestBodyFilter.class)
public @interface MyAdjustRequestBodyExample {

    String value();

    class MyRequestBodyFilter implements OAEBaseFilter<RequestBody> {
        @Override
        public RequestBody filter(RequestBody requestBody, AnnotationInstance annotationInstance) {
            Object value = JsonUtil.parseValue(annotationInstance.value().asString());
            if (value instanceof Map) {
                Object oldValue = requestBody;
                if (requestBody instanceof Map) {
                    value = merge(oldValue, value);
                }
            }
            try {
                System.out.println(new ObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return requestBody;
        }

        Object merge(Object oldValue, Object newValue) {
            if (newValue instanceof Map) {
                Map<?, ?> newValues = (Map<?, ?>) newValue;
                if (oldValue instanceof Map) {
                    Map oldValues = new LinkedHashMap((Map) oldValue);
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
