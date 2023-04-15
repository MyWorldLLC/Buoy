package com.myworldvw.buoy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface StructField {
    int index() default 0;
    Class<?> type();
    boolean pointer() default false;
    String field() default "";
}
