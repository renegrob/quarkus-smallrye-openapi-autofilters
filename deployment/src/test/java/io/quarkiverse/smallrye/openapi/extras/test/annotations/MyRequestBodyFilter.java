package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import static io.quarkiverse.smallrye.openapi.extras.runtime.utils.ExampleUtil.exampleFromSchema;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.jboss.jandex.AnnotationInstance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilterSelector;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.utils.LocalRefResolver;
import io.smallrye.openapi.runtime.io.JsonUtil;

@OAEFilterSelector(annotationTypes = MyAdjustRequestBodyExample.class)
public class MyRequestBodyFilter extends OAEBaseFilter<RequestBody> {

    public MyRequestBodyFilter() {
        super(RequestBody.class);
    }

    @Override
    public RequestBody filter(RequestBody requestBody, AnnotationInstance annotationInstance, Map<String, Object> context) {
        final MediaType json = requestBody.getContent().getMediaType("application/json");
        Object oldExample = json.getExample();
        if (oldExample == null) {
            final LocalRefResolver refResolver = (LocalRefResolver) context.get(ContextKey.REF_RESOLVER);
            //            final Components components = (Components) context.get(ContextKey.COMPONENTS);
            //            final String schemaKey = ModelUtil.nameFromRef(json.getSchema().getRef());
            //            Schema schema = components.getSchemas().get(schemaKey);
            oldExample = exampleFromSchema(refResolver.resolve(Schema.class, json.getSchema().getRef()));
        }

        Object value = JsonUtil.parseValue(annotationInstance.value().asString());
        if (value instanceof Map) {
            Object oldValue = oldExample;
            if (oldValue instanceof Map) {
                value = merge(oldValue, value);
            }
        }
        try {
            System.out.println(new ObjectMapper().writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        json.setExample(value);
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
