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

import com.myworldvw.buoy.*;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

@Struct(
        name = "numbers_t",
        fields = {
                @StructField(name = "a", type = short.class),
                @StructField(name = "b", type = int.class)
        }
)
public class NumbersT {

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(name = "a")
    protected VarHandle a;

    @FieldHandle(name = "b")
    protected VarHandle b;

    @FunctionHandle(name = "add_short", returns = int.class, params = {int.class, short.class})
    protected MethodHandle add_short;

    @FunctionHandle(name = "add_numbers_t", returns = int.class, params = { MemorySegment.class })
    protected MethodHandle add_numbers_t;

    public int addShort() throws Throwable {
        return (int) add_short.invokeExact((int)b.get(self), (short)a.get(self));
    }

    public void set(short a, int b){
        this.a.set(self, a);
        this.b.set(self, b);
    }

    public int addNumbersT() throws Throwable {
        return (int) add_numbers_t.invokeExact(self);
    }

}
