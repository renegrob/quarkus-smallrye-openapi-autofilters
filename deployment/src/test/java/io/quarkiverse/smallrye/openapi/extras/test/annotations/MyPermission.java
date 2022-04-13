package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEOperationTemplate;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@OAEOperationTemplate(property = "description", regex = "$", template = " required permissions: #{value}")
public @interface MyPermission {
    String[] value();
}
