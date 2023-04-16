package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public class FunctionHandler<T> implements MappingHandler<T> {

    protected final Field field;
    protected final String name;
    protected final FunctionDescriptor descriptor;

    protected MethodHandle handle;

    public FunctionHandler(Field field, String functionName, FunctionDescriptor descriptor){
        this.field = field;
        name = functionName;
        this.descriptor = descriptor;
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {
        if(handle == null){
            handle = mapper.defineOrGetFunction(name, descriptor);
        }

        field.set(target, handle);
    }
}
