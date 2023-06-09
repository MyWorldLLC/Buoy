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
        name = "inner_t",
        fields = {
                @StructField(name = "a", type = byte.class),
                @StructField(name = "b", type = int.class)
        }
)
public class InnerT {

    @FunctionHandle(name = "make_inner_t_array", returns = MemorySegment.class)
    public static MethodHandle makeInnerTArray;

    public static MemorySegment makeInnerTArray() throws Throwable {
        return (MemorySegment) makeInnerTArray.invokeExact();
    }

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(name = "a")
    protected VarHandle a;

    @FieldHandle(name = "b")
    protected VarHandle b;

    public byte getInnerA(){
        return (byte) a.get(self);
    }

    public int getInnerB(){
        return (int) b.get(self);
    }

    public void setInnerB(int value){
        b.set(self, value);
    }
}
