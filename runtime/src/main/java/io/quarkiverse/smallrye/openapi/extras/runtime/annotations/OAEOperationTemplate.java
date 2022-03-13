package io.quarkiverse.smallrye.openapi.extras.runtime.annotations;

public @interface OAEOperationTemplate {

    String property();

    String template();

    String regex() default "(?s).*";
}
