package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.jboss.jandex.AnnotationInstance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilterSelector;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.smallrye.openapi.runtime.io.JsonUtil;

@OAEFilterSelector(annotationTypes = MyAdjustRequestBodyExample.class)
public class MyRequestBodyFilter extends OAEBaseFilter<RequestBody> {

    public MyRequestBodyFilter() {
        super(RequestBody.class);
    }

    @Override
    public RequestBody filter(RequestBody requestBody, AnnotationInstance annotationInstance, Map<String, Object> context) {
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

    static Object merge(Object oldValue, Object newValue) {
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
