package com.myworldvw.buoy;

import com.myworldvw.buoy.mapping.*;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
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

    public StructDef defineOrGetStruct(Class<?> type){
        return structs.computeIfAbsent(type.getName(), (t) -> {
            var structAnnotation = type.getAnnotation(CStruct.class);
            if(structAnnotation != null){
                var builder = StructDef.create(structAnnotation.name(), structAnnotation.packed());
                for(var field : structAnnotation.fields()){
                    builder.with(new FieldDef(field.index(), field.field(), field.type(), field.pointer()));
                }
                return builder.build();
            }
            return null;
        });
    }

    public MemoryLayout getLayout(Class<?> targetType){
        return layouts.computeIfAbsent(targetType, (t) -> {
            var structDef = structs.get(targetType.getName());
            if(structDef == null){
                throw new IllegalStateException("No struct definition found for class " + targetType.getName());
            }
            return calculateLayout(structDef);
        });
    }

    public MethodHandle defineOrGetFunction(String name, FunctionDescriptor functionDesc){
        return cachedFunctionHandles.computeIfAbsent(name, (n) -> {
            var fPtr = lookup.lookup(name)
                    .orElseThrow(() -> new IllegalArgumentException("Function not found: " + name));

            var handle = Linker.nativeLinker().downcallHandle(fPtr, functionDesc);

            return handle;
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
    public <T> T populateStruct(T target, MemorySegment segment) throws IllegalAccessException {
        var handlers = (ObjectHandlers<T>) objectHandlers.get(target.getClass());
        for(var handler : handlers.structFieldHandlers()){
            handler.handle(this, segment, target);
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public <T> T populateFunctions(T target) throws IllegalAccessException {
        var handlers = (ObjectHandlers<T>) objectHandlers.get(target.getClass());
        for(var handler : handlers.functionHandlers()){
            handler.handle(this, target);
        }
        return target;
    }

    public <T> T populate(T target, MemorySegment segment) throws IllegalAccessException {
        populateStruct(target, segment);
        populateFunctions(target);
        return target;
    }

    public long sizeOf(Class<?> type){
        return layoutFor(type).bitSize();
    }

    public <T> MemoryLayout layoutFor(Class<T> c){
        return layouts.computeIfAbsent(c, (t) -> {
            // Primitive types are already defined in layouts
            return calculateLayout(defineOrGetStruct(c));
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
        var structDef = defineOrGetStruct(targetType);

        var fieldHandlers = new ArrayList<StructMappingHandler<T>>();
        var functionHandlers = new ArrayList<FunctionHandler<T>>();
        for(var field : targetType.getDeclaredFields()){
            field.setAccessible(true);
            // TODO - handle self pointers to nested structs
            // Handle self pointers
            var selfPtr = field.getAnnotation(SelfPointer.class);
            if(selfPtr != null){
                fieldHandlers.add(new SelfPointerHandler<>(field));
            }

            var fieldHandle = field.getAnnotation(FieldHandle.class);
            if(fieldHandle != null){
                fieldHandlers.add(new FieldHandler<>(structDef.field(fieldHandle.field()), field));
            }

            var functionHandle = field.getAnnotation(FunctionHandle.class);
            if(functionHandle != null){
                var builder = CFunction.create();
                builder.withReturn(getLayout(functionHandle.returns()));
                for(var param : functionHandle.params()){
                    builder.withParam(getLayout(param));
                }

                var descriptor = builder.build();

                defineOrGetFunction(functionHandle.name(), descriptor);

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

    public MethodHandle toCFunction(Object target, String method, MemorySession scope){
        // TODO - reflectively get a method handle and create an upcall stub
        return null;
    }

}
