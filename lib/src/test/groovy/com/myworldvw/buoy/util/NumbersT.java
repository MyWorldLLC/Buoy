package com.myworldvw.buoy.util;

import com.myworldvw.buoy.*;

import java.lang.foreign.Addressable;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

@CStruct(
        name = "numbers_t",
        fields = {
                @StructField(index = 0, field = "a", type = int.class),
                @StructField(index = 1, field = "b", type = short.class)
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
        return (int) add_short.invokeExact((int)a.get(self), (short)b.get(self));
    }

    public void set(int a, short b){
        this.a.set(self, a);
        this.b.set(self, b);
    }

    public int addNumbersT() throws Throwable {
        return (int) add_numbers_t.invokeExact((Addressable)self.address());
    }

}
