package io.quarkiverse.smallrye.openapi.extras.runtime.annotations;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;

public @interface OAEFilter {

    Class<? extends OAEBaseFilter> filterClass(); // TODO: generate code!
}
