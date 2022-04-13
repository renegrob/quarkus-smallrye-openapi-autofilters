package io.quarkiverse.smallrye.openapi.extras.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface OAEFilter {

    Class<? extends OAEBaseFilter> filterClass(); // TODO: generate code!
}
