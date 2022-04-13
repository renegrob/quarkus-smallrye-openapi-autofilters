package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import static java.util.Collections.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.jandex.AnnotationInstance;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;

public class AnnotationFilters<T> {

    private final AnnotationInstance annotationInstance;
    private final Collection<OAEBaseFilter<T>> filters = new ArrayList<>();

    public AnnotationFilters(AnnotationInstance annotationInstance) {
        this.annotationInstance = annotationInstance;
    }

    public AnnotationInstance getAnnotationInstance() {
        return annotationInstance;
    }

    public void addFilter(OAEBaseFilter<T> filter) {
        filters.add(filter);
    }

    public Collection<OAEBaseFilter<T>> filters() {
        return unmodifiableCollection(filters);
    }
}
