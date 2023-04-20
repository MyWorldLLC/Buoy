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
        layouts.put(Byte.class, ValueLayout.JAVA_BYTE);
        layouts.put(Boolean.class, ValueLayout.JAVA_BOOLEAN);
        layouts.put(Character.class, ValueLayout.JAVA_CHAR);
        layouts.put(Double.class, ValueLayout.JAVA_DOUBLE);
        layouts.put(Float.class, ValueLayout.JAVA_FLOAT);
        layouts.put(Integer.class, ValueLayout.JAVA_INT);
        layouts.put(Long.class, ValueLayout.JAVA_LONG);
        layouts.put(Short.class, ValueLayout.JAVA_SHORT);
        layouts.put(MemorySegment.class, ValueLayout.ADDRESS);

        cachedFunctionHandles = new HashMap<>();

        architecture = Platform.detectArchitecture();
    }

    public void defineStruct(StructDef model){
        if(structs.containsKey(model.name())){
            throw new IllegalStateException("Struct %s is already defined".formatted(model.name()));
        }
        structs.put(model.name(), model);
    }

    protected CStruct getStructAnnotation(Class<?> type){
        var annotation = type.getAnnotation(CStruct.class);
        if(annotation == null){
            throw new IllegalArgumentException("Class %s is not annotated with @CStruct".formatted(type.getName()));
        }
        return annotation;
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
            for (var field : struct.fields()) {
                builder.with(new FieldDef(field.index(), field.field(), field.type(), field.pointer()));
            }

            return builder.build();
        });
    }

    public MemoryLayout getLayout(Class<?> targetType){
        return layouts.computeIfAbsent(targetType, (t) -> {
            var structAnnotation = getStructAnnotation(targetType);
            var structDef = structs.get(structAnnotation.name());
            if(structDef == null){
                throw new IllegalStateException("No struct definition found for class " + targetType.getName());
            }
            return calculateLayout(structDef);
        });
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

    @SuppressWarnings("unchecked")
    public <T> T populateStructFieldHandles(T target, MemorySegment segment) throws IllegalAccessException {
        var handlers = (ObjectHandlers<T>) objectHandlers.get(target.getClass());
        for(var handler : handlers.structFieldHandlers()){
            handler.handle(this, segment, target);
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public <T> T populateFunctionHandles(T target) throws IllegalAccessException {
        var handlers = (ObjectHandlers<T>) objectHandlers.get(target.getClass());
        for(var handler : handlers.functionHandlers()){
            handler.handle(this, target);
        }
        return target;
    }

    public <T> T populate(T target, MemorySegment segment) throws IllegalAccessException {
        populateStructFieldHandles(target, segment);
        populateFunctionHandles(target);
        return target;
    }

    public long sizeOf(FieldDef field){
        return field.isPointer() ? sizeOf(MemorySegment.class) : sizeOf(field.type());
    }

    public long sizeOf(Class<?> type){
        return layoutFor(type).bitSize();
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
            var layout = getLayout(field.type());

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
        var structDef = getOrDefineStruct(targetType);

        var fieldHandlers = new ArrayList<StructMappingHandler<T>>();
        var functionHandlers = new ArrayList<FunctionHandler<T>>();
        for(var field : targetType.getDeclaredFields()){
            field.setAccessible(true);
            // Handle self pointers
            var selfPtr = field.getAnnotation(SelfPointer.class);
            if(selfPtr != null){
                fieldHandlers.add(new SelfPointerHandler<>(field));
            }

            var fieldHandle = field.getAnnotation(FieldHandle.class);
            if(fieldHandle != null){
                fieldHandlers.add(new FieldHandler<>(structDef.field(fieldHandle.field()), field));
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
                builder.withReturn(getLayout(functionHandle.returns()));
                for(var param : functionHandle.params()){
                    builder.withParam(getLayout(param));
                }

                var descriptor = builder.build();

                getOrDefineFunction(functionHandle.name(), descriptor);

                functionHandlers.add(new FunctionHandler<>(field, functionHandle.name(), descriptor));
            }
        }

        var handlers = new ObjectHandlers<>(targetType, fieldHandlers, functionHandlers);
        return register(targetType, handlers);
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
