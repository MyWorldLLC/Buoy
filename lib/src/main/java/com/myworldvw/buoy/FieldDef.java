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

public record FieldDef(int index, String name, Class<?> type, boolean isPointer, long array) {

    public FieldDef{
        if(array < 0){
            throw new IllegalArgumentException("Array parameter must be >= 0: " + array);
        }
    }

    FieldDef(int index, String name, Class<?> type){
        this(index, name, type, false, 0);
    }

    public boolean isArray(){
        return array > 0;
    }
}
