package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;
import com.myworldvw.buoy.GlobalHandle;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class Globals {

    @GlobalHandle(name = "errno", type = int.class)
    public MemorySegment errno;

    @FunctionHandle(name = "set_and_get_errno", returns = int.class, params = {int.class})
    public MethodHandle setAndGetErrno;

    @FunctionHandle(name = "get_errno", returns = int.class)
    public MethodHandle getErrno;

    public int setAndGetErrno(int errno) throws Throwable {
        return (int) setAndGetErrno.invokeExact(errno);
    }

    public int getErrno() throws Throwable {
        return (int) getErrno.invokeExact();
    }

}
