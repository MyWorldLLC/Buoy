package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemorySegment;

public interface FunctionMappingHandler<T> {

    void fill(NativeMapper mapper, T target) throws IllegalAccessException;

    default T handle(NativeMapper mapper, T target) throws IllegalAccessException{
        fill(mapper, target);
        return target;
    }

}
