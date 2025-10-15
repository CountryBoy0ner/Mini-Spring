package com.innowise.miniSpring.minispring;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ClassScanner {

    public static Set<Class<?>> findClasses(String basePackage) {
        Set<Class<?>> classes = new HashSet<>();
        String path = basePackage.replace('.','/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null) {
            throw new RuntimeException("Package not found: " +basePackage);
        }

        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            throw new RuntimeException("Directory not found for package:" + basePackage);
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(basePackage + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }
        return classes;
    }//todo
}