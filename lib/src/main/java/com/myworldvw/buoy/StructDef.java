package com.myworldvw.buoy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record StructDef(String name, boolean isPacked, FieldDef... fields) {

    public FieldDef field(String name){
        return Arrays.stream(fields)
                .filter(f -> f.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static Builder create(){
        return new Builder();
    }

    public static Builder create(String name){
        return new Builder(name, false);
    }

    public static Builder create(String name, boolean isPacked){
        return new Builder(name, isPacked);
    }

    public static class Builder {

        protected final String name;
        protected final boolean isPacked;
        protected final List<FieldDef> fields = new ArrayList<>();

        protected Builder() {
            this(null, false);
        }

        protected Builder(String name, boolean isPacked) {
            this.name = name;
            this.isPacked = isPacked;
        }

        public Builder with(FieldDef field) {
            fields.add(field);
            return this;
        }

        public StructDef build() {
            return new StructDef(name, isPacked, fields.toArray(new FieldDef[]{}));
        }

    }

}
