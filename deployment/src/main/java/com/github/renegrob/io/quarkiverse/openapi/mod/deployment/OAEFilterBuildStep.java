package com.github.renegrob.io.quarkiverse.openapi.mod.deployment;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jsoup.Connection;

import io.quarkiverse.smallrye.openapi.extras.runtime.annotations.OAEFilterSelector;
import io.quarkiverse.smallrye.openapi.extras.runtime.filters.OAEFilter;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.smallrye.openapi.deployment.OpenApiFilteredIndexViewBuildItem;
import io.quarkus.smallrye.openapi.deployment.spi.AddToOpenAPIDefinitionBuildItem;
import io.smallrye.openapi.runtime.scanner.FilteredIndexView;

public class OAEFilterBuildStep {

    public static final String INIT = "<init>";

    private static final DotName OAEFilterSelectorDotName = DotName.createSimple(OAEFilterSelector.class.getName());
    private static final DotName OAEFilterDotName = DotName.createSimple(OAEFilter.class.getName());
    private static final DotName OperationDotName = DotName.createSimple(Operation.class.getName());

    private static final DotName RequestBodyDotName = DotName.createSimple(Connection.Request.class.getName());

    private static final DotName ParameterDotName = DotName.createSimple(Parameter.class.getName());

    @BuildStep
    void addOpenApiModConfigFilter(BuildProducer<AddToOpenAPIDefinitionBuildItem> addToOpenAPIDefinitionProducer,
            OpenApiFilteredIndexViewBuildItem apiFilteredIndexViewBuildItem) {

        final FilteredIndexView index = apiFilteredIndexViewBuildItem.getIndex();

        Map<String, Object> filterSingletons = new HashMap<>();
        Map<DotName, Collection<OAEFilter<?>>> annotationToFilterClass = new HashMap<>();

        ClassLoader cl = getClass().getClassLoader();

        OAEFilters filters = new OAEFilters();

        final Collection<AnnotationInstance> aeoFilterAnnotations = index.getAnnotations(OAEFilterSelectorDotName);
        for (AnnotationInstance oaeFilter : aeoFilterAnnotations) {
            if (oaeFilter.target().kind() == AnnotationTarget.Kind.CLASS) {
                final ClassInfo filterClassInfo = oaeFilter.target().asClass();
                final OAEFilter<?> filterInstance = (OAEFilter<?>) createSingleton(cl, filterClassInfo, filterSingletons);

                final Type[] annotationTypes = oaeFilter.value("annotationTypes").asClassArray();
                for (Type annotationType : annotationTypes) {
                    annotationToFilterClass.computeIfAbsent(annotationType.name(), key -> new HashSet<>()).add(filterInstance);
                }
            }
        }
        for (Map.Entry<DotName, Collection<OAEFilter<?>>> entry : annotationToFilterClass.entrySet()) {
            final Collection<AnnotationInstance> annotationInstances = index.getAnnotations(entry.getKey());
            final Collection<OAEFilter<?>> oaeFilters = entry.getValue();
            for (AnnotationInstance ai : annotationInstances) {

                if (ai.target().kind().equals(AnnotationTarget.Kind.METHOD)) {
                    MethodInfo method = ai.target().asMethod();
                    for (OAEFilter<?> oaeFilter : oaeFilters) {
                        filters.createForMethod(method).addFilter(oaeFilter).addAnnotationInstance(ai);
                    }
                }
                if (ai.target().kind().equals(AnnotationTarget.Kind.CLASS)) {
                    ClassInfo classInfo = ai.target().asClass();
                    List<MethodInfo> methods = classInfo.methods();
                    for (MethodInfo method : methods) {
                        for (OAEFilter<?> oaeFilter : oaeFilters) {
                            filters.createForMethod(method).addFilter(oaeFilter).addAnnotationInstance(ai);
                        }
                    }
                }

                //                if (ai.target().kind().equals(AnnotationTarget.Kind.METHOD_PARAMETER)) {
                //                    MethodParameterInfo parameter = ai.target().asMethodParameter();
                //                    filters.createForMethod(parameter.method())
                //                            .createForParameter(parameter)
                //                            .addAnnotation(ai)
                //                            .addFilters(oaeFilters);
                //                }
            }
        }

        addToOpenAPIDefinitionProducer.produce(new AddToOpenAPIDefinitionBuildItem(new OAEFilterDelegate(filters)));
    }

    private Object createSingleton(ClassLoader cl, ClassInfo filterClassInfo, Map<String, Object> classNameToSingleton) {
        final String className = filterClassInfo.name().toString();
        return classNameToSingleton.computeIfAbsent(className, k -> createInstance(cl, k));
    }

    private Object createInstance(ClassLoader cl, String className) {
        Object instance;
        try {
            final Class<?> filterClass = cl.loadClass(className);
            instance = filterClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return instance;
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
