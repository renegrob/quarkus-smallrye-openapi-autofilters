package io.quarkiverse.smallrye.openapi.extras.runtime.filters;

import java.util.Map;

import org.jboss.jandex.AnnotationInstance;

public interface OAEFilter<T> {

    T filter(T item, AnnotationInstance annotationInstance, Map<String, Object> context);

    Class<T> itemType();

    class ContextKey {
        public static final String REF_RESOLVER = "REF_RESOLVER";
        public static final String COMPONENTS = "COMPONENTS";
        public static final String PATH = "PATH";
        public static final String PATH_ITEM = "PATH_ITEM";
        public static final String HTTP_METHOD = "HTTP_METHOD";

        public static final String CLASS_NAME = "CLASS_NAME";
        public static final String METHOD_NAME = "METHOD_NAME";

        private ContextKey() {
        }
    }
}
