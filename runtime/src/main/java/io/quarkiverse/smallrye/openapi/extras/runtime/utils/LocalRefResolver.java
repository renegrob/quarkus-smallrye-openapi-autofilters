package io.quarkiverse.smallrye.openapi.extras.runtime.utils;

import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;

public class LocalRefResolver {

    private final OpenAPI openAPI;

    public LocalRefResolver(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    // #/components/schemas/Fruit
    public <T> T resolve(Class<T> resultType, String ref) {
        StringTokenizer tokenizer = new StringTokenizer(ref, "/");
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        String token = tokenizer.nextToken();
        if ("#".equals(token)) {
            token = tokenizer.nextToken();
            switch (token) {
                case "components":
                    return resolve(resultType, openAPI.getComponents(), tokenizer);
                //                case "info":
                //                    return openAPI.getInfo();
                //                case "servers":
                //                    return openAPI.getServers();
                //                case "security":
                //                    return openAPI.getSecurity();
            }
        }
        return null;
    }

    private <T> T resolve(Class<T> resultType, Components components, StringTokenizer tokenizer) {
        Object result;
        result = components;
        if (tokenizer.hasMoreTokens()) {
            final String name = tokenizer.nextToken();
            switch (name) {
                case "headers":
                    result = getMapEntry(components.getHeaders(), tokenizer);
                    break;
                case "examples":
                    result = getMapEntry(components.getExamples(), tokenizer);
                    break;
                case "parameters":
                    result = getMapEntry(components.getParameters(), tokenizer);
                    break;
                case "schemas":
                    result = getMapEntry(components.getSchemas(), tokenizer);
                    break;
                case "requestBodies":
                    result = getMapEntry(components.getRequestBodies(), tokenizer);
                    break;
                case "links":
                    result = getMapEntry(components.getLinks(), tokenizer);
                    break;
                case "responses":
                    result = getMapEntry(components.getResponses(), tokenizer);
                    break;
                case "securitySchemes":
                    result = getMapEntry(components.getSecuritySchemes(), tokenizer);
                    break;
            }
        }
        return resultType.isInstance(result) ? resultType.cast(result) : null;
    }

    private Object getMapEntry(Map<String, ?> map, StringTokenizer tokenizer) {
        if (tokenizer.hasMoreTokens()) {
            return map.get(tokenizer.nextToken());
        }
        return map;
    }
}
