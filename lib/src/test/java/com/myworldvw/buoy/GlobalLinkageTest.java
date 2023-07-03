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

import com.myworldvw.buoy.util.Globals;
import com.myworldvw.buoy.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GlobalLinkageTest {

    @Test
    void linkToGlobalField() throws Throwable {

        var globals = TestUtil.makeGlobals();

        assertNotEquals(MemorySegment.NULL, Globals.error);
        assertEquals(globals.getErrorAddress().address(), Globals.error.address());

    }

    @Test
    void getAndSetGlobalField() throws Throwable {

        var globals = TestUtil.makeGlobals();
        Globals.error.set(ValueLayout.JAVA_INT, 0, 0xFFEB);

        assertEquals(globals.getError(), Globals.error.get(ValueLayout.JAVA_INT, 0));
        assertEquals(0xFFEB, globals.getError());
    }

}
