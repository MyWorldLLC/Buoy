package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemorySegment;

public interface StructMappingHandler<T> {

    void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException;

    default T handle(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException{
        fill(mapper, segment, target);
        return target;
    }

}
