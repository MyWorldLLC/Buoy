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

import java.lang.foreign.Addressable;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

@Struct(
        name = "outer_t",
        fields = {
                @StructField(name = "outer_a", type = byte.class),
                @StructField(name = "nested", type = InnerT.class),
                @StructField(name = "nested_ptr", type = InnerT.class, pointer = true)
        }
)
public class OuterT {

    @SelfPointer
    protected MemorySegment self;

    @FieldHandle(name = "outer_a")
    protected VarHandle outerA;

    @FieldHandle(name = "nested")
    protected InnerT nested = new InnerT();

    @FieldHandle(name = "nested_ptr")
    protected InnerT nestedPtr = new InnerT();

    public byte getOuterA(){
        return (byte) outerA.get(self);
    }

    public InnerT getNested(){
        return nested;
    }

    public InnerT getNestedPtr(){
        return nestedPtr;
    }

    @FunctionHandle(name = "make_outer_t_value", returns = OuterT.class, params = {MemorySegment.class})
    protected static MethodHandle makeOuterTValue;

    public static MemorySegment makeOuterT(SegmentAllocator returnAllocator, MemorySegment innerTPtr) throws Throwable {
        return (MemorySegment) makeOuterTValue.invokeExact(returnAllocator, (Addressable)innerTPtr);
    }
}
