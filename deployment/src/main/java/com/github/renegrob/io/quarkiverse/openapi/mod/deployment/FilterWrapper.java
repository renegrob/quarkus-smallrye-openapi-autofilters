package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter;

public class FilterWrapper<T> {

    private final OAEFilter<T> oaeFilter;
    private final List<AnnotationInstance> annotationInstances = new ArrayList<>();

    public FilterWrapper(OAEFilter<T> oaeFilter) {
        this.oaeFilter = oaeFilter;
    }

    public void addAnnotationInstance(AnnotationInstance ai) {
        annotationInstances.add(ai);
    }

    T filter(T item, Map<String, Object> context) {
        for (AnnotationInstance ai : annotationInstances) {
            if (item == null) {
                break;
            }
            item = oaeFilter.filter(item, ai, context);
        }
        return item;
    }
}
