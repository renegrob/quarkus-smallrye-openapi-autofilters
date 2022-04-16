package io.quarkiverse.smallrye.openapi.extras.runtime.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;

public final class ExampleUtil {

    private ExampleUtil() {
    }

    public static Object exampleFromSchema(Schema schema) {
        if (schema.getType() == Schema.SchemaType.OBJECT) {
            Map<String, Object> objectMap = new LinkedHashMap<>();
            for (Map.Entry<String, Schema> entry : schema.getProperties().entrySet()) {
                objectMap.put(entry.getKey(), exampleFromSchema(entry.getValue()));
            }
            return objectMap;
        } else {
            return schema.getExample();
        }
    }
}
