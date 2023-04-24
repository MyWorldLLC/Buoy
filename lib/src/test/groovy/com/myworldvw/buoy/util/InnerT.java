package com.myworldvw.buoy.util;

import com.myworldvw.buoy.CStruct;
import com.myworldvw.buoy.FieldHandle;
import com.myworldvw.buoy.SelfPointer;
import com.myworldvw.buoy.StructField;


import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;

@CStruct(
        name = "inner_t",
        fields = {
                @StructField(name = "a", type = byte.class),
                @StructField(name = "b", type = int.class)
        }
)
public class InnerT {

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(name = "a")
    protected VarHandle a;

    @FieldHandle(name = "b")
    protected VarHandle b;

    public byte getInnerA(){
        return (byte) a.get(self);
    }

    public int getInnerB(){
        return (int) b.get(self);
    }
}
