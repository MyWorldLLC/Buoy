package com.myworldvw.buoy.mapping;

import java.util.Arrays;

public record StructModel(String name, FieldModel... fields) {

    public FieldModel field(String name){
        return Arrays.stream(fields)
                .filter(f -> f.name().equals(name))
                .findFirst()
                .orElse(null);
    }

}
