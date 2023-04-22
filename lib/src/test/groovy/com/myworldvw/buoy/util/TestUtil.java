package com.myworldvw.buoy.util;

import com.myworldvw.buoy.NativeMapper;
import com.myworldvw.buoy.Platform;

import java.lang.foreign.Linker;
import java.lang.foreign.MemorySession;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Path;

public class TestUtil {

    public static final SymbolLookup testLib = Platform.loadLibrary(Path.of(System.getProperty("buoy.test.lib.path") + "/" + Platform.standardLibraryName("native")), MemorySession.global());


    public static TestFunctionHandles makeFunctionHandles() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(TestFunctionHandles.class);
        return mapper.populateFunctionHandles(new TestFunctionHandles());
    }

    public static NumbersT makeNumbersT() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(NumbersT.class);
        return mapper.populate(new NumbersT(), Platform.allocateStruct(mapper.getLayout(NumbersT.class), MemorySession.global()));
    }

    public static Globals makeGlobals() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(Globals.class);
        return mapper.populate(new Globals(), null);
    }

    public static Object create(){
        return null;
    }

}
