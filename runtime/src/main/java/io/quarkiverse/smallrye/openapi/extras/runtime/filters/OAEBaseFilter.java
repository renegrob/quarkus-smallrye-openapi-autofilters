package io.quarkiverse.smallrye.openapi.extras.runtime.filters;

public abstract class OAEBaseFilter<T> implements OAEFilter<T> {

    private final Class<T> itemType;

    public OAEBaseFilter(Class<T> itemType) {
        this.itemType = itemType;
    }

    public Class<T> itemType() {
        return itemType;
    }
}
