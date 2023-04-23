package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public class FunctionHandler<T> implements FunctionMappingHandler<T> {

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
    public void fill(NativeMapper mapper, T target) throws IllegalAccessException {

        if(Util.skipField(field, target)){
            return;
        }

        if(handle == null){
            handle = mapper.getOrDefineFunction(name, descriptor);
        }

        field.set(target, handle);
    }
}
