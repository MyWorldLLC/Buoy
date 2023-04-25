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

package com.myworldvw.buoy.mapping;

import com.myworldvw.buoy.NativeMapper;

import java.lang.foreign.FunctionDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public class FunctionHandler<T> implements FunctionMappingHandler<T> {

    protected final Field field;
    protected final String name;
    protected final FunctionDescriptor descriptor;

    protected MethodHandle handle;

    public FunctionHandler(Field field, String functionName, FunctionDescriptor descriptor){
        this.field = field;
        name = functionName;
        this.descriptor = descriptor;
    }

    @Override
    public void fill(NativeMapper mapper, T target) throws IllegalAccessException {

        if(Util.skipField(field, target)){
            return;
        }

        if(handle == null){
            handle = mapper.getOrDefineFunction(name, descriptor);
        }

        field.set(target, handle);
    }
}
