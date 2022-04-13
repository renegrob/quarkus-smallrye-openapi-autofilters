package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;

import io.smallrye.openapi.runtime.util.JandexUtil;

public class OperationAnnotationInfo {

    private Map<String, MethodAnnotationHolder> methods = new LinkedHashMap<>();

    public MethodAnnotationHolder getByMethodRef(String ref) {
        return methods.get(ref);
    }

    public MethodAnnotationHolder createForMethod(MethodInfo method) {
        String ref = JandexUtil.createUniqueMethodReference(method.declaringClass(), method);
        return methods.computeIfAbsent(ref, k -> new MethodAnnotationHolder(method));
    }

    public boolean isEmpty() {
        return methods.isEmpty();
    }

    static abstract class AbstractAnnotationHolder {

        private Map<String, AnnotationFilters> annotationFilters = new LinkedHashMap<>();

        public AnnotationFilters addAnnotation(AnnotationInstance ai) {
            final String key = ai.name().toString();
            return annotationFilters.computeIfAbsent(key, k -> new AnnotationFilters(ai));
        }

        public AnnotationFilters getAnnotationConfig(String className) {
            return this.annotationFilters.get(className);
        }

        public Collection<AnnotationFilters> configurations() {
            return annotationFilters.values();
        }
    }

    public static class MethodAnnotationHolder extends AbstractAnnotationHolder {

        private MethodInfo method;
        private Map<String, ParameterAnnotationHolder> parameters = new LinkedHashMap<>();

        public MethodAnnotationHolder(MethodInfo method) {
            this.method = method;
        }

        public MethodInfo getMethod() {
            return method;
        }

        public ParameterAnnotationHolder createForParameter(MethodParameterInfo parameter) {
            String ref = JandexUtil.createUniqueMethodParameterRef(parameter);
            return parameters.computeIfAbsent(ref, k -> new ParameterAnnotationHolder(parameter));
        }

        public ParameterAnnotationHolder byParamRef(String paramRef) {
            return parameters.get(paramRef);
        }
    }

    public static class ParameterAnnotationHolder extends AbstractAnnotationHolder {

        private MethodParameterInfo parameter;

        public ParameterAnnotationHolder(MethodParameterInfo parameter) {
            this.parameter = parameter;
        }

        public MethodParameterInfo getParameter() {
            return parameter;
        }
    }
}
