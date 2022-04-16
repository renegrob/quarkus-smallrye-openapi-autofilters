package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter;

public abstract class AbstractFiltersHolder {

    private Map<Class<?>, List<FilterWrapper<?>>> typeToFilters = new HashMap<>();
    private Map<OAEFilter<?>, FilterWrapper<?>> instanceToWrapper = new LinkedHashMap<>();

    public FilterWrapper<?> addFilter(OAEFilter<?> oaeFilter) {
        final FilterWrapper<?> filterWrapper = instanceToWrapper.computeIfAbsent(oaeFilter,
                k -> new FilterWrapper<>(oaeFilter));
        typeToFilters.computeIfAbsent(oaeFilter.itemType(), k -> new ArrayList<>()).add(filterWrapper);
        return filterWrapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> List<FilterWrapper<T>> getFiltersByItemType(Class<T> itemType) {
        return (List<FilterWrapper<T>>) (List) typeToFilters.getOrDefault(itemType, List.of());
    }

    public <T> T filter(Class<T> itemType, T item, Map<String, Object> context) {
        for (FilterWrapper<T> filterWrapper : getFiltersByItemType(itemType)) {
            if (item == null) {
                break;
            }
            item = filterWrapper.filter(item, context);
        }
        return item;
    }
}
