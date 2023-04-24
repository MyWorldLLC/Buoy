package com.myworldvw.buoy.util;

import com.myworldvw.buoy.*;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

@CStruct(
        name = "outer_t",
        fields = {
                @StructField(name = "outer_a", type = byte.class),
                @StructField(name = "nested", type = InnerT.class),
                @StructField(name = "nested_ptr", type = MemorySegment.class)
        }
)
public class OuterT {

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(name = "outer_a")
    protected VarHandle outerA;

    @FieldHandle(name = "nested")
    protected InnerT nested;

    @FieldHandle(name = "nested_ptr")
    protected InnerT nestedPtr;

    public byte getOuterA(){
        return (byte) outerA.get(self);
    }

    public InnerT getNested(){
        return nested;
    }

    public InnerT getNestedPtr(){
        return nestedPtr;
    }

    //outer_t make_outer_t_value(inner_t* nested)
    @FunctionHandle(name = "make_outer_t_value", returns = OuterT.class, params = {MemorySegment.class})
    protected static MethodHandle makeOuterTValue;

    public static MemorySegment makeOuterT(MemorySegment innerTPtr) throws Throwable {
        return makeOuterT(innerTPtr);
    }
}
