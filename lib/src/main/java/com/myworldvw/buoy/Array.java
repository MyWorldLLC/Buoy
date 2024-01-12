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

public record Array<T>(MemorySegment array, MemoryLayout typeLayout, Class<T> carrierType, boolean isPointer) {

    public long length(){
        return array.byteSize() / typeLayout().byteSize();
    }

    public MemorySegment get(long index){
        return get(array, typeLayout, index);
    }

    public T get(long index, T target, NativeMapper mapper) throws IllegalAccessException {
        mapper.populate(target, get(index));
        return target;
    }

    public void set(long index, MemorySegment value){
        set(array, typeLayout, index, value);
    }

    public MemorySegment getAddress(long index){
        return getAddress(array, index);
    }

    public void setAddress(long index, long a){
        setAddress(index, MemorySegment.ofAddress(a));
    }

    public void setAddress(long index, MemorySegment a){
        setAddress(array, index, a);
    }

    public boolean getBoolean(long index){
        return getBoolean(array, index);
    }

    public void setBoolean(long index, boolean b){
        setBoolean(array, index, b);
    }

    public byte getByte(long index){
        return getByte(array, index);
    }

    public void setByte(long index, byte b){
        setByte(array, index, b);
    }

    public char getChar(long index){
        return getChar(array, index);
    }

    public void setChar(long index, char c){
        setChar(array, index, c);
    }

    public double getDouble(long index){
        return getDouble(array, index);
    }

    public void setDouble(long index, double d){
        setDouble(array, index, d);
    }

    public float getFloat(long index){
        return getFloat(array, index);
    }

    public void setFloat(long index, float f){
        setFloat(array, index, f);
    }

    public int getInt(long index){
        return getInt(array, index);
    }

    public void setInt(long index, int i){
        setInt(array, index, i);
    }

    public long getLong(long index){
        return getLong(array, index);
    }

    public void setLong(long index, long l){
       setLong(array, index, l);
    }

    public short getShort(long index){
        return getShort(array, index);
    }

    public void setShort(long index, short s){
       setShort(array, index, s);
    }

    public static MemorySegment cast(MemorySegment p, MemoryLayout targetType, long length){
        return Pointer.cast(p, MemoryLayout.sequenceLayout(length, targetType));
    }

    public static MemorySegment cast(MemorySegment p, MemoryLayout targetType, long length, Arena arena){
        return Pointer.cast(p, MemoryLayout.sequenceLayout(length, targetType), arena);
    }

    public static MemorySegment get(MemorySegment p, MemoryLayout type, long index){
        return p.asSlice(type.byteSize() * index, type.byteSize());
    }

    public static void set(MemorySegment p, MemoryLayout type, long index, MemorySegment e){
        MemorySegment.copy(e, 0, p, type.byteSize() * index, type.byteSize());
    }

    public static MemorySegment getAddress(MemorySegment p, long index){
        return p.get(ValueLayout.ADDRESS, ValueLayout.ADDRESS.byteSize() * index);
    }

    public static void setAddress(MemorySegment p, long index, MemorySegment a){
        p.set(ValueLayout.ADDRESS, ValueLayout.ADDRESS.byteSize() * index, a);
    }

    public static boolean getBoolean(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN.byteSize() * index);
    }

    public static void setBoolean(MemorySegment p, long index, boolean b){
        p.set(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN.byteSize() * index, b);
    }

    public static byte getByte(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BYTE.byteSize() * index);
    }

    public static void setByte(MemorySegment p, long index, byte b){
        p.set(ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BYTE.byteSize() * index, b);
    }

    public static char getChar(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR.byteSize() * index);
    }

    public static void setChar(MemorySegment p, long index, char c){
        p.set(ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR.byteSize() * index, c);
    }

    public static double getDouble(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE.byteSize() * index);
    }

    public static void setDouble(MemorySegment p, long index, double d){
        p.set(ValueLayout.JAVA_DOUBLE, ValueLayout.JAVA_DOUBLE.byteSize() * index, d);
    }

    public static float getFloat(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT.byteSize() * index);
    }

    public static void setFloat(MemorySegment p, long index, float f){
        p.set(ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT.byteSize() * index, f);
    }

    public static int getInt(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT.byteSize() * index);
    }

    public static void setInt(MemorySegment p, long index, int i){
        p.set(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT.byteSize() * index, i);
    }

    public static long getLong(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG.byteSize() * index);
    }

    public static void setLong(MemorySegment p, long index, long l){
        p.set(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG.byteSize() * index, l);
    }

    public static short getShort(MemorySegment p, long index){
        return p.get(ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT.byteSize() * index);
    }

    public static void setShort(MemorySegment p, long index, short s){
        p.set(ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT.byteSize() * index, s);
    }

}
