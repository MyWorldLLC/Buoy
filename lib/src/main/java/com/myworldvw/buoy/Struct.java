package com.myworldvw.buoy;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;

public class Struct {

    public static VarHandle handle(MemoryLayout struct, String field){
        return struct.varHandle(MemoryLayout.PathElement.groupElement(field));
    }

    public static Builder create(){
        return new Builder();
    }

    public static Builder create(String name){
        return new Builder(name);
    }

    public static class Builder {

        protected final String name;
        protected final List<MemoryLayout> elements = new ArrayList<>();

        protected Builder(){
            this(null);
        }

        protected Builder(String name){
            this.name = name;
        }

        public Builder with(MemoryLayout layout){
            elements.add(layout);
            return this;
        }

        public Builder with(String name, MemoryLayout layout){
            return with(layout.withName(name));
        }

        public Builder withAddress(String name){
            return with(ValueLayout.ADDRESS.withName(name));
        }

        public Builder withBoolean(String name){
            return with(ValueLayout.JAVA_BOOLEAN.withName(name));
        }

        public Builder withByte(String name){
            return with(ValueLayout.JAVA_BYTE.withName(name));
        }

        public Builder withChar(String name){
            return with(ValueLayout.JAVA_CHAR.withName(name));
        }

        public Builder withDouble(String name){
            return with(ValueLayout.JAVA_DOUBLE.withName(name));
        }

        public Builder withFloat(String name){
            return with(ValueLayout.JAVA_FLOAT.withName(name));
        }

        public Builder withInt(String name){
            return with(ValueLayout.JAVA_INT.withName(name));
        }

        public Builder withLong(String name){
            return with(ValueLayout.JAVA_LONG.withName(name));
        }

        public Builder withShort(String name){
            return with(ValueLayout.JAVA_SHORT.withName(name));
        }

        public MemoryLayout build(){
            return MemoryLayout.structLayout(elements.toArray(new MemoryLayout[]{})).withName(name);
        }
    }

}
