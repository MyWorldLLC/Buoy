/*
 * Copyright (c) 2023. MyWorld, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myworldvw.buoy.util;

import com.myworldvw.buoy.Array;
import com.myworldvw.buoy.NativeMapper;
import com.myworldvw.buoy.Platform;

import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Path;

public class TestUtil {

    public static final SymbolLookup testLib = Platform.loadLibrary(Path.of(System.getProperty("buoy.test.lib.path") + "/" + Platform.standardLibraryName("native")), SegmentScope.global());

    public static NativeMapper makeMapper(){
        return new NativeMapper(testLib)
                .register(OuterT.class)
                .register(TestFunctionHandles.class)
                .register(Globals.class)
                .register(NumbersT.class)
                .register(Statics.class);
    }

    public static TestFunctionHandles makeFunctionHandles() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(TestFunctionHandles.class);
        return mapper.populateFunctionHandles(new TestFunctionHandles());
    }

    public static NumbersT makeNumbersT() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(NumbersT.class);
        return mapper.populate(new NumbersT(), Platform.allocate(mapper.getLayout(NumbersT.class), SegmentScope.global()));
    }

    public static Globals makeGlobals() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(Globals.class);
        mapper.populateStatic(Globals.class);
        return mapper.populate(new Globals(), null);
    }

    public static Statics makeStatics() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(Statics.class);
        return mapper.populate(new Statics(), null);
    }

    public static void fillStatics() throws IllegalAccessException {
        var mapper = new NativeMapper(testLib);
        mapper.register(Statics.class);
        mapper.populateStatic(Statics.class);
    }

    public static OuterT makeOuterT() throws Throwable {
        var mapper = new NativeMapper(testLib);
        mapper.register(OuterT.class);
        mapper.populateStatic(OuterT.class);

        var innerPtr = mapper.allocate(InnerT.class, SegmentScope.global());
        return mapper.populate(new OuterT(), OuterT.makeOuterT(SegmentAllocator.nativeAllocator(SegmentScope.auto()), innerPtr));
    }

    public static Array<InnerT> makeInnerTArray() throws Throwable {
        var mapper = new NativeMapper(testLib);
        mapper.register(InnerT.class);
        mapper.populateFunctionHandles(InnerT.class);

        return mapper.arrayOf(InnerT.makeInnerTArray(), InnerT.class, 3, SegmentScope.global());
    }

}
