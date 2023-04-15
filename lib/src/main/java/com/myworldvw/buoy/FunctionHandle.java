package com.myworldvw.buoy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FunctionHandle {

    String name();
    Class<?> returns() default void.class;
    Class<?>[] params() default {};

}
