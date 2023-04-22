package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

public interface FunctionMappingHandler<T> {

    void fill(NativeMapper mapper, T target) throws IllegalAccessException;

    default T handle(NativeMapper mapper, T target) throws IllegalAccessException{
        fill(mapper, target);
        return target;
    }

}
