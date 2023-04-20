package com.myworldvw.buoy.util;

import com.myworldvw.buoy.NativeMapper;
import com.myworldvw.buoy.Platform;

import java.lang.foreign.MemorySession;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Path;

public class TestUtil {

    public static final SymbolLookup testLib = Platform.loadLibrary(Path.of(System.getProperty("buoy.test.lib.path")), MemorySession.global());


    public static TestFunctionHandles makeFunctionHandles() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(TestFunctionHandles.class);
        return mapper.populateFunctionHandles(new TestFunctionHandles());
    }

    public static Object create(){
        return null;
    }

}
