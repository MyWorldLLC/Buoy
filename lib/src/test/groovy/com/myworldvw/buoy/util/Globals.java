package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;
import com.myworldvw.buoy.GlobalHandle;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class Globals {

    @GlobalHandle(name = "test_error", type = int.class)
    public static MemorySegment error;

    @FunctionHandle(name = "set_and_get_error", returns = int.class, params = {int.class})
    public MethodHandle setAndGetError;

    @FunctionHandle(name = "get_error", returns = int.class)
    public MethodHandle getError;

    @FunctionHandle(name = "get_error_address", returns = MemoryAddress.class)
    public MethodHandle getErrorAddress;

    public int setAndGetError(int error) throws Throwable {
        return (int) setAndGetError.invokeExact(error);
    }

    public int getError() throws Throwable {
        return (int) getError.invokeExact();
    }

    public MemoryAddress getErrorAddress() throws Throwable {
        return (MemoryAddress) getErrorAddress.invokeExact();
    }

}
