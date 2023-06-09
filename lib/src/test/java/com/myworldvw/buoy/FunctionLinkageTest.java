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

import com.myworldvw.buoy.util.Statics;
import com.myworldvw.buoy.util.TestFunctionHandles;
import com.myworldvw.buoy.util.TestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionLinkageTest {

    @Test
    void succeedsMappingFunctionHandles(){

        assertDoesNotThrow(TestUtil::makeFunctionHandles);

    }

    @Test
    void mapsAddIntegers() throws Throwable {

        var functions = TestUtil.makeFunctionHandles();

        assertNotNull(functions.add);
        assertEquals(3, functions.add(1, 2));

    }

    @Test
    void fillsStaticFieldsInStaticUseContext() throws IllegalAccessException {

        TestUtil.fillStatics();

        assertNotNull(Statics.staticAdd);

    }

    @Test
    void doesNotFillStaticFieldsInInstanceUseContext() throws IllegalAccessException {

        TestUtil.makeFunctionHandles();

        assertNull(TestFunctionHandles.unused);
    }

}
