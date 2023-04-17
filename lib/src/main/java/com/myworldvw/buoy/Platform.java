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

    public static Architecture detectArchitecture(){
        var arch = System.getProperty("os.arch");
        return switch (arch){
            case "amd64" -> Architecture.AMD64;
            case "x86" -> Architecture.X86;
            case "ia64" -> Architecture.IA64;
            case "unknown" -> Architecture.UNKNOWN;
            default -> Architecture.UNKNOWN;
        };
    }

    public enum OperatingSystemFamily {
        LINUX, MAC_OSX, WINDOWS
    }

    public enum Architecture {
        AMD64(64), IA64(64), X86(32), UNKNOWN(64);
        int width;

        Architecture(int width){
            this.width = width;
        }

        public int width(){
            return width;
        }
    }
}
