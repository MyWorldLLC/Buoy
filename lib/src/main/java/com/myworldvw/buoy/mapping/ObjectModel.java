package com.myworldvw.buoy.mapping;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Field;
import java.util.Map;

public class ObjectModel<T> {

    protected Map<Field, FieldHandler> fieldCache;

    public T fill(T instance, MemorySegment structPtr){



        return instance;
    }




}
