package com.myworldvw.buoy;

import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class Pointer {

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

    public static void setFloat(MemorySegment p, Float f){
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
