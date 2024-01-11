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
import java.lang.foreign.MemorySegment.Scope;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public class Platform {

    public static final MemoryLayout SIZE_T = ValueLayout.JAVA_LONG;

    public static final int C_TRUE = 1;
    public static final int C_FALSE = 0;

    public static int toCBoolean(boolean b){
        return b ? C_TRUE : C_FALSE;
    }

    public static MemorySegment toCFunction(MethodHandle method, FunctionDescriptor descriptor, Arena arena){
        return Linker.nativeLinker().upcallStub(method, descriptor, arena);
    }

    public static String standardLibraryName(String name){
        var os = detectOS();
        return os == null ? name : switch (os){
            case LINUX -> "lib" + name + ".so";
            case MAC_OSX -> "lib" + name + ".dylib";
            case WINDOWS -> name + ".dll";
        };
    }

    public static SymbolLookup loadLibrary(String libName, Arena arena){
        return SymbolLookup.libraryLookup(libName, arena);
    }

    public static SymbolLookup loadLibrary(Path libPath, Arena arena){
        return SymbolLookup.libraryLookup(libPath, arena);
    }

    public static Arena globalArena(){
        return Arena.global();
    }

    public static Arena autoArena(){
        return Arena.ofAuto();
    }

    public static MemorySegment allocate(MemoryLayout layout){
        return allocate(layout, Arena.global());
    }

    public static MemorySegment allocate(MemoryLayout layout, long count){
        return allocate(layout, count, Arena.global());
    }

    public static MemorySegment allocate(MemoryLayout layout, Arena arena){
        return allocate(layout, 1, arena);
    }

    public static MemorySegment allocate(MemoryLayout layout, long count, Arena arena){
        return arena.allocate(layout.byteSize() * count);
    }

    public static long offsetOf(MemoryLayout structLayout, String field){
        return structLayout.byteOffset(MemoryLayout.PathElement.groupElement(field));
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
