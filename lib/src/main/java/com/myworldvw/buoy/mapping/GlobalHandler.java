package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;

public class GlobalHandler<T> implements StructMappingHandler<T> {

    protected final Field field;
    protected final String name;
    protected final Class<?> type;
    protected final boolean pointer;

    protected volatile MemorySegment symbolPtr;

    public GlobalHandler(Field field, String name, Class<?> type, boolean pointer){
        this.field = field;
        this.name = name;
        this.type = type;
        this.pointer = pointer;

        if(!field.getType().equals(MemorySegment.class)){
            throw new IllegalArgumentException("Globals can only be accessed via MemorySegment");
        }
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {

        if(symbolPtr == null){
            symbolPtr = mapper.getLookup().lookup(name)
                    .orElseThrow(() -> new IllegalArgumentException("Symbol " + name + " not found"));
            // re-interpret the 0-length MemorySegment returned by symbol lookup as a segment with a length
            // matching the target type.
           symbolPtr =  MemorySegment.ofAddress(
                   symbolPtr.address(),
                   pointer ? mapper.sizeOf(MemorySegment.class) : mapper.sizeOf(type),
                   symbolPtr.session());
        }

        field.set(target, symbolPtr);

    }
}
