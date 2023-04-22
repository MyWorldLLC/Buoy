package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;
import com.myworldvw.buoy.GlobalHandle;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class Globals {

    @GlobalHandle(name = "errno", type = int.class)
    public MemorySegment errno;

    @FunctionHandle(name = "set_errno", params = {int.class})
    public MethodHandle setErrno;

    @FunctionHandle(name = "get_errno", returns = int.class)
    public MethodHandle getErrno;

    public void setErrno(int errno) throws Throwable {
        setErrno.invokeExact(errno);
    }

    public int getErrno() throws Throwable {
        return (int) getErrno.invokeExact();
    }

}
