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
import com.myworldvw.buoy.Pointer;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;

public class GlobalHandler<T> implements StructMappingHandler<T> {

    protected final Field field;
    protected final String name;
    protected final Class<?> type;
    protected final boolean pointer;

    protected volatile MemorySegment symbolPtr;

    public GlobalHandler(Field field, String name, Class<?> type, boolean pointer){
        this.field = field;
        this.name = name;
        this.type = type;
        this.pointer = pointer;

        if(!field.getType().equals(MemorySegment.class)){
            throw new IllegalArgumentException("Globals can only be accessed via MemorySegment");
        }
    }

    @Override
    public void fill(NativeMapper mapper, MemorySegment segment, T target) throws IllegalAccessException {
        if(Util.skipField(field, target)){
            return;
        }

        if(symbolPtr == null){
            symbolPtr = mapper.getLookup().lookup(name)
                    .map(symbol -> Pointer.cast(symbol, mapper.getLayout(pointer ? MemorySegment.class : type)))
                    .orElseThrow(() -> new IllegalArgumentException("Symbol " + name + " not found"));
        }

        field.set(target, symbolPtr);
    }
}
