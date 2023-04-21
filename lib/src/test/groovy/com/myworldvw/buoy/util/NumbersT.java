package com.myworldvw.buoy.util;

import com.myworldvw.buoy.*;

import java.lang.foreign.Addressable;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

@CStruct(
        name = "numbers_t",
        fields = {
                @StructField(index = 0, field = "a", type = short.class),
                @StructField(index = 1, field = "b", type = int.class)
        }
)
public class NumbersT {

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(field = "a")
    protected VarHandle a;

    @FieldHandle(field = "b")
    protected VarHandle b;

    @FunctionHandle(name = "add_short", returns = int.class, params = {int.class, short.class})
    protected MethodHandle add_short;

    @FunctionHandle(name = "add_numbers_t", returns = int.class, params = { MemorySegment.class })
    protected MethodHandle add_numbers_t;

    public int addShort() throws Throwable {
        return (int) add_short.invokeExact((int)b.get(self), (short)a.get(self));
    }

    public void set(short a, int b){
        this.a.set(self, a);
        this.b.set(self, b);
    }

    public int addNumbersT() throws Throwable {
        return (int) add_numbers_t.invokeExact((Addressable)self.address());
    }

}
