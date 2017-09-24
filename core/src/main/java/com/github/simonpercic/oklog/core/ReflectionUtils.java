package com.github.simonpercic.oklog.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Java Reflection utils.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        // no instance
    }

    /**
     * Is class available.
     *
     * @param className class name
     * @return true if class exists, false otherwise
     */
    public static boolean hasClass(@NotNull String className) {
        return getClass(className) != null;
    }

    /**
     * Get method on class from class name, method name and param type.
     *
     * @param className class name
     * @param methodName method name
     * @param paramTypes param types
     * @return method on class
     */
    @Nullable
    public static Method getMethod(@NotNull String className, @NotNull String methodName, Class<?>... paramTypes) {
        Class<?> clazz = getClass(className);

        if (clazz == null) {
            return null;
        }

        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    /**
     * Get class from class name.
     *
     * @param className class name
     * @return class from class name
     */
    @Nullable private static Class<?> getClass(@NotNull String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
