package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;
import com.myworldvw.buoy.Pointer;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
        if(Util.skipField(field, target)){
            return;
        }

        if(symbolPtr == null){
            symbolPtr = mapper.getLookup().lookup(name)
                    .map(symbol -> Pointer.cast(symbol, mapper.getLayout(pointer ? MemorySegment.class : type)))
                    .orElseThrow(() -> new IllegalArgumentException("Symbol " + name + " not found"));
        }

        field.set(target, symbolPtr);
    }
}
