package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.FieldDef;
import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class FieldHandler<T> implements StructMappingHandler<T> {

    protected final FieldDef model;
    protected final Field field;

    protected volatile VarHandle handle;

    public FieldHandler(FieldDef model, Field field){
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
        // Given the field type:
        // If it's a VarHandle, we get a handle to the field in the struct
        // If it's a MemorySegment, we calculate the field offset and assign it
        // If it's an object, we determine if it's a nested struct or a pointer to a struct,
        // and we populate it with the offset of the field (nested) *or* the memory address
        // contained in the field (pointer) as the object's self-pointer segment

        if(Util.skipField(field, target)){
            return;
        }

        var fieldType = field.getType();
        if(fieldType.equals(VarHandle.class)){
            if(handle == null){
                handle = getHandle(mapper.getLayout(target.getClass()));
            }
            field.set(target, handle);
        }else if(fieldType.equals(MemorySegment.class)){
            field.set(target, segmentForField(mapper, target, segment));
        }else{
            var structDef = mapper.getOrDefineStruct(fieldType);
            if(structDef == null){
                throw new IllegalArgumentException("Not a mappable type for a field handle: " + fieldType);
            }

            var structSegment = segmentForField(mapper, target, segment);
            if(model.isPointer()){
                // If this is a pointer type, we have to construct a new segment starting at the address
                // contained in this segment, with the length of the field type.
                structSegment = MemorySegment.ofAddress(
                        structSegment.get(ValueLayout.ADDRESS, 0),
                        mapper.sizeOf(model.type()),
                        structSegment.session());
            }
            var nestedTarget = field.get(target);
            if(nestedTarget != null){
                mapper.populate(nestedTarget, structSegment);
            }
        }
    }

    protected MemorySegment segmentForField(NativeMapper mapper, Object target, MemorySegment segment){
        var offset = mapper.getLayout(target.getClass()).byteOffset(MemoryLayout.PathElement.groupElement(model.name()));
        return model.isPointer()
                ? segment.asSlice(offset, ValueLayout.ADDRESS.byteSize())
                : segment.asSlice(offset, mapper.sizeOf(model));
    }
}
