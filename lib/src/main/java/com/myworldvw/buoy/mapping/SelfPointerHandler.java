package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;
import com.myworldvw.buoy.SelfPointer;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;

public class SelfPointerHandler<T> implements MappingHandler<T> {

    protected final Field field;

    public SelfPointerHandler(Field field){
        this.field = field;
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {
        field.set(target, segment);
    }
}
