package com.myworldvw.buoy.util;

import com.myworldvw.buoy.FunctionHandle;

import java.lang.invoke.MethodHandle;

public class Statics {

    @FunctionHandle(name = "add", returns = int.class, params = {int.class, int.class})
    public static MethodHandle staticAdd;

    @FunctionHandle(name = "add", returns = int.class, params = {int.class, int.class})
    public MethodHandle instanceAdd;

}
