package com.myworldvw.buoy;

import com.myworldvw.buoy.mapping.ObjectModel;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

public class NativeMapper {

    protected final SymbolLookup lookup;

    protected final Map<String, MemoryLayout> structs;
    protected final Map<String, FunctionDescriptor> functions;
    protected final Map<String, MethodHandle> functionHandles;

    public NativeMapper(){
        this(Linker.nativeLinker().defaultLookup());
    }

    public NativeMapper(SymbolLookup lookup){
        this.lookup = lookup;

        structs = new HashMap<>();
        functions = new HashMap<>();

        functionHandles = new HashMap<>();
    }

    public void defineStruct(MemoryLayout structLayout){
        structs.put(structLayout.name()
                .orElseThrow(() -> new IllegalArgumentException("Struct layouts must be named")),
                structLayout);
    }

    public MethodHandle defineFunction(String name, FunctionDescriptor functionDesc){
        var fPtr = lookup.lookup(name)
                .orElseThrow(() -> new IllegalArgumentException("Function not found: " + name));

        var handle = Linker.nativeLinker().downcallHandle(fPtr, functionDesc);

        functions.put(name, functionDesc);
        functionHandles.put(name, handle);

        return handle;
    }

    public MethodHandle getFunction(String name){
        var handle = functionHandles.get(name);
        if(handle == null){
            throw new IllegalArgumentException("Function has not been mapped: " + name);
        }

        return handle;
    }

    public NativeMapper register(Class<?> structModel){
        // TODO
        return this;
    }

    public Object populate(Object structModel){
        return structModel;
    }

}
