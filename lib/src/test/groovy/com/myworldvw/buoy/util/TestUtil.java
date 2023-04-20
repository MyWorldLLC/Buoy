package com.myworldvw.buoy.util;

import com.myworldvw.buoy.NativeMapper;

public class TestUtil {

    static {
        System.load(System.getProperty("buoy.test.lib.path"));
        System.out.println("Loaded native library");
    }

    public static TestFunctionHandles makeFunctionHandles() throws IllegalAccessException {
        var mapper = new NativeMapper();
        mapper.register(TestFunctionHandles.class);
        return mapper.populateFunctionHandles(new TestFunctionHandles());
    }

    public static Object create(){
        return null;
    }

}
