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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StructLayoutTest {

    @Test
    void mapStructFields() throws Throwable {

        var numbers = TestUtil.makeNumbersT();
        numbers.set((short)1, 2);

        assertEquals(3, numbers.addShort());
        assertEquals(3, numbers.addNumbersT());
    }

    @Test
    void mapModelsToInlineAndStructPointers() throws Throwable {

        // ====== Setup ======
        var outer = TestUtil.makeOuterT();
        var mapper = TestUtil.makeMapper();

        var first = outer.getNestedArray().get(0, new InnerT(), mapper);
        var second = outer.getNestedArray().get(1, new InnerT(), mapper);
        var third = outer.getNestedArray().get(2, new InnerT(), mapper);

        // ====== Assertions ======
        assertEquals(123, outer.getOuterA());

        assertEquals(45, outer.getNested().getInnerA());
        assertEquals(67, outer.getNested().getInnerB());

        assertEquals(11, first.getInnerA());
        assertEquals(22, first.getInnerB());

        assertEquals(33, second.getInnerA());
        assertEquals(44, second.getInnerB());

        assertEquals(55, third.getInnerA());
        assertEquals(66, third.getInnerB());

        assertEquals(89, outer.getNestedPtr().getInnerA());
        assertEquals(10, outer.getNestedPtr().getInnerB());
    }
}
