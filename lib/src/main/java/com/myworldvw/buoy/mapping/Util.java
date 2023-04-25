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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Util {

    public static <T> boolean skipField(Field field, T target){
        // Skip fields that are static when the target is not a class
        var isStatic = Modifier.isStatic(field.getModifiers());
        var targetIsClass = target instanceof Class;
        return (isStatic && !targetIsClass) ||
                (!isStatic && targetIsClass);
    }

    public static <T> boolean isStaticTarget(T target){
        return target instanceof Class;
    }

}
