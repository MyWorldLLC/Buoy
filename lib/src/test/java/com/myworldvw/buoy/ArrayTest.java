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

package com.myworldvw.buoy;

import com.myworldvw.buoy.util.InnerT;
import com.myworldvw.buoy.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.lang.foreign.SegmentScope;
import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArrayTest {

    @Test
    void getArrayElements() throws Throwable {

        // ====== Setup ======
        var array = TestUtil.makeInnerTArray();
        var mapper = new NativeMapper(TestUtil.testLib);
        mapper.register(InnerT.class);

        var first = array.get(0, new InnerT(), mapper);
        var second = array.get(1, new InnerT(), mapper);
        var third = array.get(2, new InnerT(), mapper);


        // ====== Assertions ======
        assertEquals(array.length(), 3);

        assertEquals(first.getInnerA(), (byte) 11);
        assertEquals(first.getInnerB(), 22);

        assertEquals(second.getInnerA(), (byte) 33);
        assertEquals(second.getInnerB(), 44);

        assertEquals(third.getInnerA(), (byte) 55);
        assertEquals(third.getInnerB(), 66);
    }

    @Test
    void setArrayElementFields() throws Throwable {

        // ====== Setup ======
        var array = TestUtil.makeInnerTArray();
        var mapper = new NativeMapper(TestUtil.testLib);
        mapper.register(InnerT.class);

        var first = array.get(0, new InnerT(), mapper);
        var second = array.get(1, new InnerT(), mapper);
        var third = array.get(2, new InnerT(), mapper);

        second.setInnerB(77);

        // ====== Assertions ======
        assertEquals(first.getInnerA(), (byte) 11);
        assertEquals(first.getInnerB(), 22);

        assertEquals(second.getInnerA(), (byte) 33);
        assertEquals(second.getInnerB(), 77);

        assertEquals(third.getInnerA(), (byte) 55);
        assertEquals(third.getInnerB(), 66);
    }

    @Test
    void setPrimitiveArrayElements(){

        // ====== Setup ======
        var array = Platform.allocate(ValueLayout.JAVA_INT, 3, SegmentScope.global());
        Array.setInt(array, 0, 0xAA);
        Array.setInt(array, 1, 0xBB);
        Array.setInt(array, 2, 0xCC);

        // ====== Assertions ======
        assertEquals(Array.getInt(array, 0), 0xAA);
        assertEquals(Array.getInt(array, 1), 0xBB);
        assertEquals(Array.getInt(array, 2), 0xCC);

    }
}
