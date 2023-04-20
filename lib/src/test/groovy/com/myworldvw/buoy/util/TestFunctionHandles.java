package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;

import java.lang.invoke.MethodHandle;

public class TestFunctionHandles {

    @FunctionHandle(name = "add", returns = int.class, params = {int.class, int.class})
    public MethodHandle addFn;

}
