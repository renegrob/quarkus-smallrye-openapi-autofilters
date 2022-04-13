package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import org.jboss.jandex.Type;
import org.jsoup.Connection;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilter;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEBaseFilter;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.smallrye.openapi.deployment.OpenApiFilteredIndexViewBuildItem;
import io.quarkus.smallrye.openapi.deployment.spi.AddToOpenAPIDefinitionBuildItem;
import io.smallrye.openapi.runtime.scanner.FilteredIndexView;

public class OAEFilterBuildStep {

    public static final String INIT = "<init>";

    private static final DotName OAEFilterDotName = DotName.createSimple(OAEFilter.class.getName());
    private static final DotName OAEBaseFilterDotName = DotName.createSimple(OAEBaseFilter.class.getName());
    private static final DotName OperationDotName = DotName.createSimple(Operation.class.getName());

    private static final DotName RequestBodyDotName = DotName.createSimple(Connection.Request.class.getName());

    private static final DotName ParameterDotName = DotName.createSimple(Parameter.class.getName());

    @BuildStep
    void addOpenApiModConfigFilter(BuildProducer<AddToOpenAPIDefinitionBuildItem> addToOpenAPIDefinitionProducer,
            OpenApiFilteredIndexViewBuildItem apiFilteredIndexViewBuildItem) {

        final FilteredIndexView index = apiFilteredIndexViewBuildItem.getIndex();

        final Collection<AnnotationInstance> aeoFilterAnnotations = index
                .getAnnotations(OAEFilterDotName);

        Map<String, Object> classNameToSingleton = new HashMap<>();

        //ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = getClass().getClassLoader();

        OperationAnnotationInfo operationAnnotationInfo = new OperationAnnotationInfo();
        for (AnnotationInstance oaeFilter : aeoFilterAnnotations) {
            if (oaeFilter.target().kind() == AnnotationTarget.Kind.CLASS) {
                final DotName filterClassName = oaeFilter.value("filterClass").asClass().name();
                System.out.println(filterClassName);
                final ClassInfo filterClassInfo = index.getClassByName(filterClassName);
                final Type filterType = filterClassInfo.interfaceTypes().stream()
                        .filter(ifc -> ifc.name().equals(OAEBaseFilterDotName))
                        .map(ifc -> ifc.asParameterizedType().arguments().get(0)).collect(
                                Collectors.toList())
                        .get(0);
                System.out.println("Filter type: " + filterType);
                System.out.println(oaeFilter.target().asClass());

                final Collection<AnnotationInstance> filterAnnotations = index
                        .getAnnotations(oaeFilter.target().asClass().name());
                if (OperationDotName.equals(filterType.name())) {
                    OAEBaseFilter<Operation> operationFilter = (OAEBaseFilter<Operation>) createSingleton(cl, filterClassInfo,
                            classNameToSingleton);
                    configureOperationAnnotationFilter(operationAnnotationInfo, filterAnnotations, operationFilter);
                }
                // TODO: RequestBody
                if (ParameterDotName.equals(filterType.name())) {
                    OAEBaseFilter<Parameter> parameterFilter = (OAEBaseFilter<Parameter>) createSingleton(cl, filterClassInfo,
                            classNameToSingleton);
                    configureParameterAnnotationFilter(operationAnnotationInfo, filterAnnotations, parameterFilter);

                }
            }
        }

        addToOpenAPIDefinitionProducer.produce(
                new AddToOpenAPIDefinitionBuildItem(
                        new OAEFilterDelegate(operationAnnotationInfo)));
    }

    private Object createSingleton(ClassLoader cl, ClassInfo filterClassInfo, Map<String, Object> classNameToSingleton) {
        final String className = filterClassInfo.name().toString();
        return classNameToSingleton.computeIfAbsent(className, k -> createInstance(cl, k));
    }

    private Object createInstance(ClassLoader cl, String filterClassName) {
        Object instance;
        try {
            final Class<?> filterClass = cl.loadClass(filterClassName);
            instance = filterClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    private void configureOperationAnnotationFilter(OperationAnnotationInfo operationAnnotationInfo,
            Collection<AnnotationInstance> annotationInstances, OAEBaseFilter<Operation> filterInstance) {

        for (AnnotationInstance ai : annotationInstances) {
            if (ai.target().kind().equals(AnnotationTarget.Kind.METHOD)) {
                MethodInfo method = ai.target().asMethod();
                //String ref = JandexUtil.createUniqueMethodReference(method.declaringClass(), method);
                //System.out.println(String.format("method annotationInstance: %s -> %s", ref, ai));
                operationAnnotationInfo.createForMethod(method).addAnnotation(ai).addFilter(filterInstance);
            }
            if (ai.target().kind().equals(AnnotationTarget.Kind.CLASS)) {
                ClassInfo classInfo = ai.target().asClass();
                List<MethodInfo> methods = classInfo.methods();
                for (MethodInfo method : methods) {
                    // String ref = JandexUtil.createUniqueMethodReference(classInfo, method);
                    //System.out.println(String.format("class annotationInstance: %s -> %s", ref, ai));
                    operationAnnotationInfo.createForMethod(method).addAnnotation(ai).addFilter(filterInstance);
                    ;
                }
            }
        }
    }

    private void configureParameterAnnotationFilter(OperationAnnotationInfo operationAnnotationInfo,
            Collection<AnnotationInstance> annotationInstances, OAEBaseFilter<Parameter> filterInstance) {

        for (AnnotationInstance ai : annotationInstances) {
            if (ai.target().kind().equals(AnnotationTarget.Kind.METHOD_PARAMETER)) {
                MethodParameterInfo parameter = ai.target().asMethodParameter();
                operationAnnotationInfo
                        .createForMethod(parameter.method())
                        .createForParameter(parameter)
                        .addAnnotation(ai)
                        .addFilter(filterInstance);
            }
        }
    }
    /*
     * private void implementCrudRepositories(BuildProducer<GeneratedBeanBuildItem> generatedBeans,
     * BuildProducer<GeneratedClassBuildItem> generatedClasses,
     * BuildProducer<ReflectiveClassBuildItem> reflectiveClasses,
     * IndexView index) {
     * 
     * // ClassOutput beansClassOutput = new GeneratedBeanGizmoAdaptor(generatedBeans);
     * ClassOutput nonBeansClassOutput = new GeneratedClassGizmoAdaptor(generatedClasses, true);
     * // reflectiveClasses.produce(new ReflectiveClassBuildItem(true, false, className));
     * 
     * try (ClassCreator implClassCreator = ClassCreator.builder().classOutput(nonBeansClassOutput)
     * .interfaces(OAEBaseFilterDotName.toString()).className(implName.toString())
     * .build()) {
     * implClassCreator.getMethodCreator()
     * }
     * }
     * 
     */
}
