package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;

public class SelfPointerHandler<T> implements StructMappingHandler<T> {

    protected final Field field;

    public SelfPointerHandler(Field field){
        this.field = field;
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {
        if(!Util.skipField(field, target)){
            field.set(target, segment);
        }
    }
}
