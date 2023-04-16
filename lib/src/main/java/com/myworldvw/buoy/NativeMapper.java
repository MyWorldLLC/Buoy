package com.myworldvw.buoy;

import com.myworldvw.buoy.mapping.StructModel;

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
    protected final Map<String, MethodHandle> cachedFunctionHandles;

    public NativeMapper(){
        this(Linker.nativeLinker().defaultLookup());
    }

    public NativeMapper(SymbolLookup lookup){
        this.lookup = lookup;

        structs = new HashMap<>();

        cachedFunctionHandles = new HashMap<>();
    }

    public void defineStruct(MemoryLayout structLayout){
        structs.put(structLayout.name()
                .orElseThrow(() -> new IllegalArgumentException("Struct layouts must be named")),
                structLayout);
    }

    public MemoryLayout getLayout(Class<?> targetType){
        return null; // TODO
    }

    public MethodHandle defineOrGetFunction(String name, FunctionDescriptor functionDesc){
        return cachedFunctionHandles.computeIfAbsent(name, (n) -> {
            var fPtr = lookup.lookup(name)
                    .orElseThrow(() -> new IllegalArgumentException("Function not found: " + name));

            var handle = Linker.nativeLinker().downcallHandle(fPtr, functionDesc);

            return handle;
        });
    }

    public MethodHandle getFunction(String name){
        var handle = cachedFunctionHandles.get(name);
        if(handle == null){
            throw new IllegalArgumentException("Function has not been mapped: " + name);
        }

        return handle;
    }

    public NativeMapper register(Class<?> targetType){
        // TODO - define structs & functions from annotations
        return this;
    }

    public Object populateStruct(Object target){
        return target;
    }

    public Object populateStruct(Object target, StructModel model){
        return target;
    }

    public Object populateFunctions(Object target){
        return target;
    }

    public Object populate(Object target){
        populateStruct(target);
        populateFunctions(target);
        return target;
    }

}
