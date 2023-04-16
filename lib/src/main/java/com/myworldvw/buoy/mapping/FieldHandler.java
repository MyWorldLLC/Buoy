package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class FieldHandler<T> implements MappingHandler<T> {

    protected final FieldModel model;
    protected final Field field;

    protected volatile VarHandle handle;

    public FieldHandler(FieldModel model, Field field){
        this.model = model;
        this.field = field;
    }

    public VarHandle getHandle(MemoryLayout layout){
        return layout.varHandle(MemoryLayout.PathElement.groupElement(model.name()));
    }

    public MethodHandle getAccessor(MemoryLayout layout, VarHandle.AccessMode mode){
        return getHandle(layout).toMethodHandle(mode);
    }

    public MethodHandle getGetter(MemoryLayout layout){
        return getAccessor(layout, VarHandle.AccessMode.GET);
    }

    public MethodHandle getGetter(MemorySegment ptr, MemoryLayout layout){
        return getGetter(layout).bindTo(ptr);
    }

    public MethodHandle getSetter(MemoryLayout layout){
        return getAccessor(layout, VarHandle.AccessMode.SET);
    }

    public MethodHandle getSetter(MemorySegment ptr, MemoryLayout layout){
        return getSetter(layout).bindTo(ptr);
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {
        if(handle == null){
            handle = getHandle(mapper.getLayout(target.getClass()));
        }
        field.set(target, handle);
    }
}
