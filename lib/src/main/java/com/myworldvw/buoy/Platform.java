package com.myworldvw.buoy;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Platform {

    public static final MemoryLayout SIZE_T = ValueLayout.JAVA_LONG;

    public static final int C_TRUE = 1;
    public static final int C_FALSE = 0;

    public static int toCBoolean(boolean b){
        return b ? C_TRUE : C_FALSE;
    }

    public static MemorySegment toCFunction(MethodHandle method, FunctionDescriptor descriptor, MemorySession scope){
        return Linker.nativeLinker().upcallStub(method, descriptor, scope);
    }

    public static OperatingSystemFamily detectOS(){
        var os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win")){
            return OperatingSystemFamily.WINDOWS;
        }else if(os.contains("linux")){
            return OperatingSystemFamily.LINUX;
        }else if(os.contains("mac")){
            return OperatingSystemFamily.MAC_OSX;
        }

        return null;
    }

    public enum OperatingSystemFamily {
        LINUX, MAC_OSX, WINDOWS
    }
}
