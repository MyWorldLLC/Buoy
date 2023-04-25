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

import java.lang.invoke.MethodHandle;

public class TestFunctionHandles {

    @FunctionHandle(name = "add", returns = int.class, params = {int.class, int.class})
    public MethodHandle add;

    @FunctionHandle(name = "add", returns = int.class, params = {int.class, int.class})
    public static MethodHandle unused;

    public int add(int a, int b) throws Throwable {
        return (int) add.invokeExact(a, b);
    }

}
