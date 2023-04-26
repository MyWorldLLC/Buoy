/*
 * Copyright (c) 2023. MyWorld, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myworldvw.buoy;

import com.myworldvw.buoy.mapping.*;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class NativeMapper {

    protected final SymbolLookup lookup;
    protected final Platform.Architecture architecture;

    protected final Map<Class<?>, ObjectHandlers<?>> objectHandlers;

    protected final Map<String, StructDef> structs;

    protected final Map<Class<?>, MemoryLayout> layouts;

    protected final Map<String, MethodHandle> cachedFunctionHandles;

    public NativeMapper(){
        this(Linker.nativeLinker().defaultLookup());
    }

    public NativeMapper(SymbolLookup lookup){
        this.lookup = lookup;

        objectHandlers = new HashMap<>();

        structs = new HashMap<>();

        layouts = new HashMap<>();
        layouts.put(byte.class, ValueLayout.JAVA_BYTE);
        layouts.put(boolean.class, ValueLayout.JAVA_BOOLEAN);
        layouts.put(char.class, ValueLayout.JAVA_CHAR);
        layouts.put(double.class, ValueLayout.JAVA_DOUBLE);
        layouts.put(float.class, ValueLayout.JAVA_FLOAT);
        layouts.put(int.class, ValueLayout.JAVA_INT);
        layouts.put(long.class, ValueLayout.JAVA_LONG);
        layouts.put(short.class, ValueLayout.JAVA_SHORT);
        layouts.put(MemorySegment.class, ValueLayout.ADDRESS);
        layouts.put(MemoryAddress.class, ValueLayout.ADDRESS);

        cachedFunctionHandles = new HashMap<>();

        architecture = Platform.detectArchitecture();
    }

    public SymbolLookup getLookup(){
        return lookup;
    }

    public void defineStruct(StructDef model){
        if(structs.containsKey(model.name())){
            throw new IllegalStateException("Struct %s is already defined".formatted(model.name()));
        }
        structs.put(model.name(), model);
    }

    protected Struct getStructAnnotation(Class<?> type){
        var annotation = type.getAnnotation(Struct.class);
        if(annotation == null){
            throw new IllegalArgumentException("Class %s is not annotated with @CStruct".formatted(type.getName()));
        }
        return annotation;
    }

    public boolean isStructType(Class<?> type){
        return type.getAnnotation(Struct.class) != null;
    }

    public String getStructName(Class<?> type){
        return getStructAnnotation(type).name();
    }

    public StructDef getStruct(Class<?> type){
        return structs.get(getStructAnnotation(type).name());
    }

    public StructDef getStruct(String name){
        return structs.get(name);
    }

    public boolean isStructDefined(String name){
        return structs.containsKey(name);
    }

    public StructDef getOrDefineStruct(Class<?> type){
        var struct = getStructAnnotation(type);
        return structs.computeIfAbsent(struct.name(), (t) -> {

            var builder = StructDef.create(struct.name(), struct.packed());
            for (int i = 0; i < struct.fields().length; i++) {
                var field = struct.fields()[i];
                builder.with(new FieldDef(i, field.name(), field.type(), field.pointer(), field.array()));
            }

            return builder.build();
        });
    }

    public MemoryLayout getArrayLayout(Class<?> targetType, long length){
        var layout = getLayout(targetType);
        return getArrayLayout(layout, length);
    }

    public MemoryLayout getArrayLayout(MemoryLayout elementLayout, long length){
        return MemoryLayout.sequenceLayout(length, elementLayout);
    }

    public MemoryLayout getLayout(FieldDef field){
        var layout = getLayout(field.isPointer() ? MemorySegment.class : field.type());
        return field.isArray() ? getArrayLayout(layout, field.array()) : layout;
    }

    public MemoryLayout getLayout(Class<?> targetType){
        var layout = layouts.get(targetType);
        if(layout == null){
            var structAnnotation = getStructAnnotation(targetType);
            var structDef = structs.get(structAnnotation.name());
            if(structDef == null){
                throw new IllegalStateException("No struct definition found for class " + targetType.getName());
            }
            layout = calculateLayout(structDef);
            layouts.put(targetType, layout);
        }
        return layout;
    }

    public MethodHandle getOrDefineFunction(String name, FunctionDescriptor functionDesc){
        return cachedFunctionHandles.computeIfAbsent(name, (n) -> {
            var fPtr = lookup.lookup(name)
                    .orElseThrow(() -> new IllegalArgumentException("Function not found: " + name));

            return Linker.nativeLinker().downcallHandle(fPtr, functionDesc);
        });
    }

    public boolean isFunctionDefined(String name){
        return cachedFunctionHandles.containsKey(name);
    }

    public MethodHandle getFunction(String name){
        var handle = cachedFunctionHandles.get(name);
        if(handle == null){
            throw new IllegalArgumentException("Function has not been mapped: " + name);
        }

        return handle;
    }

    public MemorySegment getGlobalSymbol(String name, Class<?> type){
        return getGlobalSymbol(name, type, null);
    }

    public MemorySegment getGlobalSymbol(String name, Class<?> type, MemorySession scope){
        return lookup.lookup(name)
                .map(symbol -> MemorySegment.ofAddress(
                        symbol.address(),
                        sizeOf(type),
                        scope != null ? scope : MemorySession.openShared()
                ))
                .orElseThrow(() -> new IllegalArgumentException("Symbol " + name + " not found"));
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectHandlers<T> getHandlers(T target){
        return target instanceof Class ?
                (ObjectHandlers<T>) objectHandlers.get(target) :
                (ObjectHandlers<T>) objectHandlers.get(target.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T populateStructFieldHandles(T target, MemorySegment segment) throws IllegalAccessException {
        var handlers = getHandlers(target);
        for(var handler : handlers.structFieldHandlers()){
            handler.handle(this, segment, target);
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public <T> T populateFunctionHandles(T target) throws IllegalAccessException {
        var handlers = getHandlers(target);
        for(var handler : handlers.functionHandlers()){
            handler.handle(this, target);
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public <T> T populateGlobals(T target) throws IllegalAccessException {
        var handlers = getHandlers(target);
        for(var handler : handlers.globalHandlers()){
            handler.handle(this, null, target);
        }
        return target;
    }

    public <T> T populate(T target, MemorySegment segment) throws IllegalAccessException {
        populateStructFieldHandles(target, segment);
        populateFunctionHandles(target);
        populateGlobals(target);
        return target;
    }

    public NativeMapper populateStatic(Class<?> target) throws IllegalAccessException {
        populate(target, null);
        return this;
    }

    public long sizeOf(FieldDef field){
        return field.isPointer() ? sizeOf(MemorySegment.class) : sizeOf(field.type());
    }

    public long sizeOf(Class<?> type){
        if(type.equals(void.class)){
            return 0;
        }
        return layoutFor(type).byteSize();
    }

    public MemorySegment allocate(Class<?> type, MemorySession scope){
        return Platform.allocate(getLayout(type), scope);
    }

    public <T> Array<T> arrayOf(MemoryAddress array, Class<T> type, long length, MemorySession scope){
        return arrayOf(array, type, false, length, scope);
    }

    public <T> Array<T> arrayOf(MemoryAddress array, Class<T> type, boolean isPointer, long length, MemorySession scope){
        var segment = Array.cast(array, getLayout(type), length, scope);
        return arrayOf(segment, type, isPointer);
    }

    public <T> Array<T> arrayOf(MemorySegment array, Class<T> type){
        return arrayOf(array, type, false);
    }

    public <T> Array<T> arrayOf(MemorySegment array, Class<T> type, boolean isPointer){
        return new Array<>(array, getLayout(type), type, isPointer);
    }


    public <T> MemoryLayout layoutFor(Class<T> c){
        return layouts.computeIfAbsent(c, (t) -> {
            // Primitive types are already defined in layouts
            return calculateLayout(getOrDefineStruct(c));
        });
    }

    public MemoryLayout calculateLayout(StructDef def) {
        var structSize = 0;
        MemoryLayout largestElement = null;

        var layouts = new ArrayList<>();

        for(int i = 0; i < def.fields().length; i++){
            var field = def.fields()[i];
            var layout = getLayout(field).withName(field.name());

            // Track largest element so that we can set
            // end padding of the struct accordingly
            if(largestElement == null || largestElement.bitSize() < layout.bitSize()){
                largestElement = layout;
            }

            if(!def.isPacked()){
                var padding = makePadding(structSize, layout);
                if(padding != null){
                    layouts.add(padding);
                    structSize += padding.bitSize();
                }
            }

            layouts.add(layout);
            structSize += layout.bitSize();

        }

        if(!def.isPacked() && largestElement != null){
            // End padding is based on largest element alignment requirements
            var padding = makePadding(structSize, largestElement);
            if(padding != null){
                layouts.add(padding);
            }
        }

        return MemoryLayout.structLayout(layouts.toArray(new MemoryLayout[]{})).withName(def.name());
    }

    public MemoryLayout makePadding(long structSize, MemoryLayout element){
        if(structSize == 0){
            return null; // Struct has no elements yet, so we don't need padding
        }
        var remainder = structSize % element.bitAlignment();
        return remainder != 0 ? MemoryLayout.paddingLayout(element.bitAlignment() - remainder) : null;
    }

    public <T> NativeMapper register(Class<T> targetType){
        return register(targetType, false);
    }

    public <T> NativeMapper register(Class<T> targetType, boolean registerSupers){
        StructDef structDef = null;
        if(isStructType(targetType)){
            structDef = getOrDefineStruct(targetType);
        }

        var fieldHandlers = new ArrayList<StructMappingHandler<T>>();
        var functionHandlers = new ArrayList<FunctionHandler<T>>();
        var globalHandlers = new ArrayList<GlobalHandler<T>>();
        for(var field : targetType.getDeclaredFields()){
            field.setAccessible(true);
            // Handle self pointers
            var selfPtr = field.getAnnotation(SelfPointer.class);
            if(selfPtr != null){
                if(structDef == null){
                    throw new IllegalArgumentException("Cannot use @SelfPointer on a non-struct class: " + targetType.getName());
                }
                fieldHandlers.add(new SelfPointerHandler<>(field));
            }

            var fieldHandle = field.getAnnotation(FieldHandle.class);
            if(fieldHandle != null){
                if(structDef == null){
                    throw new IllegalArgumentException("Cannot use @FieldHandle on a non-struct class: " + targetType.getName());
                }
                fieldHandlers.add(new FieldHandler<>(structDef.field(fieldHandle.name()), field));
                // Recurse to register nested struct types
                if(!field.getType().equals(VarHandle.class) &&
                        !field.getType().equals(MemorySegment.class) &&
                        !isRegistered(field.getType())){
                    register(field.getType());
                }
            }

            var functionHandle = field.getAnnotation(FunctionHandle.class);
            if(functionHandle != null){
                var builder = FunctionDef.create();
                if(!functionHandle.returns().equals(void.class)){
                    builder.withReturn(getLayout(functionHandle.returns()));
                }

                for(var param : functionHandle.params()){
                    builder.withParam(getLayout(param));
                }

                var descriptor = builder.build();

                getOrDefineFunction(functionHandle.name(), descriptor);

                functionHandlers.add(new FunctionHandler<>(field, functionHandle.name(), descriptor));
            }

            var globalHandle = field.getAnnotation(GlobalHandle.class);
            if(globalHandle != null){
                globalHandlers.add(new GlobalHandler<>(field, globalHandle.name(), globalHandle.type(), globalHandle.pointer()));
            }
        }

        var handlers = new ObjectHandlers<>(targetType, fieldHandlers, functionHandlers, globalHandlers);
        register(targetType, handlers);

        if(registerSupers && !targetType.getSuperclass().equals(Object.class)){
            register(targetType.getSuperclass());
        }
        return this;
    }

    public <T> NativeMapper register(Class<T> targetType, ObjectHandlers<T> handlers){
        if(objectHandlers.containsKey(targetType)){
            throw new IllegalStateException("Mapping is already defined for class " + targetType.getName());
        }
        objectHandlers.put(targetType, handlers);
        return this;
    }

    public boolean isRegistered(Class<?> targetType){
        return objectHandlers.containsKey(targetType);
    }

    public MemorySegment toCFunction(Object target, String method, MemorySession scope) throws IllegalAccessException {

        var candidates = Arrays.stream(target.getClass().getMethods()).filter(m -> m.getName().equals(method)).toList();
        if(candidates.size() > 1){
            throw new IllegalArgumentException("Method %s is ambiguous for type %s".formatted(method, target.getClass()));
        }

        if(candidates.size() == 0){
            throw new IllegalArgumentException("Method %s not found for type %s".formatted(method, target.getClass()));
        }

        return toCFunction(target, candidates.get(0), scope);
    }

    public MemorySegment toCFunction(Object target, String method, MemorySession scope, Class<?> retType, Class<?>... paramTypes) throws IllegalAccessException {

        var candidate = Arrays.stream((target instanceof Class ? ((Class<?>) target) : target.getClass()).getMethods())
                .filter(m -> m.getName().equals(method))
                .filter(m -> m.getReturnType().equals(retType))
                .filter(m -> Arrays.equals(m.getParameterTypes(), paramTypes))
                .findFirst();

        var m = candidate.orElseThrow(() -> new IllegalArgumentException("Method %s not found for type %s".formatted(method, target.getClass())));

        return toCFunction(target, m, scope);
    }

    public MemorySegment toCFunction(Object target, Method method, MemorySession scope) throws IllegalAccessException {
        var handle = MethodHandles.lookup().unreflect(method);
        if((method.getModifiers() & Modifier.STATIC) != 0){
            handle.bindTo(target);
        }
        return Platform.toCFunction(handle, getFunctionDescriptor(method), scope);
    }

    public FunctionDescriptor getFunctionDescriptor(Method m){
        var rType = m.getReturnType();
        var paramTypes = Arrays.stream(m.getParameterTypes())
                .map(this::getLayout)
                .toArray(MemoryLayout[]::new);

        return rType.equals(void.class)
                ? FunctionDescriptor.ofVoid(paramTypes)
                : FunctionDescriptor.of(getLayout(rType), paramTypes);
    }

}
