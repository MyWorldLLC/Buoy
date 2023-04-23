package com.myworldvw.buoy.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Util {

    public static <T> boolean skipField(Field field, T target){
        // Skip fields that are static when the target is not a class
        var isStatic = Modifier.isStatic(field.getModifiers());
        var targetIsClass = target instanceof Class;
        return (isStatic && !targetIsClass) ||
                (!isStatic && targetIsClass);
    }

    public static <T> boolean isStaticTarget(T target){
        return target instanceof Class;
    }

}
