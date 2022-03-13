package io.quarkiverse.smallrye.openapi.extras.runtime.filters;

import org.jboss.jandex.AnnotationInstance;

@FunctionalInterface
public interface OAEBaseFilter<T> {

    T filter(T item, AnnotationInstance annotationInstance);
}
