package com.myworldvw.buoy.mapping;

public record FieldDef(int index, String name, Class<?> type, boolean isPointer) {

    FieldDef(int index, String name, Class<?> type){
        this(index, name, type, false);
    }

}
