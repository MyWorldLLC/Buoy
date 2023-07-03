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

import com.myworldvw.buoy.FunctionHandle;
import com.myworldvw.buoy.GlobalHandle;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

public class Globals {

    @GlobalHandle(name = "test_error", type = int.class)
    public static MemorySegment error;

    @FunctionHandle(name = "set_and_get_error", returns = int.class, params = {int.class})
    public MethodHandle setAndGetError;

    @FunctionHandle(name = "get_error", returns = int.class)
    public MethodHandle getError;

    @FunctionHandle(name = "get_error_address", returns = MemorySegment.class)
    public MethodHandle getErrorAddress;

    public int setAndGetError(int error) throws Throwable {
        return (int) setAndGetError.invokeExact(error);
    }

    public int getError() throws Throwable {
        return (int) getError.invokeExact();
    }

    public MemorySegment getErrorAddress() throws Throwable {
        return (MemorySegment) getErrorAddress.invokeExact();
    }

}
