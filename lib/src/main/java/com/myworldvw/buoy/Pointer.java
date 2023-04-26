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

import java.lang.foreign.*;

public class Pointer {

    public static MemorySegment cast(MemorySegment p, MemoryLayout targetType){
        return MemorySegment.ofAddress(p.address(), targetType.byteSize(), p.session());
    }

    public static MemorySegment cast(Addressable p, MemoryLayout targetType, MemorySession scope){
        return MemorySegment.ofAddress(p.address(), targetType.byteSize(), scope);
    }

    public static MemoryAddress getAddress(MemorySegment p){
        return p.get(ValueLayout.ADDRESS, 0);
    }

    public static void setAddress(MemorySegment p, MemoryAddress a){
        p.set(ValueLayout.ADDRESS, 0, a);
    }

    public static boolean getBoolean(MemorySegment p){
        return p.get(ValueLayout.JAVA_BOOLEAN, 0);
    }

    public static void setBoolean(MemorySegment p, boolean b){
        p.set(ValueLayout.JAVA_BOOLEAN, 0, b);
    }

    public static byte getByte(MemorySegment p){
        return p.get(ValueLayout.JAVA_BYTE, 0);
    }

    public static void setByte(MemorySegment p, byte b){
        p.set(ValueLayout.JAVA_BYTE, 0, b);
    }

    public static char getChar(MemorySegment p){
        return p.get(ValueLayout.JAVA_CHAR, 0);
    }

    public static void setChar(MemorySegment p, char c){
        p.set(ValueLayout.JAVA_CHAR, 0, c);
    }

    public static double getDouble(MemorySegment p){
        return p.get(ValueLayout.JAVA_DOUBLE, 0);
    }

    public static void setDouble(MemorySegment p, double d){
        p.set(ValueLayout.JAVA_DOUBLE, 0, d);
    }

    public static float getFloat(MemorySegment p){
        return p.get(ValueLayout.JAVA_FLOAT, 0);
    }

    public static void setFloat(MemorySegment p, float f){
        p.set(ValueLayout.JAVA_FLOAT, 0, f);
    }

    public static int getInt(MemorySegment p){
        return p.get(ValueLayout.JAVA_INT, 0);
    }

    public static void setInt(MemorySegment p, int i){
        p.set(ValueLayout.JAVA_INT, 0, i);
    }

    public static long getLong(MemorySegment p){
        return p.get(ValueLayout.JAVA_LONG, 0);
    }

    public static void setLong(MemorySegment p, long l){
        p.set(ValueLayout.JAVA_LONG, 0, l);
    }

    public static short getShort(MemorySegment p){
        return p.get(ValueLayout.JAVA_SHORT, 0);
    }

    public static void setShort(MemorySegment p, short s){
        p.set(ValueLayout.JAVA_SHORT, 0, s);
    }
}
