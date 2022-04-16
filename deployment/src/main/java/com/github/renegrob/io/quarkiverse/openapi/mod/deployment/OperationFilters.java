package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jandex.MethodParameterInfo;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter;

public class OperationFilters {

    private Map<Class<?>, List<FilterWrapper<?>>> typeToFilters = new HashMap<>();
    private Map<OAEFilter<?>, FilterWrapper<?>> filterMap = new LinkedHashMap<>();

    public ParameterFilters createForParameter(MethodParameterInfo parameter) {
        return null;
    }

    public FilterWrapper<?> addFilter(OAEFilter<?> oaeFilter) {
        final FilterWrapper<?> filterWrapper = filterMap.computeIfAbsent(oaeFilter, k -> new FilterWrapper<>(oaeFilter));
        typeToFilters.computeIfAbsent(oaeFilter.itemType(), k -> new ArrayList<>()).add(filterWrapper);
        return filterWrapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> List<FilterWrapper<T>> getFiltersByItemType(Class<T> itemType) {
        return (List<FilterWrapper<T>>) (List) typeToFilters.get(itemType);
    }
}
