package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;
import com.myworldvw.buoy.GlobalHandle;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class Globals {

    @GlobalHandle(name = "error", type = int.class)
    public static MemorySegment error;

    @FunctionHandle(name = "set_and_get_error", returns = int.class, params = {int.class})
    public MethodHandle setAndGetError;

    @FunctionHandle(name = "get_error", returns = int.class)
    public MethodHandle getError;

    public int setAndGetError(int errno) throws Throwable {
        return (int) setAndGetError.invokeExact(errno);
    }

    public int getError() throws Throwable {
        return (int) getError.invokeExact();
    }

}
