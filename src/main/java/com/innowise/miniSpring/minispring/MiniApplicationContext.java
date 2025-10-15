package com.innowise.miniSpring.minispring;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiniApplicationContext {

    private final Map<Class<?>, Object> beanMap = new HashMap<>();
    private final Map<Class<?>, String> beanScopes = new HashMap<>();
    private final Set<Class<?>> creatingBeans = new HashSet<>();

    public MiniApplicationContext(Class<?>... componentClasses) {
        try {

            for (Class<?> clazz : componentClasses) {
                if (!clazz.isAnnotationPresent(Component.class)) continue;

                String scope = "singleton";
                if (clazz.isAnnotationPresent(Scope.class)) {
                    scope = clazz.getAnnotation(Scope.class).value();
                }

                beanScopes.put(clazz, scope);

                if (scope.equals("singleton")) {
                    Object instance = createBean(clazz);
                    beanMap.put(clazz, instance);
                }
            }

            for (Object bean : beanMap.values()) {
                injectDependencies(bean);
            }
            for (Object bean : beanMap.values()) {
                if (bean instanceof InitializingBean) {
                    ((InitializingBean) bean).afterPropertiesSet();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public MiniApplicationContext(String basePackage) {
        this(ClassScanner.findClasses(basePackage).toArray(new Class[0])); // for scanning
    }

    private Object createBean(Class<?> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 1) {
            Constructor<?> constructor = constructors[0];
            Class<?>[] paramTypes = constructor.getParameterTypes();

            if (paramTypes.length == 0) {
                return constructor.newInstance();
            }

            Object[] params = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getBean(paramTypes[i]);
            }

            return constructor.newInstance(params);
        }

        return clazz.getDeclaredConstructor().newInstance();
    }


    private void injectDependencies(Object bean) throws IllegalAccessException {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> dependencyType = field.getType();
                Object dependency = getBean(dependencyType);
                field.setAccessible(true);
                field.set(bean, dependency);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public <E> E getBean(Class<E> type) {
        try {
            String scope = beanScopes.getOrDefault(type, "singleton");

            if (scope.equals("singleton")) {
                return (E) beanMap.get(type);
            }

            if (creatingBeans.contains(type)) {
                throw new RuntimeException("Circular dependency detected for " + type.getName());
            }

            creatingBeans.add(type);

            E instance = (E) createBean(type);
            injectDependencies(instance);

            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            creatingBeans.remove(type);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Error during creation of Bean: " + type.getName(), e);
        }
    }
}
